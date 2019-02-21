package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.locationtech.proj4j.units.Units;

import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.sf.CircularString;
import mil.nga.sf.CompoundCurve;
import mil.nga.sf.Curve;
import mil.nga.sf.CurvePolygon;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryCollection;
import mil.nga.sf.GeometryType;
import mil.nga.sf.LineString;
import mil.nga.sf.MultiLineString;
import mil.nga.sf.MultiPoint;
import mil.nga.sf.MultiPolygon;
import mil.nga.sf.Point;
import mil.nga.sf.Polygon;
import mil.nga.sf.PolyhedralSurface;
import mil.nga.sf.TIN;
import mil.nga.sf.Triangle;
import mil.nga.sf.proj.Projection;
import mil.nga.sf.proj.ProjectionConstants;
import mil.nga.sf.proj.ProjectionTransform;
import mil.nga.sf.util.GeometryUtils;

/**
 * Provides conversions methods between geometry object and Google Maps Android
 * API v2 Shapes
 *
 * @author osbornb
 */
public class GoogleMapShapeConverter {

    /**
     * Projection
     */
    private final Projection projection;

    /**
     * Transformation to WGS 84
     */
    private final ProjectionTransform toWgs84;

    /**
     * Transformation from WGS 84
     */
    private final ProjectionTransform fromWgs84;

    /**
     * Transformation to Web Mercator
     */
    private final ProjectionTransform toWebMercator;

    /**
     * Transformation from Web Mercator
     */
    private final ProjectionTransform fromWebMercator;

    /**
     * Convert polygon exteriors to specified orientation
     */
    private PolygonOrientation exteriorOrientation = PolygonOrientation.COUNTERCLOCKWISE;

    /**
     * Convert polygon holes to specified orientation
     */
    private PolygonOrientation holeOrientation = PolygonOrientation.CLOCKWISE;

    /**
     * Tolerance in meters for simplifying lines and polygons to a similar curve with fewer points.
     * Default is null resulting in no simplification
     */
    private Double simplifyTolerance;

    /**
     * Constructor
     *
     * @since 1.3.2
     */
    public GoogleMapShapeConverter() {
        this(null);
    }

    /**
     * Constructor with specified projection, see
     * {@link FeatureDao#getProjection}
     *
     * @param projection projection
     */
    public GoogleMapShapeConverter(Projection projection) {
        this.projection = projection;
        if (projection != null) {
            toWgs84 = projection
                    .getTransformation(ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);
            Projection wgs84 = toWgs84.getToProjection();
            fromWgs84 = wgs84.getTransformation(projection);
            toWebMercator = projection.getTransformation(ProjectionConstants.EPSG_WEB_MERCATOR);
            Projection webMercator = toWebMercator.getToProjection();
            fromWebMercator = webMercator.getTransformation(projection);
        } else {
            toWgs84 = null;
            fromWgs84 = null;
            toWebMercator = null;
            fromWebMercator = null;
        }
    }

    /**
     * Get the projection
     *
     * @return projection
     */
    public Projection getProjection() {
        return projection;
    }

    /**
     * Get exterior orientation for conversions. Defaults to PolygonOrientation.COUNTERCLOCKWISE
     *
     * @return exterior orientation
     * @since 1.3.2
     */
    public PolygonOrientation getExteriorOrientation() {
        return exteriorOrientation;
    }

    /**
     * Set the exterior orientation for conversions, set to null to maintain orientation
     *
     * @param exteriorOrientation orientation
     * @since 1.3.2
     */
    public void setExteriorOrientation(PolygonOrientation exteriorOrientation) {
        this.exteriorOrientation = exteriorOrientation;
    }

    /**
     * Get polygon hole orientation for conversions. Defaults to PolygonOrientation.CLOCKWISE
     *
     * @return hole orientation
     * @since 1.3.2
     */
    public PolygonOrientation getHoleOrientation() {
        return holeOrientation;
    }

    /**
     * Set the polygon hole orientation for conversions, set to null to maintain orientation
     *
     * @param holeOrientation orientation
     * @since 1.3.2
     */
    public void setHoleOrientation(PolygonOrientation holeOrientation) {
        this.holeOrientation = holeOrientation;
    }

    /**
     * Get the simplify tolerance in meters to simplify lines and polygons to similar curves with fewer points
     *
     * @return simplify tolerance in meters, null for no simplification
     */
    public Double getSimplifyTolerance() {
        return simplifyTolerance;
    }

    /**
     * Set the simplify tolerance in meters to simplify lines and polygons to similar curves with fewer points
     *
     * @param simplifyTolerance simplify tolerance in meters, null for no simplification
     */
    public void setSimplifyTolerance(Double simplifyTolerance) {
        this.simplifyTolerance = simplifyTolerance;
    }

    /**
     * Transform a projection point to WGS84
     *
     * @param point projection point
     * @return WGS84 point
     */
    public Point toWgs84(Point point) {
        if (projection != null) {
            point = toWgs84.transform(point);
        }
        return point;
    }

    /**
     * Transform a WGS84 point to the projection
     *
     * @param point WGS84 point
     * @return projection point
     */
    public Point toProjection(Point point) {
        if (projection != null) {
            point = fromWgs84.transform(point);
        }
        return point;
    }

    /**
     * Convert a {@link Point} to a {@link LatLng}
     *
     * @param point point
     * @return lat lng
     */
    public LatLng toLatLng(Point point) {
        point = toWgs84(point);
        LatLng latLng = new LatLng(point.getY(), point.getX());
        return latLng;
    }

    /**
     * Convert a {@link LatLng} to a {@link Point}
     *
     * @param latLng lat lng
     * @return point
     */
    public Point toPoint(LatLng latLng) {
        return toPoint(latLng, false, false);
    }

    /**
     * Convert a {@link LatLng} to a {@link Point}
     *
     * @param latLng lat lng
     * @param hasZ   has z flag
     * @param hasM   has m flag
     * @return point
     */
    public Point toPoint(LatLng latLng, boolean hasZ, boolean hasM) {
        double y = latLng.latitude;
        double x = latLng.longitude;
        Point point = new Point(hasZ, hasM, x, y);
        point = toProjection(point);
        return point;
    }

    /**
     * Convert a {@link LineString} to a {@link PolylineOptions}
     *
     * @param lineString line string
     * @return polyline options
     */
    public PolylineOptions toPolyline(LineString lineString) {

        PolylineOptions polylineOptions = new PolylineOptions();
        Double z = null;

        // Try to simplify the number of points in the line string
        List<Point> points = simplifyPoints(lineString.getPoints());

        for (Point point : points) {
            LatLng latLng = toLatLng(point);
            polylineOptions.add(latLng);
            if (point.hasZ()) {
                z = (z == null) ? point.getZ() : Math.max(z, point.getZ());
            }
        }

        if (lineString.hasZ() && z != null) {
            polylineOptions.zIndex(z.floatValue());
        }

        return polylineOptions;
    }

    /**
     * Convert a {@link Polyline} to a {@link LineString}
     *
     * @param polyline polyline
     * @return line string
     */
    public LineString toLineString(Polyline polyline) {
        return toLineString(polyline, false, false);
    }

    /**
     * Convert a {@link Polyline} to a {@link LineString}
     *
     * @param polyline polyline
     * @param hasZ     has z flag
     * @param hasM     has m flag
     * @return line string
     */
    public LineString toLineString(Polyline polyline, boolean hasZ, boolean hasM) {
        return toLineString(polyline.getPoints(), hasZ, hasM);
    }

    /**
     * Convert a {@link PolylineOptions} to a {@link LineString}
     *
     * @param polyline polyline options
     * @return line string
     */
    public LineString toLineString(PolylineOptions polyline) {
        return toLineString(polyline, false, false);
    }

    /**
     * Convert a {@link PolylineOptions} to a {@link LineString}
     *
     * @param polyline polyline options
     * @param hasZ     has z flag
     * @param hasM     has m flag
     * @return line string
     */
    public LineString toLineString(PolylineOptions polyline, boolean hasZ,
                                   boolean hasM) {
        return toLineString(polyline.getPoints(), hasZ, hasM);
    }

    /**
     * Convert a list of {@link LatLng} to a {@link LineString}
     *
     * @param latLngs lat lngs
     * @return line string
     */
    public LineString toLineString(List<LatLng> latLngs) {
        return toLineString(latLngs, false, false);
    }

    /**
     * Convert a list of {@link LatLng} to a {@link LineString}
     *
     * @param latLngs lat lngs
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return line string
     */
    public LineString toLineString(List<LatLng> latLngs, boolean hasZ,
                                   boolean hasM) {

        LineString lineString = new LineString(hasZ, hasM);

        populateLineString(lineString, latLngs);

        return lineString;
    }

    /**
     * Convert a list of {@link LatLng} to a {@link CircularString}
     *
     * @param latLngs lat lngs
     * @return circular string
     */
    public CircularString toCircularString(List<LatLng> latLngs) {
        return toCircularString(latLngs, false, false);
    }

    /**
     * Convert a list of {@link LatLng} to a {@link CircularString}
     *
     * @param latLngs lat lngs
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return circular string
     */
    public CircularString toCircularString(List<LatLng> latLngs, boolean hasZ,
                                           boolean hasM) {

        CircularString circularString = new CircularString(hasZ, hasM);

        populateLineString(circularString, latLngs);

        return circularString;
    }

    /**
     * Convert a list of {@link LatLng} to a {@link LineString}
     *
     * @param lineString line string
     * @param latLngs    lat lngs
     */
    public void populateLineString(LineString lineString, List<LatLng> latLngs) {

        for (LatLng latLng : latLngs) {
            Point point = toPoint(latLng, lineString.hasZ(), lineString.hasM());
            lineString.addPoint(point);
        }
    }

    /**
     * Convert a {@link Polygon} to a {@link PolygonOptions}
     *
     * @param polygon polygon
     * @return polygon options
     */
    public PolygonOptions toPolygon(Polygon polygon) {

        PolygonOptions polygonOptions = new PolygonOptions();

        List<LineString> rings = polygon.getRings();

        if (!rings.isEmpty()) {

            Double z = null;

            // Add the polygon points
            LineString polygonLineString = rings.get(0);

            // Try to simplify the number of points in the polygon ring
            List<Point> points = simplifyPoints(polygonLineString.getPoints());

            for (Point point : points) {
                LatLng latLng = toLatLng(point);
                polygonOptions.add(latLng);
                if (point.hasZ()) {
                    z = (z == null) ? point.getZ() : Math.max(z, point.getZ());
                }
            }

            // Add the holes
            for (int i = 1; i < rings.size(); i++) {
                LineString hole = rings.get(i);

                // Try to simplify the number of points in the polygon hole
                List<Point> holePoints = simplifyPoints(hole.getPoints());

                List<LatLng> holeLatLngs = new ArrayList<LatLng>();
                for (Point point : holePoints) {
                    LatLng latLng = toLatLng(point);
                    holeLatLngs.add(latLng);
                    if (point.hasZ()) {
                        z = (z == null) ? point.getZ() : Math.max(z,
                                point.getZ());
                    }
                }
                polygonOptions.addHole(holeLatLngs);
            }

            if (polygon.hasZ() && z != null) {
                polygonOptions.zIndex(z.floatValue());
            }
        }

        return polygonOptions;
    }

    /**
     * Convert a {@link CurvePolygon} to a {@link PolygonOptions}
     *
     * @param curvePolygon curve polygon
     * @return polygon options
     * @since 1.4.1
     */
    public PolygonOptions toCurvePolygon(CurvePolygon curvePolygon) {

        PolygonOptions polygonOptions = new PolygonOptions();

        List<Curve> rings = curvePolygon.getRings();

        if (!rings.isEmpty()) {

            Double z = null;

            // Add the polygon points
            Curve curve = rings.get(0);
            if (curve instanceof CompoundCurve) {
                CompoundCurve compoundCurve = (CompoundCurve) curve;
                for (LineString lineString : compoundCurve.getLineStrings()) {

                    // Try to simplify the number of points in the compound curve
                    List<Point> points = simplifyPoints(lineString.getPoints());

                    for (Point point : points) {
                        LatLng latLng = toLatLng(point);
                        polygonOptions.add(latLng);
                        if (point.hasZ()) {
                            z = (z == null) ? point.getZ() : Math.max(z, point.getZ());
                        }
                    }
                }
            } else if (curve instanceof LineString) {
                LineString lineString = (LineString) curve;

                // Try to simplify the number of points in the curve
                List<Point> points = simplifyPoints(lineString.getPoints());

                for (Point point : points) {
                    LatLng latLng = toLatLng(point);
                    polygonOptions.add(latLng);
                    if (point.hasZ()) {
                        z = (z == null) ? point.getZ() : Math.max(z, point.getZ());
                    }
                }
            } else {
                throw new GeoPackageException("Unsupported Curve Type: "
                        + curve.getClass().getSimpleName());
            }

            // Add the holes
            for (int i = 1; i < rings.size(); i++) {
                Curve hole = rings.get(i);
                List<LatLng> holeLatLngs = new ArrayList<LatLng>();
                if (hole instanceof CompoundCurve) {
                    CompoundCurve holeCompoundCurve = (CompoundCurve) hole;
                    for (LineString holeLineString : holeCompoundCurve.getLineStrings()) {

                        // Try to simplify the number of points in the hole
                        List<Point> holePoints = simplifyPoints(holeLineString.getPoints());

                        for (Point point : holePoints) {
                            LatLng latLng = toLatLng(point);
                            holeLatLngs.add(latLng);
                            if (point.hasZ()) {
                                z = (z == null) ? point.getZ() : Math.max(z,
                                        point.getZ());
                            }
                        }
                    }
                } else if (hole instanceof LineString) {
                    LineString holeLineString = (LineString) hole;

                    // Try to simplify the number of points in the hole
                    List<Point> holePoints = simplifyPoints(holeLineString.getPoints());

                    for (Point point : holePoints) {
                        LatLng latLng = toLatLng(point);
                        holeLatLngs.add(latLng);
                        if (point.hasZ()) {
                            z = (z == null) ? point.getZ() : Math.max(z,
                                    point.getZ());
                        }
                    }
                } else {
                    throw new GeoPackageException("Unsupported Curve Hole Type: "
                            + hole.getClass().getSimpleName());
                }

                polygonOptions.addHole(holeLatLngs);
            }

            if (curvePolygon.hasZ() && z != null) {
                polygonOptions.zIndex(z.floatValue());
            }
        }

        return polygonOptions;
    }

    /**
     * When the simplify tolerance is set, simplify the points to a similar
     * curve with fewer points.
     *
     * @param points ordered points
     * @return simplified points
     */
    private List<Point> simplifyPoints(List<Point> points) {

        List<Point> simplifiedPoints = null;
        if (simplifyTolerance != null) {

            // Reproject to web mercator if not in meters
            if (projection != null && !projection.isUnit(Units.METRES)) {
                points = toWebMercator.transform(points);
            }

            // Simplify the points
            simplifiedPoints = GeometryUtils.simplifyPoints(points,
                    simplifyTolerance);

            // Reproject back to the original projection
            if (projection != null && !projection.isUnit(Units.METRES)) {
                simplifiedPoints = fromWebMercator.transform(simplifiedPoints);
            }
        } else {
            simplifiedPoints = points;
        }

        return simplifiedPoints;
    }

    /**
     * Convert a {@link com.google.android.gms.maps.model.Polygon} to a
     * {@link Polygon}
     *
     * @param polygon polygon
     * @return polygon
     */
    public Polygon toPolygon(com.google.android.gms.maps.model.Polygon polygon) {
        return toPolygon(polygon, false, false);
    }

    /**
     * Convert a {@link com.google.android.gms.maps.model.Polygon} to a
     * {@link Polygon}
     *
     * @param polygon polygon
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return polygon
     */
    public Polygon toPolygon(com.google.android.gms.maps.model.Polygon polygon,
                             boolean hasZ, boolean hasM) {
        return toPolygon(polygon.getPoints(), polygon.getHoles(), hasZ, hasM);
    }

    /**
     * Convert a {@link com.google.android.gms.maps.model.Polygon} to a
     * {@link Polygon}
     *
     * @param polygon polygon options
     * @return polygon
     */
    public Polygon toPolygon(PolygonOptions polygon) {
        return toPolygon(polygon, false, false);
    }

    /**
     * Convert a {@link com.google.android.gms.maps.model.Polygon} to a
     * {@link Polygon}
     *
     * @param polygon polygon options
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return polygon
     */
    public Polygon toPolygon(PolygonOptions polygon, boolean hasZ, boolean hasM) {
        return toPolygon(polygon.getPoints(), polygon.getHoles(), hasZ, hasM);
    }

    /**
     * Convert a list of {@link LatLng} and list of hole list {@link LatLng} to
     * a {@link Polygon}
     *
     * @param latLngs lat lngs
     * @param holes   list of holes
     * @return polygon
     */
    public Polygon toPolygon(List<LatLng> latLngs, List<List<LatLng>> holes) {
        return toPolygon(latLngs, holes, false, false);
    }

    /**
     * Convert a list of {@link LatLng} and list of hole list {@link LatLng} to
     * a {@link Polygon}
     *
     * @param latLngs lat lngs
     * @param holes   list of holes
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return polygon
     */
    public Polygon toPolygon(List<LatLng> latLngs, List<List<LatLng>> holes,
                             boolean hasZ, boolean hasM) {

        Polygon polygon = new Polygon(hasZ, hasM);

        // Close the ring if needed and determine orientation
        closePolygonRing(latLngs);
        PolygonOrientation ringOrientation = getOrientation(latLngs);

        // Add the polygon points
        LineString polygonLineString = new LineString(hasZ, hasM);
        for (LatLng latLng : latLngs) {
            Point point = toPoint(latLng);
            // Add exterior in desired orientation order
            if (exteriorOrientation == null || exteriorOrientation == ringOrientation) {
                polygonLineString.addPoint(point);
            } else {
                polygonLineString.getPoints().add(0, point);
            }
        }
        polygon.addRing(polygonLineString);

        // Add the holes
        if (holes != null) {
            for (List<LatLng> hole : holes) {

                // Close the hole if needed and determine orientation
                closePolygonRing(hole);
                PolygonOrientation ringHoleOrientation = getOrientation(hole);

                LineString holeLineString = new LineString(hasZ, hasM);
                for (LatLng latLng : hole) {
                    Point point = toPoint(latLng);
                    // Add holes in desired orientation order
                    if (holeOrientation == null || holeOrientation == ringHoleOrientation) {
                        holeLineString.addPoint(point);
                    } else {
                        holeLineString.getPoints().add(0, point);
                    }
                }
                polygon.addRing(holeLineString);
            }
        }

        return polygon;
    }

    /**
     * Close the polygon ring (exterior or hole) points if needed
     *
     * @param points ring points
     * @since 1.3.2
     */
    public void closePolygonRing(List<LatLng> points) {
        if (!PolyUtil.isClosedPolygon(points)) {
            LatLng first = points.get(0);
            points.add(new LatLng(first.latitude, first.longitude));
        }
    }

    /**
     * Determine the closed points orientation
     *
     * @param points closed points
     * @return orientation
     * @since 1.3.2
     */
    public PolygonOrientation getOrientation(List<LatLng> points) {
        return SphericalUtil.computeSignedArea(points) >= 0 ? PolygonOrientation.COUNTERCLOCKWISE : PolygonOrientation.CLOCKWISE;
    }

    /**
     * Convert a {@link MultiPoint} to a {@link MultiLatLng}
     *
     * @param multiPoint multi point
     * @return multi lat lng
     */
    public MultiLatLng toLatLngs(MultiPoint multiPoint) {

        MultiLatLng multiLatLng = new MultiLatLng();

        for (Point point : multiPoint.getPoints()) {
            LatLng latLng = toLatLng(point);
            multiLatLng.add(latLng);
        }

        return multiLatLng;
    }

    /**
     * Convert a {@link MultiLatLng} to a {@link MultiPoint}
     *
     * @param latLngs lat lngs
     * @return multi point
     */
    public MultiPoint toMultiPoint(MultiLatLng latLngs) {
        return toMultiPoint(latLngs, false, false);
    }

    /**
     * Convert a {@link MultiLatLng} to a {@link MultiPoint}
     *
     * @param latLngs lat lngs
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return multi point
     */
    public MultiPoint toMultiPoint(MultiLatLng latLngs, boolean hasZ,
                                   boolean hasM) {
        return toMultiPoint(latLngs.getLatLngs(), hasZ, hasM);
    }

    /**
     * Convert a {@link MultiLatLng} to a {@link MultiPoint}
     *
     * @param latLngs lat lngs
     * @return multi point
     */
    public MultiPoint toMultiPoint(List<LatLng> latLngs) {
        return toMultiPoint(latLngs, false, false);
    }

    /**
     * Convert a {@link MultiLatLng} to a {@link MultiPoint}
     *
     * @param latLngs lat lngs
     * @param hasZ    has z flag
     * @param hasM    has m flag
     * @return multi point
     */
    public MultiPoint toMultiPoint(List<LatLng> latLngs, boolean hasZ,
                                   boolean hasM) {

        MultiPoint multiPoint = new MultiPoint(hasZ, hasM);

        for (LatLng latLng : latLngs) {
            Point point = toPoint(latLng);
            multiPoint.addPoint(point);
        }

        return multiPoint;
    }

    /**
     * Convert a {@link MultiLineString} to a {@link MultiPolylineOptions}
     *
     * @param multiLineString multi line string
     * @return multi polyline options
     */
    public MultiPolylineOptions toPolylines(MultiLineString multiLineString) {

        MultiPolylineOptions polylines = new MultiPolylineOptions();

        for (LineString lineString : multiLineString.getLineStrings()) {
            PolylineOptions polyline = toPolyline(lineString);
            polylines.add(polyline);
        }

        return polylines;
    }

    /**
     * Convert a list of {@link Polyline} to a {@link MultiLineString}
     *
     * @param polylineList polyline list
     * @return multi line string
     */
    public MultiLineString toMultiLineString(List<Polyline> polylineList) {
        return toMultiLineString(polylineList, false, false);
    }

    /**
     * Convert a list of {@link Polyline} to a {@link MultiLineString}
     *
     * @param polylineList polyline list
     * @param hasZ         has z flag
     * @param hasM         has m flag
     * @return multi line string
     */
    public MultiLineString toMultiLineString(List<Polyline> polylineList,
                                             boolean hasZ, boolean hasM) {

        MultiLineString multiLineString = new MultiLineString(hasZ, hasM);

        for (Polyline polyline : polylineList) {
            LineString lineString = toLineString(polyline);
            multiLineString.addLineString(lineString);
        }

        return multiLineString;
    }

    /**
     * Convert a list of List<LatLng> to a {@link MultiLineString}
     *
     * @param polylineList polyline list
     * @return multi line string
     */
    public MultiLineString toMultiLineStringFromList(
            List<List<LatLng>> polylineList) {
        return toMultiLineStringFromList(polylineList, false, false);
    }

    /**
     * Convert a list of List<LatLng> to a {@link MultiLineString}
     *
     * @param polylineList polyline list
     * @param hasZ         has z flag
     * @param hasM         has m flag
     * @return multi line string
     */
    public MultiLineString toMultiLineStringFromList(
            List<List<LatLng>> polylineList, boolean hasZ, boolean hasM) {

        MultiLineString multiLineString = new MultiLineString(hasZ, hasM);

        for (List<LatLng> polyline : polylineList) {
            LineString lineString = toLineString(polyline);
            multiLineString.addLineString(lineString);
        }

        return multiLineString;
    }

    /**
     * Convert a list of List<LatLng> to a {@link CompoundCurve}
     *
     * @param polylineList polyline list
     * @return compound curve
     */
    public CompoundCurve toCompoundCurveFromList(List<List<LatLng>> polylineList) {
        return toCompoundCurveFromList(polylineList, false, false);
    }

    /**
     * Convert a list of List<LatLng> to a {@link CompoundCurve}
     *
     * @param polylineList polyline list
     * @param hasZ         has z flag
     * @param hasM         has m flag
     * @return compound curve
     */
    public CompoundCurve toCompoundCurveFromList(
            List<List<LatLng>> polylineList, boolean hasZ, boolean hasM) {

        CompoundCurve compoundCurve = new CompoundCurve(hasZ, hasM);

        for (List<LatLng> polyline : polylineList) {
            LineString lineString = toLineString(polyline);
            compoundCurve.addLineString(lineString);
        }

        return compoundCurve;
    }

    /**
     * Convert a {@link MultiPolylineOptions} to a {@link MultiLineString}
     *
     * @param multiPolylineOptions multi polyline options
     * @return multi line string
     */
    public MultiLineString toMultiLineStringFromOptions(
            MultiPolylineOptions multiPolylineOptions) {
        return toMultiLineStringFromOptions(multiPolylineOptions, false, false);
    }

    /**
     * Convert a {@link MultiPolylineOptions} to a {@link MultiLineString}
     *
     * @param multiPolylineOptions multi polyline options
     * @param hasZ                 has z flag
     * @param hasM                 has m flag
     * @return multi line string
     */
    public MultiLineString toMultiLineStringFromOptions(
            MultiPolylineOptions multiPolylineOptions, boolean hasZ,
            boolean hasM) {

        MultiLineString multiLineString = new MultiLineString(hasZ, hasM);

        for (PolylineOptions polyline : multiPolylineOptions
                .getPolylineOptions()) {
            LineString lineString = toLineString(polyline);
            multiLineString.addLineString(lineString);
        }

        return multiLineString;
    }

    /**
     * Convert a {@link MultiPolylineOptions} to a {@link CompoundCurve}
     *
     * @param multiPolylineOptions multi polyline options
     * @return compound curve
     */
    public CompoundCurve toCompoundCurveFromOptions(
            MultiPolylineOptions multiPolylineOptions) {
        return toCompoundCurveFromOptions(multiPolylineOptions, false, false);
    }

    /**
     * Convert a {@link MultiPolylineOptions} to a {@link CompoundCurve}
     *
     * @param multiPolylineOptions multi polyline options
     * @param hasZ                 has z flag
     * @param hasM                 has m flag
     * @return compound curve
     */
    public CompoundCurve toCompoundCurveFromOptions(
            MultiPolylineOptions multiPolylineOptions, boolean hasZ,
            boolean hasM) {

        CompoundCurve compoundCurve = new CompoundCurve(hasZ, hasM);

        for (PolylineOptions polyline : multiPolylineOptions
                .getPolylineOptions()) {
            LineString lineString = toLineString(polyline);
            compoundCurve.addLineString(lineString);
        }

        return compoundCurve;
    }

    /**
     * Convert a {@link MultiPolygon} to a {@link MultiPolygonOptions}
     *
     * @param multiPolygon multi polygon
     * @return multi polygon options
     */
    public MultiPolygonOptions toPolygons(MultiPolygon multiPolygon) {

        MultiPolygonOptions polygons = new MultiPolygonOptions();

        for (Polygon polygon : multiPolygon.getPolygons()) {
            PolygonOptions polygonOptions = toPolygon(polygon);
            polygons.add(polygonOptions);
        }

        return polygons;
    }

    /**
     * Convert a list of {@link com.google.android.gms.maps.model.Polygon} to a
     * {@link MultiPolygon}
     *
     * @param polygonList polygon list
     * @return multi polygon
     */
    public MultiPolygon toMultiPolygon(
            List<com.google.android.gms.maps.model.Polygon> polygonList) {
        return toMultiPolygon(polygonList, false, false);
    }

    /**
     * Convert a list of {@link com.google.android.gms.maps.model.Polygon} to a
     * {@link MultiPolygon}
     *
     * @param polygonList polygon list
     * @param hasZ        has z flag
     * @param hasM        has m flag
     * @return multi polygon
     */
    public MultiPolygon toMultiPolygon(
            List<com.google.android.gms.maps.model.Polygon> polygonList,
            boolean hasZ, boolean hasM) {

        MultiPolygon multiPolygon = new MultiPolygon(hasZ, hasM);

        for (com.google.android.gms.maps.model.Polygon mapPolygon : polygonList) {
            Polygon polygon = toPolygon(mapPolygon);
            multiPolygon.addPolygon(polygon);
        }

        return multiPolygon;
    }

    /**
     * Convert a list of {@link Polygon} to a {@link MultiPolygon}
     *
     * @param polygonList polygon list
     * @return multi polygon
     */
    public MultiPolygon createMultiPolygon(List<Polygon> polygonList) {
        return createMultiPolygon(polygonList, false, false);
    }

    /**
     * Convert a list of {@link Polygon} to a {@link MultiPolygon}
     *
     * @param polygonList polygon list
     * @param hasZ        has z flag
     * @param hasM        has m flag
     * @return multi polygon
     */
    public MultiPolygon createMultiPolygon(List<Polygon> polygonList,
                                           boolean hasZ, boolean hasM) {

        MultiPolygon multiPolygon = new MultiPolygon(hasZ, hasM);

        for (Polygon polygon : polygonList) {
            multiPolygon.addPolygon(polygon);
        }

        return multiPolygon;
    }

    /**
     * Convert a {@link MultiPolygonOptions} to a {@link MultiPolygon}
     *
     * @param multiPolygonOptions multi polygon options
     * @return multi polygon
     */
    public MultiPolygon toMultiPolygonFromOptions(
            MultiPolygonOptions multiPolygonOptions) {
        return toMultiPolygonFromOptions(multiPolygonOptions, false, false);
    }

    /**
     * Convert a list of {@link PolygonOptions} to a {@link MultiPolygon}
     *
     * @param multiPolygonOptions multi polygon options
     * @param hasZ                has z flag
     * @param hasM                has m flag
     * @return multi polygon
     */
    public MultiPolygon toMultiPolygonFromOptions(
            MultiPolygonOptions multiPolygonOptions, boolean hasZ, boolean hasM) {

        MultiPolygon multiPolygon = new MultiPolygon(hasZ, hasM);

        for (PolygonOptions mapPolygon : multiPolygonOptions
                .getPolygonOptions()) {
            Polygon polygon = toPolygon(mapPolygon);
            multiPolygon.addPolygon(polygon);
        }

        return multiPolygon;
    }

    /**
     * Convert a {@link CompoundCurve} to a {@link MultiPolylineOptions}
     *
     * @param compoundCurve compound curve
     * @return multi polyline options
     */
    public MultiPolylineOptions toPolylines(CompoundCurve compoundCurve) {

        MultiPolylineOptions polylines = new MultiPolylineOptions();

        for (LineString lineString : compoundCurve.getLineStrings()) {
            PolylineOptions polyline = toPolyline(lineString);
            polylines.add(polyline);
        }

        return polylines;
    }

    /**
     * Convert a list of {@link Polyline} to a {@link CompoundCurve}
     *
     * @param polylineList polyline list
     * @return compound curve
     */
    public CompoundCurve toCompoundCurve(List<Polyline> polylineList) {
        return toCompoundCurve(polylineList, false, false);
    }

    /**
     * Convert a list of {@link Polyline} to a {@link CompoundCurve}
     *
     * @param polylineList polyline list
     * @param hasZ         has z flag
     * @param hasM         has m flag
     * @return compound curve
     */
    public CompoundCurve toCompoundCurve(List<Polyline> polylineList,
                                         boolean hasZ, boolean hasM) {

        CompoundCurve compoundCurve = new CompoundCurve(hasZ, hasM);

        for (Polyline polyline : polylineList) {
            LineString lineString = toLineString(polyline);
            compoundCurve.addLineString(lineString);
        }

        return compoundCurve;
    }

    /**
     * Convert a {@link MultiPolylineOptions} to a {@link CompoundCurve}
     *
     * @param multiPolylineOptions multi polyline options
     * @return compound curve
     */
    public CompoundCurve toCompoundCurveWithOptions(
            MultiPolylineOptions multiPolylineOptions) {
        return toCompoundCurveWithOptions(multiPolylineOptions, false, false);
    }

    /**
     * Convert a {@link MultiPolylineOptions} to a {@link CompoundCurve}
     *
     * @param multiPolylineOptions multi polyline options
     * @param hasZ                 has z flag
     * @param hasM                 has m flag
     * @return compound curve
     */
    public CompoundCurve toCompoundCurveWithOptions(
            MultiPolylineOptions multiPolylineOptions, boolean hasZ,
            boolean hasM) {

        CompoundCurve compoundCurve = new CompoundCurve(hasZ, hasM);

        for (PolylineOptions polyline : multiPolylineOptions
                .getPolylineOptions()) {
            LineString lineString = toLineString(polyline);
            compoundCurve.addLineString(lineString);
        }

        return compoundCurve;
    }

    /**
     * Convert a {@link PolyhedralSurface} to a {@link MultiPolygonOptions}
     *
     * @param polyhedralSurface polyhedral surface
     * @return multi polygon options
     */
    public MultiPolygonOptions toPolygons(PolyhedralSurface polyhedralSurface) {

        MultiPolygonOptions polygons = new MultiPolygonOptions();

        for (Polygon polygon : polyhedralSurface.getPolygons()) {
            PolygonOptions polygonOptions = toPolygon(polygon);
            polygons.add(polygonOptions);
        }

        return polygons;
    }

    /**
     * Convert a list of {@link Polygon} to a {@link PolyhedralSurface}
     *
     * @param polygonList polygon list
     * @return polyhedral surface
     */
    public PolyhedralSurface toPolyhedralSurface(
            List<com.google.android.gms.maps.model.Polygon> polygonList) {
        return toPolyhedralSurface(polygonList, false, false);
    }

    /**
     * Convert a list of {@link Polygon} to a {@link PolyhedralSurface}
     *
     * @param polygonList polygon list
     * @param hasZ        has z flag
     * @param hasM        has m flag
     * @return polyhedral surface
     */
    public PolyhedralSurface toPolyhedralSurface(
            List<com.google.android.gms.maps.model.Polygon> polygonList,
            boolean hasZ, boolean hasM) {

        PolyhedralSurface polyhedralSurface = new PolyhedralSurface(hasZ, hasM);

        for (com.google.android.gms.maps.model.Polygon mapPolygon : polygonList) {
            Polygon polygon = toPolygon(mapPolygon);
            polyhedralSurface.addPolygon(polygon);
        }

        return polyhedralSurface;
    }

    /**
     * Convert a {@link MultiPolygonOptions} to a {@link PolyhedralSurface}
     *
     * @param multiPolygonOptions multi polygon options
     * @return polyhedral surface
     */
    public PolyhedralSurface toPolyhedralSurfaceWithOptions(
            MultiPolygonOptions multiPolygonOptions) {
        return toPolyhedralSurfaceWithOptions(multiPolygonOptions, false, false);
    }

    /**
     * Convert a {@link MultiPolygonOptions} to a {@link PolyhedralSurface}
     *
     * @param multiPolygonOptions multi polygon options
     * @param hasZ                has z flag
     * @param hasM                has m flag
     * @return polyhedral surface
     */
    public PolyhedralSurface toPolyhedralSurfaceWithOptions(
            MultiPolygonOptions multiPolygonOptions, boolean hasZ, boolean hasM) {

        PolyhedralSurface polyhedralSurface = new PolyhedralSurface(hasZ, hasM);

        for (PolygonOptions mapPolygon : multiPolygonOptions
                .getPolygonOptions()) {
            Polygon polygon = toPolygon(mapPolygon);
            polyhedralSurface.addPolygon(polygon);
        }

        return polyhedralSurface;
    }

    /**
     * Convert a {@link Geometry} to a Map shape
     *
     * @param geometry geometry
     * @return google map shape
     */
    @SuppressWarnings("unchecked")
    public GoogleMapShape toShape(Geometry geometry) {

        GoogleMapShape shape = null;

        GeometryType geometryType = geometry.getGeometryType();
        switch (geometryType) {
            case POINT:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.LAT_LNG, toLatLng((Point) geometry));
                break;
            case LINESTRING:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYLINE_OPTIONS,
                        toPolyline((LineString) geometry));
                break;
            case POLYGON:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYGON_OPTIONS,
                        toPolygon((Polygon) geometry));
                break;
            case MULTIPOINT:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_LAT_LNG,
                        toLatLngs((MultiPoint) geometry));
                break;
            case MULTILINESTRING:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYLINE_OPTIONS,
                        toPolylines((MultiLineString) geometry));
                break;
            case MULTIPOLYGON:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYGON_OPTIONS,
                        toPolygons((MultiPolygon) geometry));
                break;
            case CIRCULARSTRING:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYLINE_OPTIONS,
                        toPolyline((CircularString) geometry));
                break;
            case COMPOUNDCURVE:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYLINE_OPTIONS,
                        toPolylines((CompoundCurve) geometry));
                break;
            case CURVEPOLYGON:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYGON_OPTIONS,
                        toCurvePolygon((CurvePolygon) geometry));
                break;
            case POLYHEDRALSURFACE:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYGON_OPTIONS,
                        toPolygons((PolyhedralSurface) geometry));
                break;
            case TIN:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYGON_OPTIONS,
                        toPolygons((TIN) geometry));
                break;
            case TRIANGLE:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYGON_OPTIONS,
                        toPolygon((Triangle) geometry));
                break;
            case GEOMETRYCOLLECTION:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.COLLECTION,
                        toShapes((GeometryCollection<Geometry>) geometry));
                break;
            default:
                throw new GeoPackageException("Unsupported Geometry Type: "
                        + geometryType.getName());
        }

        return shape;
    }

    /**
     * Convert a {@link GeometryCollection} to a list of Map shapes
     *
     * @param geometryCollection geometry collection
     * @return google map shapes
     */
    public List<GoogleMapShape> toShapes(
            GeometryCollection<Geometry> geometryCollection) {

        List<GoogleMapShape> shapes = new ArrayList<GoogleMapShape>();

        for (Geometry geometry : geometryCollection.getGeometries()) {
            GoogleMapShape shape = toShape(geometry);
            shapes.add(shape);
        }

        return shapes;
    }

    /**
     * Convert a {@link Geometry} to a Map shape and add it
     *
     * @param map      google map
     * @param geometry geometry
     * @return google map shape
     */
    @SuppressWarnings("unchecked")
    public GoogleMapShape addToMap(GoogleMap map, Geometry geometry) {

        GoogleMapShape shape = null;

        GeometryType geometryType = geometry.getGeometryType();
        switch (geometryType) {
            case POINT:
                shape = new GoogleMapShape(geometryType, GoogleMapShapeType.MARKER,
                        addLatLngToMap(map, toLatLng((Point) geometry)));
                break;
            case LINESTRING:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYLINE, addPolylineToMap(map,
                        toPolyline((LineString) geometry)));
                break;
            case POLYGON:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYGON, addPolygonToMap(map,
                        toPolygon((Polygon) geometry)));
                break;
            case MULTIPOINT:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_MARKER, addLatLngsToMap(map,
                        toLatLngs((MultiPoint) geometry)));
                break;
            case MULTILINESTRING:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYLINE, addPolylinesToMap(map,
                        toPolylines((MultiLineString) geometry)));
                break;
            case MULTIPOLYGON:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYGON, addPolygonsToMap(map,
                        toPolygons((MultiPolygon) geometry)));
                break;
            case CIRCULARSTRING:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYLINE, addPolylineToMap(map,
                        toPolyline((CircularString) geometry)));
                break;
            case COMPOUNDCURVE:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYLINE, addPolylinesToMap(map,
                        toPolylines((CompoundCurve) geometry)));
                break;
            case CURVEPOLYGON:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYGON, addPolygonToMap(map,
                        toCurvePolygon((CurvePolygon) geometry)));
                break;
            case POLYHEDRALSURFACE:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYGON, addPolygonsToMap(map,
                        toPolygons((PolyhedralSurface) geometry)));
                break;
            case TIN:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.MULTI_POLYGON, addPolygonsToMap(map,
                        toPolygons((TIN) geometry)));
                break;
            case TRIANGLE:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.POLYGON, addPolygonToMap(map,
                        toPolygon((Triangle) geometry)));
                break;
            case GEOMETRYCOLLECTION:
                shape = new GoogleMapShape(geometryType,
                        GoogleMapShapeType.COLLECTION, addToMap(map,
                        (GeometryCollection<Geometry>) geometry));
                break;
            default:
                throw new GeoPackageException("Unsupported Geometry Type: "
                        + geometryType.getName());
        }

        return shape;
    }

    /**
     * Add a shape to the map
     *
     * @param map   google map
     * @param shape google map shape
     * @return google map shape
     */
    public static GoogleMapShape addShapeToMap(GoogleMap map,
                                               GoogleMapShape shape) {

        GoogleMapShape addedShape = null;

        switch (shape.getShapeType()) {

            case LAT_LNG:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MARKER, addLatLngToMap(map,
                        (LatLng) shape.getShape()));
                break;
            case MARKER_OPTIONS:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MARKER, addMarkerOptionsToMap(map,
                        (MarkerOptions) shape.getShape()));
                break;
            case POLYLINE_OPTIONS:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.POLYLINE, addPolylineToMap(map,
                        (PolylineOptions) shape.getShape()));
                break;
            case POLYGON_OPTIONS:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.POLYGON, addPolygonToMap(map,
                        (PolygonOptions) shape.getShape()));
                break;
            case MULTI_LAT_LNG:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MULTI_MARKER, addLatLngsToMap(map,
                        (MultiLatLng) shape.getShape()));
                break;
            case MULTI_POLYLINE_OPTIONS:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MULTI_POLYLINE, addPolylinesToMap(map,
                        (MultiPolylineOptions) shape.getShape()));
                break;
            case MULTI_POLYGON_OPTIONS:
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MULTI_POLYGON, addPolygonsToMap(map,
                        (MultiPolygonOptions) shape.getShape()));
                break;
            case COLLECTION:
                List<GoogleMapShape> addedShapeList = new ArrayList<GoogleMapShape>();
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape
                        .getShape();
                for (GoogleMapShape shapeListItem : shapeList) {
                    addedShapeList.add(addShapeToMap(map, shapeListItem));
                }
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.COLLECTION, addedShapeList);
                break;
            default:
                throw new GeoPackageException("Unsupported Shape Type: "
                        + shape.getShapeType());

        }

        return addedShape;
    }

    /**
     * Add a LatLng to the map
     *
     * @param map    google map
     * @param latLng lat lng
     * @return marker
     */
    public static Marker addLatLngToMap(GoogleMap map, LatLng latLng) {
        return addLatLngToMap(map, latLng, new MarkerOptions());
    }

    /**
     * Add MarkerOptions to the map
     *
     * @param map     google map
     * @param options marker options
     * @return marker
     */
    public static Marker addMarkerOptionsToMap(GoogleMap map,
                                               MarkerOptions options) {
        return map.addMarker(options);
    }

    /**
     * Add a LatLng to the map
     *
     * @param map     google map
     * @param latLng  lat lng
     * @param options marker options
     * @return marker
     */
    public static Marker addLatLngToMap(GoogleMap map, LatLng latLng,
                                        MarkerOptions options) {
        return map.addMarker(options.position(latLng));
    }

    /**
     * Add a Polyline to the map
     *
     * @param map      google map
     * @param polyline polyline options
     * @return polyline
     */
    public static Polyline addPolylineToMap(GoogleMap map,
                                            PolylineOptions polyline) {
        return map.addPolyline(polyline);
    }

    /**
     * Add a Polygon to the map
     *
     * @param map     google map
     * @param polygon polygon options
     * @return polygon
     */
    public static com.google.android.gms.maps.model.Polygon addPolygonToMap(
            GoogleMap map, PolygonOptions polygon) {
        return map.addPolygon(polygon);
    }

    /**
     * Add a list of LatLngs to the map
     *
     * @param map     google map
     * @param latLngs lat lngs
     * @return multi marker
     */
    public static MultiMarker addLatLngsToMap(GoogleMap map, MultiLatLng latLngs) {
        MultiMarker multiMarker = new MultiMarker();
        for (LatLng latLng : latLngs.getLatLngs()) {
            MarkerOptions markerOptions = new MarkerOptions();
            if (latLngs.getMarkerOptions() != null) {
                markerOptions.icon(latLngs.getMarkerOptions().getIcon());
                markerOptions.anchor(latLngs.getMarkerOptions().getAnchorU(),
                        markerOptions.getAnchorV());
                markerOptions.draggable(latLngs.getMarkerOptions()
                        .isDraggable());
                markerOptions.visible(latLngs.getMarkerOptions().isVisible());
                markerOptions.zIndex(latLngs.getMarkerOptions().getZIndex());
            }
            Marker marker = addLatLngToMap(map, latLng, markerOptions);
            multiMarker.add(marker);
        }
        return multiMarker;
    }

    /**
     * Add a list of Polylines to the map
     *
     * @param map       google map
     * @param polylines multi polyline options
     * @return multi polyline
     */
    public static MultiPolyline addPolylinesToMap(GoogleMap map,
                                                  MultiPolylineOptions polylines) {
        MultiPolyline multiPolyline = new MultiPolyline();
        for (PolylineOptions polylineOption : polylines.getPolylineOptions()) {
            if (polylines.getOptions() != null) {
                polylineOption.color(polylines.getOptions().getColor());
                polylineOption.geodesic(polylines.getOptions().isGeodesic());
                polylineOption.visible(polylines.getOptions().isVisible());
                polylineOption.zIndex(polylines.getOptions().getZIndex());
                polylineOption.width(polylines.getOptions().getWidth());
            }
            Polyline polyline = addPolylineToMap(map, polylineOption);
            multiPolyline.add(polyline);
        }
        return multiPolyline;
    }

    /**
     * Add a list of Polygons to the map
     *
     * @param map      google map
     * @param polygons multi polygon options
     * @return multi polygon
     */
    public static mil.nga.geopackage.map.geom.MultiPolygon addPolygonsToMap(
            GoogleMap map, MultiPolygonOptions polygons) {
        mil.nga.geopackage.map.geom.MultiPolygon multiPolygon = new mil.nga.geopackage.map.geom.MultiPolygon();
        for (PolygonOptions polygonOption : polygons.getPolygonOptions()) {
            if (polygons.getOptions() != null) {
                polygonOption.fillColor(polygons.getOptions().getFillColor());
                polygonOption.strokeColor(polygons.getOptions()
                        .getStrokeColor());
                polygonOption.geodesic(polygons.getOptions().isGeodesic());
                polygonOption.visible(polygons.getOptions().isVisible());
                polygonOption.zIndex(polygons.getOptions().getZIndex());
                polygonOption.strokeWidth(polygons.getOptions().getStrokeWidth());
            }
            com.google.android.gms.maps.model.Polygon polygon = addPolygonToMap(
                    map, polygonOption);
            multiPolygon.add(polygon);
        }
        return multiPolygon;
    }

    /**
     * Convert a {@link GeometryCollection} to a list of Map shapes and add to
     * the map
     *
     * @param map                google map
     * @param geometryCollection geometry collection
     * @return google map shapes
     */
    public List<GoogleMapShape> addToMap(GoogleMap map,
                                         GeometryCollection<Geometry> geometryCollection) {

        List<GoogleMapShape> shapes = new ArrayList<GoogleMapShape>();

        for (Geometry geometry : geometryCollection.getGeometries()) {
            GoogleMapShape shape = addToMap(map, geometry);
            shapes.add(shape);
        }

        return shapes;
    }

    /**
     * Add a shape to the map as markers
     *
     * @param map                      google map
     * @param shape                    google map shape
     * @param markerOptions            marker options
     * @param polylineMarkerOptions    polyline marker options
     * @param polygonMarkerOptions     polygon marker options
     * @param polygonMarkerHoleOptions polygon marker hole options
     * @param globalPolylineOptions    global polyline options
     * @param globalPolygonOptions     global polygon options
     * @return google map shape markers
     */
    public GoogleMapShapeMarkers addShapeToMapAsMarkers(GoogleMap map,
                                                        GoogleMapShape shape, MarkerOptions markerOptions,
                                                        MarkerOptions polylineMarkerOptions,
                                                        MarkerOptions polygonMarkerOptions,
                                                        MarkerOptions polygonMarkerHoleOptions,
                                                        PolylineOptions globalPolylineOptions,
                                                        PolygonOptions globalPolygonOptions) {

        GoogleMapShapeMarkers shapeMarkers = new GoogleMapShapeMarkers();
        GoogleMapShape addedShape = null;

        switch (shape.getShapeType()) {

            case LAT_LNG:
                if (markerOptions == null) {
                    markerOptions = new MarkerOptions();
                }
                Marker latLngMarker = addLatLngToMap(map,
                        (LatLng) shape.getShape(), markerOptions);
                shapeMarkers.add(latLngMarker);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MARKER, latLngMarker);
                break;
            case MARKER_OPTIONS:
                MarkerOptions shapeMarkerOptions = (MarkerOptions) shape.getShape();
                if (markerOptions != null) {
                    shapeMarkerOptions.icon(markerOptions.getIcon());
                    shapeMarkerOptions.anchor(markerOptions.getAnchorU(),
                            markerOptions.getAnchorV());
                    shapeMarkerOptions.draggable(markerOptions.isDraggable());
                    shapeMarkerOptions.visible(markerOptions.isVisible());
                    shapeMarkerOptions.zIndex(markerOptions.getZIndex());
                }
                Marker markerOptionsMarker = addMarkerOptionsToMap(map,
                        shapeMarkerOptions);
                shapeMarkers.add(markerOptionsMarker);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MARKER, markerOptionsMarker);
                break;
            case POLYLINE_OPTIONS:
                PolylineMarkers polylineMarkers = addPolylineToMapAsMarkers(map,
                        (PolylineOptions) shape.getShape(), polylineMarkerOptions,
                        globalPolylineOptions);
                shapeMarkers.add(polylineMarkers);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.POLYLINE_MARKERS, polylineMarkers);
                break;
            case POLYGON_OPTIONS:
                PolygonMarkers polygonMarkers = addPolygonToMapAsMarkers(
                        shapeMarkers, map, (PolygonOptions) shape.getShape(),
                        polygonMarkerOptions, polygonMarkerHoleOptions,
                        globalPolygonOptions);
                shapeMarkers.add(polygonMarkers);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.POLYGON_MARKERS, polygonMarkers);
                break;
            case MULTI_LAT_LNG:
                MultiLatLng multiLatLng = (MultiLatLng) shape.getShape();
                if (markerOptions != null) {
                    multiLatLng.setMarkerOptions(markerOptions);
                }
                MultiMarker multiMarker = addLatLngsToMap(map, multiLatLng);
                shapeMarkers.add(multiMarker);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MULTI_MARKER, multiMarker);
                break;
            case MULTI_POLYLINE_OPTIONS:
                MultiPolylineMarkers multiPolylineMarkers = addMultiPolylineToMapAsMarkers(
                        shapeMarkers, map, (MultiPolylineOptions) shape.getShape(),
                        polylineMarkerOptions, globalPolylineOptions);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MULTI_POLYLINE_MARKERS,
                        multiPolylineMarkers);
                break;
            case MULTI_POLYGON_OPTIONS:
                MultiPolygonMarkers multiPolygonMarkers = addMultiPolygonToMapAsMarkers(
                        shapeMarkers, map, (MultiPolygonOptions) shape.getShape(),
                        polygonMarkerOptions, polygonMarkerHoleOptions,
                        globalPolygonOptions);
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.MULTI_POLYGON_MARKERS,
                        multiPolygonMarkers);
                break;
            case COLLECTION:
                List<GoogleMapShape> addedShapeList = new ArrayList<GoogleMapShape>();
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape
                        .getShape();
                for (GoogleMapShape shapeListItem : shapeList) {
                    GoogleMapShapeMarkers shapeListItemMarkers = addShapeToMapAsMarkers(
                            map, shapeListItem, markerOptions,
                            polylineMarkerOptions, polygonMarkerOptions,
                            polygonMarkerHoleOptions, globalPolylineOptions,
                            globalPolygonOptions);
                    shapeMarkers.add(shapeListItemMarkers);
                    addedShapeList.add(shapeListItemMarkers.getShape());
                }
                addedShape = new GoogleMapShape(shape.getGeometryType(),
                        GoogleMapShapeType.COLLECTION, addedShapeList);
                break;
            default:
                throw new GeoPackageException("Unsupported Shape Type: "
                        + shape.getShapeType());

        }

        shapeMarkers.setShape(addedShape);

        return shapeMarkers;
    }

    /**
     * Add the list of points as markers
     *
     * @param map                 google map
     * @param points              points
     * @param customMarkerOptions custom marker options
     * @param ignoreIdenticalEnds ignore identical ends flag
     * @return list of markers
     */
    public List<Marker> addPointsToMapAsMarkers(GoogleMap map,
                                                List<LatLng> points, MarkerOptions customMarkerOptions,
                                                boolean ignoreIdenticalEnds) {

        List<Marker> markers = new ArrayList<Marker>();
        for (int i = 0; i < points.size(); i++) {
            LatLng latLng = points.get(i);

            if (points.size() > 1 && i + 1 == points.size() && ignoreIdenticalEnds) {
                LatLng firstLatLng = points.get(0);
                if (latLng.latitude == firstLatLng.latitude
                        && latLng.longitude == firstLatLng.longitude) {
                    break;
                }
            }

            MarkerOptions markerOptions = new MarkerOptions();
            if (customMarkerOptions != null) {
                markerOptions.icon(customMarkerOptions.getIcon());
                markerOptions.anchor(customMarkerOptions.getAnchorU(),
                        customMarkerOptions.getAnchorV());
                markerOptions.draggable(customMarkerOptions.isDraggable());
                markerOptions.visible(customMarkerOptions.isVisible());
                markerOptions.zIndex(customMarkerOptions.getZIndex());
            }
            Marker marker = addLatLngToMap(map, latLng, markerOptions);
            markers.add(marker);
        }
        return markers;
    }

    /**
     * Add a Polyline to the map as markers
     *
     * @param map                   google map
     * @param polylineOptions       polyline options
     * @param polylineMarkerOptions polyline marker options
     * @param globalPolylineOptions global polyline options
     * @return polyline markers
     */
    public PolylineMarkers addPolylineToMapAsMarkers(GoogleMap map,
                                                     PolylineOptions polylineOptions,
                                                     MarkerOptions polylineMarkerOptions,
                                                     PolylineOptions globalPolylineOptions) {

        PolylineMarkers polylineMarkers = new PolylineMarkers(this);

        if (globalPolylineOptions != null) {
            polylineOptions.color(globalPolylineOptions.getColor());
            polylineOptions.geodesic(globalPolylineOptions.isGeodesic());
            polylineOptions.visible(globalPolylineOptions.isVisible());
            polylineOptions.zIndex(globalPolylineOptions.getZIndex());
            polylineOptions.width(globalPolylineOptions.getWidth());
        }

        Polyline polyline = addPolylineToMap(map, polylineOptions);
        polylineMarkers.setPolyline(polyline);

        List<Marker> markers = addPointsToMapAsMarkers(map,
                polylineOptions.getPoints(), polylineMarkerOptions, false);
        polylineMarkers.setMarkers(markers);

        return polylineMarkers;
    }

    /**
     * Add a Polygon to the map as markers
     *
     * @param shapeMarkers             google map shape markers
     * @param map                      google map
     * @param polygonOptions           polygon options
     * @param polygonMarkerOptions     polygon marker options
     * @param polygonMarkerHoleOptions polygon marker hole options
     * @param globalPolygonOptions     global polygon options
     * @return polygon markers
     */
    public PolygonMarkers addPolygonToMapAsMarkers(
            GoogleMapShapeMarkers shapeMarkers, GoogleMap map,
            PolygonOptions polygonOptions, MarkerOptions polygonMarkerOptions,
            MarkerOptions polygonMarkerHoleOptions,
            PolygonOptions globalPolygonOptions) {

        PolygonMarkers polygonMarkers = new PolygonMarkers(this);

        if (globalPolygonOptions != null) {
            polygonOptions.fillColor(globalPolygonOptions.getFillColor());
            polygonOptions.strokeColor(globalPolygonOptions.getStrokeColor());
            polygonOptions.geodesic(globalPolygonOptions.isGeodesic());
            polygonOptions.visible(globalPolygonOptions.isVisible());
            polygonOptions.zIndex(globalPolygonOptions.getZIndex());
            polygonOptions.strokeWidth(globalPolygonOptions.getStrokeWidth());
        }

        com.google.android.gms.maps.model.Polygon polygon = addPolygonToMap(
                map, polygonOptions);
        polygonMarkers.setPolygon(polygon);

        List<Marker> markers = addPointsToMapAsMarkers(map,
                polygon.getPoints(), polygonMarkerOptions, true);
        polygonMarkers.setMarkers(markers);

        for (List<LatLng> holes : polygon.getHoles()) {
            List<Marker> holeMarkers = addPointsToMapAsMarkers(map, holes,
                    polygonMarkerHoleOptions, true);
            PolygonHoleMarkers polygonHoleMarkers = new PolygonHoleMarkers(
                    polygonMarkers);
            polygonHoleMarkers.setMarkers(holeMarkers);
            shapeMarkers.add(polygonHoleMarkers);
            polygonMarkers.addHole(polygonHoleMarkers);
        }

        return polygonMarkers;
    }

    /**
     * Add a MultiPolylineOptions to the map as markers
     *
     * @param shapeMarkers          google map shape markers
     * @param map                   google map
     * @param multiPolyline         multi polyline options
     * @param polylineMarkerOptions polyline marker options
     * @param globalPolylineOptions global polyline options
     * @return multi polyline markers
     */
    public MultiPolylineMarkers addMultiPolylineToMapAsMarkers(
            GoogleMapShapeMarkers shapeMarkers, GoogleMap map,
            MultiPolylineOptions multiPolyline,
            MarkerOptions polylineMarkerOptions,
            PolylineOptions globalPolylineOptions) {
        MultiPolylineMarkers polylines = new MultiPolylineMarkers();
        for (PolylineOptions polylineOptions : multiPolyline
                .getPolylineOptions()) {
            PolylineMarkers polylineMarker = addPolylineToMapAsMarkers(map,
                    polylineOptions, polylineMarkerOptions,
                    globalPolylineOptions);
            shapeMarkers.add(polylineMarker);
            polylines.add(polylineMarker);
        }
        return polylines;
    }

    /**
     * Add a MultiPolygonOptions to the map as markers
     *
     * @param shapeMarkers             google map shape markers
     * @param map                      google map
     * @param multiPolygon             multi polygon options
     * @param polygonMarkerOptions     polygon marker options
     * @param polygonMarkerHoleOptions polygon marker hole options
     * @param globalPolygonOptions     global polygon options
     * @return multi polygon markers
     */
    public MultiPolygonMarkers addMultiPolygonToMapAsMarkers(
            GoogleMapShapeMarkers shapeMarkers, GoogleMap map,
            MultiPolygonOptions multiPolygon,
            MarkerOptions polygonMarkerOptions,
            MarkerOptions polygonMarkerHoleOptions,
            PolygonOptions globalPolygonOptions) {
        MultiPolygonMarkers multiPolygonMarkers = new MultiPolygonMarkers();
        for (PolygonOptions polygon : multiPolygon.getPolygonOptions()) {
            PolygonMarkers polygonMarker = addPolygonToMapAsMarkers(
                    shapeMarkers, map, polygon, polygonMarkerOptions,
                    polygonMarkerHoleOptions, globalPolygonOptions);
            shapeMarkers.add(polygonMarker);
            multiPolygonMarkers.add(polygonMarker);
        }
        return multiPolygonMarkers;
    }

    /**
     * Get a list of points as LatLng from a list of Markers
     *
     * @param markers list of markers
     * @return lat lngs
     */
    public List<LatLng> getPointsFromMarkers(List<Marker> markers) {
        List<LatLng> points = new ArrayList<LatLng>();
        for (Marker marker : markers) {
            points.add(marker.getPosition());
        }
        return points;
    }

    /**
     * Convert a GoogleMapShape to a Geometry
     *
     * @param shape google map shape
     * @return geometry
     */
    public Geometry toGeometry(GoogleMapShape shape) {

        Geometry geometry = null;
        Object shapeObject = shape.getShape();

        switch (shape.getGeometryType()) {

            case POINT:
                LatLng point = null;
                switch (shape.getShapeType()) {
                    case LAT_LNG:
                        point = (LatLng) shapeObject;
                        break;
                    case MARKER_OPTIONS:
                        MarkerOptions markerOptions = (MarkerOptions) shapeObject;
                        point = markerOptions.getPosition();
                        break;
                    case MARKER:
                        Marker marker = (Marker) shapeObject;
                        point = marker.getPosition();
                        break;
                    default:
                        throw new GeoPackageException("Not a valid "
                                + shape.getGeometryType().getName() + " shape type: "
                                + shape.getShapeType());
                }
                if (point != null) {
                    geometry = toPoint(point);
                }

                break;
            case LINESTRING:
            case CIRCULARSTRING:
                List<LatLng> lineStringPoints = null;
                switch (shape.getShapeType()) {
                    case POLYLINE_OPTIONS:
                        PolylineOptions polylineOptions = (PolylineOptions) shapeObject;
                        lineStringPoints = polylineOptions.getPoints();
                        break;
                    case POLYLINE:
                        Polyline polyline = (Polyline) shapeObject;
                        lineStringPoints = polyline.getPoints();
                        break;
                    case POLYLINE_MARKERS:
                        PolylineMarkers polylineMarkers = (PolylineMarkers) shapeObject;
                        if (!polylineMarkers.isValid()) {
                            throw new GeoPackageException(
                                    PolylineMarkers.class.getSimpleName()
                                            + " is not valid to create "
                                            + shape.getGeometryType().getName());
                        }
                        if (!polylineMarkers.isDeleted()) {
                            lineStringPoints = getPointsFromMarkers(polylineMarkers
                                    .getMarkers());
                        }
                        break;
                    default:
                        throw new GeoPackageException("Not a valid "
                                + shape.getGeometryType().getName() + " shape type: "
                                + shape.getShapeType());
                }
                if (lineStringPoints != null) {
                    switch (shape.getGeometryType()) {
                        case LINESTRING:
                            geometry = toLineString(lineStringPoints);
                            break;
                        case CIRCULARSTRING:
                            geometry = toCircularString(lineStringPoints);
                            break;
                        default:
                            throw new GeoPackageException("Unhandled "
                                    + shape.getGeometryType().getName());
                    }
                }

                break;
            case POLYGON:
                List<LatLng> polygonPoints = null;
                List<List<LatLng>> holePointList = null;
                switch (shape.getShapeType()) {
                    case POLYGON_OPTIONS:
                        PolygonOptions polygonOptions = (PolygonOptions) shapeObject;
                        polygonPoints = polygonOptions.getPoints();
                        holePointList = polygonOptions.getHoles();
                        break;
                    case POLYGON:
                        com.google.android.gms.maps.model.Polygon polygon = (com.google.android.gms.maps.model.Polygon) shapeObject;
                        polygonPoints = polygon.getPoints();
                        holePointList = polygon.getHoles();
                        break;
                    case POLYGON_MARKERS:
                        PolygonMarkers polygonMarkers = (PolygonMarkers) shapeObject;
                        if (!polygonMarkers.isValid()) {
                            throw new GeoPackageException(
                                    PolygonMarkers.class.getSimpleName()
                                            + " is not valid to create "
                                            + shape.getGeometryType().getName());
                        }
                        if (!polygonMarkers.isDeleted()) {
                            polygonPoints = getPointsFromMarkers(polygonMarkers
                                    .getMarkers());
                            holePointList = new ArrayList<List<LatLng>>();
                            for (PolygonHoleMarkers hole : polygonMarkers.getHoles()) {
                                if (!hole.isDeleted()) {
                                    List<LatLng> holePoints = getPointsFromMarkers(hole
                                            .getMarkers());
                                    holePointList.add(holePoints);
                                }
                            }
                        }
                        break;
                    default:
                        throw new GeoPackageException("Not a valid "
                                + shape.getGeometryType().getName() + " shape type: "
                                + shape.getShapeType());
                }
                if (polygonPoints != null) {
                    geometry = toPolygon(polygonPoints, holePointList);
                }

                break;
            case MULTIPOINT:
                List<LatLng> multiPoints = null;
                switch (shape.getShapeType()) {
                    case MULTI_LAT_LNG:
                        MultiLatLng multiLatLng = (MultiLatLng) shapeObject;
                        multiPoints = multiLatLng.getLatLngs();
                        break;
                    case MULTI_MARKER:
                        MultiMarker multiMarker = (MultiMarker) shapeObject;
                        multiPoints = getPointsFromMarkers(multiMarker.getMarkers());
                        break;
                    default:
                        throw new GeoPackageException("Not a valid "
                                + shape.getGeometryType().getName() + " shape type: "
                                + shape.getShapeType());
                }
                if (multiPoints != null) {
                    geometry = toMultiPoint(multiPoints);
                }

                break;
            case MULTILINESTRING:
            case COMPOUNDCURVE:
                switch (shape.getShapeType()) {
                    case MULTI_POLYLINE_OPTIONS:
                        MultiPolylineOptions multiPolylineOptions = (MultiPolylineOptions) shapeObject;
                        switch (shape.getGeometryType()) {
                            case MULTILINESTRING:
                                geometry = toMultiLineStringFromOptions(multiPolylineOptions);
                                break;
                            case COMPOUNDCURVE:
                                geometry = toCompoundCurveFromOptions(multiPolylineOptions);
                                break;
                            default:
                                throw new GeoPackageException("Unhandled "
                                        + shape.getGeometryType().getName());
                        }
                        break;
                    case MULTI_POLYLINE:
                        MultiPolyline multiPolyline = (MultiPolyline) shapeObject;
                        switch (shape.getGeometryType()) {
                            case MULTILINESTRING:
                                geometry = toMultiLineString(multiPolyline.getPolylines());
                                break;
                            case COMPOUNDCURVE:
                                geometry = toCompoundCurve(multiPolyline.getPolylines());
                                break;
                            default:
                                throw new GeoPackageException("Unhandled "
                                        + shape.getGeometryType().getName());
                        }
                        break;
                    case MULTI_POLYLINE_MARKERS:
                        MultiPolylineMarkers multiPolylineMarkers = (MultiPolylineMarkers) shapeObject;
                        if (!multiPolylineMarkers.isValid()) {
                            throw new GeoPackageException(
                                    MultiPolylineMarkers.class.getSimpleName()
                                            + " is not valid to create "
                                            + shape.getGeometryType().getName());
                        }
                        if (!multiPolylineMarkers.isDeleted()) {
                            List<List<LatLng>> multiPolylineMarkersList = new ArrayList<List<LatLng>>();
                            for (PolylineMarkers polylineMarkers : multiPolylineMarkers
                                    .getPolylineMarkers()) {
                                if (!polylineMarkers.isDeleted()) {
                                    multiPolylineMarkersList
                                            .add(getPointsFromMarkers(polylineMarkers
                                                    .getMarkers()));
                                }
                            }
                            switch (shape.getGeometryType()) {
                                case MULTILINESTRING:
                                    geometry = toMultiLineStringFromList(multiPolylineMarkersList);
                                    break;
                                case COMPOUNDCURVE:
                                    geometry = toCompoundCurveFromList(multiPolylineMarkersList);
                                    break;
                                default:
                                    throw new GeoPackageException("Unhandled "
                                            + shape.getGeometryType().getName());
                            }
                        }
                        break;
                    default:
                        throw new GeoPackageException("Not a valid "
                                + shape.getGeometryType().getName() + " shape type: "
                                + shape.getShapeType());
                }

                break;
            case MULTIPOLYGON:
                switch (shape.getShapeType()) {
                    case MULTI_POLYGON_OPTIONS:
                        MultiPolygonOptions multiPolygonOptions = (MultiPolygonOptions) shapeObject;
                        geometry = toMultiPolygonFromOptions(multiPolygonOptions);
                        break;
                    case MULTI_POLYGON:
                        mil.nga.geopackage.map.geom.MultiPolygon multiPolygon = (mil.nga.geopackage.map.geom.MultiPolygon) shapeObject;
                        geometry = toMultiPolygon(multiPolygon.getPolygons());
                        break;
                    case MULTI_POLYGON_MARKERS:
                        MultiPolygonMarkers multiPolygonMarkers = (MultiPolygonMarkers) shapeObject;
                        if (!multiPolygonMarkers.isValid()) {
                            throw new GeoPackageException(
                                    MultiPolygonMarkers.class.getSimpleName()
                                            + " is not valid to create "
                                            + shape.getGeometryType().getName());
                        }
                        if (!multiPolygonMarkers.isDeleted()) {
                            List<Polygon> multiPolygonMarkersList = new ArrayList<Polygon>();
                            for (PolygonMarkers polygonMarkers : multiPolygonMarkers
                                    .getPolygonMarkers()) {

                                if (!polygonMarkers.isDeleted()) {

                                    List<LatLng> multiPolygonPoints = getPointsFromMarkers(polygonMarkers
                                            .getMarkers());
                                    List<List<LatLng>> multiPolygonHolePoints = new ArrayList<List<LatLng>>();
                                    for (PolygonHoleMarkers hole : polygonMarkers
                                            .getHoles()) {
                                        if (!hole.isDeleted()) {
                                            List<LatLng> holePoints = getPointsFromMarkers(hole
                                                    .getMarkers());
                                            multiPolygonHolePoints.add(holePoints);
                                        }
                                    }

                                    multiPolygonMarkersList
                                            .add(toPolygon(multiPolygonPoints,
                                                    multiPolygonHolePoints));
                                }

                            }
                            geometry = createMultiPolygon(multiPolygonMarkersList);
                        }
                        break;
                    default:
                        throw new GeoPackageException("Not a valid "
                                + shape.getGeometryType().getName() + " shape type: "
                                + shape.getShapeType());
                }
                break;

            case POLYHEDRALSURFACE:
            case TIN:
            case TRIANGLE:
                throw new GeoPackageException("Unsupported GeoPackage type: "
                        + shape.getGeometryType());
            case GEOMETRYCOLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shapeObject;
                GeometryCollection<Geometry> geometryCollection = new GeometryCollection<Geometry>(
                        false, false);
                for (GoogleMapShape shapeListItem : shapeList) {
                    Geometry subGeometry = toGeometry(shapeListItem);
                    if (subGeometry != null) {
                        geometryCollection.addGeometry(subGeometry);
                    }
                }
                if (geometryCollection.numGeometries() > 0) {
                    geometry = geometryCollection;
                }
                break;
            default:
        }

        return geometry;
    }

    /**
     * Transform the bounding box in the feature projection to web mercator
     *
     * @param boundingBox bounding box in feature projection
     * @return bounding box in web mercator
     */
    public BoundingBox boundingBoxToWebMercator(BoundingBox boundingBox) {
        if (projection == null) {
            throw new GeoPackageException("Shape Converter projection is null");
        }
        return boundingBox.transform(toWebMercator);
    }

    /**
     * Transform the bounding box in the feature projection to WGS84
     *
     * @param boundingBox bounding box in feature projection
     * @return bounding box in WGS84
     */
    public BoundingBox boundingBoxToWgs84(BoundingBox boundingBox) {
        if (projection == null) {
            throw new GeoPackageException("Shape Converter projection is null");
        }
        return boundingBox.transform(toWgs84);
    }

    /**
     * Transform the bounding box in web mercator to the feature projection
     *
     * @param boundingBox bounding box in web mercator
     * @return bounding box in the feature projection
     */
    public BoundingBox boundingBoxFromWebMercator(BoundingBox boundingBox) {
        if (projection == null) {
            throw new GeoPackageException("Shape Converter projection is null");
        }
        return boundingBox.transform(fromWebMercator);
    }

    /**
     * Transform the bounding box in WGS84 to the feature projection
     *
     * @param boundingBox bounding box in WGS84
     * @return bounding box in the feature projection
     */
    public BoundingBox boundingBoxFromWgs84(BoundingBox boundingBox) {
        if (projection == null) {
            throw new GeoPackageException("Shape Converter projection is null");
        }
        return boundingBox.transform(fromWgs84);
    }

}

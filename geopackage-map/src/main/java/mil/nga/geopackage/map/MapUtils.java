package mil.nga.geopackage.map;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.map.geom.GoogleMapShape;
import mil.nga.geopackage.map.geom.MultiLatLng;
import mil.nga.geopackage.map.geom.MultiPolygonOptions;
import mil.nga.geopackage.map.geom.MultiPolylineOptions;
import mil.nga.geopackage.map.tiles.TileBoundingBoxMapUtils;
import mil.nga.sf.proj.ProjectionConstants;

/**
 * Map utilities
 *
 * @author osbornb
 * @since 2.0.0
 */
public class MapUtils {

    /**
     * Get the current zoom level of the map
     *
     * @param map google map
     * @return current zoom level
     */
    public static float getCurrentZoom(GoogleMap map) {
        return map.getCameraPosition().zoom;
    }

    /**
     * Get the tolerance distance meters in the current region of the map
     *
     * @param view view
     * @param map  google map
     * @return tolerance distance in meters
     */
    public static double getToleranceDistance(View view, GoogleMap map) {

        BoundingBox boundingBox = getBoundingBox(map);

        double boundingBoxWidth = TileBoundingBoxMapUtils.getLongitudeDistance(boundingBox);
        double boundingBoxHeight = TileBoundingBoxMapUtils.getLatitudeDistance(boundingBox);

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        double meters = 0;

        if (viewWidth > 0 && viewHeight > 0) {

            double widthMeters = boundingBoxWidth / viewWidth;
            double heightMeters = boundingBoxHeight / viewHeight;

            meters = Math.min(widthMeters, heightMeters);
        }

        return meters;
    }

    /**
     * Get the WGS84 bounding box of the current map view screen.
     * The max longitude will be larger than the min resulting in values larger than 180.0.
     *
     * @param map google map
     * @return current bounding box
     */
    public static BoundingBox getBoundingBox(GoogleMap map) {

        LatLngBounds visibleBounds = map.getProjection()
                .getVisibleRegion().latLngBounds;
        LatLng southwest = visibleBounds.southwest;
        LatLng northeast = visibleBounds.northeast;

        double minLatitude = southwest.latitude;
        double maxLatitude = northeast.latitude;

        double minLongitude = southwest.longitude;
        double maxLongitude = northeast.longitude;
        if (maxLongitude < minLongitude) {
            maxLongitude += (2 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH);
        }

        BoundingBox boundingBox = new BoundingBox(minLongitude, minLatitude, maxLongitude, maxLatitude);

        return boundingBox;
    }

    /**
     * Build a bounding box using the click location, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     */
    public static BoundingBox buildClickBoundingBox(LatLng latLng, View view, GoogleMap map, float screenClickPercentage) {

        LatLngBoundingBox latLngBoundingBox = buildClickLatLngBoundingBox(latLng, view, map, screenClickPercentage);

        // Create the bounding box to query for features
        BoundingBox boundingBox = new BoundingBox(
                latLngBoundingBox.getLeftCoordinate().longitude,
                latLngBoundingBox.getDownCoordinate().latitude,
                latLngBoundingBox.getRightCoordinate().longitude,
                latLngBoundingBox.getUpCoordinate().latitude);

        return boundingBox;
    }

    /**
     * Build a lat lng bounds using the click location, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     */
    public static LatLngBounds buildClickLatLngBounds(LatLng latLng, View view, GoogleMap map, float screenClickPercentage) {

        LatLngBoundingBox latLngBoundingBox = buildClickLatLngBoundingBox(latLng, view, map, screenClickPercentage);

        double southWestLongitude = Math.min(latLngBoundingBox.getLeftCoordinate().longitude, latLngBoundingBox.getDownCoordinate().longitude);

        LatLng southWest = new LatLng(latLngBoundingBox.getDownCoordinate().latitude, latLngBoundingBox.getLeftCoordinate().longitude);
        LatLng northEast = new LatLng(latLngBoundingBox.getUpCoordinate().latitude, latLngBoundingBox.getRightCoordinate().longitude);

        LatLngBounds latLngBounds = new LatLngBounds(southWest, northEast);

        return latLngBounds;
    }

    /**
     * Get the allowable tolerance distance in meters from the click location on the map view and map with the screen percentage tolerance.
     *
     * @param latLng                click location
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return tolerance distance in meters
     */
    public static double getToleranceDistance(LatLng latLng, View view, GoogleMap map, float screenClickPercentage) {

        LatLngBoundingBox latLngBoundingBox = buildClickLatLngBoundingBox(latLng, view, map, screenClickPercentage);

        double longitudeDistance = SphericalUtil.computeDistanceBetween(latLngBoundingBox.getLeftCoordinate(), latLngBoundingBox.getRightCoordinate());
        double latitudeDistance = SphericalUtil.computeDistanceBetween(latLngBoundingBox.getDownCoordinate(), latLngBoundingBox.getUpCoordinate());

        double distance = Math.max(longitudeDistance, latitudeDistance);

        return distance;
    }

    /**
     * Build a lat lng bounding box using the click location, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return lat lng bounding box
     */
    public static LatLngBoundingBox buildClickLatLngBoundingBox(LatLng latLng, View view, GoogleMap map, float screenClickPercentage) {

        // Get the screen width and height a click occurs from a feature
        int width = (int) Math.round(view.getWidth() * screenClickPercentage);
        int height = (int) Math.round(view.getHeight() * screenClickPercentage);

        // Get the screen click location
        Projection projection = map.getProjection();
        android.graphics.Point clickLocation = projection.toScreenLocation(latLng);

        // Get the screen click locations in each width or height direction
        android.graphics.Point left = new android.graphics.Point(clickLocation);
        android.graphics.Point up = new android.graphics.Point(clickLocation);
        android.graphics.Point right = new android.graphics.Point(clickLocation);
        android.graphics.Point down = new android.graphics.Point(clickLocation);
        left.offset(-width, 0);
        up.offset(0, -height);
        right.offset(width, 0);
        down.offset(0, height);

        // Get the coordinates of the bounding box points
        LatLng leftCoordinate = projection.fromScreenLocation(left);
        LatLng upCoordinate = projection.fromScreenLocation(up);
        LatLng rightCoordinate = projection.fromScreenLocation(right);
        LatLng downCoordinate = projection.fromScreenLocation(down);

        LatLngBoundingBox latLngBoundingBox = new LatLngBoundingBox(leftCoordinate, upCoordinate, rightCoordinate, downCoordinate);

        return latLngBoundingBox;
    }

    /**
     * Is the point on or near the shape
     *
     * @param point     lat lng point
     * @param shape     map shape
     * @param geodesic  geodesic check flag
     * @param tolerance distance tolerance
     * @return true if point is on shape
     */
    public static boolean isPointOnShape(LatLng point,
                                         GoogleMapShape shape, boolean geodesic, double tolerance) {

        boolean onShape = false;

        switch (shape.getShapeType()) {

            case LAT_LNG:
                onShape = isPointNearPoint(point, (LatLng) shape.getShape(), tolerance);
                break;
            case MARKER_OPTIONS:
                onShape = isPointNearMarker(point, (MarkerOptions) shape.getShape(), tolerance);
                break;
            case POLYLINE_OPTIONS:
                onShape = isPointOnPolyline(point, (PolylineOptions) shape.getShape(), geodesic, tolerance);
                break;
            case POLYGON_OPTIONS:
                onShape = isPointOnPolygon(point, (PolygonOptions) shape.getShape(), geodesic, tolerance);
                break;
            case MULTI_LAT_LNG:
                onShape = isPointNearMultiLatLng(point, (MultiLatLng) shape.getShape(), tolerance);
                break;
            case MULTI_POLYLINE_OPTIONS:
                onShape = isPointOnMultiPolyline(point, (MultiPolylineOptions) shape.getShape(), geodesic, tolerance);
                break;
            case MULTI_POLYGON_OPTIONS:
                onShape = isPointOnMultiPolygon(point, (MultiPolygonOptions) shape.getShape(), geodesic, tolerance);
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape
                        .getShape();
                for (GoogleMapShape shapeListItem : shapeList) {
                    onShape = isPointOnShape(point, shapeListItem, geodesic, tolerance);
                    if (onShape) {
                        break;
                    }
                }
                break;
            default:
                throw new GeoPackageException("Unsupported Shape Type: "
                        + shape.getShapeType());

        }

        return onShape;
    }

    /**
     * Is the point near the shape marker
     *
     * @param point       point
     * @param shapeMarker shape marker
     * @param tolerance   distance tolerance
     * @return true if near
     */
    public static boolean isPointNearMarker(LatLng point, MarkerOptions shapeMarker, double tolerance) {
        return isPointNearPoint(point, shapeMarker.getPosition(), tolerance);
    }

    /**
     * Is the point near the shape point
     *
     * @param point      point
     * @param shapePoint shape point
     * @param tolerance  distance tolerance
     * @return true if near
     */
    public static boolean isPointNearPoint(LatLng point, LatLng shapePoint, double tolerance) {
        return SphericalUtil.computeDistanceBetween(point, shapePoint) <= tolerance;
    }

    /**
     * Is the point near any points in the multi lat lng
     *
     * @param point       point
     * @param multiLatLng multi lat lng
     * @param tolerance   distance tolerance
     * @return true if near
     */
    public static boolean isPointNearMultiLatLng(LatLng point, MultiLatLng multiLatLng, double tolerance) {
        boolean near = false;
        for (LatLng multiPoint : multiLatLng.getLatLngs()) {
            near = isPointNearPoint(point, multiPoint, tolerance);
            if (near) {
                break;
            }
        }
        return near;
    }

    /**
     * Is the point on the polyline
     *
     * @param point     point
     * @param polyline  polyline
     * @param geodesic  geodesic check flag
     * @param tolerance distance tolerance
     * @return true if on the line
     */
    public static boolean isPointOnPolyline(LatLng point, PolylineOptions polyline, boolean geodesic, double tolerance) {
        return PolyUtil.isLocationOnPath(point, polyline.getPoints(), geodesic, tolerance);
    }

    /**
     * Is the point on the multi polyline
     *
     * @param point         point
     * @param multiPolyline multi polyline
     * @param geodesic      geodesic check flag
     * @param tolerance     distance tolerance
     * @return true if on the multi line
     */
    public static boolean isPointOnMultiPolyline(LatLng point, MultiPolylineOptions multiPolyline, boolean geodesic, double tolerance) {
        boolean near = false;
        for (PolylineOptions polyline : multiPolyline.getPolylineOptions()) {
            near = isPointOnPolyline(point, polyline, geodesic, tolerance);
            if (near) {
                break;
            }
        }
        return near;
    }

    /**
     * Is the point of the polygon
     *
     * @param point     point
     * @param polygon   polygon
     * @param geodesic  geodesic check flag
     * @param tolerance distance tolerance
     * @return true if on the polygon
     */
    public static boolean isPointOnPolygon(LatLng point, PolygonOptions polygon, boolean geodesic, double tolerance) {

        boolean onPolygon = PolyUtil.containsLocation(point, polygon.getPoints(), geodesic) ||
                PolyUtil.isLocationOnEdge(point, polygon.getPoints(), geodesic, tolerance);

        if (onPolygon) {
            for (List<LatLng> hole : polygon.getHoles()) {
                if (PolyUtil.containsLocation(point, hole, geodesic)) {
                    onPolygon = false;
                    break;
                }
            }
        }

        return onPolygon;
    }

    /**
     * Is the point on the multi polygon
     *
     * @param point        point
     * @param multiPolygon multi polygon
     * @param geodesic     geodesic check flag
     * @param tolerance    distance tolerance
     * @return true if on the multi polygon
     */
    public static boolean isPointOnMultiPolygon(LatLng point, MultiPolygonOptions multiPolygon, boolean geodesic, double tolerance) {
        boolean near = false;
        for (PolygonOptions polygon : multiPolygon.getPolygonOptions()) {
            near = isPointOnPolygon(point, polygon, geodesic, tolerance);
            if (near) {
                break;
            }
        }
        return near;
    }

}

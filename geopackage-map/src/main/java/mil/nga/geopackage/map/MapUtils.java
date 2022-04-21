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
import mil.nga.geopackage.style.PixelBounds;
import mil.nga.proj.ProjectionConstants;

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
        return buildClickBoundingBox(latLng, null, view, map, screenClickPercentage);
    }

    /**
     * Build a bounding box using the click location, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param pixelBounds           click pixel bounds
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static BoundingBox buildClickBoundingBox(LatLng latLng, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return buildClickBoundingBox(latLng, 0.0, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Build a bounding box using the click location, zoom level, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static BoundingBox buildClickBoundingBox(LatLng latLng, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return buildClickBoundingBox(latLng, 1.0f, zoom, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Build a bounding box using the click location, display density, zoom level, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static BoundingBox buildClickBoundingBox(LatLng latLng, float density, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {

        LatLngBoundingBox latLngBoundingBox = buildClickLatLngBoundingBox(latLng, density, zoom, pixelBounds, view, map, screenClickPercentage);

        // Create the bounding box to query for features
        BoundingBox bbox = buildClickBoundingBox(latLngBoundingBox);

        return bbox;
    }

    /**
     * Build a bounding box using the click location
     *
     * @param boundingBox click bounding box
     * @return bounding box
     * @since 6.3.0
     */
    public static BoundingBox buildClickBoundingBox(LatLngBoundingBox boundingBox) {
        return new BoundingBox(
                boundingBox.getLeftCoordinate().longitude,
                boundingBox.getDownCoordinate().latitude,
                boundingBox.getRightCoordinate().longitude,
                boundingBox.getUpCoordinate().latitude);
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
        return buildClickLatLngBounds(latLng, null, view, map, screenClickPercentage);
    }

    /**
     * Build a lat lng bounds using the click location, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param pixelBounds           click pixel bounds
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static LatLngBounds buildClickLatLngBounds(LatLng latLng, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return buildClickLatLngBounds(latLng, 0.0, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Build a lat lng bounds using the click location, zoom level, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static LatLngBounds buildClickLatLngBounds(LatLng latLng, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return buildClickLatLngBounds(latLng, 1.0f, zoom, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Build a lat lng bounds using the click location, display density, zoom level, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  view
     * @param map                   Google map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static LatLngBounds buildClickLatLngBounds(LatLng latLng, float density, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {

        LatLngBoundingBox latLngBoundingBox = buildClickLatLngBoundingBox(latLng, density, zoom, pixelBounds, view, map, screenClickPercentage);

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
        return getToleranceDistance(latLng, null, view, map, screenClickPercentage);
    }

    /**
     * Get the allowable tolerance distance in meters from the click location on the map view and map with the screen percentage tolerance.
     *
     * @param latLng                click location
     * @param pixelBounds           click pixel bounds
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return tolerance distance in meters
     * @since 6.3.0
     */
    public static double getToleranceDistance(LatLng latLng, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return getToleranceDistance(latLng, 0.0, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Get the allowable tolerance distance in meters from the click location on the map view and map with the screen percentage tolerance.
     *
     * @param latLng                click location
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return tolerance distance in meters
     * @since 6.3.0
     */
    public static double getToleranceDistance(LatLng latLng, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return getToleranceDistance(latLng, 1.0f, zoom, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Get the allowable tolerance distance in meters from the click location on the map view and map with the screen percentage tolerance.
     *
     * @param latLng                click location
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return tolerance distance in meters
     * @since 6.3.0
     */
    public static double getToleranceDistance(LatLng latLng, float density, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {

        LatLngBoundingBox latLngBoundingBox = buildClickLatLngBoundingBox(latLng, density, zoom, pixelBounds, view, map, screenClickPercentage);

        double distance = getToleranceDistance(latLng, latLngBoundingBox);

        return distance;
    }

    /**
     * Get the allowable tolerance distance in meters from the click bounding box
     *
     * @param latLng      click location
     * @param boundingBox click bounding box
     * @return tolerance distance in meters
     * @since 6.3.0
     */
    public static double getToleranceDistance(LatLng latLng, LatLngBoundingBox boundingBox) {

        double northwest = SphericalUtil.computeDistanceBetween(boundingBox.getNorthwestCoordinate(), latLng);
        double northeast = SphericalUtil.computeDistanceBetween(boundingBox.getNortheastCoordinate(), latLng);
        double southeast = SphericalUtil.computeDistanceBetween(boundingBox.getSoutheastCoordinate(), latLng);
        double southwest = SphericalUtil.computeDistanceBetween(boundingBox.getSouthwestCoordinate(), latLng);

        double distance = Math.max(northwest, northeast);
        distance = Math.max(distance, southeast);
        distance = Math.max(distance, southwest);

        return distance;
    }

    /**
     * Get the allowable tolerance distance in meters from the click bounding box
     *
     * @param boundingBox click bounding box
     * @return tolerance distance in meters
     * @since 6.3.0
     */
    public static double getToleranceDistance(LatLngBoundingBox boundingBox) {

        double distance1 = SphericalUtil.computeDistanceBetween(boundingBox.getNorthwestCoordinate(), boundingBox.getSoutheastCoordinate());
        double distance2 = SphericalUtil.computeDistanceBetween(boundingBox.getSouthwestCoordinate(), boundingBox.getNortheastCoordinate());

        double distance = Math.max(distance1, distance2) / 2.0;

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
        return buildClickLatLngBoundingBox(latLng, null, view, map, screenClickPercentage);
    }

    /**
     * Build a lat lng bounding box using the click location, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param pixelBounds           click pixel bounds
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return lat lng bounding box
     * @since 6.3.0
     */
    public static LatLngBoundingBox buildClickLatLngBoundingBox(LatLng latLng, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return buildClickLatLngBoundingBox(latLng, 0.0, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Build a lat lng bounding box using the click location, zoom level, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return lat lng bounding box
     * @since 6.3.0
     */
    public static LatLngBoundingBox buildClickLatLngBoundingBox(LatLng latLng, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {
        return buildClickLatLngBoundingBox(latLng, 1.0f, zoom, pixelBounds, view, map, screenClickPercentage);
    }

    /**
     * Build a lat lng bounding box using the click location, display density, zoom level, pixel bounds, map view, map, and screen percentage tolerance.
     * The bounding box can be used to query for features that were clicked
     *
     * @param latLng                click location
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param zoom                  current zoom level
     * @param pixelBounds           click pixel bounds
     * @param view                  map view
     * @param map                   map
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return lat lng bounding box
     * @since 6.3.0
     */
    public static LatLngBoundingBox buildClickLatLngBoundingBox(LatLng latLng, float density, double zoom, PixelBounds pixelBounds, View view, GoogleMap map, float screenClickPercentage) {

        // Get the pixels a click occurs from a feature
        double pixels = Math.max(view.getWidth(), view.getHeight()) * screenClickPercentage;
        double leftPixels = pixels;
        double upPixels = pixels;
        double rightPixels = pixels;
        double downPixels = pixels;

        if (pixelBounds != null) {
            double adjust = 1.0 + zoom - (int) zoom;
            adjust *= density;
            leftPixels += (pixelBounds.getLeft() * adjust);
            upPixels += (pixelBounds.getUp() * adjust);
            rightPixels += (pixelBounds.getRight() * adjust);
            downPixels += (pixelBounds.getDown() * adjust);
        }

        int leftOffset = (int) Math.ceil(leftPixels);
        int upOffset = (int) Math.ceil(upPixels);
        int rightOffset = (int) Math.ceil(rightPixels);
        int downOffset = (int) Math.ceil(downPixels);

        // Get the screen click location
        Projection projection = map.getProjection();
        android.graphics.Point clickLocation = projection.toScreenLocation(latLng);

        // Get the screen click locations in each width or height direction
        android.graphics.Point left = new android.graphics.Point(clickLocation);
        android.graphics.Point up = new android.graphics.Point(clickLocation);
        android.graphics.Point right = new android.graphics.Point(clickLocation);
        android.graphics.Point down = new android.graphics.Point(clickLocation);
        left.offset(-leftOffset, 0);
        up.offset(0, -upOffset);
        right.offset(rightOffset, 0);
        down.offset(0, downOffset);

        // Get the coordinates of the bounding box points
        LatLng leftCoordinate = projection.fromScreenLocation(left);
        LatLng upCoordinate = projection.fromScreenLocation(up);
        LatLng rightCoordinate = projection.fromScreenLocation(right);
        LatLng downCoordinate = projection.fromScreenLocation(down);

        LatLngBoundingBox latLngBoundingBox = new LatLngBoundingBox(leftCoordinate, upCoordinate, rightCoordinate, downCoordinate);

        return latLngBoundingBox;
    }

    /**
     * Build a bounding box using the location coordinate click location and map view bounds
     *
     * @param latLng                click location
     * @param mapBounds             map bounds
     * @param screenClickPercentage screen click percentage between 0.0 and 1.0 for how close a feature
     *                              on the screen must be to be included in a click query
     * @return bounding box
     * @since 6.3.0
     */
    public static BoundingBox buildClickBoundingBox(LatLng latLng, BoundingBox mapBounds, float screenClickPercentage) {

        // Get the screen width and height a click occurs from a feature
        double width = TileBoundingBoxMapUtils.getLongitudeDistance(mapBounds) * screenClickPercentage;
        double height = TileBoundingBoxMapUtils.getLatitudeDistance(mapBounds) * screenClickPercentage;

        LatLng leftCoordinate = SphericalUtil.computeOffset(latLng, width, 270);
        LatLng upCoordinate = SphericalUtil.computeOffset(latLng, height, 0);
        LatLng rightCoordinate = SphericalUtil.computeOffset(latLng, width, 90);
        LatLng downCoordinate = SphericalUtil.computeOffset(latLng, height, 180);

        BoundingBox bbox = new BoundingBox(leftCoordinate.longitude, downCoordinate.latitude, rightCoordinate.longitude, upCoordinate.latitude);

        return bbox;
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

    /**
     * Is the point on or near the shape, returning the distance when on the shape
     *
     * @param point     lat lng point
     * @param shape     map shape
     * @param geodesic  geodesic check flag
     * @param tolerance distance tolerance
     * @return distance when on shape, -1.0 when distance not calculated, null when not on shape
     * @since 6.3.1
     */
    public static Double isPointOnShapeDistance(LatLng point,
                                                GoogleMapShape shape, boolean geodesic, double tolerance) {

        Double distance = null;

        switch (shape.getShapeType()) {

            case LAT_LNG:
                distance = isPointNearPointDistance(point, (LatLng) shape.getShape(), tolerance);
                break;
            case MARKER_OPTIONS:
                distance = isPointNearMarkerDistance(point, (MarkerOptions) shape.getShape(), tolerance);
                break;
            case POLYLINE_OPTIONS:
                if (isPointOnPolyline(point, (PolylineOptions) shape.getShape(), geodesic, tolerance)) {
                    distance = -1.0;
                }
                break;
            case POLYGON_OPTIONS:
                if (isPointOnPolygon(point, (PolygonOptions) shape.getShape(), geodesic, tolerance)) {
                    distance = -1.0;
                }
                break;
            case MULTI_LAT_LNG:
                distance = isPointNearMultiLatLngDistance(point, (MultiLatLng) shape.getShape(), tolerance);
                break;
            case MULTI_POLYLINE_OPTIONS:
                if (isPointOnMultiPolyline(point, (MultiPolylineOptions) shape.getShape(), geodesic, tolerance)) {
                    distance = -1.0;
                }
                break;
            case MULTI_POLYGON_OPTIONS:
                if (isPointOnMultiPolygon(point, (MultiPolygonOptions) shape.getShape(), geodesic, tolerance)) {
                    distance = -1.0;
                }
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape
                        .getShape();
                for (GoogleMapShape shapeListItem : shapeList) {
                    Double shapeDistance = isPointOnShapeDistance(point, shapeListItem, geodesic, tolerance);
                    if (distance == null || (shapeDistance != null && shapeDistance >= 0 && shapeDistance < distance)) {
                        distance = shapeDistance;
                    }
                }
                break;
            default:
                throw new GeoPackageException("Unsupported Shape Type: "
                        + shape.getShapeType());

        }

        return distance;
    }

    /**
     * Is the point near the shape marker, returning the distance when on marker
     *
     * @param point       point
     * @param shapeMarker shape marker
     * @param tolerance   distance tolerance
     * @return distance when on marker, null when not
     * @since 6.3.1
     */
    public static Double isPointNearMarkerDistance(LatLng point, MarkerOptions shapeMarker, double tolerance) {
        return isPointNearPointDistance(point, shapeMarker.getPosition(), tolerance);
    }

    /**
     * Is the point near the shape point, returning the distance when on point
     *
     * @param point      point
     * @param shapePoint shape point
     * @param tolerance  distance tolerance
     * @return distance when on point, null when not
     * @since 6.3.1
     */
    public static Double isPointNearPointDistance(LatLng point, LatLng shapePoint, double tolerance) {
        double distance = SphericalUtil.computeDistanceBetween(point, shapePoint);
        return distance <= tolerance ? distance : null;
    }

    /**
     * Is the point near any points in the multi lat lng, returning the nearest distance when on multi lat lng
     *
     * @param point       point
     * @param multiLatLng multi lat lng
     * @param tolerance   distance tolerance
     * @return distance when on multi lat lng, null when not
     * @since 6.3.1
     */
    public static Double isPointNearMultiLatLngDistance(LatLng point, MultiLatLng multiLatLng, double tolerance) {
        Double distance = null;
        for (LatLng multiPoint : multiLatLng.getLatLngs()) {
            Double pointDistance = isPointNearPointDistance(point, multiPoint, tolerance);
            if (distance == null || (pointDistance != null && pointDistance < distance)) {
                distance = pointDistance;
            }
        }
        return distance;
    }

}

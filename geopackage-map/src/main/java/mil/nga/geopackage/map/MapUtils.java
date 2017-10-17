package mil.nga.geopackage.map;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.map.tiles.TileBoundingBoxMapUtils;
import mil.nga.geopackage.projection.ProjectionConstants;

/**
 * Map utilities
 *
 * @author osbornb
 * @since 1.4.2
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

        BoundingBox boundingBox = new BoundingBox(minLongitude, maxLongitude, minLatitude, maxLatitude);

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
                latLngBoundingBox.getRightCoordinate().longitude,
                latLngBoundingBox.getDownCoordinate().latitude,
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

}

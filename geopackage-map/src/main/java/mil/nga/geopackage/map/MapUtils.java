package mil.nga.geopackage.map;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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

        double widthMeters = boundingBoxWidth / view.getWidth();
        double heightMeters = boundingBoxHeight / view.getHeight();

        double meters = Math.min(widthMeters, heightMeters);

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

}

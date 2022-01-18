package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import org.junit.Test;

import mil.nga.geopackage.BoundingBox;
import mil.nga.proj.Projection;
import mil.nga.proj.ProjectionConstants;
import mil.nga.proj.ProjectionFactory;
import mil.nga.sf.Point;
import mil.nga.sf.proj.GeometryTransform;

/**
 * Feature Overlay Query tests
 *
 * @author osbornb
 */
public class FeatureOverlayQueryTest {

    /**
     * Test Tile Bounds
     */
    @Test
    public void testTileBounds() {

        double longitude = -77.196785;
        double latitude = 38.753195;
        int zoom = 6;

        LatLng coordinate = new LatLng(latitude, longitude);
        BoundingBox boundingBox = FeatureOverlayQuery.tileBounds(coordinate, zoom);

        TestCase.assertEquals(-78.75000000000001, boundingBox.getMinLongitude(), 0.0);
        TestCase.assertEquals(36.597889133070204, boundingBox.getMinLatitude(), 0.0);
        TestCase.assertEquals(-73.125, boundingBox.getMaxLongitude(), 0.0);
        TestCase.assertEquals(40.97989806962014, boundingBox.getMaxLatitude(), 0.0);

        Point point = new Point(longitude, latitude);
        BoundingBox boundingBox2 = FeatureOverlayQuery.tileBounds(point, zoom);

        TestCase.assertEquals(boundingBox, boundingBox2);

        Projection projection = ProjectionFactory.getProjection(ProjectionConstants.EPSG_WEB_MERCATOR);
        GeometryTransform transform = GeometryTransform.create(
                ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM, projection);
        point = transform.transform(point);
        boundingBox = FeatureOverlayQuery.tileBounds(projection, point, zoom);

        TestCase.assertEquals(-8766409.899970295, boundingBox.getMinLongitude(), 0.0);
        TestCase.assertEquals(4383204.9499851465, boundingBox.getMinLatitude(), 0.0);
        TestCase.assertEquals(-8140237.7642581295, boundingBox.getMaxLongitude(), 0.0);
        TestCase.assertEquals(5009377.085697312, boundingBox.getMaxLatitude(), 0.0);

        longitude = 151.215026;
        latitude = -33.856686;
        zoom = 15;

        coordinate = new LatLng(latitude, longitude);
        boundingBox = FeatureOverlayQuery.tileBounds(coordinate, zoom);

        TestCase.assertEquals(151.20483398437506, boundingBox.getMinLongitude(), 0.0);
        TestCase.assertEquals(-33.86129311351553, boundingBox.getMinLatitude(), 0.0);
        TestCase.assertEquals(151.21582031250003, boundingBox.getMaxLongitude(), 0.0);
        TestCase.assertEquals(-33.852169701407426, boundingBox.getMaxLatitude(), 0.0);

        point = new Point(longitude, latitude);
        boundingBox2 = FeatureOverlayQuery.tileBounds(point, zoom);

        TestCase.assertEquals(boundingBox, boundingBox2);

        point = transform.transform(point);
        boundingBox = FeatureOverlayQuery.tileBounds(projection, point, zoom);

        TestCase.assertEquals(16832045.124622095, boundingBox.getMinLongitude(), 0.0);
        TestCase.assertEquals(-4010192.2519534864, boundingBox.getMinLatitude(), 0.0);
        TestCase.assertEquals(16833268.117074657, boundingBox.getMaxLongitude(), 0.0);
        TestCase.assertEquals(-4008969.2595009245, boundingBox.getMaxLatitude(), 0.0);

    }

}

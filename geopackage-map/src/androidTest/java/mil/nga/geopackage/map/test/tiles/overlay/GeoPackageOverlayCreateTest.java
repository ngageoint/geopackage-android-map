package mil.nga.geopackage.map.test.tiles.overlay;

import org.junit.Test;

import java.sql.SQLException;

import mil.nga.geopackage.map.test.CreateGeoPackageTestCase;

/**
 * Test GeoPackage Overlay from a created database
 * 
 * @author osbornb
 */
public class GeoPackageOverlayCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GeoPackageOverlayCreateTest() {

	}

	/**
	 * Test overlay
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testOverlay() throws SQLException {

		GeoPackageOverlayUtils.testOverlay(geoPackage);

	}

}

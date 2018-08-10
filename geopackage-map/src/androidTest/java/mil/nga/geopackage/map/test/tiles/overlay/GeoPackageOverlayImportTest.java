package mil.nga.geopackage.map.test.tiles.overlay;

import org.junit.Test;

import java.sql.SQLException;

import mil.nga.geopackage.map.test.ImportGeoPackageTestCase;

/**
 * Test GeoPackage Overlay from an imported database
 * 
 * @author osbornb
 */
public class GeoPackageOverlayImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GeoPackageOverlayImportTest() {

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

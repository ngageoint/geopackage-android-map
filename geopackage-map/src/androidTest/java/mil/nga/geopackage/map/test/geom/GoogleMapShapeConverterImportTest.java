package mil.nga.geopackage.map.test.geom;

import java.sql.SQLException;

import mil.nga.geopackage.map.test.ImportGeoPackageTestCase;

/**
 * Test Google Map Shape Converter from an imported database
 * 
 * @author osbornb
 */
public class GoogleMapShapeConverterImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GoogleMapShapeConverterImportTest() {

	}

	/**
	 * Test shapes
	 * 
	 * @throws SQLException
	 */
	public void testShapes() throws SQLException {

		GoogleMapShapeConverterUtils.testShapes(geoPackage);

	}

}

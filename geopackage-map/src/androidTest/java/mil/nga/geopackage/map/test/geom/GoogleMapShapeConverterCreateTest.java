package mil.nga.geopackage.map.test.geom;

import org.junit.Test;

import java.sql.SQLException;

import mil.nga.geopackage.map.test.CreateGeoPackageTestCase;

/**
 * Test Google Map Shape Converter from a created database
 * 
 * @author osbornb
 */
public class GoogleMapShapeConverterCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GoogleMapShapeConverterCreateTest() {

	}

	/**
	 * Test shapes
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testShapes() throws SQLException {

		GoogleMapShapeConverterUtils.testShapes(geoPackage);

	}

}

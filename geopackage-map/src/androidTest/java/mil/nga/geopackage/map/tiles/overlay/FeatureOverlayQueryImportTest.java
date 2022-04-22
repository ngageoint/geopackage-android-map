package mil.nga.geopackage.map.tiles.overlay;

import org.junit.Test;

import mil.nga.geopackage.map.ImportGeoPackageTestCase;

/**
 * Test Feature Overlay Query from an imported database
 *
 * @author osbornb
 */
public class FeatureOverlayQueryImportTest extends ImportGeoPackageTestCase {

    /**
     * Test Build Map Click Table Data
     */
    @Test
    public void testBuildMapClickTableData() {

        FeatureOverlayQueryUtils.testBuildMapClickTableData(activity, geoPackage);

    }

}

package mil.nga.geopackage.map.tiles.overlay;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.features.index.FeatureIndexManager;
import mil.nga.geopackage.features.index.FeatureIndexResults;
import mil.nga.geopackage.features.index.FeatureIndexType;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.map.geom.GoogleMapShapeConverter;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.geopackage.tiles.features.DefaultFeatureTiles;
import mil.nga.geopackage.tiles.features.FeatureTiles;
import mil.nga.geopackage.tiles.overlay.FeatureRowData;
import mil.nga.geopackage.tiles.overlay.FeatureTableData;
import mil.nga.proj.Projection;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryType;
import mil.nga.sf.Point;

/**
 * Feature overlay query utils
 *
 * @author osbornb
 */
public class FeatureOverlayQueryUtils {

    private static final int MAX_CLICKS_PER_TABLE = 10;

    public static void testBuildMapClickTableData(Activity activity, GeoPackage geoPackage) {

        float density = activity.getApplicationContext().getResources().getDisplayMetrics().density;

        for (String featureTable : geoPackage.getFeatureTables()) {

            FeatureDao featureDao = geoPackage.getFeatureDao(featureTable);

            // Index if not already
            FeatureIndexManager indexer = new FeatureIndexManager(activity, geoPackage, featureDao);
            try {
                if (!indexer.isIndexed()) {
                    indexer.setIndexLocation(FeatureIndexType.GEOPACKAGE);
                    indexer.index();
                }
            } finally {
                indexer.close();
            }

            Projection projection = featureDao.getProjection();
            GoogleMapShapeConverter shapeConverter = new GoogleMapShapeConverter(projection);
            BoundingBox featureBounds = featureDao.getBoundingBox();

            FeatureTiles featureTiles = new DefaultFeatureTiles(activity, geoPackage, featureDao, density);
            try {
                FeatureOverlay featureOverlay = new FeatureOverlay(featureTiles);

                FeatureOverlayQuery featureOverlayQuery = new FeatureOverlayQuery(activity, featureOverlay, featureTiles);
                featureOverlayQuery.calculateStylePixelBounds();

                List<Geometry> geometries = new ArrayList<>();

                FeatureIndexResults indexResults = featureOverlayQuery.queryFeatures(featureBounds);
                try {
                    for (FeatureRow featureRow : indexResults) {
                        Geometry geometry = featureRow.getGeometryValue();
                        if (geometry != null) {
                            geometries.add(geometry);
                            if (geometries.size() >= MAX_CLICKS_PER_TABLE) {
                                break;
                            }
                        }
                    }
                } finally {
                    indexResults.close();
                }

                for (Geometry geometry : geometries) {

                    Point point = geometry.getCentroid();
                    LatLng clickLocation = shapeConverter.toLatLng(point);

                    double zoom = Math.random() * 21.0;

                    BoundingBox tileBounds = TileBoundingBoxUtils.getWGS84TileBounds(projection, point, (int) zoom);

                    FeatureTableData featureTableData = featureOverlayQuery.buildMapClickTableDataWithMapBounds(clickLocation, zoom, tileBounds);

                    if (geometry.getGeometryType() == GeometryType.POINT) {
                        TestCase.assertNotNull(featureTableData);
                    }

                    if (featureTableData != null) {

                        TestCase.assertEquals(featureTable, featureTableData.getName());
                        TestCase.assertTrue(featureTableData.getCount() > 0);
                        TestCase.assertEquals(featureTableData.getCount(), featureTableData.getRows().size());

                        boolean nonPoint = false;

                        for (FeatureRowData featureRowData : featureTableData.getRows()) {

                            TestCase.assertNotNull(featureRowData.getIdColumn());
                            TestCase.assertTrue(featureRowData.getId() >= 0);
                            TestCase.assertNotNull(featureRowData.getGeometryColumn());
                            TestCase.assertNotNull(featureRowData.getGeometryData());
                            TestCase.assertNotNull(featureRowData.getGeometry());
                            GeometryType geometryType = featureRowData.getGeometryType();
                            TestCase.assertNotNull(geometryType);
                            TestCase.assertNotNull(featureRowData.getGeometryEnvelope());
                            TestCase.assertNotNull(featureRowData.jsonCompatible());

                            if (geometryType == GeometryType.POINT) {
                                TestCase.assertFalse(nonPoint);
                            } else {
                                nonPoint = true;
                            }
                        }

                    }

                }

            } finally {
                featureTiles.close();
            }
        }

    }

}

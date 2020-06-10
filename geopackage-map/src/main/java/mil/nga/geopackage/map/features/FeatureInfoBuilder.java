package mil.nga.geopackage.map.features;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.extension.schema.columns.DataColumns;
import mil.nga.geopackage.extension.schema.columns.DataColumnsDao;
import mil.nga.geopackage.features.index.FeatureIndexListResults;
import mil.nga.geopackage.features.index.FeatureIndexResults;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.map.MapUtils;
import mil.nga.geopackage.map.R;
import mil.nga.geopackage.map.geom.GoogleMapShape;
import mil.nga.geopackage.map.geom.GoogleMapShapeConverter;
import mil.nga.geopackage.map.tiles.overlay.FeatureOverlayQuery;
import mil.nga.geopackage.srs.SpatialReferenceSystem;
import mil.nga.geopackage.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.tiles.overlay.FeatureRowData;
import mil.nga.geopackage.tiles.overlay.FeatureTableData;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryType;
import mil.nga.sf.Point;
import mil.nga.sf.proj.Projection;
import mil.nga.sf.proj.ProjectionTransform;
import mil.nga.sf.util.GeometryPrinter;

/**
 * Feature Info Builder for building feature result based info messages or table data from feature results
 *
 * @author osbornb
 * @since 2.0.0
 */
public class FeatureInfoBuilder {

    /**
     * Feature DAO
     */
    private final FeatureDao featureDao;

    /**
     * Geometry Type
     */
    private final GeometryType geometryType;

    /**
     * Geometry types to ignore
     */
    private Set<GeometryType> ignoreGeometryTypes = new HashSet<>();

    /**
     * Table name used when building text
     */
    private String name;

    /**
     * Max number of points clicked to return detailed information about
     */
    private int maxPointDetailedInfo;

    /**
     * Max number of features clicked to return detailed information about
     */
    private int maxFeatureDetailedInfo;

    /**
     * Print Point geometries within detailed info when true
     */
    private boolean detailedInfoPrintPoints;

    /**
     * Print Feature geometries within detailed info when true
     */
    private boolean detailedInfoPrintFeatures;

    /**
     * Geodesic check flag
     */
    private boolean geodesic = false;

    /**
     * Constructor
     *
     * @param context    context
     * @param featureDao feature dao
     */
    public FeatureInfoBuilder(Context context, FeatureDao featureDao) {

        this.featureDao = featureDao;

        geometryType = featureDao.getGeometryType();
        name = featureDao.getDatabase() + " - " + featureDao.getTableName();

        Resources resources = context.getResources();

        maxPointDetailedInfo = resources.getInteger(R.integer.map_feature_max_point_detailed_info);
        maxFeatureDetailedInfo = resources.getInteger(R.integer.map_feature_max_feature_detailed_info);

        detailedInfoPrintPoints = resources.getBoolean(R.bool.map_feature_detailed_info_print_points);
        detailedInfoPrintFeatures = resources.getBoolean(R.bool.map_feature_detailed_info_print_features);
    }

    /**
     * Get the name used in text
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name used in text
     *
     * @param name table reference name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the max points in a query to print detailed results about
     *
     * @return max point detailed info
     */
    public int getMaxPointDetailedInfo() {
        return maxPointDetailedInfo;
    }

    /**
     * Set the max points in a query to print detailed results about
     *
     * @param maxPointDetailedInfo max number of points to include detailed information about
     */
    public void setMaxPointDetailedInfo(int maxPointDetailedInfo) {
        this.maxPointDetailedInfo = maxPointDetailedInfo;
    }

    /**
     * Get the max features in a query to print detailed results about
     *
     * @return max feature detailed info
     */
    public int getMaxFeatureDetailedInfo() {
        return maxFeatureDetailedInfo;
    }

    /**
     * Set the max features in a query to print detailed results about
     *
     * @param maxFeatureDetailedInfo max number of features to include detailed information about
     */
    public void setMaxFeatureDetailedInfo(int maxFeatureDetailedInfo) {
        this.maxFeatureDetailedInfo = maxFeatureDetailedInfo;
    }

    /**
     * Is the detailed info going to print point geometries
     *
     * @return detailed info print points flag
     */
    public boolean isDetailedInfoPrintPoints() {
        return detailedInfoPrintPoints;
    }

    /**
     * Set the detailed info to print point geometries
     *
     * @param detailedInfoPrintPoints true to include detailed info on point geometries
     */
    public void setDetailedInfoPrintPoints(boolean detailedInfoPrintPoints) {
        this.detailedInfoPrintPoints = detailedInfoPrintPoints;
    }

    /**
     * Is the detailed info going to print feature geometries
     *
     * @return detailed info print features flag
     */
    public boolean isDetailedInfoPrintFeatures() {
        return detailedInfoPrintFeatures;
    }

    /**
     * Set the detailed info to print feature geometries
     *
     * @param detailedInfoPrintFeatures true to include detailed info on feature geometries
     */
    public void setDetailedInfoPrintFeatures(boolean detailedInfoPrintFeatures) {
        this.detailedInfoPrintFeatures = detailedInfoPrintFeatures;
    }

    /**
     * Is geodesic checking enabled
     *
     * @return true if geodesic
     */
    public boolean isGeodesic() {
        return geodesic;
    }

    /**
     * Set the geodesic check flag
     *
     * @param geodesic true for geodesic checking
     */
    public void setGeodesic(boolean geodesic) {
        this.geodesic = geodesic;
    }

    /**
     * Ignore the provided geometry type
     *
     * @param geometryType geometry type
     */
    public void ignoreGeometryType(GeometryType geometryType) {
        ignoreGeometryTypes.add(geometryType);
    }

    /**
     * Build a feature results information message and close the results
     *
     * @param results feature index results
     * @return results message or null if no results
     */
    public String buildResultsInfoMessageAndClose(FeatureIndexResults results) {
        return buildResultsInfoMessageAndClose(results, 0, null, null);
    }

    /**
     * Build a feature results information message and close the results
     *
     * @param results    feature index results
     * @param projection desired geometry projection
     * @return results message or null if no results
     */
    public String buildResultsInfoMessageAndClose(FeatureIndexResults results, Projection projection) {
        return buildResultsInfoMessageAndClose(results, 0, null, projection);
    }

    /**
     * Build a feature results information message and close the results
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation map click location
     * @return results message or null if no results
     */
    public String buildResultsInfoMessageAndClose(FeatureIndexResults results, double tolerance, LatLng clickLocation) {
        return buildResultsInfoMessageAndClose(results, tolerance, clickLocation, null);
    }

    /**
     * Build a feature results information message and close the results
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation map click location
     * @param projection    desired geometry projection
     * @return results message or null if no results
     */
    public String buildResultsInfoMessageAndClose(FeatureIndexResults results, double tolerance, LatLng clickLocation, Projection projection) {
        String message = null;

        try {
            message = buildResultsInfoMessage(results, tolerance, clickLocation, projection);
        } finally {
            results.close();
        }

        return message;
    }

    /**
     * Build a feature results information message
     *
     * @param results   feature index results
     * @param tolerance distance tolerance
     * @return results message or null if no results
     */
    public String buildResultsInfoMessage(FeatureIndexResults results, double tolerance) {
        return buildResultsInfoMessage(results, tolerance, null, null);
    }

    /**
     * Build a feature results information message
     *
     * @param results    feature index results
     * @param tolerance  distance tolerance
     * @param projection desired geometry projection
     * @return results message or null if no results
     */
    public String buildResultsInfoMessage(FeatureIndexResults results, double tolerance, Projection projection) {
        return buildResultsInfoMessage(results, tolerance, null, projection);
    }

    /**
     * Build a feature results information message
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation map click location
     * @return results message or null if no results
     */
    public String buildResultsInfoMessage(FeatureIndexResults results, double tolerance, LatLng clickLocation) {
        return buildResultsInfoMessage(results, tolerance, clickLocation, null);
    }

    /**
     * Build a feature results information message
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation map click location
     * @param projection    desired geometry projection
     * @return results message or null if no results
     */
    public String buildResultsInfoMessage(FeatureIndexResults results, double tolerance, LatLng clickLocation, Projection projection) {

        String message = null;

        // Fine filter results so that the click location is within the tolerance of each feature row result
        FeatureIndexResults filteredResults = fineFilterResults(results, tolerance, clickLocation);

        long featureCount = filteredResults.count();
        if (featureCount > 0) {

            int maxFeatureInfo = 0;
            if (geometryType == GeometryType.POINT) {
                maxFeatureInfo = maxPointDetailedInfo;
            } else {
                maxFeatureInfo = maxFeatureDetailedInfo;
            }

            if (featureCount <= maxFeatureInfo) {
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append(name)
                        .append("\n");

                int featureNumber = 0;

                DataColumnsDao dataColumnsDao = getDataColumnsDao();

                for (FeatureRow featureRow : filteredResults) {

                    featureNumber++;
                    if (featureNumber > maxFeatureInfo) {
                        break;
                    }

                    if (featureCount > 1) {
                        if (featureNumber > 1) {
                            messageBuilder.append("\n");
                        } else {
                            messageBuilder.append("\n")
                                    .append(featureCount)
                                    .append(" Features")
                                    .append("\n");
                        }
                        messageBuilder.append("\n")
                                .append("Feature ")
                                .append(featureNumber)
                                .append(":")
                                .append("\n");
                    }

                    int geometryColumn = featureRow.getGeometryColumnIndex();
                    for (int i = 0; i < featureRow.columnCount(); i++) {
                        if (i != geometryColumn) {
                            Object value = featureRow.getValue(i);
                            if (value != null) {
                                String columnName = featureRow.getColumnName(i);
                                columnName = getColumnName(dataColumnsDao, featureRow, columnName);
                                messageBuilder.append("\n")
                                        .append(columnName)
                                        .append(": ")
                                        .append(value);
                            }
                        }
                    }

                    GeoPackageGeometryData geomData = featureRow.getGeometry();
                    if (geomData != null && geomData.getGeometry() != null) {

                        boolean printFeatures = false;
                        if (geomData.getGeometry().getGeometryType() == GeometryType.POINT) {
                            printFeatures = detailedInfoPrintPoints;
                        } else {
                            printFeatures = detailedInfoPrintFeatures;
                        }

                        if (printFeatures) {
                            if (projection != null) {
                                projectGeometry(geomData, projection);
                            }
                            messageBuilder.append("\n\n");
                            messageBuilder.append(GeometryPrinter.getGeometryString(geomData.getGeometry()));
                        }
                    }

                }

                message = messageBuilder.toString();
            } else {
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append(name)
                        .append("\n\t")
                        .append(featureCount)
                        .append(" features");
                if (clickLocation != null) {
                    messageBuilder.append(" near location:\n");
                    Point point = new Point(clickLocation.longitude, clickLocation.latitude);
                    messageBuilder.append(GeometryPrinter.getGeometryString(point));
                }
                message = messageBuilder.toString();
            }
        }

        return message;
    }

    /**
     * Build a feature results information message
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation map click location
     * @return feature table data or null if not results
     */
    public FeatureTableData buildTableDataAndClose(FeatureIndexResults results, double tolerance, LatLng clickLocation) {
        return buildTableDataAndClose(results, tolerance, clickLocation, null);
    }

    /**
     * Build a feature results information message
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation map click location
     * @param projection    desired geometry projection
     * @return feature table data or null if not results
     */
    public FeatureTableData buildTableDataAndClose(FeatureIndexResults results, double tolerance, LatLng clickLocation, Projection projection) {

        FeatureTableData tableData = null;

        // Fine filter results so that the click location is within the tolerance of each feature row result
        FeatureIndexResults filteredResults = fineFilterResults(results, tolerance, clickLocation);

        long featureCount = filteredResults.count();
        if (featureCount > 0) {

            int maxFeatureInfo = 0;
            if (geometryType == GeometryType.POINT) {
                maxFeatureInfo = maxPointDetailedInfo;
            } else {
                maxFeatureInfo = maxFeatureDetailedInfo;
            }

            if (featureCount <= maxFeatureInfo) {

                DataColumnsDao dataColumnsDao = getDataColumnsDao();

                List<FeatureRowData> rows = new ArrayList<>();

                for (FeatureRow featureRow : filteredResults) {

                    Map<String, Object> values = new HashMap<>();
                    String geometryColumnName = null;

                    int geometryColumn = featureRow.getGeometryColumnIndex();
                    for (int i = 0; i < featureRow.columnCount(); i++) {

                        Object value = featureRow.getValue(i);

                        String columnName = featureRow.getColumnName(i);

                        columnName = getColumnName(dataColumnsDao, featureRow, columnName);

                        if (i == geometryColumn) {
                            geometryColumnName = columnName;
                            if (projection != null && value != null) {
                                GeoPackageGeometryData geomData = (GeoPackageGeometryData) value;
                                projectGeometry(geomData, projection);
                            }
                        }

                        if (value != null) {
                            values.put(columnName, value);
                        }
                    }

                    FeatureRowData featureRowData = new FeatureRowData(values, geometryColumnName);
                    rows.add(featureRowData);
                }

                tableData = new FeatureTableData(featureDao.getTableName(), featureCount, rows);
            } else {
                tableData = new FeatureTableData(featureDao.getTableName(), featureCount);
            }
        }

        results.close();

        return tableData;
    }

    /**
     * Project the geometry into the provided projection
     *
     * @param geometryData geometry data
     * @param projection   projection
     */
    public void projectGeometry(GeoPackageGeometryData geometryData, Projection projection) {

        if (geometryData.getGeometry() != null) {

            SpatialReferenceSystemDao srsDao = SpatialReferenceSystemDao.create(featureDao.getDb());
            try {
                int srsId = geometryData.getSrsId();
                SpatialReferenceSystem srs = srsDao.queryForId((long) srsId);

                if (!projection.equals(srs.getOrganization(), srs.getOrganizationCoordsysId())) {

                    Projection geomProjection = srs.getProjection();
                    ProjectionTransform transform = geomProjection.getTransformation(projection);

                    Geometry projectedGeometry = transform.transform(geometryData.getGeometry());
                    geometryData.setGeometry(projectedGeometry);
                    SpatialReferenceSystem projectionSrs = srsDao.getOrCreateCode(projection.getAuthority(), Long.parseLong(projection.getCode()));
                    geometryData.setSrsId((int) projectionSrs.getSrsId());
                }
            } catch (SQLException e) {
                throw new GeoPackageException("Failed to project geometry to projection with Authority: "
                        + projection.getAuthority() + ", Code: " + projection.getCode(), e);
            }
        }

    }

    /**
     * Get a Data Columns DAO
     *
     * @return data columns dao
     */
    private DataColumnsDao getDataColumnsDao() {
        DataColumnsDao dataColumnsDao = DataColumnsDao.create(featureDao.getDb());
        try {
            if (!dataColumnsDao.isTableExists()) {
                dataColumnsDao = null;
            }
        } catch (SQLException e) {
            dataColumnsDao = null;
            Log.e(FeatureOverlayQuery.class.getSimpleName(), "Failed to get a Data Columns DAO", e);
        }
        return dataColumnsDao;
    }

    /**
     * Get the column name by checking for a DataColumns name, otherwise returns the provided column name
     *
     * @param dataColumnsDao data columns dao
     * @param featureRow     feature row
     * @param columnName     column name
     * @return column name
     */
    private String getColumnName(DataColumnsDao dataColumnsDao, FeatureRow featureRow, String columnName) {

        String newColumnName = columnName;

        if (dataColumnsDao != null) {
            try {
                DataColumns dataColumn = dataColumnsDao.getDataColumn(featureRow.getTable().getTableName(), columnName);
                if (dataColumn != null) {
                    newColumnName = dataColumn.getName();
                }
            } catch (SQLException e) {
                Log.e(FeatureOverlayQuery.class.getSimpleName(),
                        "Failed to search for Data Column name for column: " + columnName
                                + ", Feature Table: " + featureRow.getTable().getTableName(), e);
            }
        }

        return newColumnName;
    }

    /**
     * Fine filter the index results verifying the click location is within the tolerance of each feature row
     *
     * @param results       feature index results
     * @param tolerance     distance tolerance
     * @param clickLocation click location
     * @return filtered feature index results
     */
    private FeatureIndexResults fineFilterResults(FeatureIndexResults results, double tolerance, LatLng clickLocation) {

        FeatureIndexResults filteredResults = null;
        if (ignoreGeometryTypes.contains(geometryType)) {
            filteredResults = new FeatureIndexListResults();
        } else if (clickLocation == null && ignoreGeometryTypes.isEmpty()) {
            filteredResults = results;
        } else {

            FeatureIndexListResults filteredListResults = new FeatureIndexListResults();

            GoogleMapShapeConverter converter = new GoogleMapShapeConverter(
                    featureDao.getProjection());

            for (FeatureRow featureRow : results) {

                GeoPackageGeometryData geomData = featureRow.getGeometry();
                if (geomData != null) {
                    Geometry geometry = geomData.getGeometry();
                    if (geometry != null) {

                        if (!ignoreGeometryTypes.contains(geometry.getGeometryType())) {

                            if (clickLocation != null) {

                                GoogleMapShape mapShape = converter.toShape(geometry);
                                if (MapUtils.isPointOnShape(clickLocation, mapShape, geodesic, tolerance)) {

                                    filteredListResults.addRow(featureRow);

                                }
                            } else {
                                filteredListResults.addRow(featureRow);
                            }

                        }
                    }
                }

            }

            filteredResults = filteredListResults;
        }

        return filteredResults;
    }

}

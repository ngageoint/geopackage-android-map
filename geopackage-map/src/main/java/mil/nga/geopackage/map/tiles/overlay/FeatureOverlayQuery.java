package mil.nga.geopackage.map.tiles.overlay;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.features.index.FeatureIndexManager;
import mil.nga.geopackage.features.index.FeatureIndexResults;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.map.MapUtils;
import mil.nga.geopackage.map.R;
import mil.nga.geopackage.map.features.FeatureInfoBuilder;
import mil.nga.geopackage.map.tiles.TileBoundingBoxMapUtils;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.geopackage.tiles.TileGrid;
import mil.nga.geopackage.tiles.features.FeatureTiles;
import mil.nga.geopackage.tiles.overlay.FeatureTableData;
import mil.nga.sf.Point;
import mil.nga.sf.proj.Projection;
import mil.nga.sf.proj.ProjectionConstants;
import mil.nga.sf.proj.ProjectionFactory;

/**
 * Used to query the features represented by tiles, either being drawn from or linked to the features
 *
 * @author osbornb
 * @since 1.1.0
 */
public class FeatureOverlayQuery {

    /**
     * Bounded Overlay
     */
    private final BoundedOverlay boundedOverlay;

    /**
     * Feature Tiles
     */
    private final FeatureTiles featureTiles;

    /**
     * Screen click percentage between 0.0 and 1.0 for how close a feature on the screen must be
     * to be included in a click query
     */
    private float screenClickPercentage;

    /**
     * Flag indicating if building info messages for tiles with features over the max is enabled
     */
    private boolean maxFeaturesInfo;

    /**
     * Flag indicating if building info messages for clicked features is enabled
     */
    private boolean featuresInfo;

    /**
     * Feature info builder
     */
    private FeatureInfoBuilder featureInfoBuilder;

    /**
     * Constructor
     *
     * @param context        context
     * @param featureOverlay feature overlay
     */
    public FeatureOverlayQuery(Context context, FeatureOverlay featureOverlay) {
        this(context, featureOverlay, featureOverlay.getFeatureTiles());
    }

    /**
     * Constructor
     *
     * @param context        context
     * @param boundedOverlay bounded overlay
     * @param featureTiles   feature tiles
     * @since 1.2.5
     */
    public FeatureOverlayQuery(Context context, BoundedOverlay boundedOverlay, FeatureTiles featureTiles) {
        this.boundedOverlay = boundedOverlay;
        this.featureTiles = featureTiles;

        Resources resources = context.getResources();

        // Get the screen percentage to determine when a feature is clicked
        TypedValue screenPercentage = new TypedValue();
        resources.getValue(R.dimen.map_feature_overlay_click_screen_percentage, screenPercentage, true);
        screenClickPercentage = screenPercentage.getFloat();

        maxFeaturesInfo = resources.getBoolean(R.bool.map_feature_overlay_max_features_info);
        featuresInfo = resources.getBoolean(R.bool.map_feature_overlay_features_info);

        FeatureDao featureDao = featureTiles.getFeatureDao();
        featureInfoBuilder = new FeatureInfoBuilder(context, featureDao);
    }

    /**
     * Close the feature overlay query connection
     *
     * @since 1.2.7
     */
    public void close() {
        if (featureTiles != null) {
            featureTiles.close();
        }
    }

    /**
     * Get the bounded overlay
     *
     * @return bounded overlay
     * @since 1.2.5
     */
    public BoundedOverlay getBoundedOverlay() {
        return boundedOverlay;
    }

    /**
     * Get the feature tiles
     *
     * @return feature tiles
     */
    public FeatureTiles getFeatureTiles() {
        return featureTiles;
    }

    /**
     * Get the feature info builder
     *
     * @return feature info builder
     */
    public FeatureInfoBuilder getFeatureInfoBuilder() {
        return featureInfoBuilder;
    }

    /**
     * Get the screen click percentage, between 0.0 and 1.0
     *
     * @return screen click percentage
     */
    public float getScreenClickPercentage() {
        return screenClickPercentage;
    }

    /**
     * Set the screen click percentage, between 0.0 and 1.0
     *
     * @param screenClickPercentage screen click percentage
     */
    public void setScreenClickPercentage(float screenClickPercentage) {
        if (screenClickPercentage < 0.0 || screenClickPercentage > 1.0) {
            throw new GeoPackageException("Screen click percentage must be a float between 0.0 and 1.0, not " + screenClickPercentage);
        }
        this.screenClickPercentage = screenClickPercentage;
    }

    /**
     * Determine if the the feature overlay is on for the current zoom level of the map at the location
     *
     * @param map    google map
     * @param latLng lat lon location
     * @return true if on
     * @since 1.2.6
     */
    public boolean isOnAtCurrentZoom(GoogleMap map, LatLng latLng) {
        float zoom = MapUtils.getCurrentZoom(map);
        boolean on = isOnAtCurrentZoom(zoom, latLng);
        return on;
    }

    /**
     * Determine if the feature overlay is on for the provided zoom level at the location
     *
     * @param zoom   zoom level
     * @param latLng lat lon location
     * @return true if on
     * @since 1.2.6
     */
    public boolean isOnAtCurrentZoom(double zoom, LatLng latLng) {

        Point point = new Point(latLng.longitude, latLng.latitude);
        TileGrid tileGrid = TileBoundingBoxUtils.getTileGridFromWGS84(point, (int) zoom);

        boolean on = boundedOverlay.hasTile((int) tileGrid.getMinX(), (int) tileGrid.getMinY(), (int) zoom);
        return on;
    }

    /**
     * Get the count of features in the tile at the lat lng coordinate and zoom level
     *
     * @param latLng lat lng location
     * @param zoom   zoom level
     * @return count
     */
    public long tileFeatureCount(LatLng latLng, double zoom) {
        int zoomValue = (int) zoom;
        long tileFeaturesCount = tileFeatureCount(latLng, zoomValue);
        return tileFeaturesCount;
    }

    /**
     * Get the count of features in the tile at the lat lng coordinate and zoom level
     *
     * @param latLng lat lng location
     * @param zoom   zoom level
     * @return count
     */
    public long tileFeatureCount(LatLng latLng, int zoom) {
        Point point = new Point(latLng.longitude, latLng.latitude);
        long tileFeaturesCount = tileFeatureCount(point, zoom);
        return tileFeaturesCount;
    }

    /**
     * Get the count of features in the tile at the point coordinate and zoom level
     *
     * @param point point location
     * @param zoom  zoom level
     * @return count
     */
    public long tileFeatureCount(Point point, double zoom) {
        int zoomValue = (int) zoom;
        long tileFeaturesCount = tileFeatureCount(point, zoomValue);
        return tileFeaturesCount;
    }

    /**
     * Get the count of features in the tile at the point coordinate and zoom level
     *
     * @param point point location
     * @param zoom  zoom level
     * @return count
     */
    public long tileFeatureCount(Point point, int zoom) {
        TileGrid tileGrid = TileBoundingBoxUtils.getTileGridFromWGS84(point, zoom);
        return featureTiles.queryIndexedFeaturesCount((int) tileGrid.getMinX(), (int) tileGrid.getMinY(), zoom);
    }

    /**
     * Determine if the provided count of features in the tile is more than the configured max features per tile
     *
     * @param tileFeaturesCount tile features count
     * @return true if more than the max features, false if less than or no configured max features
     */
    public boolean isMoreThanMaxFeatures(long tileFeaturesCount) {
        return featureTiles.getMaxFeaturesPerTile() != null && tileFeaturesCount > featureTiles.getMaxFeaturesPerTile().intValue();
    }

    /**
     * Build a bounding box using the location coordinate click location and map view bounds
     *
     * @param latLng    click location
     * @param mapBounds map bounds
     * @return bounding box
     * @since 1.2.7
     */
    public BoundingBox buildClickBoundingBox(LatLng latLng, BoundingBox mapBounds) {

        // Get the screen width and height a click occurs from a feature
        double width = TileBoundingBoxMapUtils.getLongitudeDistance(mapBounds) * screenClickPercentage;
        double height = TileBoundingBoxMapUtils.getLatitudeDistance(mapBounds) * screenClickPercentage;

        LatLng leftCoordinate = SphericalUtil.computeOffset(latLng, width, 270);
        LatLng upCoordinate = SphericalUtil.computeOffset(latLng, height, 0);
        LatLng rightCoordinate = SphericalUtil.computeOffset(latLng, width, 90);
        LatLng downCoordinate = SphericalUtil.computeOffset(latLng, height, 180);

        BoundingBox boundingBox = new BoundingBox(leftCoordinate.longitude, downCoordinate.latitude, rightCoordinate.longitude, upCoordinate.latitude);

        return boundingBox;
    }

    /**
     * Query for features in the WGS84 projected bounding box
     *
     * @param boundingBox query bounding box in WGS84 projection
     * @return feature index results, must be closed
     */
    public FeatureIndexResults queryFeatures(BoundingBox boundingBox) {
        return queryFeatures(boundingBox, null);
    }

    /**
     * Query for features in the WGS84 projected bounding box
     *
     * @param columns     columns
     * @param boundingBox query bounding box in WGS84 projection
     * @return feature index results, must be closed
     * @since 3.5.0
     */
    public FeatureIndexResults queryFeatures(String[] columns, BoundingBox boundingBox) {
        return queryFeatures(columns, boundingBox, null);
    }

    /**
     * Query for features in the bounding box
     *
     * @param boundingBox query bounding box
     * @param projection  bounding box projection
     * @return feature index results, must be closed
     */
    public FeatureIndexResults queryFeatures(BoundingBox boundingBox, Projection projection) {
        return queryFeatures(featureTiles.getFeatureDao().getColumnNames(), boundingBox, projection);
    }

    /**
     * Query for features in the bounding box
     *
     * @param columns     columns
     * @param boundingBox query bounding box
     * @param projection  bounding box projection
     * @return feature index results, must be closed
     * @since 3.5.0
     */
    public FeatureIndexResults queryFeatures(String[] columns, BoundingBox boundingBox, Projection projection) {

        if (projection == null) {
            projection = ProjectionFactory.getProjection(ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);
        }

        // Query for features
        FeatureIndexManager indexManager = featureTiles.getIndexManager();
        if (indexManager == null) {
            throw new GeoPackageException("Index Manager is not set on the Feature Tiles and is required to query indexed features");
        }
        FeatureIndexResults results = indexManager.query(columns, boundingBox, projection);
        return results;
    }

    /**
     * Check if the features are indexed
     *
     * @return true if indexed
     * @since 1.1.1
     */
    public boolean isIndexed() {
        return featureTiles.isIndexQuery();
    }

    /**
     * Get a max features information message
     *
     * @param tileFeaturesCount tile features count
     * @return max features message
     */
    public String buildMaxFeaturesInfoMessage(long tileFeaturesCount) {
        return featureInfoBuilder.getName() + "\n\t" + tileFeaturesCount + " features";
    }

    /**
     * Perform a query based upon the map click location and build a info message
     *
     * @param latLng location
     * @param view   view
     * @param map    Google Map
     * @return information message on what was clicked, or null
     */
    public String buildMapClickMessage(LatLng latLng, View view, GoogleMap map) {
        return buildMapClickMessage(latLng, view, map, null);
    }

    /**
     * Perform a query based upon the map click location and build a info message
     *
     * @param latLng     location
     * @param view       view
     * @param map        Google Map
     * @param projection desired geometry projection
     * @return information message on what was clicked, or null
     * @since 1.2.7
     */
    public String buildMapClickMessage(LatLng latLng, View view, GoogleMap map, Projection projection) {

        // Get the zoom level
        double zoom = MapUtils.getCurrentZoom(map);

        // Build a bounding box to represent the click location
        BoundingBox boundingBox = MapUtils.buildClickBoundingBox(latLng, view, map, screenClickPercentage);

        // Get the map click distance tolerance
        double tolerance = MapUtils.getToleranceDistance(latLng, view, map, screenClickPercentage);

        String message = buildMapClickMessage(latLng, zoom, boundingBox, tolerance, projection);

        return message;
    }

    /**
     * Perform a query based upon the map click location and build a info message
     *
     * @param latLng    location
     * @param zoom      current zoom level
     * @param mapBounds map view bounds
     * @param tolerance tolerance distance
     * @return information message on what was clicked, or nil
     * @since 2.0.0
     */
    public String buildMapClickMessageWithMapBounds(LatLng latLng, double zoom, BoundingBox mapBounds, double tolerance) {
        return buildMapClickMessageWithMapBounds(latLng, zoom, mapBounds, tolerance, null);
    }

    /**
     * Perform a query based upon the map click location and build a info message
     *
     * @param latLng     location
     * @param zoom       current zoom level
     * @param mapBounds  map view bounds
     * @param tolerance  tolerance distance
     * @param projection desired geometry projection
     * @return information message on what was clicked, or nil
     * @since 2.0.0
     */
    public String buildMapClickMessageWithMapBounds(LatLng latLng, double zoom, BoundingBox mapBounds, double tolerance, Projection projection) {

        // Build a bounding box to represent the click location
        BoundingBox boundingBox = buildClickBoundingBox(latLng, mapBounds);

        String message = buildMapClickMessage(latLng, zoom, boundingBox, tolerance, projection);

        return message;
    }

    /**
     * Perform a query based upon the map click location and build a info message
     *
     * @param latLng      location
     * @param zoom        current zoom level
     * @param boundingBox click bounding box
     * @param tolerance   tolerance distance
     * @param projection  desired geometry projection
     * @return information message on what was clicked, or null
     */
    private String buildMapClickMessage(LatLng latLng, double zoom, BoundingBox boundingBox, double tolerance, Projection projection) {
        String message = null;

        // Verify the features are indexed and we are getting information
        if (isIndexed() && (maxFeaturesInfo || featuresInfo)) {

            if (isOnAtCurrentZoom(zoom, latLng)) {

                // Get the number of features in the tile location
                long tileFeatureCount = tileFeatureCount(latLng, zoom);

                // If more than a configured max features to draw
                if (isMoreThanMaxFeatures(tileFeatureCount)) {

                    // Build the max features message
                    if (maxFeaturesInfo) {
                        message = buildMaxFeaturesInfoMessage(tileFeatureCount);
                    }

                }
                // Else, query for the features near the click
                else if (featuresInfo) {

                    // Query for results and build the message
                    FeatureIndexResults results = queryFeatures(boundingBox, projection);
                    message = featureInfoBuilder.buildResultsInfoMessageAndClose(results, tolerance, latLng, projection);

                }

            }
        }

        return message;
    }

    /**
     * Perform a query based upon the map click location and build feature table data
     *
     * @param latLng location
     * @param view   view
     * @param map    Google Map
     * @return table data on what was clicked, or null
     * @since 1.2.7
     */
    public FeatureTableData buildMapClickTableData(LatLng latLng, View view, GoogleMap map) {
        return buildMapClickTableData(latLng, view, map, null);
    }

    /**
     * Perform a query based upon the map click location and build feature table data
     *
     * @param latLng     location
     * @param view       view
     * @param map        Google Map
     * @param projection desired geometry projection
     * @return table data on what was clicked, or null
     * @since 1.2.7
     */
    public FeatureTableData buildMapClickTableData(LatLng latLng, View view, GoogleMap map, Projection projection) {

        // Get the zoom level
        double zoom = MapUtils.getCurrentZoom(map);

        // Build a bounding box to represent the click location
        BoundingBox boundingBox = MapUtils.buildClickBoundingBox(latLng, view, map, screenClickPercentage);

        // Get the map click distance tolerance
        double tolerance = MapUtils.getToleranceDistance(latLng, view, map, screenClickPercentage);

        FeatureTableData tableData = buildMapClickTableData(latLng, zoom, boundingBox, tolerance, projection);

        return tableData;
    }

    /**
     * Perform a query based upon the map click location and build feature table data
     *
     * @param latLng    location
     * @param zoom      current zoom level
     * @param mapBounds map view bounds
     * @param tolerance distance tolerance
     * @return table data on what was clicked, or null
     * @since 2.0.0
     */
    public FeatureTableData buildMapClickTableDataWithMapBounds(LatLng latLng, double zoom, BoundingBox mapBounds, double tolerance) {
        return buildMapClickTableDataWithMapBounds(latLng, zoom, mapBounds, tolerance, null);
    }

    /**
     * Perform a query based upon the map click location and build feature table data
     *
     * @param latLng     location
     * @param zoom       current zoom level
     * @param mapBounds  map view bounds
     * @param tolerance  distance tolerance
     * @param projection desired geometry projection
     * @return table data on what was clicked, or null
     * @since 2.0.0
     */
    public FeatureTableData buildMapClickTableDataWithMapBounds(LatLng latLng, double zoom, BoundingBox mapBounds, double tolerance, Projection projection) {

        // Build a bounding box to represent the click location
        BoundingBox boundingBox = buildClickBoundingBox(latLng, mapBounds);

        FeatureTableData tableData = buildMapClickTableData(latLng, zoom, boundingBox, tolerance, projection);

        return tableData;
    }

    /**
     * Perform a query based upon the map click location and build feature table data
     *
     * @param latLng      location
     * @param zoom        current zoom level
     * @param boundingBox click bounding box
     * @param tolerance   distance tolerance
     * @param projection  desired geometry projection
     * @return table data on what was clicked, or null
     */
    private FeatureTableData buildMapClickTableData(LatLng latLng, double zoom, BoundingBox boundingBox, double tolerance, Projection projection) {
        FeatureTableData tableData = null;

        // Verify the features are indexed and we are getting information
        if (isIndexed() && (maxFeaturesInfo || featuresInfo)) {

            if (isOnAtCurrentZoom(zoom, latLng)) {

                // Get the number of features in the tile location
                long tileFeatureCount = tileFeatureCount(latLng, zoom);

                // If more than a configured max features to draw
                if (isMoreThanMaxFeatures(tileFeatureCount)) {

                    // Build the max features message
                    if (maxFeaturesInfo) {
                        tableData = new FeatureTableData(featureTiles.getFeatureDao().getTableName(), tileFeatureCount);
                    }

                }
                // Else, query for the features near the click
                else if (featuresInfo) {

                    // Query for results and build the message
                    FeatureIndexResults results = queryFeatures(boundingBox, projection);
                    tableData = featureInfoBuilder.buildTableDataAndClose(results, tolerance, latLng, projection);
                }

            }
        }

        return tableData;
    }

}

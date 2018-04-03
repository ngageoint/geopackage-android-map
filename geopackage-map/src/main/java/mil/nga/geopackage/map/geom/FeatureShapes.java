package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.map.MapUtils;
import mil.nga.geopackage.projection.ProjectionConstants;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.sf.GeometryType;

/**
 * Maintains a collection of feature map shapes by database, table name, and feature id
 *
 * @author osbornb
 * @since 2.0.0
 */
public class FeatureShapes {

    /**
     * Mapping between databases, feature tables, feature ids, and shapes
     */
    private Map<String, Map<String, Map<Long, List<GoogleMapShape>>>> databases = new HashMap<>();

    /**
     * Constructor
     */
    public FeatureShapes() {

    }

    /**
     * Get the mapping between databases and tables
     *
     * @return databases to tables mapping
     */
    public Map<String, Map<String, Map<Long, List<GoogleMapShape>>>> getDatabases() {
        return databases;
    }

    /**
     * Get the databases count
     *
     * @return databases count
     */
    public int getDatabasesCount() {
        return databases.size();
    }

    /**
     * Get the mapping between tables and feature ids for the database
     *
     * @param database GeoPackage database
     * @return tables to features ids mapping
     */
    public Map<String, Map<Long, List<GoogleMapShape>>> getTables(String database) {

        Map<String, Map<Long, List<GoogleMapShape>>> tables = databases.get(database);
        if (tables == null) {
            tables = new HashMap<>();
            databases.put(database, tables);
        }
        return tables;
    }

    /**
     * Get the tables count for the database
     *
     * @param database GeoPackage database
     * @return tables count
     */
    public int getTablesCount(String database) {
        return getTables(database).size();
    }

    /**
     * Get the mapping between feature ids and map shapes for the database and table
     *
     * @param database GeoPackage database
     * @param table    table name
     * @return feature ids to map shapes mapping
     */
    public Map<Long, List<GoogleMapShape>> getFeatureIds(String database, String table) {
        Map<String, Map<Long, List<GoogleMapShape>>> tables = getTables(database);
        Map<Long, List<GoogleMapShape>> featureIds = getFeatureIds(tables, table);
        return featureIds;
    }

    /**
     * Get the feature ids count for the database and table
     *
     * @param database GeoPackage database
     * @param table    table name
     * @return feature ids count
     */
    public int getFeatureIdsCount(String database, String table) {
        return getFeatureIds(database, table).size();
    }

    /**
     * Get the feature ids and map shapes for the tables and table
     *
     * @param tables tables
     * @param table  table name
     * @return feature ids to map shapes mapping
     */
    private Map<Long, List<GoogleMapShape>> getFeatureIds(Map<String, Map<Long, List<GoogleMapShape>>> tables, String table) {

        Map<Long, List<GoogleMapShape>> featureIds = tables.get(table);
        if (featureIds == null) {
            featureIds = new HashMap<>();
            tables.put(table, featureIds);
        }
        return featureIds;
    }

    /**
     * Get the map shapes for the database, table, and feature id
     *
     * @param database  GeoPackage database
     * @param table     table name
     * @param featureId feature id
     * @return map shapes
     */
    public List<GoogleMapShape> getShapes(String database, String table, long featureId) {
        Map<Long, List<GoogleMapShape>> featureIds = getFeatureIds(database, table);
        List<GoogleMapShape> shapes = getShapes(featureIds, featureId);
        return shapes;
    }

    /**
     * Get the map shapes count for the database, table, and feature id
     *
     * @param database  GeoPackage database
     * @param table     table name
     * @param featureId feature id
     * @return map shapes count
     */
    public int getShapesCount(String database, String table, long featureId) {
        return getShapes(database, table, featureId).size();
    }

    /**
     * Get the map shapes for the feature ids and feature id
     *
     * @param featureIds feature ids
     * @param featureId  feature id
     * @return map shapes
     */
    private List<GoogleMapShape> getShapes(Map<Long, List<GoogleMapShape>> featureIds, long featureId) {

        List<GoogleMapShape> shapes = featureIds.get(featureId);
        if (shapes == null) {
            shapes = new ArrayList<>();
            featureIds.put(featureId, shapes);
        }
        return shapes;
    }

    /**
     * Add a map shape with the feature id, database, and table
     *
     * @param mapShape  map shape
     * @param featureId feature id
     * @param database  GeoPackage database
     * @param table     table name
     */
    public void addMapShape(GoogleMapShape mapShape, long featureId, String database, String table) {
        List<GoogleMapShape> shapes = getShapes(database, table, featureId);
        shapes.add(mapShape);
    }

    /**
     * Check if map shapes exist for the feature id, database, and table
     *
     * @param featureId feature id
     * @param database  GeoPackage database
     * @param table     table name
     * @return true if exists
     */
    public boolean exists(long featureId, String database, String table) {
        boolean exists = false;
        Map<String, Map<Long, List<GoogleMapShape>>> tables = databases.get(database);
        if (tables != null) {

            Map<Long, List<GoogleMapShape>> featureIds = tables.get(table);
            if (featureIds != null) {
                List<GoogleMapShape> shapes = featureIds.get(featureId);
                exists = shapes != null && !shapes.isEmpty();
            }
        }
        return exists;
    }

    /**
     * Remove all map shapes from the map
     *
     * @return count of removed features
     */
    public int removeShapes() {

        int count = 0;
        for (String database : databases.keySet()) {
            count += removeShapes(database);
        }

        clear();

        return count;
    }

    /**
     * Remove all map shapes in the database from the map
     *
     * @param database GeoPackage database
     * @return count of removed features
     */
    public int removeShapes(String database) {

        int count = 0;

        Map<String, Map<Long, List<GoogleMapShape>>> tables = getTables(database);

        if (tables != null) {

            for (String table : tables.keySet()) {

                count += removeShapes(database, table);
            }

            tables.clear();
        }

        return count;
    }

    /**
     * Remove all map shapes in the database and table from the map
     *
     * @param database GeoPackage database
     * @param table    table name
     * @return count of removed features
     */
    public int removeShapes(String database, String table) {

        int count = 0;

        Map<Long, List<GoogleMapShape>> featureIds = getFeatureIds(database, table);

        if (featureIds != null) {

            for (long featureId : featureIds.keySet()) {

                List<GoogleMapShape> mapShapes = getShapes(featureIds, featureId);

                if (mapShapes != null) {

                    for (GoogleMapShape mapShape : mapShapes) {
                        mapShape.remove();
                    }
                }
                count++;
            }

            featureIds.clear();
        }

        return count;
    }

    /**
     * Remove all map shapes that are not visible in the map
     *
     * @param map map
     * @return count of removed features
     */
    public int removeShapesNotWithinMap(GoogleMap map) {

        int count = 0;

        BoundingBox boundingBox = MapUtils.getBoundingBox(map);

        for (String database : databases.keySet()) {
            count += removeShapesNotWithinMap(boundingBox, database);
        }

        return count;
    }

    /**
     * Remove all map shapes int the database that are not visible in the map
     *
     * @param map      map
     * @param database GeoPackage database
     * @return count of removed features
     */
    public int removeShapesNotWithinMap(GoogleMap map, String database) {

        BoundingBox boundingBox = MapUtils.getBoundingBox(map);

        int count = removeShapesNotWithinMap(boundingBox, database);

        return count;
    }

    /**
     * Remove all map shapes in the database that are not visible in the bounding box
     *
     * @param boundingBox bounding box
     * @param database    GeoPackage database
     * @return count of removed features
     */
    private int removeShapesNotWithinMap(BoundingBox boundingBox, String database) {

        int count = 0;

        Map<String, Map<Long, List<GoogleMapShape>>> tables = getTables(database);

        if (tables != null) {

            for (String table : tables.keySet()) {
                count += removeShapesNotWithinMap(boundingBox, database, table);
            }
        }

        return count;
    }

    /**
     * Remove all map shapes in the database and table that are not visible in the map
     *
     * @param map      map
     * @param database GeoPackage database
     * @param table    table name
     * @return count of removed features
     */
    public int removeShapesNotWithinMap(GoogleMap map, String database, String table) {

        BoundingBox boundingBox = MapUtils.getBoundingBox(map);

        int count = removeShapesNotWithinMap(boundingBox, database, table);

        return count;
    }

    /**
     * Remove all map shapes in the database and table that are not visible in the bounding box
     *
     * @param boundingBox bounding box
     * @param database    GeoPackage database
     * @return count of removed features
     */
    public int removeShapesNotWithinMap(BoundingBox boundingBox, String database, String table) {

        int count = 0;

        Map<Long, List<GoogleMapShape>> featureIds = getFeatureIds(database, table);

        if (featureIds != null) {

            List<Long> deleteFeatureIds = new ArrayList<>();

            for (long featureId : featureIds.keySet()) {

                List<GoogleMapShape> mapShapes = getShapes(featureIds, featureId);

                if (mapShapes != null) {

                    boolean delete = true;
                    for (GoogleMapShape mapShape : mapShapes) {
                        BoundingBox mapShapeBoundingBox = mapShape.boundingBox();
                        boolean allowEmpty = mapShape.getGeometryType() == GeometryType.POINT;
                        if (TileBoundingBoxUtils.overlap(mapShapeBoundingBox, boundingBox, ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH, allowEmpty) != null) {
                            delete = false;
                            break;
                        }
                    }
                    if (delete) {
                        deleteFeatureIds.add(featureId);
                    }
                }
            }

            for (long deleteFeatureId : deleteFeatureIds) {

                List<GoogleMapShape> mapShapes = getShapes(featureIds, deleteFeatureId);

                if (mapShapes != null) {

                    for (GoogleMapShape mapShape : mapShapes) {
                        mapShape.remove();
                    }

                    featureIds.remove(deleteFeatureId);
                }
                count++;
            }
        }

        return count;
    }

    /**
     * Clear
     */
    public void clear() {
        databases.clear();
    }

}

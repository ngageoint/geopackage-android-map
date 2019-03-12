package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.map.MapUtils;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.sf.GeometryType;
import mil.nga.sf.proj.ProjectionConstants;

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
    private Map<String, Map<String, Map<Long, FeatureShape>>> databases = new HashMap<>();

    /**
     * Constructor
     */
    public FeatureShapes() {

    }

    /**
     * Get the mapping between databases and tables
     *
     * @return databases to tables mapping
     * @since 3.2.0
     */
    public Map<String, Map<String, Map<Long, FeatureShape>>> getDatabases() {
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
     * @since 3.2.0
     */
    public Map<String, Map<Long, FeatureShape>> getTables(String database) {

        Map<String, Map<Long, FeatureShape>> tables = databases.get(database);
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
     * @since 3.2.0
     */
    public Map<Long, FeatureShape> getFeatureIds(String database, String table) {
        Map<String, Map<Long, FeatureShape>> tables = getTables(database);
        Map<Long, FeatureShape> featureIds = getFeatureIds(tables, table);
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
    private Map<Long, FeatureShape> getFeatureIds(Map<String, Map<Long, FeatureShape>> tables, String table) {

        Map<Long, FeatureShape> featureIds = tables.get(table);
        if (featureIds == null) {
            featureIds = new HashMap<>();
            tables.put(table, featureIds);
        }
        return featureIds;
    }

    /**
     * Get the feature shape for the database, table, and feature id
     *
     * @param database  GeoPackage database
     * @param table     table name
     * @param featureId feature id
     * @return feature shape
     * @since 3.2.0
     */
    public FeatureShape getFeatureShape(String database, String table, long featureId) {
        Map<Long, FeatureShape> featureIds = getFeatureIds(database, table);
        FeatureShape featureShape = getFeatureShape(featureIds, featureId);
        return featureShape;
    }

    /**
     * Get the feature shape count for the database, table, and feature id
     *
     * @param database  GeoPackage database
     * @param table     table name
     * @param featureId feature id
     * @return map shapes count
     * @since 3.2.0
     */
    public int getFeatureShapeCount(String database, String table, long featureId) {
        return getFeatureShape(database, table, featureId).count();
    }

    /**
     * Get the map shapes for the feature ids and feature id
     *
     * @param featureIds feature ids
     * @param featureId  feature id
     * @return feature shape
     */
    private FeatureShape getFeatureShape(Map<Long, FeatureShape> featureIds, long featureId) {

        FeatureShape featureShape = featureIds.get(featureId);
        if (featureShape == null) {
            featureShape = new FeatureShape(featureId);
            featureIds.put(featureId, featureShape);
        }
        return featureShape;
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
        FeatureShape featureShape = getFeatureShape(database, table, featureId);
        featureShape.addShape(mapShape);
    }

    /**
     * Add a map metadata shape with the feature id, database, and table
     *
     * @param mapShape  map metadata shape
     * @param featureId feature id
     * @param database  GeoPackage database
     * @param table     table name
     * @since 3.2.0
     */
    public void addMapMetadataShape(GoogleMapShape mapShape, long featureId, String database, String table) {
        FeatureShape featureShape = getFeatureShape(database, table, featureId);
        featureShape.addMetadataShape(mapShape);
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
        Map<String, Map<Long, FeatureShape>> tables = databases.get(database);
        if (tables != null) {

            Map<Long, FeatureShape> featureIds = tables.get(table);
            if (featureIds != null) {
                FeatureShape shapes = featureIds.get(featureId);
                exists = shapes != null && shapes.hasShapes();
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
        return removeShapesWithExclusions(null);
    }

    /**
     * Remove all map shapes from the map, excluding shapes with the excluded type
     *
     * @param excludedType Google Map Shape Type to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesWithExclusion(GoogleMapShapeType excludedType) {
        Set<GoogleMapShapeType> excludedTypes = new HashSet<>();
        excludedTypes.add(excludedType);
        return removeShapesWithExclusions(excludedTypes);
    }

    /**
     * Remove all map shapes from the map, excluding shapes with the excluded types
     *
     * @param excludedTypes Google Map Shape Types to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesExcluding(GoogleMapShapeType... excludedTypes) {
        Set<GoogleMapShapeType> excluded = new HashSet<>();
        for (GoogleMapShapeType excludedType : excludedTypes) {
            excluded.add(excludedType);
        }
        return removeShapesWithExclusions(excluded);
    }

    /**
     * Remove all map shapes from the map, excluding shapes with the excluded types
     *
     * @param excludedTypes Google Map Shape Types to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesWithExclusions(Set<GoogleMapShapeType> excludedTypes) {

        int count = 0;
        Iterator<String> iterator = databases.keySet().iterator();
        while (iterator.hasNext()) {
            String database = iterator.next();
            count += removeShapesWithExclusions(database, excludedTypes);

            if (getTablesCount(database) <= 0) {
                iterator.remove();
            }
        }

        return count;
    }

    /**
     * Remove all map shapes in the database from the map
     *
     * @param database GeoPackage database
     * @return count of removed features
     */
    public int removeShapes(String database) {
        return removeShapesWithExclusions(database, null);
    }

    /**
     * Remove all map shapes in the database from the map, excluding shapes with the excluded type
     *
     * @param database     GeoPackage database
     * @param excludedType Google Map Shape Type to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesWithExclusion(String database, GoogleMapShapeType excludedType) {
        Set<GoogleMapShapeType> excludedTypes = new HashSet<>();
        excludedTypes.add(excludedType);
        return removeShapesWithExclusions(database, excludedTypes);
    }

    /**
     * Remove all map shapes in the database from the map, excluding shapes with the excluded types
     *
     * @param database      GeoPackage database
     * @param excludedTypes Google Map Shape Types to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesExcluding(String database, GoogleMapShapeType... excludedTypes) {
        Set<GoogleMapShapeType> excluded = new HashSet<>();
        for (GoogleMapShapeType excludedType : excludedTypes) {
            excluded.add(excludedType);
        }
        return removeShapesWithExclusions(database, excluded);
    }

    /**
     * Remove all map shapes in the database from the map, excluding shapes with the excluded types
     *
     * @param database      GeoPackage database
     * @param excludedTypes Google Map Shape Types to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesWithExclusions(String database, Set<GoogleMapShapeType> excludedTypes) {

        int count = 0;

        Map<String, Map<Long, FeatureShape>> tables = getTables(database);

        if (tables != null) {

            Iterator<String> iterator = tables.keySet().iterator();
            while (iterator.hasNext()) {
                String table = iterator.next();

                count += removeShapesWithExclusions(database, table, excludedTypes);

                if (getFeatureIdsCount(database, table) <= 0) {
                    iterator.remove();
                }
            }

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
        return removeShapesWithExclusions(database, table, null);
    }

    /**
     * Remove all map shapes in the database and table from the map, excluding shapes with the excluded type
     *
     * @param database     GeoPackage database
     * @param table        table name
     * @param excludedType Google Map Shape Type to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesWithExclusion(String database, String table, GoogleMapShapeType excludedType) {
        Set<GoogleMapShapeType> excludedTypes = new HashSet<>();
        excludedTypes.add(excludedType);
        return removeShapesWithExclusions(database, table, excludedTypes);
    }

    /**
     * Remove all map shapes in the database and table from the map, excluding shapes with the excluded types
     *
     * @param database      GeoPackage database
     * @param table         table name
     * @param excludedTypes Google Map Shape Types to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesExcluding(String database, String table, GoogleMapShapeType... excludedTypes) {
        Set<GoogleMapShapeType> excluded = new HashSet<>();
        for (GoogleMapShapeType excludedType : excludedTypes) {
            excluded.add(excludedType);
        }
        return removeShapesWithExclusions(database, table, excluded);
    }

    /**
     * Remove all map shapes in the database and table from the map, excluding shapes with the excluded types
     *
     * @param database      GeoPackage database
     * @param table         table name
     * @param excludedTypes Google Map Shape Types to exclude from map removal
     * @return count of removed features
     * @since 3.2.0
     */
    public int removeShapesWithExclusions(String database, String table, Set<GoogleMapShapeType> excludedTypes) {

        int count = 0;

        Map<Long, FeatureShape> featureIds = getFeatureIds(database, table);

        if (featureIds != null) {

            Iterator<Long> iterator = featureIds.keySet().iterator();
            while (iterator.hasNext()) {
                long featureId = iterator.next();

                FeatureShape featureShape = getFeatureShape(featureIds, featureId);

                if (featureShape != null) {

                    Iterator<GoogleMapShape> shapeIterator = featureShape.getShapes().iterator();
                    while (shapeIterator.hasNext()) {
                        GoogleMapShape mapShape = shapeIterator.next();
                        if (excludedTypes == null || !excludedTypes.contains(mapShape.getShapeType())) {
                            mapShape.remove();
                            shapeIterator.remove();
                        }
                    }

                }

                if (featureShape == null || !featureShape.hasShapes()) {
                    if(featureShape != null) {
                        featureShape.removeMetadataShapes();
                    }
                    iterator.remove();
                    count++;
                }

            }

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
     * @since 3.2.0
     */
    public int removeShapesNotWithinMap(BoundingBox boundingBox, String database) {

        int count = 0;

        Map<String, Map<Long, FeatureShape>> tables = getTables(database);

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

        Map<Long, FeatureShape> featureIds = getFeatureIds(database, table);

        if (featureIds != null) {

            List<Long> deleteFeatureIds = new ArrayList<>();

            for (long featureId : featureIds.keySet()) {

                FeatureShape featureShape = getFeatureShape(featureIds, featureId);

                if (featureShape != null) {

                    boolean delete = true;
                    for (GoogleMapShape mapShape : featureShape.getShapes()) {
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

                FeatureShape featureShape = getFeatureShape(featureIds, deleteFeatureId);

                if (featureShape != null) {
                    featureShape.remove();
                    featureIds.remove(deleteFeatureId);
                }
                count++;
            }
        }

        return count;
    }

    /**
     * Remove the feature shape from the database and table
     *
     * @param database  GeoPackage database
     * @param table     table name
     * @param featureId feature id
     * @return true if removed
     * @since 3.2.0
     */
    public boolean removeFeatureShape(String database, String table, long featureId) {

        boolean removed = false;

        Map<Long, FeatureShape> featureIds = getFeatureIds(database, table);
        if (featureIds != null) {
            FeatureShape featureShape = featureIds.remove(featureId);
            if (featureShape != null) {
                featureShape.remove();
                removed = true;
            }
        }

        return removed;
    }

    /**
     * Clear
     */
    public void clear() {
        databases.clear();
    }

}

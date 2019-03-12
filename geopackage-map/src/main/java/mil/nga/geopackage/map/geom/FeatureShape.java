package mil.nga.geopackage.map.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Map shapes and metadata shapes for a single feature shape
 *
 * @author osbornb
 * @since 3.2.0
 */
public class FeatureShape {

    /**
     * Feature id
     */
    private final long featureId;

    /**
     * Map Shapes
     */
    private List<GoogleMapShape> shapes = new ArrayList<>();

    /**
     * Metadata Shapes
     */
    private List<GoogleMapShape> metadataShapes = new ArrayList<>();

    /**
     * Constructor
     *
     * @param featureId feature id
     */
    public FeatureShape(long featureId) {
        this.featureId = featureId;
    }

    /**
     * Get feature id
     *
     * @return feature id
     */
    public long getFeatureId() {
        return featureId;
    }

    /**
     * Get the map shapes
     *
     * @return map shapes
     */
    public List<GoogleMapShape> getShapes() {
        return shapes;
    }

    /**
     * Get the map metadata shapes
     *
     * @return map metadata shapes
     */
    public List<GoogleMapShape> getMetadataShapes() {
        return metadataShapes;
    }

    /**
     * Add a map shape
     *
     * @param shape map shape
     */
    public void addShape(GoogleMapShape shape) {
        shapes.add(shape);
    }

    /**
     * Add a metadata map shape
     *
     * @param shape metadata map shape
     */
    public void addMetadataShape(GoogleMapShape shape) {
        metadataShapes.add(shape);
    }

    /**
     * Get the count of map shapes
     *
     * @return map shapes count
     */
    public int count() {
        return shapes.size();
    }

    /**
     * Determine if there are map shapes
     *
     * @return true if has map shapes
     */
    public boolean hasShapes() {
        return !shapes.isEmpty();
    }

    /**
     * Get the count of map metadata shapes
     *
     * @return map metadata shapes count
     */
    public int countMetadataShapes() {
        return metadataShapes.size();
    }

    /**
     * Determine if there are map metadata shapes
     *
     * @return true if has map metadata shapes
     */
    public boolean hasMetadataShapes() {
        return !metadataShapes.isEmpty();
    }

    /**
     * Remove all map shapes and metadata map shapes from the map and feature shape
     */
    public void remove() {
        removeMetadataShapes();
        removeShapes();
    }

    /**
     * Remove the map shapes from the map and feature shape
     */
    public void removeShapes() {
        for (GoogleMapShape shape : shapes) {
            shape.remove();
        }
        shapes.clear();
    }

    /**
     * Remove the map metadata shapes from the map and feature shape
     */
    public void removeMetadataShapes() {
        for (GoogleMapShape metadataShape : metadataShapes) {
            metadataShape.remove();
        }
        metadataShapes.clear();
    }

}

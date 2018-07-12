package mil.nga.geopackage.map.geom;

/**
 * Shape markers interface for handling marker changes on shapes that have
 * children
 *
 * @author osbornb
 */
public interface ShapeWithChildrenMarkers extends ShapeMarkers {

    /**
     * Create a child shape
     *
     * @return shape markers
     */
    public ShapeMarkers createChild();

}

package mil.nga.geopackage.map.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Polygon Markers object
 *
 * @author osbornb
 */
public class MultiPolygonMarkers {

    /**
     * List of Polygon Markers
     */
    private List<PolygonMarkers> polygonMarkers = new ArrayList<PolygonMarkers>();

    /**
     * Add a Polygon Marker
     *
     * @param polygonMarker polygon marker
     */
    public void add(PolygonMarkers polygonMarker) {
        polygonMarkers.add(polygonMarker);
    }

    /**
     * Get the polygon markers
     *
     * @return list of polygon markers
     */
    public List<PolygonMarkers> getPolygonMarkers() {
        return polygonMarkers;
    }

    /**
     * Set the polygon markers
     *
     * @param polygonMarkers polygon markers
     */
    public void setPolygonMarkers(List<PolygonMarkers> polygonMarkers) {
        this.polygonMarkers = polygonMarkers;
    }

    /**
     * Update based upon marker changes
     */
    public void update() {
        for (PolygonMarkers polygonMarker : polygonMarkers) {
            polygonMarker.update();
        }
    }

    /**
     * Remove the polygon and points
     */
    public void remove() {
        for (PolygonMarkers polygonMarker : polygonMarkers) {
            polygonMarker.remove();
        }
    }

    /**
     * Set visibility on the map
     *
     * @param visible visibility flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible) {
        for (PolygonMarkers polygonMarker : polygonMarkers) {
            polygonMarker.setVisible(visible);
        }
    }

    /**
     * Set the z index
     *
     * @param zIndex z index
     * @since 2.0.1
     */
    public void setZIndex(float zIndex) {
        for (PolygonMarkers polygonMarker : polygonMarkers) {
            polygonMarker.setZIndex(zIndex);
        }
    }

    /**
     * Is it valid
     *
     * @return true if valid
     */
    public boolean isValid() {
        boolean valid = true;
        for (PolygonMarkers polygon : polygonMarkers) {
            valid = polygon.isValid();
            if (!valid) {
                break;
            }
        }
        return valid;
    }

    /**
     * Is it deleted
     *
     * @return true if deleted
     */
    public boolean isDeleted() {
        boolean deleted = true;
        for (PolygonMarkers polygon : polygonMarkers) {
            deleted = polygon.isDeleted();
            if (!deleted) {
                break;
            }
        }
        return deleted;
    }

}

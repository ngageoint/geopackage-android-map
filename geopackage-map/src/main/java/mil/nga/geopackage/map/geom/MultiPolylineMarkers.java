package mil.nga.geopackage.map.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Polyline Markers object
 *
 * @author osbornb
 */
public class MultiPolylineMarkers {

    /**
     * List of polyline markers
     */
    private List<PolylineMarkers> polylineMarkers = new ArrayList<PolylineMarkers>();

    /**
     * Add a polyline marker
     *
     * @param polylineMarker polyline marker
     */
    public void add(PolylineMarkers polylineMarker) {
        polylineMarkers.add(polylineMarker);
    }

    /**
     * Get the polyline markers
     *
     * @return polyline markers
     */
    public List<PolylineMarkers> getPolylineMarkers() {
        return polylineMarkers;
    }

    /**
     * Set the polyline markers
     *
     * @param polylineMarkers polyline markers
     */
    public void setPolylineMarkers(List<PolylineMarkers> polylineMarkers) {
        this.polylineMarkers = polylineMarkers;
    }

    /**
     * Update based upon marker changes
     */
    public void update() {
        for (PolylineMarkers polylineMarker : polylineMarkers) {
            polylineMarker.update();
        }
    }

    /**
     * Remove the polyline and points
     */
    public void remove() {
        for (PolylineMarkers polylineMarker : polylineMarkers) {
            polylineMarker.remove();
        }
    }

    /**
     * Set visibility on the map
     *
     * @param visible visibility flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible) {
        for (PolylineMarkers polylineMarker : polylineMarkers) {
            polylineMarker.setVisible(visible);
        }
    }

    /**
     * Set the z index
     *
     * @param zIndex z index
     * @since 2.0.1
     */
    public void setZIndex(float zIndex) {
        for (PolylineMarkers polylineMarker : polylineMarkers) {
            polylineMarker.setZIndex(zIndex);
        }
    }

    /**
     * Is it valid
     *
     * @return true if valid
     */
    public boolean isValid() {
        boolean valid = true;
        for (PolylineMarkers polyline : polylineMarkers) {
            valid = polyline.isValid();
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
        for (PolylineMarkers polyline : polylineMarkers) {
            deleted = polyline.isDeleted();
            if (!deleted) {
                break;
            }
        }
        return deleted;
    }

}

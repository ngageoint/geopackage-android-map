package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Polyline object
 *
 * @author osbornb
 */
public class MultiPolyline {

    /**
     * List of Polylines
     */
    private List<Polyline> polylines = new ArrayList<Polyline>();

    /**
     * Add a polyline
     *
     * @param polyline polyline
     */
    public void add(Polyline polyline) {
        polylines.add(polyline);
    }

    /**
     * Get the polylines
     *
     * @return list of polylines
     */
    public List<Polyline> getPolylines() {
        return polylines;
    }

    /**
     * Set the polylines
     *
     * @param polylines list of polylines
     */
    public void setPolylines(List<Polyline> polylines) {
        this.polylines = polylines;
    }

    /**
     * Remove from the map
     */
    public void remove() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
    }

    /**
     * Set visibility on the map
     *
     * @param visible visibility flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible) {
        for (Polyline polyline : polylines) {
            polyline.setVisible(visible);
        }
    }

    /**
     * Set the z index
     *
     * @param zIndex z index
     * @since 2.0.1
     */
    public void setZIndex(float zIndex) {
        for (Polyline polyline : polylines) {
            polyline.setZIndex(zIndex);
        }
    }

}

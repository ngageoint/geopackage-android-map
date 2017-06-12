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

    private List<Polyline> polylines = new ArrayList<Polyline>();

    public void add(Polyline polyline) {
        polylines.add(polyline);
    }

    public List<Polyline> getPolylines() {
        return polylines;
    }

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

}

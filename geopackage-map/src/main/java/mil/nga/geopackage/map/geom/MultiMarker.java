package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Marker object
 *
 * @author osbornb
 */
public class MultiMarker implements ShapeMarkers {

    private List<Marker> markers = new ArrayList<Marker>();

    public void add(Marker marker) {
        markers.add(marker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    /**
     * Remove from the map
     */
    public void remove() {
        for (Marker marker : markers) {
            marker.remove();
        }
    }

    /**
     * Set visibility on the map
     *
     * @param visible visibility flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible) {
        for (Marker marker : markers) {
            marker.setVisible(visible);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Marker marker) {
        if (markers.remove(marker)) {
            marker.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNew(Marker marker) {
        add(marker);
    }

}

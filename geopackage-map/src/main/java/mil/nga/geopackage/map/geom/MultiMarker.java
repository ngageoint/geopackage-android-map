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

    /**
     * List of Markers
     */
    private List<Marker> markers = new ArrayList<Marker>();

    /**
     * Add a Marker
     *
     * @param marker marker
     */
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

    /**
     * Set the Markers
     *
     * @param markers list of Markers
     */
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
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        setVisibleMarkers(visible);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisibleMarkers(boolean visible) {
        for (Marker marker : markers) {
            marker.setVisible(visible);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setZIndex(float zIndex) {
        for (Marker marker : markers) {
            marker.setZIndex(zIndex);
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

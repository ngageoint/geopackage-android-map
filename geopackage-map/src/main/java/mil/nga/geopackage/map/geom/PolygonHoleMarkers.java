package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Polygon Hole with Markers object
 *
 * @author osbornb
 */
public class PolygonHoleMarkers implements ShapeMarkers {

    /**
     * Parent polygon
     */
    final private PolygonMarkers parentPolygon;

    /**
     * List of Markers
     */
    private List<Marker> markers = new ArrayList<Marker>();

    /**
     * Constructor
     *
     * @param polygonMarkers polygon markers
     */
    public PolygonHoleMarkers(PolygonMarkers polygonMarkers) {
        parentPolygon = polygonMarkers;
    }

    /**
     * Add a marker
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
     * Set the markers
     *
     * @param markers markers
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
     * Is it valid
     *
     * @return true if valid
     */
    public boolean isValid() {
        return markers.isEmpty() || markers.size() >= 3;
    }

    /**
     * Is it deleted
     *
     * @return true if deleted
     */
    public boolean isDeleted() {
        return markers.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Marker marker) {
        if (markers.remove(marker)) {
            marker.remove();
            parentPolygon.update();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNew(Marker marker) {
        GoogleMapShapeMarkers.addMarkerAsPolygon(marker, markers);
    }

}

package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Polyline with Markers object
 *
 * @author osbornb
 */
public class PolylineMarkers implements ShapeMarkers {

    /**
     * Shape converter
     */
    private final GoogleMapShapeConverter converter;

    /**
     * Polyline
     */
    private Polyline polyline;

    /**
     * List of Markers
     */
    private List<Marker> markers = new ArrayList<Marker>();

    /**
     * Constructor
     *
     * @param converter shape converter
     */
    public PolylineMarkers(GoogleMapShapeConverter converter) {
        this.converter = converter;
    }

    /**
     * Get the polyline
     *
     * @return
     */
    public Polyline getPolyline() {
        return polyline;
    }

    /**
     * Set the polyline
     *
     * @param polyline
     */
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
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
     * @param markers list of markers
     */
    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    /**
     * Update based upon marker changes
     */
    public void update() {
        if (polyline != null) {
            if (isDeleted()) {
                remove();
            } else {
                List<LatLng> points = converter.getPointsFromMarkers(markers);
                polyline.setPoints(points);
            }
        }
    }

    /**
     * Remove from the map
     */
    public void remove() {
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        for (Marker marker : markers) {
            marker.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        if (polyline != null) {
            polyline.setVisible(visible);
        }
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
        if (polyline != null) {
            polyline.setZIndex(zIndex);
        }
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
        return markers.isEmpty() || markers.size() >= 2;
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
            update();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNew(Marker marker) {
        GoogleMapShapeMarkers.addMarkerAsPolyline(marker, markers);
    }

}

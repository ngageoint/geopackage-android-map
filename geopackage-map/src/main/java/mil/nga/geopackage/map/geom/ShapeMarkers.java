package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Shape markers interface for handling marker changes
 *
 * @author osbornb
 */
public interface ShapeMarkers {

    /**
     * Get all markers
     *
     * @return
     */
    public List<Marker> getMarkers();

    /**
     * Delete the marker
     *
     * @param marker
     */
    public void delete(Marker marker);

    /**
     * Add the marker
     *
     * @param marker
     */
    public void addNew(Marker marker);

    /**
     * Updates visibility of all objects
     *
     * @param visible visible flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible);

    /**
     * Updates visibility of the shape representing markers
     *
     * @param visible visible flag
     * @since 1.3.2
     */
    public void setVisibleMarkers(boolean visible);

}

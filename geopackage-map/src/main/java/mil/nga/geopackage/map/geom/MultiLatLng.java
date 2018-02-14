package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple LatLng object
 *
 * @author osbornb
 */
public class MultiLatLng {

    /**
     * List of LatLngs
     */
    private List<LatLng> latLngs = new ArrayList<LatLng>();

    /**
     * Marker options
     */
    private MarkerOptions markerOptions;

    /**
     * Add a LatLng
     *
     * @param latLng LatLng
     */
    public void add(LatLng latLng) {
        latLngs.add(latLng);
    }

    /**
     * Get the LatLngs
     *
     * @return list of LatLng
     */
    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    /**
     * Get the marker options
     *
     * @return marker options
     */
    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    /**
     * Set the marker options
     *
     * @param markerOptions marker options
     */
    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    /**
     * Set the LatLngs
     *
     * @param latLngs list of LatLng
     */
    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

}

package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Polyline Options object
 *
 * @author osbornb
 */
public class MultiPolylineOptions {

    /**
     * List of Polyline Options
     */
    private List<PolylineOptions> polylineOptions = new ArrayList<PolylineOptions>();

    /**
     * Global Polyline Options
     */
    private PolylineOptions options;

    /**
     * Add a polyline option
     *
     * @param polylineOption polyline option
     */
    public void add(PolylineOptions polylineOption) {
        polylineOptions.add(polylineOption);
    }

    /**
     * Get the polyline options
     *
     * @return polyline options
     */
    public List<PolylineOptions> getPolylineOptions() {
        return polylineOptions;
    }

    /**
     * Get the global polyline options
     *
     * @return global polyline options
     */
    public PolylineOptions getOptions() {
        return options;
    }

    /**
     * Set the global polyline options
     *
     * @param options global polyline options
     */
    public void setOptions(PolylineOptions options) {
        this.options = options;
    }

    /**
     * Set the polyline options
     *
     * @param polylineOptions polyline options
     */
    public void setPolylineOptions(List<PolylineOptions> polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    /**
     * Updates visibility of the shape
     *
     * @param visible visible flag
     * @since 2.0.1
     */
    public void visible(boolean visible) {
        for (PolylineOptions options : polylineOptions) {
            options.visible(visible);
        }
    }

    /**
     * Set the z index
     *
     * @param zIndex z index
     * @since 2.0.1
     */
    public void zIndex(float zIndex) {
        for (PolylineOptions options : polylineOptions) {
            options.zIndex(zIndex);
        }
    }

}

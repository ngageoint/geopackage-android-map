package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Polygon Options object
 *
 * @author osbornb
 */
public class MultiPolygonOptions {

    /**
     * List of Polygon Options
     */
    private List<PolygonOptions> polygonOptions = new ArrayList<PolygonOptions>();

    /**
     * Global Polygon Options
     */
    private PolygonOptions options;

    /**
     * Add a polygon option
     *
     * @param polygonOption polygon option
     */
    public void add(PolygonOptions polygonOption) {
        polygonOptions.add(polygonOption);
    }

    /**
     * Get the polygon options
     *
     * @return polygon options
     */
    public List<PolygonOptions> getPolygonOptions() {
        return polygonOptions;
    }

    /**
     * Get the global polygon options
     *
     * @return global polygon options
     */
    public PolygonOptions getOptions() {
        return options;
    }

    /**
     * Set the global polygon options
     *
     * @param options polygon options
     */
    public void setOptions(PolygonOptions options) {
        this.options = options;
    }

    /**
     * Set the polygon options
     *
     * @param polygonOptions polygon options
     */
    public void setPolygonOptions(List<PolygonOptions> polygonOptions) {
        this.polygonOptions = polygonOptions;
    }

    /**
     * Updates visibility of the shape
     *
     * @param visible visible flag
     * @since 2.0.1
     */
    public void visible(boolean visible) {
        for (PolygonOptions options : polygonOptions) {
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
        for (PolygonOptions options : polygonOptions) {
            options.zIndex(zIndex);
        }
    }

}

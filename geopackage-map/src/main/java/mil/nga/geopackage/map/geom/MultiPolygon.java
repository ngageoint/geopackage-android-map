package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple Polygon object
 *
 * @author osbornb
 */
public class MultiPolygon {

    /**
     * List of polygons
     */
    private List<Polygon> polygons = new ArrayList<Polygon>();

    /**
     * Add a polygon
     *
     * @param polygon polygon
     */
    public void add(Polygon polygon) {
        polygons.add(polygon);
    }

    /**
     * Get the polygons
     *
     * @return list of polygons
     */
    public List<Polygon> getPolygons() {
        return polygons;
    }

    /**
     * Set the polygons
     *
     * @param polygons list of polygons
     */
    public void setPolygons(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    /**
     * Remove from the map
     */
    public void remove() {
        for (Polygon polygon : polygons) {
            polygon.remove();
        }
    }

    /**
     * Set visibility on the map
     *
     * @param visible visibility flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible) {
        for (Polygon polygon : polygons) {
            polygon.setVisible(visible);
        }
    }

    /**
     * Set the z index
     *
     * @param zIndex z index
     * @since 2.0.1
     */
    public void setZIndex(float zIndex) {
        for (Polygon polygon : polygons) {
            polygon.setZIndex(zIndex);
        }
    }

}

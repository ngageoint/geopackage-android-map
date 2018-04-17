package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Composite overlay comprised of multiple overlays, checking each in order for a tile
 *
 * @author osbornb
 */
public class CompositeOverlay extends BoundedOverlay {

    /**
     * Ordered list of overlays
     */
    private List<BoundedOverlay> overlays = new ArrayList<>();

    /**
     * Constructor
     */
    public CompositeOverlay() {

    }

    /**
     * Constructor
     *
     * @param overlay first overlay
     */
    public CompositeOverlay(BoundedOverlay overlay) {
        addOverlay(overlay);
    }

    /**
     * Constructor
     *
     * @param overlays ordered overlays
     */
    public CompositeOverlay(Collection<BoundedOverlay> overlays) {
        addOverlays(overlays);
    }

    /**
     * Add an overlay
     *
     * @param overlay bounded overlay
     */
    public void addOverlay(BoundedOverlay overlay) {
        overlays.add(overlay);
    }

    /**
     * Add overlays
     *
     * @param overlays ordered overlays
     */
    public void addOverlays(Collection<BoundedOverlay> overlays) {
        this.overlays.addAll(overlays);
    }

    /**
     * Clear the overlays
     */
    public void clearOverlays() {
        overlays.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasTileToRetrieve(int x, int y, int zoom) {
        boolean hasTile = false;
        for (BoundedOverlay overlay : overlays) {
            hasTile = overlay.hasTileToRetrieve(x, y, zoom);
            if (hasTile) {
                break;
            }
        }
        return hasTile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Tile retrieveTile(int x, int y, int zoom) {
        Tile tile = null;
        for (BoundedOverlay overlay : overlays) {
            tile = overlay.retrieveTile(x, y, zoom);
            if (tile != null) {
                break;
            }
        }
        return tile;
    }

}

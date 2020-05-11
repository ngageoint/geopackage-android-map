package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.Tile;

import mil.nga.geopackage.extension.nga.scale.TileScaling;
import mil.nga.geopackage.tiles.TileUtils;
import mil.nga.geopackage.tiles.retriever.GeoPackageTile;
import mil.nga.geopackage.tiles.retriever.GeoPackageTileRetriever;
import mil.nga.geopackage.tiles.retriever.TileRetriever;
import mil.nga.geopackage.tiles.user.TileDao;

/**
 * GeoPackage Map Overlay Tile Provider
 *
 * @author osbornb
 */
public class GeoPackageOverlay extends BoundedOverlay {

    /**
     * Tile retriever
     */
    private final TileRetriever retriever;

    /**
     * Constructor using GeoPackage tile sizes
     *
     * @param tileDao tile dao
     */
    public GeoPackageOverlay(TileDao tileDao) {
        this.retriever = new GeoPackageTileRetriever(tileDao);
    }

    /**
     * Constructor using the density to determine tile size
     *
     * @param tileDao tile dao
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @since 3.2.0
     */
    public GeoPackageOverlay(TileDao tileDao, float density) {
        this(tileDao, density, null);
    }

    /**
     * Constructor with specified tile size
     *
     * @param tileDao tile dao
     * @param width   tile width
     * @param height  tile height
     */
    public GeoPackageOverlay(TileDao tileDao, int width, int height) {
        this.retriever = new GeoPackageTileRetriever(tileDao, width, height);
    }

    /**
     * Constructor with tile scaling options
     *
     * @param tileDao tile dao
     * @param scaling tile scaling options
     * @since 2.0.2
     */
    public GeoPackageOverlay(TileDao tileDao, TileScaling scaling) {
        GeoPackageTileRetriever tileRetriever = new GeoPackageTileRetriever(tileDao);
        if (scaling != null) {
            tileRetriever.setScaling(scaling);
        }
        this.retriever = tileRetriever;
    }

    /**
     * Constructor using the density to determine tile size and with tile scaling options
     *
     * @param tileDao tile dao
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @param scaling tile scaling options
     * @since 3.2.0
     */
    public GeoPackageOverlay(TileDao tileDao, float density, TileScaling scaling) {
        this(tileDao, TileUtils.tileLength(density), TileUtils.tileLength(density), scaling);
    }

    /**
     * Constructor with specified tile size and tile scaling options
     *
     * @param tileDao tile dao
     * @param width   tile width
     * @param height  tile height
     * @param scaling tile scaling options
     * @since 2.0.2
     */
    public GeoPackageOverlay(TileDao tileDao, int width, int height, TileScaling scaling) {
        GeoPackageTileRetriever tileRetriever = new GeoPackageTileRetriever(tileDao, width, height);
        if (scaling != null) {
            tileRetriever.setScaling(scaling);
        }
        this.retriever = tileRetriever;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTileToRetrieve(int x, int y, int zoom) {
        return retriever.hasTile(x, y, zoom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile retrieveTile(int x, int y, int zoom) {

        GeoPackageTile geoPackageTile = retriever.getTile(x, y, zoom);
        Tile tile = GeoPackageOverlayFactory.getTile(geoPackageTile);

        return tile;
    }

}

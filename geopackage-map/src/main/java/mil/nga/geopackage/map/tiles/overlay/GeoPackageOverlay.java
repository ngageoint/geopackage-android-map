package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.Tile;

import mil.nga.geopackage.tiles.retriever.GeoPackageTile;
import mil.nga.geopackage.tiles.retriever.GeoPackageTileRetriever;
import mil.nga.geopackage.tiles.retriever.TileCreatorOptions;
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
     * Constructor with tile creator options
     *
     * @param tileDao tile dao
     * @param options tile creator options
     * @since 2.0.2
     */
    public GeoPackageOverlay(TileDao tileDao, TileCreatorOptions options) {
        GeoPackageTileRetriever tileRetriever = new GeoPackageTileRetriever(tileDao);
        tileRetriever.setOptions(options);
        this.retriever = tileRetriever;
    }

    /**
     * Constructor with specified tile size and tile creator options
     *
     * @param tileDao tile dao
     * @param width   tile width
     * @param height  tile height
     * @param options tile creator options
     * @since 2.0.2
     */
    public GeoPackageOverlay(TileDao tileDao, int width, int height, TileCreatorOptions options) {
        GeoPackageTileRetriever tileRetriever = new GeoPackageTileRetriever(tileDao, width, height);
        tileRetriever.setOptions(options);
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

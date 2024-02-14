package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.Tile;

import mil.nga.geopackage.tiles.retriever.GeoPackageTile;
import mil.nga.geopackage.tiles.retriever.TileRetriever;
import mil.nga.geopackage.tiles.retriever.XYZGeoPackageTileRetriever;
import mil.nga.geopackage.tiles.user.TileDao;

/**
 * XYZ GeoPackage Map Overlay Tile Provider, assumes XYZ tiles
 *
 * @author osbornb
 */
public class XYZGeoPackageOverlay extends BoundedOverlay {

    /**
     * Tile retriever
     */
    private final TileRetriever retriever;

    /**
     * Constructor
     *
     * @param tileDao tile dao
     */
    public XYZGeoPackageOverlay(TileDao tileDao) {
        this.retriever = new XYZGeoPackageTileRetriever(tileDao);
    }

    /**
     * Get the tile retriever
     *
     * @return retriever
     * @since 6.7.4
     */
    public TileRetriever getRetriever() {
        return retriever;
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

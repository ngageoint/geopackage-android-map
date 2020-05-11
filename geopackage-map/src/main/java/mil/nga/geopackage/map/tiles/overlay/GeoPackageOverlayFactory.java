package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.extension.nga.link.FeatureTileTableLinker;
import mil.nga.geopackage.extension.nga.scale.TileScaling;
import mil.nga.geopackage.tiles.retriever.GeoPackageTile;
import mil.nga.geopackage.tiles.user.TileDao;

/**
 * Get a tile provider for the Tile DAO
 *
 * @author osbornb
 */
public class GeoPackageOverlayFactory {

    /**
     * Get a Tile Provider for the Tile DAO
     *
     * @param tileDao tile dao
     * @return tile provider
     */
    public static TileProvider getTileProvider(TileDao tileDao) {
        return getBoundedOverlay(tileDao);
    }

    /**
     * Get a Tile Provider for the Tile DAO with the tile creator options
     *
     * @param tileDao tile dao
     * @param scaling tile scaling options
     * @return tile provider
     * @since 2.0.2
     */
    public static TileProvider getTileProvider(TileDao tileDao, TileScaling scaling) {
        return getBoundedOverlay(tileDao, scaling);
    }

    /**
     * Get a Bounded Overlay Tile Provider for the Tile DAO
     *
     * @param tileDao tile dao
     * @return bounded overlay
     * @since 1.2.5
     */
    public static BoundedOverlay getBoundedOverlay(TileDao tileDao) {

        BoundedOverlay overlay = null;

        if (tileDao.isXYZTiles()) {
            overlay = new XYZGeoPackageOverlay(tileDao);
        } else {
            overlay = new GeoPackageOverlay(tileDao);
        }

        return overlay;
    }

    /**
     * Get a Bounded Overlay Tile Provider for the Tile DAO with the display density
     *
     * @param tileDao tile dao
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return bounded overlay
     * @since 3.2.0
     */
    public static BoundedOverlay getBoundedOverlay(TileDao tileDao, float density) {

        BoundedOverlay overlay = null;

        if (tileDao.isXYZTiles()) {
            overlay = new XYZGeoPackageOverlay(tileDao);
        } else {
            overlay = new GeoPackageOverlay(tileDao, density);
        }

        return overlay;
    }

    /**
     * Get a Bounded Overlay Tile Provider for the Tile DAO with the tile creator options
     *
     * @param tileDao tile dao
     * @param scaling tile scaling options
     * @return bounded overlay
     * @since 2.0.2
     */
    public static BoundedOverlay getBoundedOverlay(TileDao tileDao, TileScaling scaling) {
        return new GeoPackageOverlay(tileDao, scaling);
    }

    /**
     * Get a Bounded Overlay Tile Provider for the Tile DAO with the display density and tile creator options
     *
     * @param tileDao tile dao
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @param scaling tile scaling options
     * @return bounded overlay
     * @since 3.2.0
     */
    public static BoundedOverlay getBoundedOverlay(TileDao tileDao, float density, TileScaling scaling) {
        return new GeoPackageOverlay(tileDao, density, scaling);
    }

    /**
     * Create a composite overlay by first adding a tile overlay for the tile DAO followed by the provided overlay
     *
     * @param tileDao tile dao
     * @param overlay bounded overlay
     * @return composite overlay
     */
    public static CompositeOverlay getCompositeOverlay(TileDao tileDao, BoundedOverlay overlay) {
        List<TileDao> tileDaos = new ArrayList<>();
        tileDaos.add(tileDao);
        return getCompositeOverlay(tileDaos, overlay);
    }

    /**
     * Create a composite overlay by first adding tile overlays for the tile DAOs followed by the provided overlay
     *
     * @param tileDaos collection of tile daos
     * @param overlay  bounded overlay
     * @return composite overlay
     */
    public static CompositeOverlay getCompositeOverlay(Collection<TileDao> tileDaos, BoundedOverlay overlay) {

        CompositeOverlay compositeOverlay = getCompositeOverlay(tileDaos);

        compositeOverlay.addOverlay(overlay);

        return compositeOverlay;
    }

    /**
     * Create a composite overlay by adding tile overlays for the tile DAOs
     *
     * @param tileDaos collection of tile daos
     * @return composite overlay
     */
    public static CompositeOverlay getCompositeOverlay(Collection<TileDao> tileDaos) {

        CompositeOverlay compositeOverlay = new CompositeOverlay();

        for (TileDao tileDao : tileDaos) {
            BoundedOverlay boundedOverlay = GeoPackageOverlayFactory.getBoundedOverlay(tileDao);
            compositeOverlay.addOverlay(boundedOverlay);
        }

        return compositeOverlay;
    }

    /**
     * Create a composite overlay linking the feature overly with
     *
     * @param featureOverlay feature overlay
     * @param geoPackage     GeoPackage
     * @return linked bounded overlay
     */
    public static BoundedOverlay getLinkedFeatureOverlay(FeatureOverlay featureOverlay, GeoPackage geoPackage) {

        BoundedOverlay overlay;

        // Get the linked tile daos
        FeatureTileTableLinker linker = new FeatureTileTableLinker(geoPackage);
        List<TileDao> tileDaos = linker.getTileDaosForFeatureTable(featureOverlay.getFeatureTiles().getFeatureDao().getTableName());

        if (!tileDaos.isEmpty()) {
            // Create a composite overlay to search for existing tiles before drawing from features
            overlay = getCompositeOverlay(tileDaos, featureOverlay);
        } else {
            overlay = featureOverlay;
        }

        return overlay;
    }


    /**
     * Get a map tile from the GeoPackage tile
     *
     * @param geoPackageTile GeoPackage tile
     * @return tile
     */
    public static Tile getTile(GeoPackageTile geoPackageTile) {
        Tile tile = null;
        if (geoPackageTile != null) {
            tile = new Tile(geoPackageTile.getWidth(), geoPackageTile.getHeight(), geoPackageTile.getData());
        }
        return tile;
    }

}

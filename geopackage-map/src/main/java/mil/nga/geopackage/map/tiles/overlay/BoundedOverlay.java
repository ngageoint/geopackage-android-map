package mil.nga.geopackage.map.tiles.overlay;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.sf.proj.Projection;
import mil.nga.sf.proj.ProjectionConstants;
import mil.nga.sf.proj.ProjectionFactory;
import mil.nga.sf.proj.ProjectionTransform;

/**
 * Abstract overlay which provides bounding returned tiles by zoom levels and/or a bounding box
 *
 * @author osbornb
 * @since 1.2.5
 */
public abstract class BoundedOverlay implements TileProvider {

    /**
     * Min zoom
     */
    private Integer minZoom;

    /**
     * Max zoom
     */
    private Integer maxZoom;

    /**
     * Web mercator bounding box
     */
    protected BoundingBox webMercatorBoundingBox;

    /**
     * Constructor
     */
    public BoundedOverlay() {

    }

    /**
     * Get the min zoom
     *
     * @return min zoom
     */
    public Integer getMinZoom() {
        return minZoom;
    }

    /**
     * Set the min zoom
     *
     * @param minZoom min zoom
     */
    public void setMinZoom(Integer minZoom) {
        this.minZoom = minZoom;
    }

    /**
     * Get the max zoom
     *
     * @return max zoom
     */
    public Integer getMaxZoom() {
        return maxZoom;
    }

    /**
     * Set the max zoom
     *
     * @param maxZoom max zoom
     */
    public void setMaxZoom(Integer maxZoom) {
        this.maxZoom = maxZoom;
    }

    /**
     * Set the bounding box, provided as the indicated projection
     *
     * @param boundingBox bounding box
     * @param projection  projection
     */
    public void setBoundingBox(BoundingBox boundingBox, Projection projection) {
        ProjectionTransform projectionToWebMercator = projection
                .getTransformation(ProjectionConstants.EPSG_WEB_MERCATOR);
        webMercatorBoundingBox = boundingBox
                .transform(projectionToWebMercator);
    }

    /**
     * Get the web mercator bounding box
     *
     * @return bounding box
     */
    public BoundingBox getWebMercatorBoundingBox() {
        return webMercatorBoundingBox;
    }

    /**
     * Get the bounding box as the provided projection
     *
     * @param projection projection
     */
    public BoundingBox getBoundingBox(Projection projection) {
        ProjectionTransform webMercatorToProjection = ProjectionFactory
                .getProjection(ProjectionConstants.EPSG_WEB_MERCATOR)
                .getTransformation(projection);
        return webMercatorBoundingBox
                .transform(webMercatorToProjection);
    }

    /**
     * Get the bounded overlay web mercator bounding box expanded as needed by the requested bounding box dimensions
     *
     * @param requestWebMercatorBoundingBox requested web mercator bounding box
     * @return web mercator bounding box
     */
    protected BoundingBox getWebMercatorBoundingBox(BoundingBox requestWebMercatorBoundingBox) {
        return webMercatorBoundingBox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile getTile(int x, int y, int zoom) {

        Tile tile = null;

        // Check if there is a tile
        if (hasTile(x, y, zoom)) {

            // Retrieve the tile
            tile = retrieveTile(x, y, zoom);
        }

        return tile;
    }

    /**
     * Determine if there is a tile for the x, y, and zoom
     *
     * @param x    x coordinate
     * @param y    y coordinate
     * @param zoom zoom value
     * @return true if there is a tile
     * @since 1.2.6
     */
    public boolean hasTile(int x, int y, int zoom) {

        // Check if generating tiles for the zoom level and is within the bounding box
        boolean hasTile = isWithinBounds(x, y, zoom);
        if (hasTile) {
            // Check if there is a tile to retrieve
            hasTile = hasTileToRetrieve(x, y, zoom);
        }

        return hasTile;
    }

    /**
     * Check if there is a tile to retrieve
     *
     * @param x    x coordinate
     * @param y    y coordinate
     * @param zoom zoom value
     * @return true if there is a tile
     */
    protected abstract boolean hasTileToRetrieve(int x, int y, int zoom);

    /**
     * Retrieve the tile
     *
     * @param x    x coordinate
     * @param y    y coordinate
     * @param zoom zoom value
     * @return tile
     */
    protected abstract Tile retrieveTile(int x, int y, int zoom);

    /**
     * Is the tile within the zoom and bounding box bounds
     *
     * @param x    x coordinate
     * @param y    y coordinate
     * @param zoom zoom value
     * @return true if within bounds
     */
    public boolean isWithinBounds(int x, int y, int zoom) {
        return isWithinZoom(zoom) && isWithinBoundingBox(x, y, zoom);
    }

    /**
     * Check if the zoom is within the overlay zoom range
     *
     * @param zoom zoom value
     * @return true if within zoom
     */
    public boolean isWithinZoom(float zoom) {
        return (minZoom == null || zoom >= minZoom) && (maxZoom == null || zoom <= maxZoom);
    }

    /**
     * Check if the tile request is within the desired tile bounds
     *
     * @param x    x coordinate
     * @param y    y coordinate
     * @param zoom zoom value
     * @return true if within bounds
     */
    public boolean isWithinBoundingBox(int x, int y, int zoom) {
        boolean withinBounds = true;

        // If a bounding box is set, check if it overlaps with the request
        if (webMercatorBoundingBox != null) {

            // Get the bounding box of the requested tile
            BoundingBox tileWebMercatorBoundingBox = TileBoundingBoxUtils
                    .getWebMercatorBoundingBox(x, y, zoom);

            // Adjust the bounding box if needed
            BoundingBox adjustedWebMercatorBoundingBox = getWebMercatorBoundingBox(tileWebMercatorBoundingBox);

            // Check if the request overlaps
            withinBounds = adjustedWebMercatorBoundingBox.intersects(
                    tileWebMercatorBoundingBox, true);
        }

        return withinBounds;
    }

}

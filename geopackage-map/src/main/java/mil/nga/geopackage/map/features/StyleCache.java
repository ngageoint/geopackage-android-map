package mil.nga.geopackage.map.features;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.extension.nga.style.FeatureStyle;
import mil.nga.geopackage.extension.nga.style.FeatureStyleExtension;
import mil.nga.geopackage.extension.nga.style.IconCache;
import mil.nga.geopackage.extension.nga.style.IconRow;
import mil.nga.geopackage.extension.nga.style.StyleRow;
import mil.nga.geopackage.features.user.FeatureRow;

/**
 * Style utilities for populating markers and shapes. Caches icons for a single GeoPackage
 *
 * @author osbornb
 * @since 3.2.0
 */
public class StyleCache {

    /**
     * Feature style extension
     */
    private final FeatureStyleExtension featureStyleExtension;

    /**
     * Icon bitmap cache
     */
    private final IconCache iconCache;

    /**
     * Display density: {@link android.util.DisplayMetrics#density}
     */
    private float density;

    /**
     * Constructor
     *
     * @param geoPackage GeoPackage
     * @param density    display density: {@link android.util.DisplayMetrics#density}
     */
    public StyleCache(GeoPackage geoPackage, float density) {
        this(geoPackage, density, IconCache.DEFAULT_CACHE_SIZE);
    }

    /**
     * Constructor
     *
     * @param geoPackage    GeoPackage
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @param iconCacheSize number of icon bitmaps to cache
     */
    public StyleCache(GeoPackage geoPackage, float density, int iconCacheSize) {
        this(new FeatureStyleExtension(geoPackage), density, iconCacheSize);
    }

    /**
     * Constructor
     *
     * @param featureStyleExtension feature style extension
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     */
    public StyleCache(FeatureStyleExtension featureStyleExtension, float density) {
        this(featureStyleExtension, density, IconCache.DEFAULT_CACHE_SIZE);
    }

    /**
     * Constructor
     *
     * @param featureStyleExtension feature style extension
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param iconCacheSize         number of icon bitmaps to cache
     */
    public StyleCache(FeatureStyleExtension featureStyleExtension, float density, int iconCacheSize) {
        this.featureStyleExtension = featureStyleExtension;
        iconCache = new IconCache(iconCacheSize);
        this.density = density;
    }

    /**
     * Clear the cache
     */
    public void clear() {
        iconCache.clear();
    }

    /**
     * Get the feature style extension
     *
     * @return feature style extension
     */
    public FeatureStyleExtension getFeatureStyleExtension() {
        return featureStyleExtension;
    }

    /**
     * Get the display density
     *
     * @return density
     */
    public float getDensity() {
        return density;
    }

    /**
     * Set the display density
     *
     * @param density density
     */
    public void setDensity(float density) {
        this.density = density;
    }

    /**
     * Create new marker options populated with the feature row style (icon or style)
     *
     * @param featureRow feature row
     * @return marker options populated with the feature style
     */
    public MarkerOptions createMarkerOptions(FeatureRow featureRow) {
        return StyleUtils.createMarkerOptions(featureStyleExtension, featureRow, density, iconCache);
    }

    /**
     * Set the feature row style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param featureRow    feature row
     * @return true if icon or style was set into the marker options
     */
    public boolean setFeatureStyle(MarkerOptions markerOptions, FeatureRow featureRow) {
        return StyleUtils.setFeatureStyle(markerOptions, featureStyleExtension, featureRow, density, iconCache);
    }

    /**
     * Create new marker options populated with the feature style (icon or style)
     *
     * @param featureStyle feature style
     * @return marker options populated with the feature style
     */
    public MarkerOptions createMarkerOptions(FeatureStyle featureStyle) {
        return StyleUtils.createMarkerOptions(featureStyle, density, iconCache);
    }

    /**
     * Set the feature style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param featureStyle  feature style
     * @return true if icon or style was set into the marker options
     */
    public boolean setFeatureStyle(MarkerOptions markerOptions, FeatureStyle featureStyle) {
        return StyleUtils.setFeatureStyle(markerOptions, featureStyle, density, iconCache);
    }

    /**
     * Create new marker options populated with the icon
     *
     * @param icon icon row
     * @return marker options populated with the icon
     */
    public MarkerOptions createMarkerOptions(IconRow icon) {
        return StyleUtils.createMarkerOptions(icon, density, iconCache);
    }

    /**
     * Set the icon into the marker options
     *
     * @param markerOptions marker options
     * @param icon          icon row
     * @return true if icon was set into the marker options
     */
    public boolean setIcon(MarkerOptions markerOptions, IconRow icon) {
        return StyleUtils.setIcon(markerOptions, icon, density, iconCache);
    }

    /**
     * Create the icon bitmap
     *
     * @param icon icon row
     * @return icon bitmap
     */
    public Bitmap createIcon(IconRow icon) {
        return StyleUtils.createIcon(icon, density, iconCache);
    }

    /**
     * Create new marker options populated with the style
     *
     * @param style style row
     * @return marker options populated with the style
     */
    public MarkerOptions createMarkerOptions(StyleRow style) {
        return StyleUtils.createMarkerOptions(style);
    }

    /**
     * Set the style into the marker options
     *
     * @param markerOptions marker options
     * @param style         style row
     * @return true if style was set into the marker options
     */
    public boolean setStyle(MarkerOptions markerOptions, StyleRow style) {
        return StyleUtils.setStyle(markerOptions, style);
    }

    /**
     * Create new polyline options populated with the feature row style
     *
     * @param featureRow feature row
     * @return polyline options populated with the feature style
     */
    public PolylineOptions createPolylineOptions(FeatureRow featureRow) {
        return StyleUtils.createPolylineOptions(featureStyleExtension, featureRow, density);
    }

    /**
     * Set the feature row style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param featureRow      feature row
     * @return true if style was set into the polyline options
     */
    public boolean setFeatureStyle(PolylineOptions polylineOptions, FeatureRow featureRow) {
        return StyleUtils.setFeatureStyle(polylineOptions, featureStyleExtension, featureRow, density);
    }

    /**
     * Create new polyline options populated with the feature style
     *
     * @param featureStyle feature style
     * @return polyline options populated with the feature style
     */
    public PolylineOptions createPolylineOptions(FeatureStyle featureStyle) {
        return StyleUtils.createPolylineOptions(featureStyle, density);
    }

    /**
     * Set the feature style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param featureStyle    feature style
     * @return true if style was set into the polyline options
     */
    public boolean setFeatureStyle(PolylineOptions polylineOptions, FeatureStyle featureStyle) {
        return StyleUtils.setFeatureStyle(polylineOptions, featureStyle, density);
    }

    /**
     * Create new polyline options populated with the style
     *
     * @param style style row
     * @return polyline options populated with the style
     */
    public PolylineOptions createPolylineOptions(StyleRow style) {
        return StyleUtils.createPolylineOptions(style, density);
    }

    /**
     * Set the style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param style           style row
     * @return true if style was set into the polyline options
     */
    public boolean setStyle(PolylineOptions polylineOptions, StyleRow style) {
        return StyleUtils.setStyle(polylineOptions, style, density);
    }

    /**
     * Create new polygon options populated with the feature row style
     *
     * @param featureRow feature row
     * @return polygon options populated with the feature style
     */
    public PolygonOptions createPolygonOptions(FeatureRow featureRow) {
        return StyleUtils.createPolygonOptions(featureStyleExtension, featureRow, density);
    }

    /**
     * Set the feature row style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param featureRow     feature row
     * @return true if style was set into the polygon options
     */
    public boolean setFeatureStyle(PolygonOptions polygonOptions, FeatureRow featureRow) {
        return StyleUtils.setFeatureStyle(polygonOptions, featureStyleExtension, featureRow, density);
    }

    /**
     * Create new polygon options populated with the feature style
     *
     * @param featureStyle feature style
     * @return polygon options populated with the feature style
     */
    public PolygonOptions createPolygonOptions(FeatureStyle featureStyle) {
        return StyleUtils.createPolygonOptions(featureStyle, density);
    }

    /**
     * Set the feature style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param featureStyle   feature style
     * @return true if style was set into the polygon options
     */
    public boolean setFeatureStyle(PolygonOptions polygonOptions, FeatureStyle featureStyle) {
        return StyleUtils.setFeatureStyle(polygonOptions, featureStyle, density);
    }

    /**
     * Create new polygon options populated with the style
     *
     * @param style style row
     * @return polygon options populated with the style
     */
    public PolygonOptions createPolygonOptions(StyleRow style) {
        return StyleUtils.createPolygonOptions(style, density);
    }

    /**
     * Set the style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param style          style row
     * @return true if style was set into the polygon options
     */
    public boolean setStyle(PolygonOptions polygonOptions, StyleRow style) {
        return StyleUtils.setStyle(polygonOptions, style, density);
    }

}

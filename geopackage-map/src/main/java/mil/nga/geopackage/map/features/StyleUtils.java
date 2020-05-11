package mil.nga.geopackage.map.features;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import mil.nga.geopackage.style.Color;

/**
 * Style utilities for populating markers and shapes
 *
 * @author osbornb
 * @since 3.2.0
 */
public class StyleUtils {

    /**
     * Create new marker options populated with the feature row style (icon or style)
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @param density    display density: {@link android.util.DisplayMetrics#density}
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(GeoPackage geoPackage, FeatureRow featureRow, float density) {
        return createMarkerOptions(geoPackage, featureRow, density, null);
    }

    /**
     * Create new marker options populated with the feature row style (icon or style)
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @param density    display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache  icon cache
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(GeoPackage geoPackage, FeatureRow featureRow, float density, IconCache iconCache) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, geoPackage, featureRow, density, iconCache);

        return markerOptions;
    }

    /**
     * Set the feature row style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param geoPackage    GeoPackage
     * @param featureRow    feature row
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, GeoPackage geoPackage, FeatureRow featureRow, float density) {
        return setFeatureStyle(markerOptions, geoPackage, featureRow, density, null);
    }

    /**
     * Set the feature row style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param geoPackage    GeoPackage
     * @param featureRow    feature row
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache     icon cache
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, GeoPackage geoPackage, FeatureRow featureRow, float density, IconCache iconCache) {

        FeatureStyleExtension featureStyleExtension = new FeatureStyleExtension(geoPackage);

        return setFeatureStyle(markerOptions, featureStyleExtension, featureRow, density, iconCache);
    }

    /**
     * Create new marker options populated with the feature row style (icon or style)
     *
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, featureStyleExtension, featureRow, density);

        return markerOptions;
    }

    /**
     * Set the feature row style (icon or style) into the marker options
     *
     * @param markerOptions         marker options
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density) {
        return setFeatureStyle(markerOptions, featureStyleExtension, featureRow, density, null);
    }

    /**
     * Create new marker options populated with the feature row style (icon or style)
     *
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache             icon cache
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density, IconCache iconCache) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, featureStyleExtension, featureRow, density, iconCache);

        return markerOptions;
    }

    /**
     * Set the feature row style (icon or style) into the marker options
     *
     * @param markerOptions         marker options
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache             icon cache
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density, IconCache iconCache) {

        FeatureStyle featureStyle = featureStyleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(markerOptions, featureStyle, density, iconCache);
    }

    /**
     * Create new marker options populated with the feature style (icon or style)
     *
     * @param featureStyle feature style
     * @param density      display density: {@link android.util.DisplayMetrics#density}
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(FeatureStyle featureStyle, float density) {
        return createMarkerOptions(featureStyle, density, null);
    }

    /**
     * Create new marker options populated with the feature style (icon or style)
     *
     * @param featureStyle feature style
     * @param density      display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache    icon cache
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(FeatureStyle featureStyle, float density, IconCache iconCache) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, featureStyle, density, iconCache);

        return markerOptions;
    }

    /**
     * Set the feature style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param featureStyle  feature style
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, FeatureStyle featureStyle, float density) {
        return setFeatureStyle(markerOptions, featureStyle, density, null);
    }

    /**
     * Set the feature style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param featureStyle  feature style
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache     icon cache
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, FeatureStyle featureStyle, float density, IconCache iconCache) {

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setIcon(markerOptions, featureStyle.getIcon(), density, iconCache);

            if (!featureStyleSet) {

                featureStyleSet = setStyle(markerOptions, featureStyle.getStyle());

            }

        }

        return featureStyleSet;
    }

    /**
     * Create new marker options populated with the icon
     *
     * @param icon    icon row
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return marker options populated with the icon
     */
    public static MarkerOptions createMarkerOptions(IconRow icon, float density) {
        return createMarkerOptions(icon, density, null);
    }

    /**
     * Create new marker options populated with the icon
     *
     * @param icon      icon row
     * @param density   display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache icon cache
     * @return marker options populated with the icon
     */
    public static MarkerOptions createMarkerOptions(IconRow icon, float density, IconCache iconCache) {

        MarkerOptions markerOptions = new MarkerOptions();
        setIcon(markerOptions, icon, density, iconCache);

        return markerOptions;
    }

    /**
     * Set the icon into the marker options
     *
     * @param markerOptions marker options
     * @param icon          icon row
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @return true if icon was set into the marker options
     */
    public static boolean setIcon(MarkerOptions markerOptions, IconRow icon, float density) {
        return setIcon(markerOptions, icon, density, null);
    }

    /**
     * Set the icon into the marker options
     *
     * @param markerOptions marker options
     * @param icon          icon row
     * @param density       display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache     icon cache
     * @return true if icon was set into the marker options
     */
    public static boolean setIcon(MarkerOptions markerOptions, IconRow icon, float density, IconCache iconCache) {

        boolean iconSet = false;

        if (icon != null) {

            Bitmap iconImage = createIcon(icon, density, iconCache);
            markerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(iconImage));
            iconSet = true;

            double anchorU = icon.getAnchorUOrDefault();
            double anchorV = icon.getAnchorVOrDefault();

            markerOptions.anchor((float) anchorU, (float) anchorV);
        }

        return iconSet;
    }

    /**
     * Create the icon bitmap
     *
     * @param icon    icon row
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return icon bitmap
     */
    public static Bitmap createIcon(IconRow icon, float density) {
        return IconCache.createIconNoCache(icon, density);
    }

    /**
     * Create the icon bitmap
     *
     * @param icon      icon row
     * @param density   display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache icon cache
     * @return icon bitmap
     */
    public static Bitmap createIcon(IconRow icon, float density, IconCache iconCache) {
        return iconCache.createIcon(icon, density);
    }

    /**
     * Create new marker options populated with the style
     *
     * @param style style row
     * @return marker options populated with the style
     */
    public static MarkerOptions createMarkerOptions(StyleRow style) {

        MarkerOptions markerOptions = new MarkerOptions();
        setStyle(markerOptions, style);

        return markerOptions;
    }

    /**
     * Set the style into the marker options
     *
     * @param markerOptions marker options
     * @param style         style row
     * @return true if style was set into the marker options
     */
    public static boolean setStyle(MarkerOptions markerOptions, StyleRow style) {

        boolean styleSet = false;

        if (style != null) {
            Color color = style.getColorOrDefault();
            float hue = color.getHue();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hue));
            styleSet = true;
        }

        return styleSet;
    }

    /**
     * Create new polyline options populated with the feature row style
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @param density    display density: {@link android.util.DisplayMetrics#density}
     * @return polyline options populated with the feature style
     */
    public static PolylineOptions createPolylineOptions(GeoPackage geoPackage, FeatureRow featureRow, float density) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setFeatureStyle(polylineOptions, geoPackage, featureRow, density);

        return polylineOptions;
    }

    /**
     * Set the feature row style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param geoPackage      GeoPackage
     * @param featureRow      feature row
     * @param density         display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polyline options
     */
    public static boolean setFeatureStyle(PolylineOptions polylineOptions, GeoPackage geoPackage, FeatureRow featureRow, float density) {

        FeatureStyleExtension featureStyleExtension = new FeatureStyleExtension(geoPackage);

        return setFeatureStyle(polylineOptions, featureStyleExtension, featureRow, density);
    }

    /**
     * Create new polyline options populated with the feature row style
     *
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @return polyline options populated with the feature style
     */
    public static PolylineOptions createPolylineOptions(FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setFeatureStyle(polylineOptions, featureStyleExtension, featureRow, density);

        return polylineOptions;
    }

    /**
     * Set the feature row style into the polyline options
     *
     * @param polylineOptions       polyline options
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polyline options
     */
    public static boolean setFeatureStyle(PolylineOptions polylineOptions, FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density) {

        FeatureStyle featureStyle = featureStyleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(polylineOptions, featureStyle, density);
    }

    /**
     * Create new polyline options populated with the feature style
     *
     * @param featureStyle feature style
     * @param density      display density: {@link android.util.DisplayMetrics#density}
     * @return polyline options populated with the feature style
     */
    public static PolylineOptions createPolylineOptions(FeatureStyle featureStyle, float density) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setFeatureStyle(polylineOptions, featureStyle, density);

        return polylineOptions;
    }

    /**
     * Set the feature style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param featureStyle    feature style
     * @param density         display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polyline options
     */
    public static boolean setFeatureStyle(PolylineOptions polylineOptions, FeatureStyle featureStyle, float density) {

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setStyle(polylineOptions, featureStyle.getStyle(), density);

        }

        return featureStyleSet;
    }

    /**
     * Create new polyline options populated with the style
     *
     * @param style   style row
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return polyline options populated with the style
     */
    public static PolylineOptions createPolylineOptions(StyleRow style, float density) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setStyle(polylineOptions, style, density);

        return polylineOptions;
    }

    /**
     * Set the style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param style           style row
     * @param density         display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polyline options
     */
    public static boolean setStyle(PolylineOptions polylineOptions, StyleRow style, float density) {

        if (style != null) {

            Color color = style.getColorOrDefault();
            polylineOptions.color(color.getColorWithAlpha());

            double width = style.getWidthOrDefault();
            polylineOptions.width((float) width * density);

        }

        return style != null;
    }

    /**
     * Create new polygon options populated with the feature row style
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @param density    display density: {@link android.util.DisplayMetrics#density}
     * @return polygon options populated with the feature style
     */
    public static PolygonOptions createPolygonOptions(GeoPackage geoPackage, FeatureRow featureRow, float density) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setFeatureStyle(polygonOptions, geoPackage, featureRow, density);

        return polygonOptions;
    }

    /**
     * Set the feature row style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param geoPackage     GeoPackage
     * @param featureRow     feature row
     * @param density        display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polygon options
     */
    public static boolean setFeatureStyle(PolygonOptions polygonOptions, GeoPackage geoPackage, FeatureRow featureRow, float density) {

        FeatureStyleExtension featureStyleExtension = new FeatureStyleExtension(geoPackage);

        return setFeatureStyle(polygonOptions, featureStyleExtension, featureRow, density);
    }

    /**
     * Create new polygon options populated with the feature row style
     *
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @return polygon options populated with the feature style
     */
    public static PolygonOptions createPolygonOptions(FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setFeatureStyle(polygonOptions, featureStyleExtension, featureRow, density);

        return polygonOptions;
    }

    /**
     * Set the feature row style into the polygon options
     *
     * @param polygonOptions        polygon options
     * @param featureStyleExtension feature style extension
     * @param featureRow            feature row
     * @param density               display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polygon options
     */
    public static boolean setFeatureStyle(PolygonOptions polygonOptions, FeatureStyleExtension featureStyleExtension, FeatureRow featureRow, float density) {

        FeatureStyle featureStyle = featureStyleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(polygonOptions, featureStyle, density);
    }

    /**
     * Create new polygon options populated with the feature style
     *
     * @param featureStyle feature style
     * @param density      display density: {@link android.util.DisplayMetrics#density}
     * @return polygon options populated with the feature style
     */
    public static PolygonOptions createPolygonOptions(FeatureStyle featureStyle, float density) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setFeatureStyle(polygonOptions, featureStyle, density);

        return polygonOptions;
    }

    /**
     * Set the feature style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param featureStyle   feature style
     * @param density        display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polygon options
     */
    public static boolean setFeatureStyle(PolygonOptions polygonOptions, FeatureStyle featureStyle, float density) {

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setStyle(polygonOptions, featureStyle.getStyle(), density);

        }

        return featureStyleSet;
    }

    /**
     * Create new polygon options populated with the style
     *
     * @param style   style row
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return polygon options populated with the style
     */
    public static PolygonOptions createPolygonOptions(StyleRow style, float density) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setStyle(polygonOptions, style, density);

        return polygonOptions;
    }

    /**
     * Set the style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param style          style row
     * @param density        display density: {@link android.util.DisplayMetrics#density}
     * @return true if style was set into the polygon options
     */
    public static boolean setStyle(PolygonOptions polygonOptions, StyleRow style, float density) {

        if (style != null) {

            Color color = style.getColorOrDefault();
            polygonOptions.strokeColor(color.getColorWithAlpha());

            double width = style.getWidthOrDefault();
            polygonOptions.strokeWidth((float) width * density);

            Color fillColor = style.getFillColor();
            if (fillColor != null) {
                polygonOptions.fillColor(fillColor.getColorWithAlpha());
            }
        }

        return style != null;
    }

}

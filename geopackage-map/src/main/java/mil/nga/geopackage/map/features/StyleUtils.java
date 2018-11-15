package mil.nga.geopackage.map.features;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.extension.style.FeatureStyle;
import mil.nga.geopackage.extension.style.FeatureStyleExtension;
import mil.nga.geopackage.extension.style.IconRow;
import mil.nga.geopackage.extension.style.StyleRow;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.io.BitmapConverter;
import mil.nga.geopackage.style.Color;

/**
 * Style utilities for populating markers and shapes
 *
 * @author osbornb
 * @since 3.1.1
 */
public class StyleUtils {

    /**
     * Create new marker options populated with the feature row style (icon or style)
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(GeoPackage geoPackage, FeatureRow featureRow) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, geoPackage, featureRow);

        return markerOptions;
    }

    /**
     * Set the feature row style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param geoPackage    GeoPackage
     * @param featureRow    feature row
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, GeoPackage geoPackage, FeatureRow featureRow) {

        FeatureStyleExtension styleExtension = new FeatureStyleExtension(geoPackage);
        FeatureStyle featureStyle = styleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(markerOptions, featureStyle);
    }

    /**
     * Create new marker options populated with the feature style (icon or style)
     *
     * @param featureStyle feature style
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(FeatureStyle featureStyle) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, featureStyle);

        return markerOptions;
    }

    /**
     * Set the feature style (icon or style) into the marker options
     *
     * @param markerOptions marker options
     * @param featureStyle  feature style
     * @return true if icon or style was set into the marker options
     */
    public static boolean setFeatureStyle(MarkerOptions markerOptions, FeatureStyle featureStyle) {

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setIcon(markerOptions, featureStyle.getIcon());

            if (!featureStyleSet) {

                featureStyleSet = setStyle(markerOptions, featureStyle.getStyle());

            }

        }

        return featureStyleSet;
    }

    /**
     * Create new marker options populated with the icon
     *
     * @param icon icon row
     * @return marker options populated with the icon
     */
    public static MarkerOptions createMarkerOptions(IconRow icon) {

        MarkerOptions markerOptions = new MarkerOptions();
        setIcon(markerOptions, icon);

        return markerOptions;
    }

    /**
     * Set the icon into the marker options
     *
     * @param markerOptions marker options
     * @param icon          icon row
     * @return true if icon was set into the marker options
     */
    public static boolean setIcon(MarkerOptions markerOptions, IconRow icon) {

        boolean iconSet = false;

        if (icon != null) {

            Bitmap iconImage = BitmapConverter.toBitmap(icon
                    .getData());

            int width = iconImage.getWidth();
            if (icon.getWidth() != null) {
                int roundedWidth = (int) Math.round(icon.getWidth());
                if (roundedWidth != width) {
                    width = roundedWidth;
                }
            }

            int height = iconImage.getHeight();
            if (icon.getHeight() != null) {
                int roundedHeight = (int) Math.round(icon.getHeight());
                if (roundedHeight != height) {
                    height = roundedHeight;
                }
            }

            if (iconImage.getWidth() != width || iconImage.getHeight() != height) {
                iconImage = iconImage.createScaledBitmap(iconImage, width, height, false);
            }

            markerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(iconImage));
            iconSet = true;

            Double anchorU = icon.getAnchorU();
            if (anchorU != null) {
                anchorU = 0.5;
            }

            Double anchorV = icon.getAnchorV();
            if (anchorV != null) {
                anchorV = 1.0;
            }

            markerOptions.anchor(anchorU.floatValue(),
                    anchorV.floatValue());
        }

        return iconSet;
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
            Color color = style.getColor();
            if (color != null) {
                float hue = color.getHue();
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hue));
                styleSet = true;
            }
        }

        return styleSet;
    }

    /**
     * Create new polyline options populated with the feature row style
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @return polyline options populated with the feature style
     */
    public static PolylineOptions createPolylineOptions(GeoPackage geoPackage, FeatureRow featureRow) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setFeatureStyle(polylineOptions, geoPackage, featureRow);

        return polylineOptions;
    }

    /**
     * Set the feature row style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param geoPackage      GeoPackage
     * @param featureRow      feature row
     * @return true if style was set into the polyline options
     */
    public static boolean setFeatureStyle(PolylineOptions polylineOptions, GeoPackage geoPackage, FeatureRow featureRow) {

        FeatureStyleExtension styleExtension = new FeatureStyleExtension(geoPackage);
        FeatureStyle featureStyle = styleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(polylineOptions, featureStyle);
    }

    /**
     * Create new polyline options populated with the feature style
     *
     * @param featureStyle feature style
     * @return polyline options populated with the feature style
     */
    public static PolylineOptions createPolylineOptions(FeatureStyle featureStyle) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setFeatureStyle(polylineOptions, featureStyle);

        return polylineOptions;
    }

    /**
     * Set the feature style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param featureStyle    feature style
     * @return true if style was set into the polyline options
     */
    public static boolean setFeatureStyle(PolylineOptions polylineOptions, FeatureStyle featureStyle) {

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setStyle(polylineOptions, featureStyle.getStyle());

        }

        return featureStyleSet;
    }

    /**
     * Create new polyline options populated with the style
     *
     * @param style style row
     * @return polyline options populated with the style
     */
    public static PolylineOptions createPolylineOptions(StyleRow style) {

        PolylineOptions polylineOptions = new PolylineOptions();
        setStyle(polylineOptions, style);

        return polylineOptions;
    }

    /**
     * Set the style into the polyline options
     *
     * @param polylineOptions polyline options
     * @param style           style row
     * @return true if style was set into the polyline options
     */
    public static boolean setStyle(PolylineOptions polylineOptions, StyleRow style) {

        boolean styleSet = false;

        if (style != null) {
            Color color = style.getColor();
            if (color != null) {
                polylineOptions.color(color.getColorWithAlpha());
                styleSet = true;
            }
            Double width = style.getWidth();
            if (width != null) {
                polylineOptions.width(width.floatValue());
                styleSet = true;
            }
        }

        return styleSet;
    }

    /**
     * Create new polygon options populated with the feature row style
     *
     * @param geoPackage GeoPackage
     * @param featureRow feature row
     * @return polygon options populated with the feature style
     */
    public static PolygonOptions createPolygonOptions(GeoPackage geoPackage, FeatureRow featureRow) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setFeatureStyle(polygonOptions, geoPackage, featureRow);

        return polygonOptions;
    }

    /**
     * Set the feature row style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param geoPackage     GeoPackage
     * @param featureRow     feature row
     * @return true if style was set into the polygon options
     */
    public static boolean setFeatureStyle(PolygonOptions polygonOptions, GeoPackage geoPackage, FeatureRow featureRow) {

        FeatureStyleExtension styleExtension = new FeatureStyleExtension(geoPackage);
        FeatureStyle featureStyle = styleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(polygonOptions, featureStyle);
    }

    /**
     * Create new polygon options populated with the feature style
     *
     * @param featureStyle feature style
     * @return polygon options populated with the feature style
     */
    public static PolygonOptions createPolygonOptions(FeatureStyle featureStyle) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setFeatureStyle(polygonOptions, featureStyle);

        return polygonOptions;
    }

    /**
     * Set the feature style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param featureStyle   feature style
     * @return true if style was set into the polygon options
     */
    public static boolean setFeatureStyle(PolygonOptions polygonOptions, FeatureStyle featureStyle) {

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setStyle(polygonOptions, featureStyle.getStyle());

        }

        return featureStyleSet;
    }

    /**
     * Create new polygon options populated with the style
     *
     * @param style style row
     * @return polygon options populated with the style
     */
    public static PolygonOptions createPolygonOptions(StyleRow style) {

        PolygonOptions polygonOptions = new PolygonOptions();
        setStyle(polygonOptions, style);

        return polygonOptions;
    }

    /**
     * Set the style into the polygon options
     *
     * @param polygonOptions polygon options
     * @param style          style row
     * @return true if style was set into the polygon options
     */
    public static boolean setStyle(PolygonOptions polygonOptions, StyleRow style) {

        boolean styleSet = false;

        if (style != null) {
            Color color = style.getColor();
            if (color != null) {
                polygonOptions.strokeColor(color.getColorWithAlpha());
                styleSet = true;
            }
            Double width = style.getWidth();
            if (width != null) {
                polygonOptions.strokeWidth(width.floatValue());
                styleSet = true;
            }
            Color fillColor = style.getFillColor();
            if (fillColor != null) {
                polygonOptions.fillColor(fillColor.getColorWithAlpha());
                styleSet = true;
            }
        }

        return styleSet;
    }

}

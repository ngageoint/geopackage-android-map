package mil.nga.geopackage.map.features;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

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

}

package mil.nga.geopackage.map.features;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

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
     * @param density    display density: {@link android.util.DisplayMetrics#density}
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(GeoPackage geoPackage, FeatureRow featureRow, float density) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, geoPackage, featureRow, density);

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

        FeatureStyleExtension styleExtension = new FeatureStyleExtension(geoPackage);
        FeatureStyle featureStyle = styleExtension.getFeatureStyle(featureRow);

        return setFeatureStyle(markerOptions, featureStyle, density);
    }

    /**
     * Create new marker options populated with the feature style (icon or style)
     *
     * @param featureStyle feature style
     * @param density      display density: {@link android.util.DisplayMetrics#density}
     * @return marker options populated with the feature style
     */
    public static MarkerOptions createMarkerOptions(FeatureStyle featureStyle, float density) {

        MarkerOptions markerOptions = new MarkerOptions();
        setFeatureStyle(markerOptions, featureStyle, density);

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

        boolean featureStyleSet = false;

        if (featureStyle != null) {

            featureStyleSet = setIcon(markerOptions, featureStyle.getIcon(), density);

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

        MarkerOptions markerOptions = new MarkerOptions();
        setIcon(markerOptions, icon, density);

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

        boolean iconSet = false;

        if (icon != null) {

            Bitmap iconImage = createIcon(icon, density);
            markerOptions.icon(BitmapDescriptorFactory
                    .fromBitmap(iconImage));
            iconSet = true;

            Double anchorU = icon.getAnchorU();
            if (anchorU == null) {
                anchorU = 0.5;
            }

            Double anchorV = icon.getAnchorV();
            if (anchorV == null) {
                anchorV = 1.0;
            }

            markerOptions.anchor(anchorU.floatValue(),
                    anchorV.floatValue());
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

        Bitmap iconImage = null;

        if (icon != null) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(icon.getData(), 0, icon.getData().length, options);
            int dataWidth = options.outWidth;
            int dataHeight = options.outHeight;

            double styleWidth = dataWidth;
            double styleHeight = dataHeight;

            double widthDensity = DisplayMetrics.DENSITY_DEFAULT;
            double heightDensity = DisplayMetrics.DENSITY_DEFAULT;

            if (icon.getWidth() != null) {
                styleWidth = icon.getWidth();
                double widthRatio = dataWidth / styleWidth;
                widthDensity *= widthRatio;
                if (icon.getHeight() == null) {
                    heightDensity = widthDensity;
                }
            }

            if (icon.getHeight() != null) {
                styleHeight = icon.getHeight();
                double heightRatio = dataHeight / styleHeight;
                heightDensity *= heightRatio;
                if (icon.getWidth() == null) {
                    widthDensity = heightDensity;
                }
            }

            options = new BitmapFactory.Options();
            options.inDensity = (int) (Math.min(widthDensity, heightDensity) + 0.5f);
            options.inTargetDensity = (int) (DisplayMetrics.DENSITY_DEFAULT * density + 0.5f);

            iconImage = BitmapConverter.toBitmap(icon
                    .getData(), options);

            if (widthDensity != heightDensity) {

                int width = (int) (styleWidth * density + 0.5f);
                int height = (int) (styleHeight * density + 0.5f);

                if (width != iconImage.getWidth() || height != iconImage.getHeight()) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(iconImage, width, height, false);
                    iconImage.recycle();
                    iconImage = scaledBitmap;
                }

            }

        }

        return iconImage;
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

        FeatureStyleExtension styleExtension = new FeatureStyleExtension(geoPackage);
        FeatureStyle featureStyle = styleExtension.getFeatureStyle(featureRow);

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

        boolean styleSet = false;

        if (style != null) {
            Color color = style.getColor();
            if (color != null) {
                polylineOptions.color(color.getColorWithAlpha());
                styleSet = true;
            }
            Double width = style.getWidth();
            if (width != null) {
                polylineOptions.width(width.floatValue() * density);
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

        FeatureStyleExtension styleExtension = new FeatureStyleExtension(geoPackage);
        FeatureStyle featureStyle = styleExtension.getFeatureStyle(featureRow);

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

        boolean styleSet = false;

        if (style != null) {
            Color color = style.getColor();
            if (color != null) {
                polygonOptions.strokeColor(color.getColorWithAlpha());
                styleSet = true;
            }
            Double width = style.getWidth();
            if (width != null) {
                polygonOptions.strokeWidth(width.floatValue() * density);
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

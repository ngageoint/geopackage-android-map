package mil.nga.geopackage.map.test.features;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.MapsInitializer;

import junit.framework.TestCase;

import org.junit.Test;

import java.sql.SQLException;

import mil.nga.geopackage.extension.nga.style.IconRow;
import mil.nga.geopackage.io.BitmapConverter;
import mil.nga.geopackage.map.features.StyleUtils;
import mil.nga.geopackage.map.test.BaseTestCase;

public class StyleUtilsTest extends BaseTestCase {

    /**
     * Test set icon
     *
     * @throws SQLException
     */
    @Test
    public void testSetIcon() throws Exception {

        MapsInitializer.initialize(activity);

        float density = (float) DisplayMetrics.DENSITY_560 / DisplayMetrics.DENSITY_DEFAULT;

        testSetIcon(density, 40, 40, 30.0, 20.0);
        testSetIcon(density, 40, 40, 200.0, 160.0);
        testSetIcon(density, 40, 40, 10.0, 5.0);
        testSetIcon(density, 40, 40, 200.0, 10.0);

        testSetIcon(density, 40, 40, 30.0, null);
        testSetIcon(density, 40, 40, null, 160.0);
        testSetIcon(density, 40, 40, null, null);

        testSetIcon(density, 40, 80, 20.0, 40.0);
        testSetIcon(density, 40, 80, 80.0, 160.0);
        testSetIcon(density, 40, 80, 10.0, 20.0);

        testSetIcon(density, 32, 37, 23.0, 31.0);
        testSetIcon(density, 32, 37, 119.0, 130.0);
        testSetIcon(density, 32, 37, 7.0, 4.0);
        testSetIcon(density, 32, 37, 165.0, 9.0);

        testSetIcon(density, 32, 37, 23.0, null);
        testSetIcon(density, 32, 37, null, 160.0);
        testSetIcon(density, 32, 37, null, null);

        for (int i = 0; i < 100; i++) {
            density = (float) (1.0f + Math.random() * 4.0f);
            int imageWidth = 1 + (int) (Math.random() * 100);
            int imageHeight = 1 + (int) (Math.random() * 100);
            double iconWidth = 1 + (Math.random() * 100);
            double iconHeight = 1 + (Math.random() * 100);
            testSetIcon(density, imageWidth, imageHeight, iconWidth, iconHeight);
        }

        for (int i = 0; i < 100; i++) {
            density = (float) (1.0f + Math.random() * 4.0f);
            int imageWidth = 1 + (int) (Math.random() * 100);
            int imageHeight = 1 + (int) (Math.random() * 100);
            testSetIcon(density, imageWidth, imageHeight, null, null);
        }

    }

    private void testSetIcon(float density, int imageWidth, int imageHeight, Double iconWidth, Double iconHeight) throws Exception {

        Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        byte[] bytes = BitmapConverter.toBytes(bitmap, Bitmap.CompressFormat.PNG);

        IconRow icon = new IconRow();
        icon.setData(bytes);
        icon.setWidth(iconWidth);
        icon.setHeight(iconHeight);

        double styleWidth;
        double styleHeight;

        if (iconWidth == null && iconHeight == null) {
            styleWidth = imageWidth;
            styleHeight = imageHeight;
        } else if (iconWidth != null && iconHeight != null) {
            styleWidth = iconWidth;
            styleHeight = iconHeight;
        } else if (iconWidth != null) {
            styleWidth = iconWidth;
            styleHeight = (iconWidth / imageWidth) * imageHeight;
        } else {
            styleHeight = iconHeight;
            styleWidth = (iconHeight / imageHeight) * imageWidth;
        }

        Bitmap iconImage = StyleUtils.createIcon(icon, density);
        TestCase.assertNotNull(iconImage);

        double expectedWidth = density * styleWidth;
        double expectedHeight = density * styleHeight;

        int lowerWidth = (int) Math.floor(expectedWidth - 0.5);
        int upperWidth = (int) Math.ceil(expectedWidth + 0.5);
        int lowerHeight = (int) Math.floor(expectedHeight - 0.5);
        int upperHeight = (int) Math.ceil(expectedHeight + 0.5);

        TestCase.assertTrue(iconImage.getWidth() + " not between " + lowerWidth + " and " + upperWidth,
                iconImage.getWidth() >= lowerWidth
                        && iconImage.getWidth() <= upperWidth);
        TestCase.assertTrue(iconImage.getHeight() + " not between " + lowerHeight + " and " + upperHeight,
                iconImage.getHeight() >= lowerHeight
                        && iconImage.getHeight() <= upperHeight);

    }

}

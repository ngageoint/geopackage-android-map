package mil.nga.geopackage.map.tiles.features;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.features.index.FeatureIndexResults;
import mil.nga.geopackage.features.user.FeatureCursor;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.projection.ProjectionTransform;
import mil.nga.geopackage.tiles.TileBoundingBoxUtils;
import mil.nga.geopackage.tiles.features.FeatureTiles;
import mil.nga.wkb.geom.Geometry;
import mil.nga.wkb.geom.GeometryCollection;
import mil.nga.wkb.geom.LineString;
import mil.nga.wkb.geom.MultiLineString;
import mil.nga.wkb.geom.MultiPoint;
import mil.nga.wkb.geom.MultiPolygon;
import mil.nga.wkb.geom.Point;
import mil.nga.wkb.geom.Polygon;

/**
 * Google maps Feature Tiles implementation
 *
 * @author osbornb
 * @since 1.2.0
 */
public class MapFeatureTiles extends FeatureTiles {

    /**
     * Projection transform from Feature DAO to Web Mercator
     */
    private final ProjectionTransform transform;

    /**
     * Constructor
     *
     * @param context
     * @param featureDao
     */
    public MapFeatureTiles(Context context, FeatureDao featureDao) {
        super(context, featureDao);
        transform = getProjectionToWebMercatorTransform(featureDao.getProjection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bitmap drawTile(BoundingBox webMercatorBoundingBox, FeatureIndexResults results) {

        // Create bitmap and canvas
        Bitmap bitmap = createNewBitmap();
        Canvas canvas = new Canvas(bitmap);

        for (FeatureRow featureRow : results) {
            drawFeature(webMercatorBoundingBox, canvas, featureRow);
        }

        return bitmap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bitmap drawTile(BoundingBox boundingBox, FeatureCursor cursor) {

        Bitmap bitmap = createNewBitmap();
        Canvas canvas = new Canvas(bitmap);

        while (cursor.moveToNext()) {
            FeatureRow row = cursor.getRow();
            drawFeature(boundingBox, canvas, row);
        }

        cursor.close();

        return bitmap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bitmap drawTile(BoundingBox boundingBox, List<FeatureRow> featureRow) {

        Bitmap bitmap = createNewBitmap();
        Canvas canvas = new Canvas(bitmap);

        for (FeatureRow row : featureRow) {
            drawFeature(boundingBox, canvas, row);
        }

        return bitmap;
    }

    /**
     * Draw the feature on the canvas
     *
     * @param boundingBox
     * @param canvas
     * @param row
     */
    private void drawFeature(BoundingBox boundingBox, Canvas canvas, FeatureRow row) {
        GeoPackageGeometryData geomData = row.getGeometry();
        if (geomData != null) {
            Geometry geometry = geomData.getGeometry();
            drawShape(boundingBox, canvas, geometry);
        }
    }

    /**
     * Draw the geometry on the canvas
     *
     * @param boundingBox
     * @param canvas
     * @param geometry
     */
    private void drawShape(BoundingBox boundingBox, Canvas canvas, Geometry geometry) {

        switch (geometry.getGeometryType()) {

            case POINT:
                Point point = (Point) geometry;
                drawPoint(boundingBox, canvas, pointPaint, point);
                break;
            case LINESTRING:
                LineString lineString = (LineString) geometry;
                Path linePath = new Path();
                addLineString(boundingBox, linePath, lineString);
                drawLinePath(canvas, linePath);
                break;
            case POLYGON:
                Polygon polygon = (Polygon) geometry;
                Path polygonPath = new Path();
                addPolygon(boundingBox, polygonPath, polygon);
                drawPolygonPath(canvas, polygonPath);
                break;
            case MULTIPOINT:
                MultiPoint multiPoint = (MultiPoint) geometry;
                for (Point pointFromMulti : multiPoint.getPoints()) {
                    drawPoint(boundingBox, canvas, pointPaint, pointFromMulti);
                }
                break;
            case MULTILINESTRING:
                MultiLineString multiLineString = (MultiLineString) geometry;
                Path multiLinePath = new Path();
                for (LineString lineStringFromMulti : multiLineString.getLineStrings()) {
                    addLineString(boundingBox, multiLinePath, lineStringFromMulti);
                }
                drawLinePath(canvas, multiLinePath);
                break;
            case MULTIPOLYGON:
                MultiPolygon multiPolygon = (MultiPolygon) geometry;
                Path multiPolygonPath = new Path();
                for (Polygon polygonFromMulti : multiPolygon.getPolygons()) {
                    addPolygon(boundingBox, multiPolygonPath, polygonFromMulti);
                }
                drawPolygonPath(canvas, multiPolygonPath);
                break;
            // TODO other WKB shapes????
            case GEOMETRYCOLLECTION:
                GeometryCollection<Geometry> geometryCollection = (GeometryCollection) geometry;
                List<Geometry> geometries = geometryCollection.getGeometries();
                for (Geometry geometryFromCollection : geometries) {
                    drawShape(boundingBox, canvas, geometryFromCollection);
                }
                break;
        }

    }

    /**
     * Draw the line path on the canvas
     *
     * @param canvas
     * @param path
     */
    private void drawLinePath(Canvas canvas, Path path) {
        canvas.drawPath(path, linePaint);
    }

    /**
     * Draw the path on the canvas
     *
     * @param canvas
     * @param path
     */
    private void drawPolygonPath(Canvas canvas, Path path) {
        canvas.drawPath(path, polygonPaint);
        if (fillPolygon) {
            path.setFillType(Path.FillType.EVEN_ODD);
            canvas.drawPath(path, polygonFillPaint);
        }
    }

    /**
     * Add the linestring to the path
     *
     * @param boundingBox
     * @param path
     * @param lineString
     */
    private void addLineString(BoundingBox boundingBox, Path path, LineString lineString) {
        List<Point> points = lineString.getPoints();
        if (points.size() >= 2) {

            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                Point webMercatorPoint = getPoint(point);
                float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox,
                        webMercatorPoint.getX());
                float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox,
                        webMercatorPoint.getY());
                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
        }
    }

    /**
     * Add the polygon on the canvas
     *
     * @param boundingBox
     * @param path
     * @param polygon
     */
    private void addPolygon(BoundingBox boundingBox, Path path, Polygon polygon) {
        List<LineString> rings = polygon.getRings();
        if (!rings.isEmpty()) {

            // Add the polygon points
            LineString polygonLineString = rings.get(0);
            List<Point> polygonPoints = polygonLineString.getPoints();
            if (polygonPoints.size() >= 2) {
                addRing(boundingBox, path, polygonPoints);

                // Add the holes
                for (int i = 1; i < rings.size(); i++) {
                    LineString holeLineString = rings.get(i);
                    List<Point> holePoints = holeLineString.getPoints();
                    if (holePoints.size() >= 2) {
                        addRing(boundingBox, path, holePoints);
                    }
                }
            }
        }
    }

    /**
     * Add a ring
     *
     * @param boundingBox
     * @param path
     * @param points
     */
    private void addRing(BoundingBox boundingBox, Path path, List<Point> points) {

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            Point webMercatorPoint = getPoint(point);
            float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox,
                    webMercatorPoint.getX());
            float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox,
                    webMercatorPoint.getY());
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
    }

    /**
     * Draw the point on the canvas
     *
     * @param boundingBox
     * @param canvas
     * @param paint
     * @param point
     */
    private void drawPoint(BoundingBox boundingBox, Canvas canvas, Paint paint, Point point) {

        Point webMercatorPoint = getPoint(point);
        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox,
                webMercatorPoint.getX());
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox,
                webMercatorPoint.getY());

        if (pointIcon != null) {
            if (x >= 0 - pointIcon.getWidth() && x <= tileWidth + pointIcon.getWidth() && y >= 0 - pointIcon.getHeight() && y <= tileHeight + pointIcon.getHeight()) {
                canvas.drawBitmap(pointIcon.getIcon(), x - pointIcon.getXOffset(), y - pointIcon.getYOffset(), paint);
            }
        } else {
            if (x >= 0 - pointRadius && x <= tileWidth + pointRadius && y >= 0 - pointRadius && y <= tileHeight + pointRadius) {
                canvas.drawCircle(x, y, pointRadius, paint);
            }
        }

    }

    /**
     * Get the web mercator point
     *
     * @param point
     * @return web mercator point
     */
    private Point getPoint(Point point) {
        return transform.transform(point);
    }

}

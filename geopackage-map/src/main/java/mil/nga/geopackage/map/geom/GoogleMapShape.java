package mil.nga.geopackage.map.geom;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.proj.ProjectionConstants;
import mil.nga.sf.GeometryType;

/**
 * Google Map Shape
 *
 * @author osbornb
 */
public class GoogleMapShape {

    /**
     * Geometry type
     */
    private GeometryType geometryType;

    /**
     * Shape type
     */
    private GoogleMapShapeType shapeType;

    /**
     * Shape objects
     */
    private Object shape;

    /**
     * Constructor
     *
     * @param geometryType geometry type
     * @param shapeType    shape type
     * @param shape        shape
     */
    public GoogleMapShape(GeometryType geometryType,
                          GoogleMapShapeType shapeType, Object shape) {
        this.geometryType = geometryType;
        this.shapeType = shapeType;
        this.shape = shape;
    }

    /**
     * Get the geometry type
     *
     * @return geometry type
     */
    public GeometryType getGeometryType() {
        return geometryType;
    }

    /**
     * Set the geometry type
     *
     * @param geometryType geometry type
     */
    public void setGeometryType(GeometryType geometryType) {
        this.geometryType = geometryType;
    }

    /**
     * Get the shape type
     *
     * @return shape type
     */
    public GoogleMapShapeType getShapeType() {
        return shapeType;
    }

    /**
     * Set the shape type
     *
     * @param shapeType shape type
     */
    public void setShapeType(GoogleMapShapeType shapeType) {
        this.shapeType = shapeType;
    }

    /**
     * Get the shape
     *
     * @return shape
     */
    public Object getShape() {
        return shape;
    }

    /**
     * Set the shape
     *
     * @param shape shape
     */
    public void setShape(Object shape) {
        this.shape = shape;
    }

    /**
     * Removes all objects added to the map
     */
    public void remove() {

        switch (shapeType) {

            case MARKER:
                ((Marker) shape).remove();
                break;
            case POLYGON:
                ((Polygon) shape).remove();
                break;
            case POLYLINE:
                ((Polyline) shape).remove();
                break;
            case MULTI_MARKER:
                ((MultiMarker) shape).remove();
                break;
            case MULTI_POLYLINE:
                ((MultiPolyline) shape).remove();
                break;
            case MULTI_POLYGON:
                ((MultiPolygon) shape).remove();
                break;
            case POLYLINE_MARKERS:
                ((PolylineMarkers) shape).remove();
                break;
            case POLYGON_MARKERS:
                ((PolygonMarkers) shape).remove();
                break;
            case MULTI_POLYLINE_MARKERS:
                ((MultiPolylineMarkers) shape).remove();
                break;
            case MULTI_POLYGON_MARKERS:
                ((MultiPolygonMarkers) shape).remove();
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape;
                for (GoogleMapShape shapeListItem : shapeList) {
                    shapeListItem.remove();
                }
                break;
            default:
        }

    }

    /**
     * Updates visibility of all objects
     *
     * @param visible visible flag
     * @since 1.3.2
     */
    public void setVisible(boolean visible) {

        switch (shapeType) {

            case MARKER_OPTIONS:
                ((MarkerOptions) shape).visible(visible);
                break;
            case POLYLINE_OPTIONS:
                ((PolylineOptions) shape).visible(visible);
                break;
            case POLYGON_OPTIONS:
                ((PolygonOptions) shape).visible(visible);
                break;
            case MULTI_POLYLINE_OPTIONS:
                ((MultiPolylineOptions) shape).visible(visible);
                break;
            case MULTI_POLYGON_OPTIONS:
                ((MultiPolygonOptions) shape).visible(visible);
                break;
            case MARKER:
                ((Marker) shape).setVisible(visible);
                break;
            case POLYGON:
                ((Polygon) shape).setVisible(visible);
                break;
            case POLYLINE:
                ((Polyline) shape).setVisible(visible);
                break;
            case MULTI_MARKER:
                ((MultiMarker) shape).setVisible(visible);
                break;
            case MULTI_POLYLINE:
                ((MultiPolyline) shape).setVisible(visible);
                break;
            case MULTI_POLYGON:
                ((MultiPolygon) shape).setVisible(visible);
                break;
            case POLYLINE_MARKERS:
                ((PolylineMarkers) shape).setVisible(visible);
                break;
            case POLYGON_MARKERS:
                ((PolygonMarkers) shape).setVisible(visible);
                break;
            case MULTI_POLYLINE_MARKERS:
                ((MultiPolylineMarkers) shape).setVisible(visible);
                break;
            case MULTI_POLYGON_MARKERS:
                ((MultiPolygonMarkers) shape).setVisible(visible);
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape;
                for (GoogleMapShape shapeListItem : shapeList) {
                    shapeListItem.setVisible(visible);
                }
                break;
            default:
        }

    }

    /**
     * Set the z index
     *
     * @param zIndex z index
     * @since 2.0.1
     */
    public void setZIndex(float zIndex) {

        switch (shapeType) {

            case MARKER_OPTIONS:
                ((MarkerOptions) shape).zIndex(zIndex);
                break;
            case POLYLINE_OPTIONS:
                ((PolylineOptions) shape).zIndex(zIndex);
                break;
            case POLYGON_OPTIONS:
                ((PolygonOptions) shape).zIndex(zIndex);
                break;
            case MULTI_POLYLINE_OPTIONS:
                ((MultiPolylineOptions) shape).zIndex(zIndex);
                break;
            case MULTI_POLYGON_OPTIONS:
                ((MultiPolygonOptions) shape).zIndex(zIndex);
                break;
            case MARKER:
                ((Marker) shape).setZIndex(zIndex);
                break;
            case POLYGON:
                ((Polygon) shape).setZIndex(zIndex);
                break;
            case POLYLINE:
                ((Polyline) shape).setZIndex(zIndex);
                break;
            case MULTI_MARKER:
                ((MultiMarker) shape).setZIndex(zIndex);
                break;
            case MULTI_POLYLINE:
                ((MultiPolyline) shape).setZIndex(zIndex);
                break;
            case MULTI_POLYGON:
                ((MultiPolygon) shape).setZIndex(zIndex);
                break;
            case POLYLINE_MARKERS:
                ((PolylineMarkers) shape).setZIndex(zIndex);
                break;
            case POLYGON_MARKERS:
                ((PolygonMarkers) shape).setZIndex(zIndex);
                break;
            case MULTI_POLYLINE_MARKERS:
                ((MultiPolylineMarkers) shape).setZIndex(zIndex);
                break;
            case MULTI_POLYGON_MARKERS:
                ((MultiPolygonMarkers) shape).setZIndex(zIndex);
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape;
                for (GoogleMapShape shapeListItem : shapeList) {
                    shapeListItem.setZIndex(zIndex);
                }
                break;
            default:
        }

    }

    /**
     * Updates all objects that could have changed from moved markers
     */
    public void update() {

        switch (shapeType) {

            case POLYLINE_MARKERS:
                ((PolylineMarkers) shape).update();
                break;
            case POLYGON_MARKERS:
                ((PolygonMarkers) shape).update();
                break;
            case MULTI_POLYLINE_MARKERS:
                ((MultiPolylineMarkers) shape).update();
                break;
            case MULTI_POLYGON_MARKERS:
                ((MultiPolygonMarkers) shape).update();
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape;
                for (GoogleMapShape shapeListItem : shapeList) {
                    shapeListItem.update();
                }
                break;
            default:
        }

    }

    /**
     * Determines if the shape is in a valid state
     *
     * @return true if valid
     */
    public boolean isValid() {

        boolean valid = true;

        switch (shapeType) {

            case POLYLINE_MARKERS:
                valid = ((PolylineMarkers) shape).isValid();
                break;
            case POLYGON_MARKERS:
                valid = ((PolygonMarkers) shape).isValid();
                break;
            case MULTI_POLYLINE_MARKERS:
                valid = ((MultiPolylineMarkers) shape).isValid();
                break;
            case MULTI_POLYGON_MARKERS:
                valid = ((MultiPolygonMarkers) shape).isValid();
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape;
                for (GoogleMapShape shapeListItem : shapeList) {
                    valid = shapeListItem.isValid();
                    if (!valid) {
                        break;
                    }
                }
                break;
            default:
        }

        return valid;
    }

    /**
     * Get a bounding box that includes the shape
     *
     * @return bounding box
     */
    public BoundingBox boundingBox() {
        BoundingBox boundingBox = new BoundingBox(Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
        expandBoundingBox(boundingBox);
        return boundingBox;
    }

    /**
     * Expand the bounding box to include the shape
     *
     * @param boundingBox bounding box
     */
    public void expandBoundingBox(BoundingBox boundingBox) {

        switch (shapeType) {

            case LAT_LNG:
                expandBoundingBox(boundingBox, (LatLng) shape);
                break;
            case MARKER_OPTIONS:
                expandBoundingBox(boundingBox,
                        ((MarkerOptions) shape).getPosition());
                break;
            case POLYLINE_OPTIONS:
                expandBoundingBox(boundingBox,
                        ((PolylineOptions) shape).getPoints());
                break;
            case POLYGON_OPTIONS:
                expandBoundingBox(boundingBox, ((PolygonOptions) shape).getPoints());
                break;
            case MULTI_LAT_LNG:
                expandBoundingBox(boundingBox, ((MultiLatLng) shape).getLatLngs());
                break;
            case MULTI_POLYLINE_OPTIONS:
                MultiPolylineOptions multiPolylineOptions = (MultiPolylineOptions) shape;
                for (PolylineOptions polylineOptions : multiPolylineOptions
                        .getPolylineOptions()) {
                    expandBoundingBox(boundingBox, polylineOptions.getPoints());
                }
                break;
            case MULTI_POLYGON_OPTIONS:
                MultiPolygonOptions multiPolygonOptions = (MultiPolygonOptions) shape;
                for (PolygonOptions polygonOptions : multiPolygonOptions
                        .getPolygonOptions()) {
                    expandBoundingBox(boundingBox, polygonOptions.getPoints());
                }
                break;
            case MARKER:
                expandBoundingBox(boundingBox, ((Marker) shape).getPosition());
                break;
            case POLYLINE:
                expandBoundingBox(boundingBox, ((Polyline) shape).getPoints());
                break;
            case POLYGON:
                expandBoundingBox(boundingBox, ((Polygon) shape).getPoints());
                break;
            case MULTI_MARKER:
                expandBoundingBoxMarkers(boundingBox,
                        ((MultiMarker) shape).getMarkers());
                break;
            case MULTI_POLYLINE:
                MultiPolyline multiPolyline = (MultiPolyline) shape;
                for (Polyline polyline : multiPolyline.getPolylines()) {
                    expandBoundingBox(boundingBox, polyline.getPoints());
                }
                break;
            case MULTI_POLYGON:
                MultiPolygon multiPolygon = (MultiPolygon) shape;
                for (Polygon polygon : multiPolygon.getPolygons()) {
                    expandBoundingBox(boundingBox, polygon.getPoints());
                }
                break;
            case POLYLINE_MARKERS:
                expandBoundingBoxMarkers(boundingBox,
                        ((PolylineMarkers) shape).getMarkers());
                break;
            case POLYGON_MARKERS:
                expandBoundingBoxMarkers(boundingBox,
                        ((PolygonMarkers) shape).getMarkers());
                break;
            case MULTI_POLYLINE_MARKERS:
                MultiPolylineMarkers multiPolylineMarkers = (MultiPolylineMarkers) shape;
                for (PolylineMarkers polylineMarkers : multiPolylineMarkers
                        .getPolylineMarkers()) {
                    expandBoundingBoxMarkers(boundingBox,
                            polylineMarkers.getMarkers());
                }
                break;
            case MULTI_POLYGON_MARKERS:
                MultiPolygonMarkers multiPolygonMarkers = (MultiPolygonMarkers) shape;
                for (PolygonMarkers polygonMarkers : multiPolygonMarkers
                        .getPolygonMarkers()) {
                    expandBoundingBoxMarkers(boundingBox,
                            polygonMarkers.getMarkers());
                }
                break;
            case COLLECTION:
                @SuppressWarnings("unchecked")
                List<GoogleMapShape> shapeList = (List<GoogleMapShape>) shape;
                for (GoogleMapShape shapeListItem : shapeList) {
                    shapeListItem.expandBoundingBox(boundingBox);
                }
                break;
        }

    }

    /**
     * Expand the bounding box by the LatLng
     *
     * @param boundingBox bounding box
     * @param latLng      lat lng
     */
    private void expandBoundingBox(BoundingBox boundingBox, LatLng latLng) {

        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        if (boundingBox.getMinLongitude() <= 3 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH && boundingBox.getMaxLongitude() >= 3 * -ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH) {
            if (longitude < boundingBox.getMinLongitude()) {
                if (boundingBox.getMinLongitude()
                        - longitude > (longitude + (2 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH)) - boundingBox.getMaxLongitude()) {
                    longitude += (2 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH);
                }
            } else if (longitude > boundingBox.getMaxLongitude()) {
                if (longitude - boundingBox.getMaxLongitude() > boundingBox.getMinLongitude()
                        - (longitude - (2 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH))) {
                    longitude -= (2 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH);
                }
            }
        }

        if (latitude < boundingBox.getMinLatitude()) {
            boundingBox.setMinLatitude(latitude);
        }
        if (latitude > boundingBox.getMaxLatitude()) {
            boundingBox.setMaxLatitude(latitude);
        }
        if (longitude < boundingBox.getMinLongitude()) {
            boundingBox.setMinLongitude(longitude);
        }
        if (longitude > boundingBox.getMaxLongitude()) {
            boundingBox.setMaxLongitude(longitude);
        }

    }

    /**
     * Expand the bounding box by the LatLngs
     *
     * @param boundingBox bounding box
     * @param latLngs     lat lngs
     */
    private void expandBoundingBox(BoundingBox boundingBox, List<LatLng> latLngs) {
        for (LatLng latLng : latLngs) {
            expandBoundingBox(boundingBox, latLng);
        }
    }

    /**
     * Expand the bounding box by the markers
     *
     * @param boundingBox bounding box
     * @param markers     list of markers
     */
    private void expandBoundingBoxMarkers(BoundingBox boundingBox,
                                          List<Marker> markers) {
        for (Marker marker : markers) {
            expandBoundingBox(boundingBox, marker.getPosition());
        }
    }

}

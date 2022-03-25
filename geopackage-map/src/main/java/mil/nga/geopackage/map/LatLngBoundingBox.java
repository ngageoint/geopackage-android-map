package mil.nga.geopackage.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Lat Lng Bounding Box, contains left, up, right, and down coordinates as opposed to
 * the LatLngBounds two corners
 *
 * @author osbornb
 * @since 2.0.0
 */
public class LatLngBoundingBox {

    /**
     * Left coordinate
     */
    private LatLng leftCoordinate;

    /**
     * Up Coordinate
     */
    private LatLng upCoordinate;

    /**
     * Right coordinate
     */
    private LatLng rightCoordinate;

    /**
     * Down coordinate
     */
    private LatLng downCoordinate;

    /**
     * Empty constructor
     */
    public LatLngBoundingBox() {

    }

    /**
     * Constructor
     *
     * @param coordinate coordinate
     * @since 6.3.0
     */
    public LatLngBoundingBox(LatLng coordinate) {
        this(coordinate, coordinate, coordinate, coordinate);
    }

    /**
     * Constructor
     *
     * @param leftCoordinate  left coordinate
     * @param upCoordinate    up coordinate
     * @param rightCoordinate right coordinate
     * @param downCoordinate  down coordinate
     */
    public LatLngBoundingBox(LatLng leftCoordinate, LatLng upCoordinate, LatLng rightCoordinate, LatLng downCoordinate) {
        this.leftCoordinate = leftCoordinate;
        this.upCoordinate = upCoordinate;
        this.rightCoordinate = rightCoordinate;
        this.downCoordinate = downCoordinate;
    }

    /**
     * Get the left coordinate
     *
     * @return left coordinate
     */
    public LatLng getLeftCoordinate() {
        return leftCoordinate;
    }

    /**
     * Set the left coordinate
     *
     * @param leftCoordinate left coordinate
     */
    public void setLeftCoordinate(LatLng leftCoordinate) {
        this.leftCoordinate = leftCoordinate;
    }

    /**
     * Get the up coordinate
     *
     * @return up coordinate
     */
    public LatLng getUpCoordinate() {
        return upCoordinate;
    }

    /**
     * Set the up coordinate
     *
     * @param upCoordinate up coordinate
     */
    public void setUpCoordinate(LatLng upCoordinate) {
        this.upCoordinate = upCoordinate;
    }

    /**
     * Get the right coordinate
     *
     * @return right coordinate
     */
    public LatLng getRightCoordinate() {
        return rightCoordinate;
    }

    /**
     * Set the right coordinate
     *
     * @param rightCoordinate right coordinate
     */
    public void setRightCoordinate(LatLng rightCoordinate) {
        this.rightCoordinate = rightCoordinate;
    }

    /**
     * Get the down coordinate
     *
     * @return down coordinate
     */
    public LatLng getDownCoordinate() {
        return downCoordinate;
    }

    /**
     * Set the down coordinate
     *
     * @param downCoordinate down coordinate
     */
    public void setDownCoordinate(LatLng downCoordinate) {
        this.downCoordinate = downCoordinate;
    }

}
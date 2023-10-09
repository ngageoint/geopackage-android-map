package mil.nga.geopackage.map.geom;

/**
 * Enumeration of Map shape types for handling geometry hierarchies
 * 
 * @author osbornb
 */
public enum GoogleMapShapeType {

	/**
	 * {@link com.google.android.gms.maps.model.LatLng}
	 */
	LAT_LNG,

	/**
	 * {@link com.google.android.gms.maps.model.MarkerOptions}
	 */
	MARKER_OPTIONS,

	/**
	 * {@link com.google.android.gms.maps.model.PolylineOptions}
	 */
	POLYLINE_OPTIONS,

	/**
	 * {@link com.google.android.gms.maps.model.PolygonOptions}
	 */
	POLYGON_OPTIONS,

	/**
	 * {@link MultiLatLng}
	 */
	MULTI_LAT_LNG,

	/**
	 * {@link MultiPolylineOptions}
	 */
	MULTI_POLYLINE_OPTIONS,

	/**
	 * {@link MultiPolygonOptions}
	 */
	MULTI_POLYGON_OPTIONS,

	/**
	 * {@link com.google.android.gms.maps.model.Marker}
	 */
	MARKER,

	/**
	 * {@link com.google.android.gms.maps.model.Polyline}
	 */
	POLYLINE,

	/**
	 * {@link com.google.android.gms.maps.model.Polygon}
	 */
	POLYGON,

	/**
	 * {@link MultiMarker}
	 */
	MULTI_MARKER,

	/**
	 * {@link MultiPolyline}
	 */
	MULTI_POLYLINE,

	/**
	 * {@link MultiPolygon}
	 */
	MULTI_POLYGON,

	/**
	 * {@link PolylineMarkers}
	 */
	POLYLINE_MARKERS,

	/**
	 * {@link PolygonMarkers}
	 */
	POLYGON_MARKERS,

	/**
	 * {@link MultiPolylineMarkers}
	 */
	MULTI_POLYLINE_MARKERS,

	/**
	 * {@link MultiPolygonMarkers}
	 */
	MULTI_POLYGON_MARKERS,

	/**
	 * Collection of shapes
	 */
	COLLECTION;

}

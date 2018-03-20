# Change Log
All notable changes to this project will be documented in this file.
Adheres to [Semantic Versioning](http://semver.org/).

---

## 2.0.3 (TBD)

* TBD

## [2.0.2](https://github.com/ngageoint/geopackage-android-map/releases/tag/2.0.2) (03-20-2018)

* GeoPackage Overlay Tile Scaling extension support for displaying missing tiles using nearby zoom levels
* geopackage-android version updated to 2.0.2

## [2.0.1](https://github.com/ngageoint/geopackage-android-map/releases/tag/2.0.1) (02-14-2018)

* geopackage-android version updated to 2.0.1
* Visible flag support for Option typed (pre map) Google Map Shapes
* Z Index support for Google Map Shapes
* Turn off Android auto backup

## [2.0.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/2.0.0) (11-20-2017)

* WARNING - BoundingBox.java (geopackage-core) coordinate constructor arguments order changed to (min lon, min lat, max lon, max lat)
  Pre-existing calls to BoundingBox coordinate constructor should swap the min lat and max lon values
* WARNING - TileGrid.java (geopackage-core) constructor arguments order changed to (minX, minY, maxX, maxY)
  Pre-existing calls to TileGrid constructor should swap the minY and maxX values
* geopackage-android version updated to 2.0.0
* MapUtils for zoom, tolerance distance, map bounds, click bounds utilities, and point on shape determinations
* FeatureShapes for maintaining active map Shapes
* GoogleMapShapeConverter geometry simplifications
* FeatureInfoBuilder for common feature creating result messages and data
* LatLngBoundingBox container of left, right, up, down coordinates
* Polygon Options color fix when editing multi polygons
* gradle plugin updated to 2.3.3
* android maven gradle plugin updated to 2.0
* maven google dependency
* compile SDK version 26
* build tools version updated to 26.0.1
* target SDK version updated to 26
* play-services-maps version 11.2.0 (removed full play-services dependency)
* test compile of multidex 1.0.2

## [1.4.1](https://github.com/ngageoint/geopackage-android-map/releases/tag/1.4.1) (07-13-2017)

* geopackage-android version updated to 1.4.1
* Curve Polygon to Android polygon support (drawn as straight lines)

## [1.4.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/1.4.0) (06-27-2017)

* geopackage-android version updated to 1.4.0

## [1.3.2](https://github.com/ngageoint/geopackage-android-map/releases/tag/1.3.2) (06-12-2017)

* geopackage-android version updated to 1.3.2
* android-maps-utils version updated to 0.5
* build tools version updated to 25.0.3
* play-services version updated to 10.2.6
* gradle plugin updated to 2.3.2
* Android Manifest cleanup
* Google Map Shape Converter fix for single point polygon markers at creation time
* Google Map Shape Converter default constructor
* Google Map Shape Converter preserves geodesic shape options setting
* Google Map Shape visible setters
* Google Map Shape Markers size and is empty methods
* Close Polygons converting from Google Map Shapes to Geometries
* Default and optional polygon counterclockwise and clockwise conversion orientations

## [1.3.1](https://github.com/ngageoint/geopackage-android-map/releases/tag/1.3.1) (02-02-2017)

* Initial Release split from [geopackage-android](https://github.com/ngageoint/geopackage-android)

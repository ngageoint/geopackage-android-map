# Change Log
All notable changes to this project will be documented in this file.
Adheres to [Semantic Versioning](http://semver.org/).

---

## 4.0.1 (TBD)

* TBD

## [4.0.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/4.0.0) (07-14-2020)

* geopackage-android version 4.0.0
* android-maps-utils version 2.0.3
* gradle plugin updated to 4.0.1

## [3.5.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.5.0) (03-11-2020)

* geopackage-android version 3.5.0
* XYZ tile rebranding, previously referred to as Google
* Queries by specified columns
* gradle plugin updated to 3.6.1

## [3.4.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.4.0) (11-14-2019)

* compile SDK version 29
* geopackage-android version 3.4.0
* android-maps-utils version 0.6.2
* Java 8
* gradle plugin updated to 3.5.2
* gradle version 5.4.1

## [3.3.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.3.0) (07-10-2019)

* geopackage-android version 3.3.0
* play-services-maps version 17.0.0
* gradle plugin updated to 3.4.2
* gradle version 5.1.1

## [3.2.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.2.0) (04-02-2019)

* geopackage-android version 3.2.0
* play-services-maps version 16.1.0
* Feature Style cache and utilities
* Feature Shape for maintaining shapes and metadata shapes for a single feature
* Feature Shapes improvements for maintaining map shapes
* Overlay improvements for bounds checking
* Overlay map and tile density support
* Map Shape Converter stroke width setting for multiple shapes and marker shapes
* Upgrade to AndroidX support libraries
* gradle plugin updated to 3.3.2
* gradle version 4.10.1

## [3.1.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.1.0) (10-04-2018)

* geopackage-android version 3.1.0
* min SDK version updated to 16
* compile SDK version 28
* play-services-maps version 16.0.0
* Android Manifest Apache HTTP legacy library flag
* gradle plugin updated to 3.2.0
* gradle version 4.6

## [3.0.2](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.0.2) (07-27-2018)

* geopackage-android version 3.0.2

## [3.0.1](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.0.1) (07-13-2018)

* geopackage-android version 3.0.1
* gradle plugin updated to 3.1.3
* android maven gradle plugin updated to 2.1
* google repository update
* compile SDK version 27
* play-services-maps version 15.0.1
* multidex version 1.0.3 test implementation
* gradle version 4.4

## [3.0.0](https://github.com/ngageoint/geopackage-android-map/releases/tag/3.0.0) (05-17-2018)

* geopackage-android version updated to 3.0.0
* [GeoPackage Core](https://github.com/ngageoint/geopackage-core-java) new WKB dependency on [Simple Features WKB library](https://github.com/ngageoint/simple-features-wkb-java)
  * Package names in dependent classes must be updated
  * GeometryType code calls must be replaced using GeometryCodes
* [GeoPackage Core](https://github.com/ngageoint/geopackage-core-java) new projection dependency on [Simple Features Projections library](https://github.com/ngageoint/simple-features-proj-java)
  * Package names in dependent classes must be updated
  * ProjectionFactory SRS calls must be replaced using SpatialReferenceSystem projection method
  * ProjectionTransform bounding box calls must be replaced using BoundingBox transform method
* Composite Overlay for combining multiple overlays into a single map overlay
* Overlay Factory composite overlay and linked feature overlay creation methods
* Map Shape Converter bounding box transformation methods

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

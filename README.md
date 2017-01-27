# GeoPackage Android Map

### GeoPackage Android Map Lib ####

The [GeoPackage Libraries](http://ngageoint.github.io/GeoPackage/) were developed at the [National Geospatial-Intelligence Agency (NGA)](http://www.nga.mil/) in collaboration with [BIT Systems](http://www.bit-sys.com/). The government has "unlimited rights" and is releasing this software to increase the impact of government investments by providing developers with the opportunity to take things in new directions. The software use, modification, and distribution rights are stipulated within the [MIT license](http://choosealicense.com/licenses/mit/).

### Pull Requests ###
If you'd like to contribute to this project, please make a pull request. We'll review the pull request and discuss the changes. All pull request contributions to this project will be released under the MIT license.

Software source code previously released under an open source license and then modified by NGA staff is considered a "joint work" (see 17 USC § 101); it is partially copyrighted, partially public domain, and as a whole is protected by the copyrights of the non-government authors and must be released according to the terms of the original open source license.

### About ###

[GeoPackage Android Map](http://ngageoint.github.io/geopackage-android-map/) is a [GeoPackage Library](http://ngageoint.github.io/GeoPackage/) SDK implementation of the Open Geospatial Consortium [GeoPackage](http://www.geopackage.org/) [spec](http://www.geopackage.org/spec/).  It is listed as an [OGC GeoPackage Implementation](http://www.geopackage.org/#implementations_nga) by the National Geospatial-Intelligence Agency.

TODO

### Usage ###

View the latest [Javadoc](http://ngageoint.github.io/geopackage-android-map/docs/api/)

#### Implementations ####

##### GeoPackage MapCache #####

The [GeoPackage MapCache](https://github.com/ngageoint/geopackage-mapcache-android) app provides an extensive standalone example on how to use the SDK.

##### MAGE #####

The [Mobile Awareness GEOINT Environment (MAGE)](https://github.com/ngageoint/mage-android) app provides mobile situational awareness capabilities. It [uses the SDK](https://github.com/ngageoint/mage-android/search?q=GeoPackage&type=Code) to provide GeoPackage functionality.

##### DICE #####

The [Disconnected Interactive Content Explorer (DICE)](https://github.com/ngageoint/disconnected-content-explorer-android) app allows users to load and display interactive content without a network connection. It [uses the SDK](https://github.com/ngageoint/disconnected-content-explorer-android/search?q=GeoPackage&type=Code) to provide GeoPackage functionality on the map and within reports.

#### Example ####

```java

// TODO

```

### Installation ###

Pull from the [Maven Central Repository](http://search.maven.org/#artifactdetails|mil.nga.geopackage|geopackage-android-map|1.3.1|aar) (AAR, POM, Source, Javadoc)

    compile "mil.nga.geopackage:geopackage-android-map:1.3.1"

### Build ###

Build this repository using Android Studio and/or Gradle.

#### Project Setup ####

Include as repositories in your project build.gradle:

    repositories {
        jcenter()
        mavenLocal()
    }

##### Normal Build #####

Include the dependency in your module build.gradle with desired version number:

    compile "mil.nga.geopackage:geopackage-android-map:1.3.1"

As part of the build process, run the "uploadArchives" task on the geopackage-android Gradle script to update the Maven local repository.

##### Local Build #####

Replace the normal build dependency in your module build.gradle with:

    compile project(':geopackage-map')

Include in your settings.gradle:

    include ':geopackage-map'

From your project directory, link the cloned SDK directory:

    ln -s ../geopackage-android-map/geopackage-map geopackage-map

### Remote Dependencies ###

* [GeoPackage Android](https://github.com/ngageoint/geopackage-android) (The MIT License (MIT)) - GeoPackage Android Lib

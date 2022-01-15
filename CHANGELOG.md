# FCanvas changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.3.1] - 2022-01-15

### Added

* Gradle build script
* CI/CD workflows

### Changed

* LaTeX-Tutorial has been translated to Markdown.

## [1.3.0] - 2014-07-31

### Added

* Methods `getKeyPressesSinceLastAsked(int)` and `getMouseButtonPressesSinceLastAsked(int)` allow to query key and mouse events without missing or registering an event twice.
* Method `saveToImage(String)` allows to save the canvas content as image file.

## 1.2.0 - 2014-??-??

### Added

* Antialiasing options
* Getters and setters for window size
* Improved handling of key and mouse events with new `XYZsinceLastAsked` and `XYZisDown` methods.

## 1.1.2 - 2013-??-??

### Fixed

* Return value of methods `Polygon.getCentroidX()` and `Polygon.getCentroidY()` are now updated upon moving the polygon.

## 1.1.1 - 2013-??-??

### Fixed

* `FCanvas.move(long, int, int)` now works correctly for rectangles

## 1.1.0 - 2013-??-??

### Added

* Version number in code as variable `FCanvas.VERSION`
* Multiple methods to poll mouse and key events
* Method `FCanvas.isVisible()` to end animation loops gracefully

### Changed

* Components have now a clear order on the z-axis: Components that have been drawn later occlude components that have been drawn earlier.

[1.3.0]: https://github.com/CSchoel/fcanvas/releases/tag/v1.3.0

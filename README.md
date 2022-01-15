# FCanvas

FCanvas is a small and extremely simple Java library that allows novice Java programmers to draw on a canvas (similar to [Processing](https://processing.org/)) while still learning the basics of the language.

The whole library requires only the following knowledge about Java:

* Import a class.
* Call static methods like `FCanvas.drawRectangle(10,10,100,100);`.
* Use primitive data types and `String`s.
* Create integer arrays (only for drawing polygons).

Unlike [Processing](https://processing.org/), `FCanvas` leaves the control flow of the entire application up to the user.
If you want to create an animation, you can, but you have to write the animation loop yourself.

## Design choices

FCanvas differs from most graphical toolkits and takes some odd choices that sacrifice performance and stability in favor of ease of use.
These design choices are explained in the following.

### Hidden objects

Most graphical toolkits use functions that set drawing properties for all following operations.
If you have changed the line color to red once in the code, all subsequent drawing functions will draw graphical primitives with a red line color.
This can be confusing for novices, because it involves a hidden state that you have to keep in mind when writing your code.

FCanvas instead creates persistent objects for each graphical primitive, allowing to set drawing properties both *after* a primitive has been drawn and only for *that one* primitive object.
In order to identify these objects without introducing object-orientation, each drawing function returns the object ID as an `long` for future use in methods like `setFillColor(long, int, int, int)`.
The reason why I chose `long` instead of integer was so that students would have an easier time distinguishing variables that contain IDs from variables that contain color or positional values.

The persistence of graphical primitives of course come at a huge performance cost if you introduce thousands of objects that have to be tracked behind the scenes for each frame in an animation.
However, I would argue this is worth it since novice's programs will most likely not be that complex and the notion of color being a property of a rectangle or circle nicely leads over to object-oriented thinking once novices have reached this state in the course.

### No affine transformations

Usually, an affine transformation matrix is part of the drawing properties that can be set before using a drawing primitive.
However, to work confidently with this feature, a fair bit of math is required.
Novices that just want to rotate a rectangle might be discouraged by this, especially if their mathematical background is not particularly strong.

FCanvas therefore only provides a single transformation function `setRotation(long, float)`, which allows to set the rotation of a graphic object around its center in degrees.
This is much more intuitive for most novices and introduces only small costs for experts that want to create complex structures.

### Polling mechanism for input events

A big challenge for animations is handling keyboard and mouse input within a single-threaded application.
FCanvas solves this by keeping track of input events internally and allowing the user to poll which events have arrived since they have last asked.
This keeps the application behavior fairly stable for simple animation loops and removes the requirement for writing separate listeners.
It also makes it difficult to handle inputs that occur at a faster rate than the animation cycle duration, but since [human reaction time is around 250 ms](https://humanbenchmark.com/tests/reactiontime), this should not be much of an issue.

### German language

Currently all documentation of FCanvas except for this readme is written in German.
This will change in future versions as I have since come to the conviction that reading English documentation is a pain that budding computer scientists will simply have to endure.
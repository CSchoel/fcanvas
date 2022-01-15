package de.thm.mni.oop.fcanvas;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 * <p>This class implements a canvas on which you can draw using static methods.</p>
 *
 * <p>The class is essentially a wrapper for the functionality of the {@link FCanvasPanel} class,
 * which provides a simplified interface without the additional Swing methods.</p>
 *
 * <p>No knowledge of concurrency, GUI design, or objects is required to use the class.
 * An understanding of arrays is also only required for the polygon methods.</p>
 *
 * <p><b>Examples:</b></p>
 * 
 * <p>Draw and display a red circle:</p>
 * 
 * <pre>
 * long o = drawOval(10,10,100,100);
 * setFillColor(255,0,0);
 * show();
 * </pre>
 * 
 * <p>Draw a sine wave with line width 3:</p>
 * 
 * <pre>
 * int actual = 0;
 * for(int i = 0; i < 200; i++) {
 *   int tmp = (int)Math.round(Math.sin(i/30.0)*200+300);
 *   long l = drawLine(i-1,lst,i,tmp);
 *   setStrokeWidth(l,3);
 *   lst = tmp;
 * }
 * </pre>
 * 
 * <p>Change the color of a circle whenever the left or right arrow key
 * was pressed (cancel with ESC):</p>
 * 
 * <pre>
 * long id = drawOval(100,100,50,50);
 * int interval = 30;
 * while(isVisible()) {
 *   if(getKeyPressesSinceLastAsked(KeyEvent.VK_ESCAPE) &gt; 0) break;
 *   if(getKeyPressesSinceLastAsked(KeyEvent.VK_LEFT) &gt; 0) setFillColor(id,255,0,0);
 *   if(getKeyPressesSinceLastAsked(KeyEvent.VK_RIGHT) &gt; 0) setFillColor(id,0,0,255);
 *   Thread.sleep(interval);
 * }
 * </pre>
 * 
 * @author Christopher Schölzel
 */
public class FCanvas {
	/** Version {@value #VERSION} */
	public static final String VERSION = "1.3.1";
	/** File extensions recognized by {@link #saveToImage(String)} */
	public static final List<String> SUPPORTED_IMAGE_FORMATS = Arrays.asList(new String[]{"jpg","bmp","png","gif"});
	/** Ask time used for methods of the {@code sinceLastAsked} family when the event is checked for the first time. */
	public static final int FIRST_ASK_TIME = 30;
	private static Map<Integer,Long> keyLastSeen = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> keyLastAsked = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> keyPressCountLast = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> keyPressCount = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> buttonLastSeen = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> buttonLastAsked = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> buttonPressCountLast = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Long> buttonPressCount = new ConcurrentHashMap<Integer,Long>();
	private static Map<Integer,Boolean> keysDown = new ConcurrentHashMap<Integer,Boolean>();
	private static Map<Integer,Boolean> buttonsDown = new ConcurrentHashMap<Integer,Boolean>();
	private volatile static Point lastMousePoint = new Point(0,0);
	private static FCanvasGUI gui = new FCanvasGUI();
	private static boolean autoupdate = true;
	/** Private constructor to prohibit instantiation */
	private FCanvas() {}
	private static class MouseVarsUpdater extends MouseAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			lastMousePoint = e.getPoint();
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			lastMousePoint = e.getPoint();
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			buttonLastSeen.put(e.getButton(), System.currentTimeMillis());
		}
		@Override
		public void mousePressed(MouseEvent e) {
			buttonsDown.put(e.getButton(), true);
			buttonPressCount.compute(e.getButton(), (k, v) -> (v == null ? 0 : v) + 1);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			buttonsDown.put(e.getButton(), false);
		}
	}
	private static class KeyVarsUpdater extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UNDEFINED) return;
			keysDown.put(e.getKeyCode(), true);
			keyPressCount.compute(e.getKeyCode(), (k, v) -> (v == null ? 0 : v) + 1);
			keyLastSeen.put(e.getKeyCode(), System.currentTimeMillis());
		}
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UNDEFINED) return;
			keysDown.put(e.getKeyCode(), false);
		}
	}
	private static class Visualizer implements Runnable {
		@Override
		public void run() {
			gui.setVisible(true);
		}
	}
	private static class Disposer implements Runnable {
		@Override
		public void run() {
			gui.dispose();
		}
	}
	/**
	* <p>Shows the Canvas window.</p>
	*
	* <p>Graphic components can be added and modified both before
	* and after calling this method.</p>
	*
	* Caution: The running program cannot be terminated until the window
	* has been closed by the user or by calling {@link #close()}.
	*
	* @see #close()
	*/
	public static void show() {
		//TODO bug if called twice
		gui.getPanel().addKeyListener(new KeyVarsUpdater());
		gui.getPanel().addMouseListener(new MouseVarsUpdater());
		gui.getPanel().addMouseMotionListener(new MouseVarsUpdater());
		gui.getPanel().setFocusable(true);
		if(!gui.isVisible()) {
			Thread t = new Thread(new Visualizer());
			t.run();
		}
	}
	/**
	 * Closes the Canvas window.
	 * 
	 * Once the window is closed, it cannot be reopened.
	 */
	public static void close() {
		SwingUtilities.invokeLater(new Disposer());
	}
	/**
	 * Draws a rectangle on the canvas.
	 * 
	 * @param left x-coordinate of the upper-left corner of the rectangle
	 * @param top Y-coordinate of the upper-left corner of the rectangle
	 * @param width Width of the rectangle
	 * @param height Height of the rectangle
	 * @return id of the created rectangle
	 */
	public static long drawRectangle(int left, int top, int width, int height) {
		long id = gui.getPanel().addRectangle(left, top, width, height);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Draws a straight line between points (x1,y1) and (x2,y2) on the canvas.
	 * 
	 * @param x1 x-coordinate of the first point
	 * @param y1 y coordinate of the first point
	 * @param x2 x-coordinate of the second point
	 * @param y2 y coordinate of the second point
	 * @return id of the created line
	 */
	public static long drawLine(int x1, int y1, int x2, int y2) {
		long id = gui.getPanel().addLine(x1,y1,x2,y2);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Draws an oval on the canvas.
	 * 
	 * @param left x-coordinate of the top left point of the oval's bounding box
	 * @param top y-coordinate of the upper left point of the oval's bounding box
	 * @param width Width of the oval
	 * @param height Height of the oval
	 * @return id of the created oval
	 */
	public static long drawOval(int left, int top, int width, int height) {
		long id = gui.getPanel().addOval(left,top,width,height);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Draws a text on the canvas.
	 * 
	 * @param text the text to draw
	 * @param left the x-coordinate of the first character
	 * @param baseline the y-coordinate of the baseline of the first character
	 * @return id of the created text
	 */
	public static long drawText(String text, int left, int baseline) {
		long id = gui.getPanel().addText(text,left,baseline);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Draws a polygon on the canvas.
	 * 
	 * @param xcoords array of x-coordinates of polygon points
	 * @param ycoords array of y-coordinates of polygon points
	 * @return id of the created polygon
	 */
	public static long drawPolygon(int[] xcoords,int[] ycoords) {
		long id = gui.getPanel().addPolygon(xcoords,ycoords);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Removes an object from the canvas.
	 * 
	 * @param id id of the object to be removed
	 * @pre id must belong to an existing canvas object
	 */
	public static void remove(long id) {
		gui.getPanel().removeComponent(id);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Moves an object on the canvas.</p>
	 * 
	 * <p>The meaning of the x and y parameters differ depending on the type of
	 * object to be moved:</p>
	 * 
	 * <ul>
	 * <li><em>Ovals and Rectangles</em>: (x,y) is the new top left point of the object.
	 * <li><em>Lines and polygons</em>: (x,y) is the new coordinate of the first point of the object.
	 * <li><em>Text</em>: x is the new x-coordinate of the first character, y is the new y-coordinate of the
	 *      Baseline of the first character.
	 * </ul>
	 * 
	 * @param id id of the object to be moved
	 * @param x x-coordinate of the new origin point
	 * @param y Y coordinate of the new origin point
	 * @pre id must belong to an existing canvas object
	 */
	public static void move(long id, int x, int y) {
		// TODO would a delta make more sense here?
		gui.getPanel().moveComponent(id, x, y);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Changes the font size of a text object.
	 * 
	 * @param id the id of the text object
	 * @param points the new font size in pt
	 * @pre id must belong to an existing text object
	 */
	public static void setFontSize(long id, int points) {
		gui.getPanel().setFontSize(id, points);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Changes the stroke width for the object.
	 * 
	 * In the case of ovals, rectangles, lines and polygons, this means the width of the border.
	 * This method has no effect on text.
	 * 
	 * @param id the id of the object
	 * @param w the new stroke width (in pixels)
	 * @pre id must belong to an existing canvas object
	 */
	public static void setStrokeWidth(long id ,int w) {
		gui.getPanel().setStrokeWidth(id, w);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Changes the fill color of an object.
	 *
	 * Has no effect on lines and text.
	 *
	 * @param id the id of the object
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @pre id must belong to an existing canvas object
	 */
	public static void setFillColor(long id ,int r, int g, int b) {
		gui.getPanel().setFillColor(id, r,g,b,255);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Changes the fill color of an object.
	 *
	 * Has no effect on lines and text.
	 *
	 * @param id the id of the object
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @param alpha value for the alpha channel (0 to 255, 0 = transparent)
	 * @pre id must belong to an existing canvas object
	 */
	public static void setFillColor(long id ,int r, int g, int b, int alpha) {
		gui.getPanel().setFillColor(id, r,g,b,alpha);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Changes the stroke color of an object.
	 *
	 * @param id the id of the object
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @pre id must belong to an existing canvas object
	 */
	public static void setStrokeColor(long id ,int r, int g, int b) {
		gui.getPanel().setStrokeColor(id, r,g,b,255);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Changes the stroke color of an object.
	 * 
	 * With <code>alpha == 0</code> the bar becomes invisible.
	 * 
	 * @param id the id of the object
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @param alpha value for the alpha channel (0 to 255, 0 = transparent)
	 * @pre id must belong to an existing canvas object
	 */
	public static void setStrokeColor(long id ,int r, int g, int b, int alpha) {
		gui.getPanel().setStrokeColor(id, r,g,b,alpha);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Changes the rotation angle for an object.</p>
	 * 
	 * <p>Rectangles, ovals, lines and polygons are rotated around their center.</p>
	 * 
	 * <p>Texts are rotated around their origin (left,baseline).</p>
	 * 
	 * @param id the id of the object
	 * @param degrees rotation angle in degrees
	 */
	public static void setRotation(long id, float degrees) {
		gui.getPanel().setRotation(id, degrees);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Changes the background color of the canvas.</p>
	 * 
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 */
	public static void setBackgroundColor(int r, int g, int b) {
		gui.getPanel().setBackgroundColor(r, g, b);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Removes all objects from the canvas.
	 */
	public static void clear() {
		gui.getPanel().clear();
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Makes all changes to the canvas visible.</p>
	 * <p>Only required if the autoupdate function has been switched off.</p>
	 * @see #setAutoUpdate(boolean)
	 */
	public static void update() {
		gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Turns the autoupdate function on and off.</p>
	 * <p>If the feature is turned off, {@link #update()} must be used
	 * before changes to the canvas become visible.</p>
	 * @param auto if <code>true</code> autoupdate function is enabled, otherwise disabled
	 * @see #update()
	 */
	public static void setAutoUpdate(boolean auto) {
		autoupdate = auto;
	}
	/**
	 * <p>Checks if the <code>key</code> was pressed <code>since</code> or less milliseconds ago.</p>
	 * <p>The key IDs can be found in the {@link KeyEvent} class.</p>
	 * <p>If two or more different keys were pressed in the specified time period,
	 * only the last one pressed will be recognized.</p>
	 * @param key the ID of the key to check (e.g. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @param since Maximum number of milliseconds that may have elapsed since the key was pressed
	 * @return <code>true</code> if the selected key was pressed before <code>since</code> or less ms
	 * @see KeyEvent
	 * @since 1.1
	 */
	public static boolean wasKeyPressed(int key, long since) {
		long now = System.currentTimeMillis();
		long earliestAllowed = now - since;
		keyLastAsked.put(key, now);
		Long lastSeen = keyLastSeen.get(key);
		if(lastSeen != null && lastSeen > earliestAllowed) return true;
		return false;
	}
	/**
	 * <p>Checks if the <code>key</code> key has been pressed since the last time the same key was pressed
	 * using {@link #wasKeyPressed(int, long)} or {@link #wasKeyPressedSinceLastAsked(int)}.</p>
	 * <p>For the first query, if neither method has been called yet,
	 * a default query time of {@value #FIRST_ASK_TIME} ms is used.</p>
	 * @param key the ID of the key to check (e.g. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @return <code>true</code> if the selected key has been pressed since the last query
	 * @see KeyEvent
	 * @since 1.2
	 * @deprecated replaced with {@link #getKeyPressesSinceLastAsked(int)}
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean wasKeyPressedSinceLastAsked(int key) {
		long now = System.currentTimeMillis();
		Long lastAsked = keyLastAsked.get(key);
		return wasKeyPressed(key,lastAsked == null ? FIRST_ASK_TIME : now-lastAsked);
	}
	/**
	 * <p>The number of times the <code>key</code> has been pressed since
	 * the same key was queried with a call to this method.</p>
	 * <p>The first time this function is called, it returns the number of key presses since
	 * the program was started.</p>
	 * @param key the ID of the key to check (e.g. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @return Number of keystrokes with the <code>key</code> key since the last call
	 * @see KeyEvent
	 * @since 1.3
	 */
	public static int getKeyPressesSinceLastAsked(int key) {
		Long lastCount = keyPressCountLast.get(key);
		if(lastCount == null) lastCount = 0l;
		Long currentCount = keyPressCount.get(key);
		if(currentCount == null) currentCount = 0l;
		keyPressCountLast.put(key, currentCount);
		return (int)(currentCount - lastCount);
	}
	/**
	 * <p>Checks if the mouse button <code>button</code> was pressed <code>since</code> or less milliseconds ago.</p>
	 * <p>The IDs of the buttons can be found in the {@link MouseEvent} class.</p>
	 * <p>If two or more different keys were pressed in the specified time period,
	 * only the last one pressed will be recognized.</p>
	 * @param button the ID of the button to check (e.g. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @param since Maximum number of milliseconds that may have elapsed since the key was pressed
	 * @return <code>true</code> if the selected key was pressed before <code>since</code> or less ms
	 * @see MouseEvent
	 * @since 1.1
	 */
	public static boolean wasMouseButtonPressed(int button, long since) {
		long now = System.currentTimeMillis();
		long earliestAllowed = now - since;
		buttonLastAsked.put(button, now);
		Long lastSeen = buttonLastSeen.get(button);
		if(lastSeen != null && lastSeen > earliestAllowed) return true;
		return false;
	}
	/**
	 * <p>Checks if the mouse button <code>button</code> has been pressed since the last time the same button was pressed
	 * using {@link #wasMouseButtonPressed(int, long)} or {@link #wasMouseButtonPressedSinceLastAsked(int)}.</p>
	 * <p>For the first query, if neither method has been called yet,
	 * a default query time of {@value #FIRST_ASK_TIME} ms is used.</p>
	 * @param button the ID of the button to check (e.g. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @return <code>true</code> if the selected key has been pressed since the last query
	 * @see MouseEvent
	 * @deprecated replaced with {@link FCanvas#getMouseButtonPressesSinceLastAsked(int)}
	 * @since 1.2
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean wasMouseButtonPressedSinceLastAsked(int button) {
		long now = System.currentTimeMillis();
		Long lastAsked = buttonLastAsked.get(button);
		return wasMouseButtonPressed(button,lastAsked == null ? FIRST_ASK_TIME : now-lastAsked);
	}
	/**
	 * <p>The number of times the <code>button</code> mouse button has been pressed since
	 * the same key was queried with a call to this method.</p>
	 * <p>The first time this function is called, it returns the number of clicks since
	 * the program was started.</p>
	 * @param button the ID of the button to check (e.g. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @return Number of clicks of the mouse button <code>button</code> since the last call
	 * @see MouseEvent
	 * @since 1.3
	 */
	public static int getMouseButtonPressesSinceLastAsked(int button) {
		Long lastCount = buttonPressCountLast.get(button);
		if(lastCount == null) lastCount = 0l;
		Long currentCount = buttonPressCount.get(button);
		if(currentCount == null) currentCount = 0l;
		buttonPressCountLast.put(button, currentCount);
		return (int)(currentCount - lastCount);
	}
	/**
	 * Returns the x-coordinate of the last known mouse position.
	 * @return x-coordinate of the mouse
	 * @since 1.1
	 */
	public static int getLastMouseX() {
		return lastMousePoint.x;
	}
	/**
	 * Returns the y-coordinate of the last known mouse position.
	 * @return y-coordinate of the mouse
	 * @since 1.1
	 */
	public static int getLastMouseY() {
		return lastMousePoint.y;
	}
	/**
	 * Indicates whether the key <code>key</code> is currently being held down.
	 * @param key the ID of the key to check (e.g. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @return <code>true</code> if the key <code>key</code> is currently being held down
	 * @see KeyEvent
	 * @since 1.2
	 */
	public static boolean isKeyDown(int key) {
		Boolean kd = keysDown.get(key);
		return kd == null ? false : kd;
	}
	/**
	 * Indicates whether the mouse button <code>button</code> is currently being held down.
	 * @param button the ID of the button to check (e.g. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @return <code>true</code> if the mouse button <code>button</code> is currently held down
	 * @see MouseEvent
	 * @since 1.2
	 */
	public static boolean isMouseButtonDown(int button) {
		Boolean bd = buttonsDown.get(button);
		return bd == null ? false : bd;
	}
	/**
	 * Indicates whether the Ctrl key is held down.
	 * @return <code>true</code> if the user is currently holding ctrl
	 * @deprecated replaced by {@link #isKeyDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isControlDown() {
		return isKeyDown(KeyEvent.VK_CONTROL);
	}
	/**
	 * Indicates whether the Shift key is held down.
	 * @return <code>true</code> if the user is currently holding Shift
	 * @deprecated replaced by {@link #isKeyDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isShiftDown() {
		return isKeyDown(KeyEvent.VK_SHIFT);
	}
	/**
	 * Indicates whether the Alt key is held down.
	 * @return <code>true</code> if the user is currently holding Alt
	 * @deprecated replaced by {@link #isKeyDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isAltDown() {
		return isKeyDown(KeyEvent.VK_ALT);
	}
	/**
	 * Indicates whether the left mouse button is held down.
	 * @return <code>true</code> if the user is currently holding down the left mouse button
	 * @deprecated replaced by {@link #isMouseButtonDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isMouse1Down() {
		return isMouseButtonDown(MouseEvent.BUTTON1);
	}
	/**
	 * Indicates whether the middle mouse button is held down.
	 * @return <code>true</code> if the user is currently holding the middle mouse button
	 * @deprecated replaced by {@link #isMouseButtonDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isMouse2Down() {
		return isMouseButtonDown(MouseEvent.BUTTON2);
	}
	/**
	 * Indicates whether the right mouse button is held down.
	 * @return <code>true</code> if the user is currently holding down the right mouse button
	 * @deprecated replaced by {@link #isMouseButtonDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isMouse3Down() {
		return isMouseButtonDown(MouseEvent.BUTTON3);
	}
	/**
	 * <p>Indicates whether the Canvas window is currently visible.</p>
	 * 
	 * <p>This method can be used, for example, to break an animation loop
	 * when the window is closed by the user.</p>
	 * 
	 * @return <code>true</code> if the window is visible, <code>false</code> otherwise
	 */
	public static boolean isVisible() {
		return gui.isVisible();
	}
	/**
	 * Returns the current width of the drawing area.
	 * @return the width of the canvas
	 * @since 1.2
	 */
	public static int getCanvasWidth() {
		return gui.getPanel().getWidth();
	}
	/**
	 * Returns the current height of the canvas.
	 * @return the height of the canvas
	 * @since 1.2
	 */
	public static int getCanvasHeight() {
		return gui.getPanel().getHeight();
	}
	/**
	 * Changes the size of the drawing area.
	 * @param w the new width of the canvas
	 * @param h the new height of the canvas
	 * @since 1.2
	 */
	public static void setCanvasSize(int w, int h) {
		int woff = gui.getInsets().left+gui.getInsets().right;
		int hoff = gui.getInsets().top+gui.getInsets().bottom;
		gui.setSize(w+woff,h+hoff);
	}
	/**
	 * <p>Changes antialiasing settings.</p>
	 * <p>Antialiasing is turned off by default. It consumes more computing power,
	 * but gives the drawn objects smoother edges.</p>
	 * @param enabled if <code>true</code> turns on antialiasing
	 * @since 1.2
	 */
	public static void setAntialiasingEnabled(boolean enabled) {
		gui.getPanel().setAntialiasingEnabled(enabled);
	}
	/**
	 * <p>Saves the current graphic as an image file.</p>
	 * <p>The file name extension determines the storage format. Supported
	 * Formats are listed in the variable {@link #SUPPORTED_IMAGE_FORMATS}.
	 * If an xtension is not supported, PNG is used by default.</p>
	 * @param fname the name of the image file
	 * @throws IOException if the file could not be written
	 * @see #SUPPORTED_IMAGE_FORMATS
	 */
	public static void saveToImage(String fname) throws IOException {
		//TODO adjust jpg compression (default value 70% is a little harsh)
		File f = new File(fname);
		BufferedImage bi = gui.getPanel().toImage();
		String ext = f.getName().substring(f.getName().lastIndexOf('.')+1);
		ext = ext.toLowerCase();
		if(!SUPPORTED_IMAGE_FORMATS.contains(ext)) ext = "png";
		ImageIO.write(bi, ext, f);
	}
	
	public static void setPixel(int x, int y, int r, int g , int b) {
		gui.getPanel().setPixel(new Point(x,y), new Color(r,g,b));
	}
	
	public static void setPixelBuffer(int x, int y) {
		gui.getPanel().updateImageBuffer(x, y);
	}
	
	public static void main(String[] args) throws InterruptedException {
		show();
		setCanvasSize(800,800);
		setAntialiasingEnabled(true);
		setPixelBuffer(800,400);
		for(int x = 0; x < 800; x++) {
			for(int y = 0; y < 400; y++) {
				setPixel(x,y,(int)(255*(x+y)/1200.0),0,0);
			}
		}
		long tx = drawText("Version: "+VERSION,10,30);
		setFontSize(tx,20);
		long r = drawRectangle(100,100,50,20);
		long r2 = drawRectangle(200,100,50,50);
		long r3 = drawRectangle(230,100,50,50);
		setStrokeColor(r2,0,0,0,0);
		setFillColor(r2,0,0,255);
		setFillColor(r3,255,0,0,150);
		Thread.sleep(1000);
		System.out.println(gui.getPanel().getSize());
		setStrokeWidth(r,5);
		setFillColor(r,255,0,0,255);
		setStrokeColor(r,0,0,255,255);
		setRotation(r,45);
		int lst = 0;
		for(int i = 0; i < 200; i++) {
			int tmp = (int)Math.round(Math.sin(i/30.0)*200+300);
			long l = drawLine(i-1,lst,i,tmp);
			setStrokeWidth(l,3);
			setStrokeColor(l,0,255,255,255);
			setRotation(l,90);
			lst = tmp;
		}
		Thread.sleep(1000);
		long o = drawOval(10,10,100,40);
		setRotation(o,10);
		long p = drawPolygon(new int[]{300,340,360,350,330},new int[]{100,110,190,200,120});
		setRotation(p,30);
		setStrokeColor(p,255,0,255,100);
		long t = drawText("Test",10,400);
		Thread.sleep(500);
		setRotation(t,45);
		setFontSize(t,20);
		Thread.sleep(1000);
		clear();
		p = drawPolygon(new int[]{300,340,360,350,330},new int[]{100,110,190,200,120});
		long r21 = drawRectangle(100,100,200,10);
		long l21 = drawLine(100,150,200,10);
		long r22 = drawRectangle(100,50,200,10);
		setFillColor(r21,0,0,0);
		setFillColor(r22,0,0,0);
		setStrokeColor(l21,255,0,0);
		long text = drawText(wasKeyPressed(KeyEvent.VK_DOWN,1000) ? "DOWN pressed" : "DOWN not pressed",200,200);
		long text2 = drawText(wasKeyPressed(KeyEvent.VK_ESCAPE,1000) ? "ESC pressed" : "ESC not pressed",300,200);
		long kugel = drawOval(getLastMouseX(),getLastMouseY(),40,40);
		for(int i = 0; i < 150; i++) {
			move(kugel,getLastMouseX(),getLastMouseY());
			remove(text);
			remove(text2);
			text = drawText(wasKeyPressed(KeyEvent.VK_DOWN,1000) ? "DOWN pressed" : "DOWN not pressed",200,200);
			text2 = drawText(wasMouseButtonPressed(MouseEvent.BUTTON1,1000) ? "Button 1 pressed" : "Button 1 not pressed",300,200);
			setRotation(p,i*2);
			Thread.sleep(33);
		}
		long ball = FCanvas.drawOval(100, 100, 20, 20);
		int bx = 100;
		int by = 100;
		while(FCanvas.isVisible()) {
			if(isKeyDown(KeyEvent.VK_LEFT)) {
				bx -= 1;
			}
			if(isKeyDown(KeyEvent.VK_RIGHT)) {
				bx += 1;
			}
			if(isKeyDown(KeyEvent.VK_DOWN)) {
				by += 1;
			}
			if(isKeyDown(KeyEvent.VK_UP)) {
				by -= 1;
			}
			move(ball,bx,by);
			Thread.sleep(10);
		}
		close();
	}
}

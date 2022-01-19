package de.thm.mni.oop.fcanvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.thm.mni.oop.fcanvas.components.FCanvasComponent;
import de.thm.mni.oop.fcanvas.components.Line;
import de.thm.mni.oop.fcanvas.components.Oval;
import de.thm.mni.oop.fcanvas.components.Polygon;
import de.thm.mni.oop.fcanvas.components.Rectangle;
import de.thm.mni.oop.fcanvas.components.Text;

/**
 * <p>This class contains the functionality behind the FCanvas interface.</p>
 * 
 * <p>The FCanvasPanel overrides the {@link #paintComponent(Graphics)} method of the JPanel class to paint a number of internally stored objects.</p>
 * 
 * <p>The methods for adding, removing and modifying components are thread-safe and can also be called from outside the event dispatch thread.</p>
 * @author Christopher Schölzel
 */
public class FCanvasPanel extends JPanel {
	private static final long serialVersionUID = 1303009389955966295L;
	private long idcounter = 0;
	private Map<Long,FCanvasComponent> components;
	private Map<Point,Color> pixels;
	private BufferedImage imageBuffer;
	private int bufferMaxX = 0;
	private int bufferMaxY = 0;
	private List<Long> sortedKeys;
	private boolean useAntialiasing = false;
	/**
	 * Creates a new CanvasPanel with white background.
	 */
	public FCanvasPanel() {
		components = new HashMap<Long,FCanvasComponent>();
		pixels = new HashMap<Point,Color>();
		sortedKeys = new ArrayList<Long>();
		setBackground(Color.WHITE);
		updateImageBuffer(1,1);
	}
	private class ComponentAdder<T> implements Runnable {
		private Map<Long,? super T> map;
		private T obj;
		private long id;
		public ComponentAdder(Map<Long,? super T> map, long id, T obj) {
			this.map = map;
			this.obj = obj;
			this.id = id;
		}
		@Override
		public void run() {
			map.put(id, obj);
			sortedKeys.add(id);
		}
	}
	private class PixelSetter implements Runnable {
		private Point p;
		private Color c;
		public PixelSetter(Point loc, Color c) {
			this.p = loc;
			this.c = c;
		}
		public void run() {
			pixels.put(p, c);
			updateImageBuffer(p.x,p.y);
			imageBuffer.setRGB(p.x, p.y, c.getRGB());
		}
	}
	private class ComponentRemover implements Runnable {
		private long id;
		public ComponentRemover(long id) {
			this.id = id;
		}
		@Override
		public void run() {
			if (components.containsKey(id)) {
				components.remove(id);
				sortedKeys.remove(id);
			}
		}
	}
	/**
	 * Increases the size of the cached BufferedImage if necessary
	 */
	public void updateImageBuffer(int maxx, int maxy) {
		if (bufferMaxX > maxx && bufferMaxY > maxy) return;
		bufferMaxX = Math.max(bufferMaxX, maxx*2);
		bufferMaxY = Math.max(bufferMaxY, maxy*2);
		imageBuffer = new BufferedImage(bufferMaxX,bufferMaxY, BufferedImage.TYPE_INT_ARGB);
		for(Point p : pixels.keySet()) {
			imageBuffer.setRGB(p.x, p.y, pixels.get(p).getRGB());
		}
	}
	/**
	 * Repaints the Canvas and makes all changes visible.
	 */
	public synchronized void updateCanvas() {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				FCanvasPanel.this.repaint();
			}
		};
		SwingUtilities.invokeLater(run);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Copy Graphics object so changes do not affect other components
		Graphics2D g2 = (Graphics2D)g.create();
		Object val = useAntialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, val);
		//remember old transformation matrix to reset
		AffineTransform t = g2.getTransform();
		//List<Long> sortedKeys = new ArrayList<Long>(components.keySet());
		//Collections.sort(sortedKeys);
		g2.drawImage(imageBuffer, null, 0,0);
		for(long id : sortedKeys) {
			FCanvasComponent c = components.get(id);
			g2.setStroke(c.getStroke());
			if(c instanceof Rectangle) {
				Rectangle r = (Rectangle) c;
				g2.rotate(Math.toRadians(r.getRotation()),r.getLeft()+r.getWidth()/2.0,r.getTop()+r.getHeight()/2.0);
				g2.setPaint(r.getFillColor());
				// draw the filled rectangle first
				g2.fillRect(r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
				g2.setPaint(r.getStrokeColor());
				// and then the border
				g2.drawRect(r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
			} else if (c instanceof Oval) {
				Oval o = (Oval)c;
				g2.rotate(Math.toRadians(o.getRotation()),o.getLeft()+o.getWidth()/2.0,o.getTop()+o.getHeight()/2.0);
				g2.setPaint(o.getFillColor());
				g2.fillOval(o.getLeft(), o.getTop(), o.getWidth(), o.getHeight());
				g2.setPaint(o.getStrokeColor());
				g2.drawOval(o.getLeft(), o.getTop(), o.getWidth(), o.getHeight());
			} else if (c instanceof Line) {
				Line l = (Line)c;
				g2.rotate(Math.toRadians(l.getRotation()),l.getX1()+(l.getX2()-l.getX1())/2.0,l.getY1()+(l.getY2()-l.getY1())/2.0);
				g2.setPaint(l.getStrokeColor());
				g2.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
			} else if (c instanceof Text) {
				Text tx = (Text)c;
				g2.rotate(Math.toRadians(tx.getRotation()),tx.getLeft(),tx.getBaseline());
				g2.setPaint(tx.getStrokeColor());
				g2.setFont(tx.getFont());
				g2.drawString(tx.getText(), tx.getLeft(), tx.getBaseline());
			} else if (c instanceof Polygon) {
				Polygon p = (Polygon)c;
				g2.rotate(Math.toRadians(p.getRotation()),p.getCentroidX(),p.getCentroidY());
				g2.setPaint(p.getFillColor());
				g2.fillPolygon(p.getXCoords(),p.getYCoords(), p.getXCoords().length);
				g2.setPaint(p.getStrokeColor());
				g2.drawPolygon(p.getXCoords(),p.getYCoords(), p.getXCoords().length);
			}
			g2.setTransform(t);
		}
	}
	
	public synchronized void setPixel(Point p, Color c) {
		PixelSetter ps = new PixelSetter(p,c);
		SwingUtilities.invokeLater(ps);
	}
	/**
	 * <p>Adds a rectangle.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param left x-coordinate of the upper-left corner of the rectangle
	 * @param top Y-coordinate of the upper-left corner of the rectangle
	 * @param width Width of the rectangle
	 * @param height Height of the rectangle
	 * @return id of the created rectangle
	 */
	public synchronized long addRectangle(int left, int top, int width, int height) {
		final Rectangle r = new Rectangle(left,top,width,height);
		final long id = ++idcounter;
		ComponentAdder<Rectangle> add = new ComponentAdder<Rectangle>(components,id,r);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Adds an oval.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param left x-coordinate of the top left point of the oval's bounding box
	 * @param top Y coordinate of the upper left point of the bounding box of the oval
	 * @param width Width of the oval
	 * @param height Height of the oval
	 * @return id of the created oval
	 */
	public synchronized long addOval(int left, int top, int width, int height) {
		final Oval o = new Oval(left,top,width,height);
		final long id = ++idcounter;
		ComponentAdder<Oval> add = new ComponentAdder<Oval>(components,id,o);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Adds a straight line between the points (x1,y1) and (x2,y2).</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param x1 x-coordinate of the first point
	 * @param y1 y coordinate of the first point
	 * @param x2 x-coordinate of the second point
	 * @param y2 y coordinate of the second point
	 * @return id of the created line
	 */
	public synchronized long addLine(int x1, int y1, int x2, int y2) {
		final Line l = new Line(x1,y1,x2,y2);
		final long id = ++idcounter;
		ComponentAdder<Line> add = new ComponentAdder<Line>(components,id,l);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Adds a polygon.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param xcoords array of x-coordinates of polygon points
	 * @param ycoords array of y-coordinates of polygon points
	 * @return id of the created polygon
	 */
	public synchronized long addPolygon(int[] xcoords, int[] ycoords) {
		final Polygon p = new Polygon(xcoords,ycoords);
		final long id = ++idcounter;
		ComponentAdder<Polygon> add = new ComponentAdder<Polygon>(components,id,p);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Adds a text.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param text the text to draw
	 * @param left the x-coordinate of the first character
	 * @param baseline the y-coordinate of the baseline of the first character
	 * @return id of the created text
	 */
	public synchronized long addText(String text, int left, int baseline) {
		final Text t = new Text(text,left,baseline);
		final long id = ++idcounter;
		ComponentAdder<Text> add = new ComponentAdder<Text>(components,id,t);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Changes the font size of a text component.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param id the id of the text component
	 * @param points the new font size in pt
	 * @pre id must belong to an existing text component
	 */
	public synchronized void setFontSize(final long id, final int points) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				FCanvasComponent c = getFCComponent(id);
				if(!(c instanceof Text)) return;
				Text t = (Text)c;
				t.setFontSize(points);
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Changes the fill color of a component.</p>
	 * 
	 * <p>Has no effect on lines and text.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 *
	 * @param id the id of the component
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @param a value for the alpha channel (0 to 255)
	 * @pre id must belong to an existing component
	 */
	public synchronized void setFillColor(final long id,final int r,final int g,final int b,final int a) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				getFCComponent(id).setFillColor(r, g, b, a);
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Changes the stroke color of a component.</p>
	 * 
	 * <p>With <code>alpha == 0</code> the bar becomes invisible.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param id the id of the component
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @param a value for the alpha channel (0 to 255)
	 * @pre id must belong to an existing component
	 */
	public synchronized void setStrokeColor(final long id,final int r,final int g,final int b,final int a) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				getFCComponent(id).setStrokeColor(r, g, b, a);
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Changes the stroke width for the component.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * In the case of ovals, rectangles and polygons, this means the width of the border.
	 * This method has no effect on text.
	 * 
	 * @param id the id of the component
	 * @param w the new stroke width (in pixels)
	 * @pre id must belong to an existing component
	 */
	public synchronized void setStrokeWidth(final long id,final int w) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				getFCComponent(id).setStrokeWidth(w);
				FCanvasPanel.this.repaint();
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Changes the rotation angle for a component.</p>
	 * 
	 * <p>Rectangles, ovals, lines and polygons are rotated around their center.</p>
	 * 
	 * <p>Texts are rotated around their origin (left,baseline).</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param id the id of the component
	 * @param r rotation angle in degrees
	 */
	public synchronized void setRotation(final long id,final float r) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				getFCComponent(id).setRotation(r);
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Moves a component.</p>
	 * 
	 * <p>The meaning of the x and y parameters differ depending on the type of
	 * Component to move:</p>
	 * 
	 * <ul>
	 * <li><em>Ovals and Rectangles</em>: (x,y) is the new top left point of the component.
	 * <li><em>Lines and polygons</em>: (x,y) is the new coordinate of the first point of the component.
	 * <li><em>Text</em>:x is the new x-coordinate of the first character, y is the new y-coordinate of
	 * the first character's baseline.
	 * </ul>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param id id of the component to move
	 * @param x x-coordinate of the new origin point
	 * @param y Y coordinate of the new origin point
	 * @pre id must belong to an existing component
	 */
	public synchronized void moveComponent(final long id,final int x, final int y) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				getFCComponent(id).move(x, y);
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * Helper function to find a component by its id.
	 * @param id the id of the component
	 * @return the respective component or <code>null</code> if no component with this id exists
	 */
	protected FCanvasComponent getFCComponent(long id) {
		if (components.containsKey(id)) return components.get(id);
		return null;
	}
	/**
	 * <p>Removes a component.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param id id of the object to be removed
	 * @pre id must belong to an existing component
	 */
	public synchronized void removeComponent(final long id) {
		Runnable run = new ComponentRemover(id);
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Changes the background color of the panel.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 * 
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 */
	public synchronized void setBackgroundColor(final int r,final int g,final int b) {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				setBackground(new Color(r,g,b));
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Removes all objects.</p>
	 * 
	 * <p>This method is thread-safe and can also be called from outside the event dispatch thread.</p>
	 */
	public synchronized void clear() {
		Runnable run = new Runnable(){
			@Override
			public void run() {
				components.clear();
				sortedKeys.clear();
			}
		};
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Changes antialiasing settings (disabled by default).</p>
	 * @param enabled if <code>true</code> uses antialiasing
	 */
	public synchronized void setAntialiasingEnabled(boolean enabled) {
		useAntialiasing = enabled;
	}

	/**
	 * Resets the whole panel removing all components and returning all
	 * settings to their default values.
	 */
	public synchronized void reset() {
		SwingUtilities.invokeLater( () ->  {
			idcounter = 0;
			bufferMaxX = 0;
			bufferMaxY = 0;
			useAntialiasing = false;
			components = new HashMap<Long,FCanvasComponent>();
			pixels = new HashMap<Point,Color>();
			sortedKeys = new ArrayList<Long>();
		});
		setBackground(Color.WHITE);
		updateImageBuffer(1,1);
	}

	/**
	 * <p>Draws the current graphic to a BufferedImage</p>
	 * @return BufferedImage with an image of the current canvas content
	 */
	public synchronized BufferedImage toImage() {
		int type = BufferedImage.TYPE_INT_ARGB;
		type = BufferedImage.TYPE_INT_RGB;
		BufferedImage img = new BufferedImage(getWidth(),getHeight(),type);
		paintComponent(img.getGraphics());
		return img;
	}
}

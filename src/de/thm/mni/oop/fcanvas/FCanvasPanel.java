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
 * <p>Diese Klasse beinhaltet die Funktionalität hinter der FCanvas-Schnittstelle.</p>
 * 
 * <p>Das FCanvasPanel überschreibt die Methode {@link #paintComponent(Graphics)} der JPanel-Klasse,
 * um eine Anzahl von intern gespeicherten Objekten zu zeichnen.</p>
 * 
 * <p>Die Methoden zum hinzufügen, entfernen und modifizieren von Komponenten sind thread-safe und 
 * können auch von außerhalb des Event Dispatch Thread aufgerufen werden.</p>
 * 
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
	 * Erstellt ein neues CanvasPanel mit weißem Hintergrund.
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
	 * Erhöht die Größe des zwischengespeicherten BufferedImage falls nötig
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
		//Graphics-objekt kopieren, damit Veränderungen sich nicht auf andere Komponenten auswirken
		Graphics2D g2 = (Graphics2D)g.create();
		Object val = useAntialiasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, val);
		//alte Transformationsmatrix merken zum Zurücksetzen
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
				//erst muss das gefüllte Rechteck gezeichnet werden
				g2.fillRect(r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
				g2.setPaint(r.getStrokeColor());
				//und dann der Rahmen darüber
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
	 * <p>Fügt ein Rechteck hinzu.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param left x-Koordinate der linken oberen Ecke des Rechtecks
	 * @param top y-Koordinate der linken oberen Ecke des Rechtecks
	 * @param width Breite des Rechtecks
	 * @param height Höhe des Rechtecks
	 * @return id des erstellten Rechtecks
	 */
	public synchronized long addRectangle(int left, int top, int width, int height) {
		final Rectangle r = new Rectangle(left,top,width,height);
		final long id = ++idcounter;
		ComponentAdder<Rectangle> add = new ComponentAdder<Rectangle>(components,id,r);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Fügt ein Oval hinzu.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param left x-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box) des Ovals
	 * @param top  y-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box) des Ovals
	 * @param width Breite des Ovals
	 * @param height Höhe des Ovals
	 * @return id des erstellten Ovals
	 */
	public synchronized long addOval(int left, int top, int width, int height) {
		final Oval o = new Oval(left,top,width,height);
		final long id = ++idcounter;
		ComponentAdder<Oval> add = new ComponentAdder<Oval>(components,id,o);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Fügt eine gerade Linie zwischen den Punkten (x1,y1) und (x2,y2) hinzu.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param x1 x-Koordinate des ersten Punkts
	 * @param y1 y-Koordinate des ersten Punkts
	 * @param x2 x-Koordinate des zweiten Punkts
	 * @param y2 y-Koordinate des zweiten Punkts
	 * @return id der erstellten Linie
	 */
	public synchronized long addLine(int x1, int y1, int x2, int y2) {
		final Line l = new Line(x1,y1,x2,y2);
		final long id = ++idcounter;
		ComponentAdder<Line> add = new ComponentAdder<Line>(components,id,l);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Fügt ein Polygon hinzu.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param xcoords array der x-Koordinaten der Polygon-Punkte
	 * @param ycoords array der y-Koordinaten der Polygon-Punkte
	 * @return id des erstellten Polygons
	 */
	public synchronized long addPolygon(int[] xcoords, int[] ycoords) {
		final Polygon p = new Polygon(xcoords,ycoords);
		final long id = ++idcounter;
		ComponentAdder<Polygon> add = new ComponentAdder<Polygon>(components,id,p);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Fügt einen Text hinzu.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param text der Text, der gezeichnet werden soll
	 * @param left die x-Koordinate des ersten Zeichens
	 * @param baseline die y-Koordinate der Baseline des ersten Zeichens
	 * @return id des erstellten Textes
	 */
	public synchronized long addText(String text, int left, int baseline) {
		final Text t = new Text(text,left,baseline);
		final long id = ++idcounter;
		ComponentAdder<Text> add = new ComponentAdder<Text>(components,id,t);
		SwingUtilities.invokeLater(add);
		return id;
	}
	/**
	 * <p>Ändert die Schriftgröße einer Textkomponente.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param id die id der Textkomponente
	 * @param points die neue Schriftgröße in pt
	 * @pre id muss zu einer existierenden Textkomponente gehören
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
	 * <p>Ändert die Füllfarbe einer Komponente.</p>
	 * 
	 * <p>Hat keinen Effekt für Linien und Texte.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 *
	 * @param id die id der Komponente
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @param a Wert für den Alphakanal (0 bis 255)
	 * @pre id muss zu einer existierenden Komponente gehören
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
	 * <p>Ändert die Strichfarbe einer Komponente.</p>
	 * 
	 * <p>Bei <code>alpha == 0</code> wird der Strich unsichtbar.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param id die id der Komponente
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @param a Wert für den Alphakanal (0 bis 255)
	 * @pre id muss zu einer existierenden Komponente gehören
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
	 * <p>Ändert die Strichbreite für die Komponente.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * Bei Ovalen, Rechtecken und Polygonen ist damit die breite des Rands gemeint.
	 * Bei Texten hat diese Methode keinen Effekt.
	 * 
	 * @param id die id der Komponente
	 * @param w die neue Strichbreite (in Pixeln)
	 * @pre id muss zu einer existierenden Komponente gehören
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
	 * <p>Ändert den Rotationswinkel für eine Komponente.</p>
	 * 
	 * <p>Rechtecke, Ovale, Linien und Polygone werden um ihren Mittelpunkt rotiert.</p>
	 * 
	 * <p>Texte werden um ihren Ursprungspunkt (left,baseline) rotiert.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param id die id der Komponente
	 * @param r Rotationswinkel in grad
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
	 * <p>Bewegt eine Komponente.</p>
	 * 
	 * <p>Die Bedeutung der Parameter x und y unterscheiden sich je nach Typ der
	 * Komponente, die bewegt werden soll:</p>
	 * 
	 * <ul>
	 * <li><em>Ovale und Rechtecke</em>: (x,y) ist der neue linke obere Punkt der Komponente.
	 * <li><em>Linien und Polygone</em>: (x,y) ist die neue Koordinate des ersten Punktes der Komponente.
	 * <li><em>Text</em>: x ist die neue x-Koordinate des ersten Zeichens, y ist die neue y-Koordinate der
	 *     Baseline des ersten Zeichens.
	 * </ul>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param id id der Komponente, die bewegt werden soll
	 * @param x x-Koordinate des neuen Ursprungspunktes
	 * @param y y-Koordinate des neuen Ursprungspunktes
	 * @pre id muss zu einer existierenden Komponente gehören
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
	 * Hilfsunktion, um eine Komponente anhand ihrer id zu finden.
	 * @param id die id der Komponente
	 * @return die jeweilige Komponente oder <code>null</code> falls keine Komponente mit dieser id existiert
	 */
	protected FCanvasComponent getFCComponent(long id) {
		if (components.containsKey(id)) return components.get(id);
		return null;
	}
	/**
	 * <p>Entfernt eine Komponente.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param id id des Objektes, das entfernt werden soll
	 * @pre id muss zu einer existierenden Komponente gehören
	 */
	public synchronized void removeComponent(final long id) {
		Runnable run = new ComponentRemover(id);
		SwingUtilities.invokeLater(run);
	}
	/**
	 * <p>Ändert die Hintergrundfarbe des Panels.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
	 * 
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
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
	 * <p>Entfernt alle Objekte.</p>
	 * 
	 * <p>Diese Methode ist thread-safe und kann auch von außerhalb des Event Dispatch Thread
	 * aufgerufen werden.</p>
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
	 * <p>Ändert die Antialiasing-Einstellungen (per Default disabled).</p>
	 * @param enabled wenn <code>true</code> wird Antialiasing verwendet
	 */
	public synchronized void setAntialiasingEnabled(boolean enabled) {
		useAntialiasing = enabled;
	}
	/**
	 * <p>Zeichnet die aktuelle Grafik auf ein BufferedImage</p>
	 * @return BufferedImage mit einem Abbild des aktuellen Canvas-Inhalt
	 */
	public synchronized BufferedImage toImage() {
		int type = BufferedImage.TYPE_INT_ARGB;
		type = BufferedImage.TYPE_INT_RGB;
		BufferedImage img = new BufferedImage(getWidth(),getHeight(),type);
		paintComponent(img.getGraphics());
		return img;
	}
}

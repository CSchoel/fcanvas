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
 * <p>Diese Klasse implementiert einen Canvas, auf dem man mit statischen Methoden zeichnen kann.</p>
 * 
 * <p>Die Klasse ist im Wesentlichen ein Wrapper für die Funktionalität der Klasse {@link FCanvasPanel},
 * der eine vereinfachte Schnittstelle bietet ohne die zusätzlichen Swing-Methoden.</p>
 * 
 * <p>Zur Benutzung der Klasse ist weder Wissen über Nebenläufigkeit und GUI-Design noch über Objekte nötig. 
 * Ein Verständnis von Arrays ist ebenfalls nur für die Polygon-Methoden Voraussetzung.</p>
 * 
 * <p><b>Beispiele:</b></p>
 * Einen roten Kreis zeichnen und anzeigen:
 * <pre>
 * long o = drawOval(10,10,100,100);
 * setFillColor(255,0,0);
 * show();
 * </pre>
 * Eine Sinuskurve mit Strichbreite 3 zeichnen:
 * <pre>
 * int lst = 0;
 * for(int i = 0; i &lt; 200; i++) {
 *   int tmp = (int)Math.round(Math.sin(i/30.0)*200+300);
 *   long l = drawLine(i-1,lst,i,tmp);
 *   setStrokeWidth(l,3);
 *   lst = tmp;
 * }
 * </pre>
 * Die Farbe eines Kreises immer ändern wenn die Pfeiltaste nach links oder rechts
 * gedrückt wurde (abbruch mit ESC):
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
	/** File extensions, die von {@link #saveToImage(String)} erkannt werden */
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
	/** privater Konstruktor um zu verhindern, dass Objekte der Klasse erzeugt werden */
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
			Long pc = buttonPressCount.get(e.getButton());
			buttonPressCount.put(e.getButton(), pc == null ? 1 : pc+1);
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
	    	Long pc = keyPressCount.get(e.getKeyCode());
	    	keyPressCount.put(e.getKeyCode(), pc == null ? 1 : pc+1);
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
	 * <p>Zeigt das Canvas-Fenster an.</p>
	 * 
	 * <p>Grafische Komponenten können sowohl vor als auch nach dem Aufruf dieser Methode
	 * hinzugefügt und verändert werden.</p>
	 * 
	 * <p>Achtung: Das laufende Programm kann erst beendet werden, wenn das Fenster vom Benutzer oder
	 * aus dem Code mit dem Aufruf von {@link #close()} geschlossen wurde.</p>
	 * 
	 * @see #close()
	 */
	public static void show() {
		//TODO bug wenn zweimal aufgerufen
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
	 * Schließt das Canvas-Fenster.
	 * 
	 * Nachdem das Fenster geschlossen wurde, kann es nicht wieder geöffnet werden.
	 */
	public static void close() {
		SwingUtilities.invokeLater(new Disposer());
	}
	/**
	 * Zeichnet ein Rechteck auf den Canvas.
	 * 
	 * @param left x-Koordinate der linken oberen Ecke des Rechtecks
	 * @param top y-Koordinate der linken oberen Ecke des Rechtecks
	 * @param width Breite des Rechtecks
	 * @param height Höhe des Rechtecks
	 * @return id des erstellten Rechtecks
	 */
	public static long drawRectangle(int left, int top, int width, int height) {
		long id = gui.getPanel().addRectangle(left, top, width, height);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Zeichnet eine gerade Linie zwischen den Punkten (x1,y1) und (x2,y2) auf den Canvas.
	 * 
	 * @param x1 x-Koordinate des ersten Punkts
	 * @param y1 y-Koordinate des ersten Punkts
	 * @param x2 x-Koordinate des zweiten Punkts
	 * @param y2 y-Koordinate des zweiten Punkts
	 * @return id der erstellten Linie
	 */
	public static long drawLine(int x1, int y1, int x2, int y2) {
		long id = gui.getPanel().addLine(x1,y1,x2,y2);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Zeichnet ein Oval auf den Canvas.
	 * 
	 * @param left x-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box) des Ovals
	 * @param top  y-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box) des Ovals
	 * @param width Breite des Ovals
	 * @param height Höhe des Ovals
	 * @return id des erstellten Ovals
	 */
	public static long drawOval(int left, int top, int width, int height) {
		long id = gui.getPanel().addOval(left,top,width,height);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Zeichnet einen Text auf den Canvas.
	 * 
	 * @param text der Text, der gezeichnet werden soll
	 * @param left die x-Koordinate des ersten Zeichens
	 * @param baseline die y-Koordinate der Baseline des ersten Zeichens
	 * @return id des erstellten Textes
	 */
	public static long drawText(String text, int left, int baseline) {
		long id = gui.getPanel().addText(text,left,baseline);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Zeichnet ein Polygon auf den Canvas.
	 * 
	 * @param xcoords array der x-Koordinaten der Polygon-Punkte
	 * @param ycoords array der y-Koordinaten der Polygon-Punkte
	 * @return id des erstellten Polygons
	 */
	public static long drawPolygon(int[] xcoords,int[] ycoords) {
		long id = gui.getPanel().addPolygon(xcoords,ycoords);
		if (autoupdate) gui.getPanel().updateCanvas();
		return id;
	}
	/**
	 * Entfernt ein Objekt vom Canvas.
	 * 
	 * @param id id des Objektes, das entfernt werden soll
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void remove(long id) {
		gui.getPanel().removeComponent(id);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Bewegt ein Objekt auf dem Canvas.</p>
	 * 
	 * <p>Die Bedeutung der Parameter x und y unterscheiden sich je nach Typ des
	 * Objektes, das Bewegt werden soll:</p>
	 * 
	 * <ul>
	 * <li><em>Ovale und Rechtecke</em>: (x,y) ist der neue linke obere Punkt des Objektes.
	 * <li><em>Linien und Polygone</em>: (x,y) ist die neue Koordinate des ersten Punktes des Objektes.
	 * <li><em>Text</em>: x ist die neue x-Koordinate des ersten Zeichens, y ist die neue y-Koordinate der
	 *     Baseline des ersten Zeichens.
	 * </ul>
	 * 
	 * @param id id des Objektes, das bewegt werden soll
	 * @param x x-Koordinate des neuen Ursprungspunktes
	 * @param y y-Koordinate des neuen Ursprungspunktes
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void move(long id, int x, int y) {
		gui.getPanel().moveComponent(id, x, y);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Ändert die Schriftgröße eines Textobjektes.
	 * 
	 * @param id die id des Textobjektes
	 * @param points die neue Schriftgröße in pt
	 * @pre id muss zu einem existierenden Text-Objekt gehören
	 */
	public static void setFontSize(long id, int points) {
		gui.getPanel().setFontSize(id, points);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Ändert die Strichbreite für das Objekt.
	 * 
	 * Bei Ovalen, Rechtecken, Linien und Polygonen ist damit die breite des Rands gemeint.
	 * Bei Texten hat diese Methode keinen Effekt.
	 * 
	 * @param id die id des Objektes
	 * @param w die neue Strichbreite (in Pixeln)
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void setStrokeWidth(long id ,int w) {
		gui.getPanel().setStrokeWidth(id, w);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Ändert die Füllfarbe eines Objektes.
	 * 
	 * Hat keinen Effekt für Linien und Texte.
	 *
	 * @param id die id des Objektes
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void setFillColor(long id ,int r, int g, int b) {
		gui.getPanel().setFillColor(id, r,g,b,255);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Ändert die Füllfarbe eines Objektes.
	 * 
	 * Hat keinen Effekt für Linien und Texte.
	 *
	 * @param id die id des Objektes
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @param alpha Wert für den Alphakanal (0 bis 255, 0 = durchsichtig)
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void setFillColor(long id ,int r, int g, int b, int alpha) {
		gui.getPanel().setFillColor(id, r,g,b,alpha);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Ändert die Strichfarbe eines Objektes.
	 * 
	 * @param id die id des Objektes
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void setStrokeColor(long id ,int r, int g, int b) {
		gui.getPanel().setStrokeColor(id, r,g,b,255);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Ändert die Strichfarbe eines Objektes.
	 * 
	 * Bei <code>alpha == 0</code> wird der Strich unsichtbar.
	 * 
	 * @param id die id des Objektes
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @param alpha Wert für den Alphakanal (0 bis 255, 0 = durchsichtig)
	 * @pre id muss zu einem existierenden Canvas-Objekt gehören
	 */
	public static void setStrokeColor(long id ,int r, int g, int b, int alpha) {
		gui.getPanel().setStrokeColor(id, r,g,b,alpha);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Ändert den Rotationswinkel für ein Objekt.</p>
	 * 
	 * <p>Rechtecke, Ovale, Linien und Polygone werden um ihren Mittelpunkt rotiert.</p>
	 * 
	 * <p>Texte werden um ihren Ursprungspunkt (left,baseline) rotiert.</p>
	 * 
	 * @param id die id des Objektes
	 * @param degrees Rotationswinkel in grad
	 */
	public static void setRotation(long id, float degrees) {
		gui.getPanel().setRotation(id, degrees);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Ändert die Hintergrundfarbe des Canvas.</p>
	 * 
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 */
	public static void setBackgroundColor(int r, int g, int b) {
		gui.getPanel().setBackgroundColor(r, g, b);
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * Entfernt alle Objekte vom Canvas.
	 */
	public static void clear() {
		gui.getPanel().clear();
		if (autoupdate) gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Macht alle Änderungen am Canvas sichtbar.</p>
	 * <p>Wird nur benötigt, wenn die autoupdate-Funktion ausgeschaltet wurde.</p>
	 * @see #setAutoUpdate(boolean)
	 */
	public static void update() {
		gui.getPanel().updateCanvas();
	}
	/**
	 * <p>Schaltet die Autoupdate-Funktion ein und aus.</p>
	 * <p>Wenn die Funktion ausgeschaltet ist, muss {@link #update()} verwendet
	 * werden, bevor Änderungen am Canvas sichtbar werden.</p>
	 * @param auto wenn <code>true</code> wird Autoupdate-Funktion eingeschaltet, ansonsten ausgeschaltet
	 * @see #update()
	 */
	public static void setAutoUpdate(boolean auto) {
		autoupdate = auto;
	}
	/**
	 * <p>Prüft ob vor <code>since</code> oder weniger millisekunden die Taste <code>key</code>
	 * gedrückt wurde.</p>
	 * <p>Die IDs der Tasten sind in der Klasse {@link KeyEvent} zu finden.</p>
	 * <p>Wurden zwei oder mehr verschiedene Tasten in dem angegebenen Zeitraum gedrückt,
	 * wird nur die zuletzt gedrückte erkannt.</p>
	 * @param key die ID der Taste, die überprüft werden soll (z.B. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @param since Anzahl an Millisekunden die seit dem Drücken der Taste höchstens vergangen sein darf
	 * @return <code>true</code> wenn die gewählte Taste vor <code>since</code> oder weniger ms gedrückt wurde
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
	 * <p>Prüft ob die Taste <code>key</code> gedrückt wurde seit die selbe Taste zum letzten mal mit 
	 * {@link #wasKeyPressed(int, long)} oder {@link #wasKeyPressedSinceLastAsked(int)} abgefragt wurde.</p>
	 * <p>Für die erste Abfrage, wenn noch keine der beiden Methoden aufgerufen wurde, 
	 * wird eine Default-Abfragezeit von {@value #FIRST_ASK_TIME} ms verwendet.</p>
	 * @param key die ID der Taste, die überprüft werden soll (z.B. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @return <code>true</code> wenn die gewählte Taste seit dem letzten Abfragen gedrückt wurde
	 * @see KeyEvent
	 * @since 1.2
	 * @deprecated ersetzt durch {@link #getKeyPressesSinceLastAsked(int)}
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean wasKeyPressedSinceLastAsked(int key) {
		long now = System.currentTimeMillis();
		Long lastAsked = keyLastAsked.get(key);
		return wasKeyPressed(key,lastAsked == null ? FIRST_ASK_TIME : now-lastAsked);
	}
	/**
	 * <p>Gibt an, wie oft die Taste <code>key</code> gedrückt wurde, seit
	 * die selbe Taste mit einem Aufruf dieser Methode abgefragt wurde.</p>
	 * <p>Beim ersten Aufruf dieser Funktion wird die Anzahl aller Tastendrücke seit
	 * dem Start des Programms zurückgegeben.</p>
	 * @param key die ID der Taste, die überprüft werden soll (z.B. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @return Anzahl der Tastendrücke mit der Taste <code>key</code> seit dem letzten Aufruf
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
	 * <p>Prüft ob vor <code>since</code> oder weniger millisekunden die Maustaste <code>button</code>
	 * gedrückt wurde.</p>
	 * <p>Die IDs der Tasten sind in der Klasse {@link MouseEvent} zu finden.</p>
	 * <p>Wurden zwei oder mehr verschiedene Tasten in dem angegebenen Zeitraum gedrückt,
	 * wird nur die zuletzt gedrückte erkannt.</p>
	 * @param button die ID der Taste, die überprüft werden soll (z.B. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @param since Anzahl an Millisekunden die seit dem Drücken der Taste höchstens vergangen sein darf
	 * @return <code>true</code> wenn die gewählte Taste vor <code>since</code> oder weniger ms gedrückt wurde
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
	 * <p>Prüft ob die Maustaste <code>button</code> gedrückt wurde seit die selbe Taste zum letzten mal mit 
	 * {@link #wasMouseButtonPressed(int, long)} oder {@link #wasMouseButtonPressedSinceLastAsked(int)} abgefragt wurde.</p>
	 * <p>Für die erste Abfrage, wenn noch keine der beiden Methoden aufgerufen wurde, 
	 * wird eine Default-Abfragezeit von {@value #FIRST_ASK_TIME} ms verwendet.</p>
	 * @param button die ID der Taste, die überprüft werden soll (z.B. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @return <code>true</code> wenn die gewählte Taste seit dem letzten Abfragen gedrückt wurde
	 * @see MouseEvent
	 * @deprecated ersetzt durch {@link FCanvas#getMouseButtonPressesSinceLastAsked(int)}
	 * @since 1.2
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean wasMouseButtonPressedSinceLastAsked(int button) {
		long now = System.currentTimeMillis();
		Long lastAsked = buttonLastAsked.get(button);
		return wasMouseButtonPressed(button,lastAsked == null ? FIRST_ASK_TIME : now-lastAsked);
	}
	/**
	 * <p>Gibt an, wie oft die Maustaste <code>button</code> gedrückt wurde, seit
	 * die selbe Taste mit einem Aufruf dieser Methode abgefragt wurde.</p>
	 * <p>Beim ersten Aufruf dieser Funktion wird die Anzahl aller Klicks seit
	 * dem Start des Programms zurückgegeben.</p>
	 * @param button die ID der Taste, die überprüft werden soll (z.B. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @return Anzahl der Klicks mit der Maustaste <code>button</code> seit dem letzten Aufruf
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
	 * Gibt die x-Koordinate der letzten bekannten Mausposition zurück.
	 * @return x-Koordinate der Maus
	 * @since 1.1
	 */
	public static int getLastMouseX() {
		return lastMousePoint.x;
	}
	/**
	 * Gibt die y-Koordinate der letzten bekannten Mausposition zurück.
	 * @return y-Koordinate der Maus
	 * @since 1.1
	 */
	public static int getLastMouseY() {
		return lastMousePoint.y;
	}
	/**
	 * Gibt an, ob die Taste <code>key</code> gerade gedrückt gehalten wird.
	 * @param key die ID der Taste, die überprüft werden soll (z.B. {@link KeyEvent#VK_0}, {@link KeyEvent#VK_ALT})
	 * @return <code>true</code> wenn die Taste <code>key</code> gerade gedrückt gehalten wird
	 * @see KeyEvent
	 * @since 1.2
	 */
	public static boolean isKeyDown(int key) {
		Boolean kd = keysDown.get(key);
		return kd == null ? false : kd;
	}
	/**
	 * Gibt an, ob die Maustaste <code>button</code> gerade gedrückt gehalten wird.
	 * @param button die ID der Taste, die überprüft werden soll (z.B. {@link MouseEvent#BUTTON1}, {@link MouseEvent#BUTTON3})
	 * @return <code>true</code> wenn die Taste <code>button</code> gerade gedrückt gehalten wird
	 * @see MouseEvent
	 * @since 1.2
	 */
	public static boolean isMouseButtonDown(int button) {
		Boolean bd = buttonsDown.get(button);
		return bd == null ? false : bd;
	}
	/**
	 * Gibt an, ob die Strg-Taste gedrückt gehalten wird.
	 * @return <code>true</code> wenn der Benutzer gerade Strg gedrückt hält
	 * @deprecated ersetzt durch {@link #isKeyDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isControlDown() {
		return isKeyDown(KeyEvent.VK_CONTROL);
	}
	/**
	 * Gibt an, ob die Shift-Taste gedrückt gehalten wird.
	 * @return <code>true</code> wenn der Benutzer gerade Shift gedrückt hält
	 * @deprecated ersetzt durch {@link #isKeyDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isShiftDown() {
		return isKeyDown(KeyEvent.VK_SHIFT);
	}
	/**
	 * Gibt an, ob die Alt-Taste gedrückt gehalten wird.
	 * @return <code>true</code> wenn der Benutzer gerade Alt gedrückt hält
	 * @deprecated ersetzt durch {@link #isKeyDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isAltDown() {
		return isKeyDown(KeyEvent.VK_ALT);
	}
	/**
	 * Gibt an, ob die linke Maustaste gedrückt gehalten wird.
	 * @return <code>true</code> wenn der Benutzer gerade die linke Maustaste gedrückt hält
	 * @deprecated ersetzt durch {@link #isMouseButtonDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isMouse1Down() {
		return isMouseButtonDown(MouseEvent.BUTTON1);
	}
	/**
	 * Gibt an, ob die mittlere Maustaste gedrückt gehalten wird.
	 * @return <code>true</code> wenn der Benutzer gerade die mittlere Maustaste gedrückt hält
	 * @deprecated ersetzt durch {@link #isMouseButtonDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isMouse2Down() {
		return isMouseButtonDown(MouseEvent.BUTTON2);
	}
	/**
	 * Gibt an, ob die rechte Maustaste gedrückt gehalten wird.
	 * @return <code>true</code> wenn der Benutzer gerade die rechte Maustaste gedrückt hält
	 * @deprecated ersetzt durch {@link #isMouseButtonDown(int)}
	 * @since 1.1
	 */
	@Deprecated(since="1.3", forRemoval=true)
	public static boolean isMouse3Down() {
		return isMouseButtonDown(MouseEvent.BUTTON3);
	}
	/**
	 * <p>Gibt an, ob das Canvas-Fenster gerade angezeigt wird.</p>
	 * 
	 * <p>Diese Methode kann zum Beispiel dazu verwendet werden, um eine Animations-Schleife
	 * abzubrechen, wenn das Fenster vom Benutzer geschlossen wird.</p>
	 * 
	 * @return <code>true</code> wenn das Fenster sichtbar ist, <code>false</code> sonst
	 */
	public static boolean isVisible() {
		return gui.isVisible();
	}
	/**
	 * Gibt die aktuelle Breite des Zeichenbereichs zurück. 
	 * @return die Breite des Canvas
	 * @since 1.2
	 */
	public static int getCanvasWidth() {
		return gui.getPanel().getWidth();
	}
	/**
	 * Gibt die aktuelle Höhe des Zeichenbereichs zurück. 
	 * @return die Höhe des Canvas
	 * @since 1.2
	 */
	public static int getCanvasHeight() {
		return gui.getPanel().getHeight();
	}
	/**
	 * Ändert die größe des Zeichenbereichs.
	 * @param w die neue Breite des Zeichenbereichs
	 * @param h die neue Höhe des Zeichenbereichs
	 * @since 1.2
	 */
	public static void setCanvasSize(int w, int h) {
		int woff = gui.getInsets().left+gui.getInsets().right;
		int hoff = gui.getInsets().top+gui.getInsets().bottom;
		gui.setSize(w+woff,h+hoff);
	}
	/**
	 * <p>Ändert die Antialiasing-Einstellungen.</p>
	 * <p>Antialiasing ist per Default ausgeschaltet. Es verbraucht mehr Rechenleistung,
	 * gibt den gezeichneten Objekten dafür aber glattere Kanten.</p>
	 * @param enabled wenn <code>true</code> wird Antialiasing eingeschaltet
	 * @since 1.2
	 */
	public static void setAntialiasingEnabled(boolean enabled) {
		gui.getPanel().setAntialiasingEnabled(enabled);
	}
	/**
	 * <p>Speichert die aktuelle Grafik als Bilddatei.</p>
	 * <p>Die Dateinamenerweiterung bestimmt dabei das Speicherformat. Unterstützte
	 * Formate stehen in der Variable {@link #SUPPORTED_IMAGE_FORMATS}. Wird eine
	 * Erweiterung nicht unterstützt, wird automatisch PNG als Format verwendet.</p>
	 * @param fname der name der Bilddatei
	 * @throws IOException wenn die Datei nicht beschrieben werden konnte
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

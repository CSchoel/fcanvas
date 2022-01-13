package de.thm.mni.oop.fcanvas.components;

/**
 * Stellt ein Rechteck dar.
 * @author Christopher Schölzel
 */
public class Rectangle extends AbstractComponent {
	private int top;
	private int left;
	private int width;
	private int height;
	/**
	 * Erstellt ein neues Rechteck
	 * @param left x-Koordinate des linken oberen Punktes
	 * @param top y-Koordinate des linken oberen Punktes
	 * @param width Breite des Rechtecks
	 * @param height Höhe des Rechtecks
	 */
	public Rectangle(int left, int top, int width, int height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
		this.rotation = 0;
	}
	/**
	 * Gibt die y-Koordinate der linken oberen Ecke zurück
	 * @return y-Koordinate der linken oberen Ecke
	 */
	public int getTop() {
		return top;
	}
	/**
	 * Ändert die y-Koordinate der linken oberen Ecke
	 * @param top neue y-Koordinate
	 */
	public void setTop(int top) {
		this.top = top;
	}
	/**
	 * Gibt die x-Koordinate der linken oberen Ecke zurück
	 * @return x-Koordinate de rlinken oberen Ecke
	 */
	public int getLeft() {
		return left;
	}
	/**
	 * Ändert die x-Koordinate der linken oberen Ecke
	 * @param left neue x-Koordinate
	 */
	public void setLeft(int left) {
		this.left = left;
	}
	/**
	 * Gibt die Breite des Rechtecks zurück.
	 * @return Breite des Rechtecks
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Ändert die Breite des Rechtecks.
	 * @param width neue Breite
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * Gibt die Höhe des Rechtecks zurück.
	 * @return Höhe des Rechtecks
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Ändert die Höhe des Rechtecks.
	 * @param height neue Höhe
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	@Override
	public void move(int x, int y) {
		left = x;
		top = y;
	}
}

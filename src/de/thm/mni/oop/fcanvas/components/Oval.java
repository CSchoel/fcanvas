package de.thm.mni.oop.fcanvas.components;

/**
 * Stellt ein Oval dar.
 * 
 * @author Christopher Schölzel
 */
public class Oval extends AbstractComponent {
	private int left;
	private int top;
	private int width;
	private int height;
	/**
	 * Erstellt ein neues Oval.
	 * @param left x-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box)
	 * @param top y-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box)
	 * @param width breite des Ovals
	 * @param height höhe des Ovals
	 */
	public Oval(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	/**
	 * Gibt die x-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box) zurück
	 * @return x-Koordinate des linken oberen Punktes der Bounding Box
	 */
	public int getLeft() {
		return left;
	}
	/**
	 * Ändert die x-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box)
	 * @param left neue x-Koordinate des linken oberen Punktes der Bounding Box
	 */
	public void setLeft(int left) {
		this.left = left;
	}
	/**
	 * Gibt die y-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box) zurück
	 * @return y-Koordinate des linken oberen Punktes der Bounding Box
	 */
	public int getTop() {
		return top;
	}
	/**
	 * Ändert die y-Koordinate des linken oberen Punktes des einschließenden Rechtecks (Bounding Box)
	 * @param top neue y-Koordinate des linken oberen Punktes der Bounding Box
	 */
	public void setTop(int top) {
		this.top = top;
	}
	/**
	 * Gibt die Breite des Ovals zurück
	 * @return breite des Ovals
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Ändert die Breite des Ovals.
	 * @param width Breite des Ovals
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * Gibt die Breite des Ovals zurück
	 * @return breite des Ovals
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Ändert die Höhe des Ovals.
	 * @param height Höhe des Ovals
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

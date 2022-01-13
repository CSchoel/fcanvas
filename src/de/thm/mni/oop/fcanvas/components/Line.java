package de.thm.mni.oop.fcanvas.components;

/**
 * Stellt eine Linie zwischen zwei Punkten dar.
 * @author Christopher Schölzel
 */
public class Line extends AbstractComponent {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	/**
	 * Erstellt eine neue Linie
	 * @param x1 x-Koordinate des ersten Punkts
	 * @param y1 y-Koordinate des ersten Punkts
	 * @param x2 x-Koordinate des zweiten Punkts
	 * @param y2 y-Koordinate des zweiten Punkts
	 */
	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	/**
	 * Gibt die x-Koordinate des ersten Punktes zurück.
	 * @return x-Koordinate des ersten Puntks
	 */
	public int getX1() {
		return x1;
	}
	/**
	 * Ändert die x-Koordinate des ersten Punktes.
	 * @param x1 die neue x-Koordinate des ersten Punktes.
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}
	/**
	 * Gibt die y-Koordinate des ersten Punktes zurück.
	 * @return y-Koordinate des ersten Puntks
	 */
	public int getY1() {
		return y1;
	}
	/**
	 * Ändert die y-Koordinate des ersten Punktes.
	 * @param y1 die neue y-Koordinate des ersten Punktes.
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}
	/**
	 * Gibt die x-Koordinate des zweiten Punktes zurück.
	 * @return x-Koordinate des zweiten Puntks
	 */
	public int getX2() {
		return x2;
	}
	/**
	 * Ändert die x-Koordinate des zweiten Punktes.
	 * @param x2 die neue x-Koordinate des zweiten Punktes.
	 */
	public void setX2(int x2) {
		this.x2 = x2;
	}
	/**
	 * Gibt die y-Koordinate des zweiten Punktes zurück.
	 * @return y-Koordinate des zweiten Puntks
	 */
	public int getY2() {
		return y2;
	}
	/**
	 * Ändert die y-Koordinate des zweiten Punktes.
	 * @param y2 die neue y-Koordinate des zweiten Punktes.
	 */
	public void setY2(int y2) {
		this.y2 = y2;
	}
	@Override
	public void move(int x, int y) {
		int shiftx = x-x1;
		int shifty = y-y1;
		this.x1 = x1 + shiftx;
		this.y1 = y1 + shifty;
		this.x2 = x2 + shiftx;
		this.y2 = y2 + shifty;
	}
}

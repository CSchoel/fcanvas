package de.thm.mni.oop.fcanvas.components;

/**
 * Represents a line between two points.
 * @author Christopher Schölzel
 */
public class Line extends AbstractComponent {
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	/**
	 * Creates a new line
	 * @param x1 x-coordinate of the first point
	 * @param y1 y coordinate of the first point
	 * @param x2 x-coordinate of the second point
	 * @param y2 y coordinate of the second point
	 */
	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	/**
	 * Returns the x coordinate of the first point.
	 * @return x-coordinate of the first point
	 */
	public int getX1() {
		return x1;
	}
	/**
	 * Changes the x-coordinate of the first point.
	 * @param x1 the new x coordinate of the first point.
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}
	/**
	 * Returns the y coordinate of the first point.
	 * @return y-coordinate of the first point
	 */
	public int getY1() {
		return y1;
	}
	/**
	 * Changes the y-coordinate of the first point.
	 * @param y1 the new y coordinate of the first point.
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}
	/**
	 * Returns the x coordinate of the second point.
	 * @return x-coordinate of the second point
	 */
	public int getX2() {
		return x2;
	}
	/**
	 * Changes the x-coordinate of the second point.
	 * @param x2 the new x-coordinate of the second point.
	 */
	public void setX2(int x2) {
		this.x2 = x2;
	}
	/**
	 * Returns the y coordinate of the second point.
	 * @return Y-coordinate of the second point
	 */
	public int getY2() {
		return y2;
	}
	/**
	 * Changes the y-coordinate of the second point.
	 * @param y2 the new y coordinate of the second point.
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

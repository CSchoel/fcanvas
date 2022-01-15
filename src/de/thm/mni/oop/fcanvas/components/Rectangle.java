package de.thm.mni.oop.fcanvas.components;

/**
 * Represents a rectangle.
 * @author Christopher Schölzel
 */
public class Rectangle extends AbstractComponent {
	private int top;
	private int left;
	private int width;
	private int height;
	/**
	 * Creates a new rectangle
	 * @param left x-coordinate of the top left point
	 * @param top Y coordinate of the top left point
	 * @param width Width of the rectangle
	 * @param height Height of the rectangle
	 */
	public Rectangle(int left, int top, int width, int height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
		this.rotation = 0;
	}
	/**
	 * Returns the y-coordinate of the upper-left corner
	 * @return y-coordinate of the upper-left corner
	 */
	public int getTop() {
		return top;
	}
	/**
	 * Changes the y-coordinate of the top-left corner
	 * @param top new y-coordinate
	 */
	public void setTop(int top) {
		this.top = top;
	}
	/**
	 * Returns the x-coordinate of the upper-left corner
	 * @return x-coordinate of the upper left corner
	 */
	public int getLeft() {
		return left;
	}
	/**
	 * Changes the x coordinate of the top left corner
	 * @param left new x coordinate
	 */
	public void setLeft(int left) {
		this.left = left;
	}
	/**
	 * Returns the width of the rectangle.
	 * @return Rectangle width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Changes the width of the rectangle.
	 * @param width new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * Returns the height of the rectangle.
	 * @return Rectangle height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Changes the height of the rectangle.
	 * @param height new height
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

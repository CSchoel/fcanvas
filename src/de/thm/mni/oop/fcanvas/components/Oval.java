package de.thm.mni.oop.fcanvas.components;

/**
 * Represents an oval.
 * 
 * @author Christopher Schölzel
 */
public class Oval extends AbstractComponent {
	private int left;
	private int top;
	private int width;
	private int height;
	/**
	 * Creates a new oval.
	 * @param left x-coordinate of the upper left point of the enclosing rectangle (bounding box)
	 * @param top y coordinate of the upper left point of the enclosing rectangle (bounding box)
	 * @param width Width of the oval
	 * @param height Height of the oval
	 */
	public Oval(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}
	/**
	 * Returns the x coordinate of the upper left point of the enclosing rectangle (bounding box).
	 * @return x-coordinate of the upper left point of the bounding box
	 */
	public int getLeft() {
		return left;
	}
	/**
	 * Changes the x-coordinate of the upper left point of the enclosing rectangle (bounding box)
	 * @param left new x-coordinate of the upper left point of the bounding box
	 */
	public void setLeft(int left) {
		this.left = left;
	}
	/**
	 * Returns the y-coordinate of the upper left point of the enclosing rectangle (bounding box).
	 * @return Y coordinate of the upper left point of the bounding box
	 */
	public int getTop() {
		return top;
	}
	/**
	 * Changes the y-coordinate of the upper left point of the enclosing rectangle (bounding box)
	 * @param top new y-coordinate of the top left point of the bounding box
	 */
	public void setTop(int top) {
		this.top = top;
	}
	/**
	 * Returns the width of the oval
	 * @return width of the oval
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * Changes the width of the oval.
	 * @param width Width of the oval
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * Returns the width of the oval
	 * @return width of the oval
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * Changes the height of the oval.
	 * @param height Height of the oval
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

package de.thm.mni.oop.fcanvas.components;

/**
 * Represents a polygon.
 * @author Christopher Schölzel
 *
 */
public class Polygon extends AbstractComponent {
	private int[] xar;
	private int[] yar;
	private float centroidX;
	private float centroidY;
	/**
	 * Creates a new polygon
	 * @param xar x-coordinates of the polygon points
	 * @param yar y-coordinates of the polygon points
	 */
	public Polygon(int[] xar, int[] yar) {
		this.xar = xar;
		this.yar = yar;
		calcCentroid();
	}
	private void calcCentroid() {
		centroidX = 0;
		centroidY = 0;
		for(int i = 0; i < xar.length; i++) {
			centroidX += xar[i];
			centroidY += yar[i];
		}
		centroidX /= xar.length;
		centroidY /= yar.length;
	}
	@Override
	public void move(int x, int y) {
		if (xar.length == 0) return;
		int shiftx = x-xar[0];
		int shifty = y-yar[0];
		for(int i = 0; i < xar.length; i++) {
			xar[i] += shiftx;
			yar[i] += shifty;
		}
		calcCentroid();
	}
	/**
	 * Returns the x-coordinate of the polygon's geometric center.
	 * @return x-coordinate of the geometric center
	 */
	public float getCentroidX() {
		return centroidX;
	}
	/**
	 * Returns the y-coordinate of the geometric center of the polygon.
	 * @return Y-coordinate of the geometric center
	 */
	public float getCentroidY() {
		return centroidY;
	}
	/**
	 * Returns the x-coordinates of the polygon points
	 * @return x-coordinates of the polygon points
	 */
	public int[] getXCoords() {
		return xar;
	}
	/**
	 * Returns the y coordinates of the polygon points
	 * @return y-coordinates of the polygon points
	 */
	public int[] getYCoords() {
		return yar;
	}
}

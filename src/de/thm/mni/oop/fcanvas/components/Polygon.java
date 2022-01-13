package de.thm.mni.oop.fcanvas.components;

/**
 * Stellt ein Polygon dar.
 * @author Christopher Schölzel
 *
 */
public class Polygon extends AbstractComponent {
	private int[] xar;
	private int[] yar;
	private float centroidX;
	private float centroidY;
	/**
	 * Erstellt ein neues Polygon
	 * @param xar x-Koordinaten der Polygonpunkte
	 * @param yar y-Koordinaten der Polygonpunkte
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
	 * Gibt die x-Koordinate des geometrischen Zentrums des Polygons zurück.
	 * @return x-Koordinate des geometrischen Zentrums
	 */
	public float getCentroidX() {
		return centroidX;
	}
	/**
	 * Gibt die y-Koordinate des geometrischen Zentrums des Polygons zurück.
	 * @return y-Koordinate des geometrischen Zentrums
	 */
	public float getCentroidY() {
		return centroidY;
	}
	/**
	 * Gibt die x-Koordinaten der Polygonpunkte zurück
	 * @return x-Koordinaten der Polygonpunkte
	 */
	public int[] getXCoords() {
		return xar;
	}
	/**
	 * Gibt die y-Koordinaten der Polygonpunkte zurück
	 * @return y-Koordinaten der Polygonpunkte
	 */
	public int[] getYCoords() {
		return yar;
	}
}

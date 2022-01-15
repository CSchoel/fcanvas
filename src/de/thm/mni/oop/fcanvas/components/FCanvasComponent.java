package de.thm.mni.oop.fcanvas.components;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * <p>A component used in a {@link de.thm.mni.oop.fcanvas.FCanvasPanel}.</p>
 *   *
 * <p>Each component contains all the information needed to draw the component.
 * It provides basic methods for setting properties and getters for use with a
 * {@link java.awt.Graphics2D} object.</p>
 * 
 * @author Christopher Schölzel
 * @see AbstractComponent
 */
public interface FCanvasComponent {
	/**
	 * Changes the rotation angle. If possible, this should express a rotation around
	 * the center of the component.
	 * 
	 * @param degree angle in degrees
	 */
	public void setRotation(float degree);
	/**
	 * Returns the rotation angle.
	 * @return Rotation angle in degrees
	 */
	public float getRotation();
	/**
	 * Changes the fill color of the component.
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @param a value for the alpha channel (0 to 255)
	 */
	public void setFillColor(int r, int g, int b, int a);
	/**
	 * Changes the stroke color of the component.
	 * 
	 * For components that specify a filled area, the dash is the border line.
	 * @param r value for the red channel (0 to 255)
	 * @param g value for the green channel (0 to 255)
	 * @param b value for the blue channel (0 to 255)
	 * @param a value for the alpha channel (0 to 255)
	 */
	public void setStrokeColor(int r, int g, int b, int a);
	/**
	 * Changes the stroke width of the component.
	 * 
	 * For components that specify a filled area, the stroke is the border line.
	 * @param w new stroke width in pixels
	 */
	public void setStrokeWidth(int w);
	/**
	 * Returns the Stroke object to use to draw the component with a
	 * {@link java.awt.Graphics2D} object.
	 * @return Stroke object containing the component's Stroke properties
	 */
	public Stroke getStroke();
	/**
	 * Returns the Paint object to use to paint the component with a
	 * {@link java.awt.Graphics2D} object.
	 * @return Paint object with the component's fill properties
	 */
	public Paint getFillColor();
	/**
	 * Returns the Color object to use to draw the component's stroke
	 * using a {@link java.awt.Graphics2D} object.
	 * 
	 * For components that specify a filled area, the dash is the border line.
	 * @return the color of the component's stroke
	 * @return die Farbe des Striches der Komponente
	 */
	public Color getStrokeColor();
	/**
	 * Moves the origin point of the component.
	 * @param x the x coordinate of the new origin point
	 * @param y the y coordinate of the new origin point
	 */
	public void move(int x, int y);
}

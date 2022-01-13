package de.thm.mni.oop.fcanvas.components;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * <p>Eine Komponente, die in einem {@link de.thm.mni.oop.fcanvas.FCanvasPanel} verwendet wird.</p>
 * 
 * <p>Jede Komponente enthält alle Informationen, die zum Zeichnen der Komponente nötig sind.
 * Sie bietet grundlegende Methoden zum setzen Veränderlicher Eigenschaften und Getter für die 
 * Verwendung mit einem {@link java.awt.Graphics2D} Objekt an.</p>
 * 
 * @author Christopher Schölzel
 * @see AbstractComponent
 */
public interface FCanvasComponent {
	/**
	 * Ändert den Rotationswinkel. Falls möglich sollte dieser eine Rotation um das Zentrum der
	 * Komponente ausdrücken.
	 * 
	 * @param degree winkel in Grad
	 */
	public void setRotation(float degree);
	/**
	 * Gibt den Rotationswinkel zurück.
	 * @return Rotationswinkel in Grad
	 */
	public float getRotation();
	/**
	 * Ändert die Füllfarbe der Komponente.
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @param a Wert für den Alphakanal (0 bis 255)
	 */
	public void setFillColor(int r, int g, int b, int a);
	/**
	 * Ändert die Strichfarbe der Komponente.
	 * 
	 * Bei Komponenten, die einen gefüllten Bereich angeben ist der Strich die Randlinie. 
	 * @param r Wert für den Rotkanal (0 bis 255)
	 * @param g Wert für den Grünkanal (0 bis 255)
	 * @param b Wert für den Blaukanal (0 bis 255)
	 * @param a Wert für den Alphakanal (0 bis 255)
	 */
	public void setStrokeColor(int r, int g, int b, int a);
	/**
	 * Ändert die Strichbreite der Komponente. 
	 * 
	 * Bei Komponenten, die einen gefüllten Bereich angeben ist der Strich die Randlinie. 
	 * @param w neue Strichbreite in Pixeln
	 */
	public void setStrokeWidth(int w);
	/**
	 * Gibt das Stroke-Objekt zurück, das zum Zeichnen der Komponente mit einem 
	 * {@link java.awt.Graphics2D}-Objekt verwendet werden soll.
	 * @return Stroke-Objekt mit den Strich-Eigenschaften der Komponente
	 */
	public Stroke getStroke();
	/**
	 * Gibt das Paint-Objekt zurück, das zum Zeichnen der Komponente mit einem
	 * {@link java.awt.Graphics2D}-Objekt verwendet werden soll.
	 * @return Paint-Objekt mit dem Fülleigenschaften der Komponente
	 */
	public Paint getFillColor();
	/**
	 * Gibt das Color-Objekt zurück, das zum Zeichnen des Striches der Komponente mit einem 
	 * {@link java.awt.Graphics2D}-Objekt verwendet werden soll.
	 *
	 * Bei Komponenten, die einen gefüllten Bereich angeben ist der Strich die Randlinie. 
	 * @return die Farbe des Striches der Komponente
	 */
	public Color getStrokeColor();
	/**
	 * Bewegt den Ursprungspunkt der Komponente.
	 * @param x die x-Koordinate des neuen Ursprungspunktes
	 * @param y die y-Koordinate des neuen Ursprungspunktes
	 */
	public void move(int x, int y);
}

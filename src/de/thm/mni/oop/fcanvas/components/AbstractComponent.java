package de.thm.mni.oop.fcanvas.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Abstrakte Basisklasse für alle Klassen sie das interface {@link FCanvasComponent}
 * implementieren.
 * 
 * @author Christopher Schölzel
 * @see FCanvasComponent
 * @see Line
 * @see Oval
 * @see Polygon
 * @see Rectangle
 * @see Text
 */
public abstract class AbstractComponent implements FCanvasComponent {
	protected Color cfill = new Color(255,255,255,0);
	protected Color cstroke = Color.BLACK;
	protected BasicStroke stroke = new BasicStroke(1);
	protected float rotation = 0.0f;
	
	@Override
	public void setRotation(float degree) {
		rotation = degree;
	}

	@Override
	public void setFillColor(int r, int g, int b, int a) {
		cfill = new Color(r,g,b,a);
	}

	@Override
	public void setStrokeColor(int r, int g, int b, int a) {
		cstroke = new Color(r,g,b,a);
	}

	@Override
	public void setStrokeWidth(int w) {
		stroke = withDifferentWidth(stroke,w);
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public Color getFillColor() {
		return cfill;
	}

	@Override
	public Color getStrokeColor() {
		return cstroke;
	}
	/**
	 * Hilfsfunktion, die ein Stroke-Objekt erzeugt, das eine Kopie des übergebenen Strokes
	 * mit anderer breite ist.
	 * @param s das alte Stroke-Objekt
	 * @param width die neue Breite in Pixeln
	 * @return eine Kopie von s mit der Breite width
	 */
	protected static BasicStroke withDifferentWidth(BasicStroke s, int width) {
		BasicStroke s2 = new BasicStroke(width,s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());
		return s2;
	}

}

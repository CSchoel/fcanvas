package de.thm.mni.oop.fcanvas.components;

import java.awt.Font;

/**
 * Stellt einen Text dar.
 * 
 * @author Christopher Schölzel
 */
public class Text extends AbstractComponent {
	private String text;
	private int left;
	private int baseline;
	private Font font;
	/**
	 * Erstellt eine neue Textkomponente.
	 * @param text der Text, der angezeigt werden soll
	 * @param left die x-Koordinate des ersten Zeichens
	 * @param baseline die y-Koordinate der Baseline des ersten Zeichens
	 */
	public Text(String text, int left, int baseline) {
		this.text = text;
		this.left = left;
		this.baseline = baseline;
		this.font = new Font("SansSerif", Font.PLAIN, 12);
	}
	/**
	 * Ändert die Schriftgröße des Textes.
	 * @param size neue Schriftgröße in pt
	 */
	public void setFontSize(int size) {
		this.font = new Font(font.getFontName(),font.getStyle(),size);
	}
	/**
	 * Gibt den Text zurück der angezeigt werden soll.
	 * @return Text, der angezeigt werden soll
	 */
	public String getText() {
		return text;
	}
	/**
	 * Ändert den Text der angezeigt werden soll.
	 * @param text neuer Text
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Gibt die x-Koordinate des ersten Zeichens zurück.
	 * @return x-Koordinate des erten Zeichens
	 */
	public int getLeft() {
		return left;
	}
	/**
	 * Ändert die x-Koordinate des ersten Zeichens.
	 * @param left neue x-Koordinate
	 */
	public void setLeft(int left) {
		this.left = left;
	}
	/**
	 * Gibt die y-Koordinate der Baseline des ersten Zeichens zurück
	 * @return y-Koordinate der Baseline
	 */
	public int getBaseline() {
		return baseline;
	}
	/**
	 * Ändert die y-Koordinate der Baseline
	 * @param baseline neue y-Koordinate
	 */
	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}
	/**
	 * Gibt das Font-Objekt zurück, das benutzt werden soll um den Text mit einem
	 * {@link java.awt.Graphics2D}-Objekt zu zeichnen.
	 * @return Font
	 */
	public Font getFont() {
		return font;
	}
	@Override
	public void move(int x, int y) {
		left = x;
		baseline = y;
	}
}

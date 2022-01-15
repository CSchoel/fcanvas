package de.thm.mni.oop.fcanvas.components;

import java.awt.Font;

/**
 * Represents a piece of text.
 * 
 * @author Christopher Schölzel
 */
public class Text extends AbstractComponent {
	private String text;
	private int left;
	private int baseline;
	private Font font;
	/**
	 * Creates a new text component.
	 * @param text the text to be displayed
	 * @param left the x-coordinate of the first character
	 * @param baseline the y-coordinate of the baseline of the first character
	 */
	public Text(String text, int left, int baseline) {
		this.text = text;
		this.left = left;
		this.baseline = baseline;
		this.font = new Font("SansSerif", Font.PLAIN, 12);
	}
	/**
	 * Changes the font size of the text.
	 * @param size new font size in pt
	 */
	public void setFontSize(int size) {
		this.font = new Font(font.getFontName(),font.getStyle(),size);
	}
	/**
	 * Returns the text to be displayed.
	 * @return Text to be displayed
	 */
	public String getText() {
		return text;
	}
	/**
	 * Changes the text to be displayed.
	 * @param text new text
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Returns the x-coordinate of the first character.
	 * @return x-coordinate of the first character
	 */
	public int getLeft() {
		return left;
	}
	/**
	 * Changes the x-coordinate of the first character.
	 * @param left new x coordinate
	 */
	public void setLeft(int left) {
		this.left = left;
	}
	/**
	 * Returns the y-coordinate of the baseline of the first character
	 * @return y-coordinate of the baseline
	 */
	public int getBaseline() {
		return baseline;
	}
	/**
	 * Changes the y-coordinate of the baseline
	 * @param baseline new y coordinate
	 */
	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}
	/**
	 * Returns the Font object to use to surround the text with a
	 * {@link java.awt.Graphics2D} object to draw.
	 * @return font
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

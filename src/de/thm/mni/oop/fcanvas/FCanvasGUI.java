package de.thm.mni.oop.fcanvas;

import javax.swing.JFrame;

/**
 * This class provides a minimalistic Swing GUI for displaying
 * {@link FCanvasPanel}s.
 * 
 * @author Christopher Schölzel
 *
 */
public class FCanvasGUI extends JFrame {
	private static final long serialVersionUID = -332099432394940188L;
	private FCanvasPanel canvas;
	/**
	 * Creates a new GUI window with a starting size of 800x600 pixels containing a single {@link FCanvasPanel}.
	 */
	public FCanvasGUI() {
		canvas = new FCanvasPanel();
		this.add(canvas);
		this.setSize(800,600);
		this.setTitle("FCanvas");
		this.setResizable(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	/**
	 * Returns the FCanvasPanel displayed in this window.
	 * @return the FCanvasPanel of that window
	 */
	public FCanvasPanel getPanel() {
		return canvas;
	}
}

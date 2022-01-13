package de.thm.mni.oop.fcanvas;

import javax.swing.JFrame;

/**
 * Diese Klasse bietet eine minimalistische SwingGUI zum Anzeigen eines
 * {@link FCanvasPanel}s.
 * 
 * @author Christopher Schölzel
 *
 */
public class FCanvasGUI extends JFrame {
	private static final long serialVersionUID = -332099432394940188L;
	private FCanvasPanel canvas;
	/**
	 * Erzeugt ein neues GUI-Fenster mit einer Startgröße von 800x600 Pixeln, das
	 * ein einzelnes {@link FCanvasPanel} enthält.
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
	 * Gibt das FCanvasPanel zurück, das in diesem Fenster angezeigt wird.
	 * @return das FCanvasPanel dieses Fensters
	 */
	public FCanvasPanel getPanel() {
		return canvas;
	}
}

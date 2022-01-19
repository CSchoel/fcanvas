package de.thm.mni.oop.fcanvas;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;

public class FCanvasTest {

    private static final Path IMAGE_DIR = Path.of(".");

    record ImageSetup(BufferedImage image, Graphics2D graphics) {}

    @Before
    public void setupCanvas() {
        FCanvas.show();
    }

    @After
    public void teardownCanvas() {
        FCanvas.reset();
    }

    @Test
    public void testRectangle() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawRect(10, 10, 100, 100);
        FCanvas.drawRectangle(10, 10, 100, 100);
        assertFCanvasEqualsImage(setup.image, "rectangle_");
    }

    @Test
    public void testLine() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawLine(10, 10, 100, 100);
        FCanvas.drawLine(10, 10, 100, 100);
        assertFCanvasEqualsImage(setup.image, "line_");
    }

    public static ImageSetup createFCanvasImageSetup() {
        BufferedImage bi = new BufferedImage(FCanvas.getCanvasWidth(), FCanvas.getCanvasHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.setBackground(Color.WHITE);
        g.setPaint(Color.BLACK);
        return new ImageSetup(bi, g);
    }

    public static void assertFCanvasEqualsImage(BufferedImage expected, String filePrefix) throws IOException {
        BufferedImage ref = FCanvas.gui.getPanel().toImage();
        assertImageEquals(expected, ref, filePrefix);
    }

    public static void assertImageEquals(BufferedImage expected, BufferedImage actual, String filePrefix) throws IOException {
        Path expPath = IMAGE_DIR.resolve(filePrefix + "_exp.png");
        Path actPath = IMAGE_DIR.resolve(filePrefix + "_act.png");
        ImageIO.write(expected, "png", expPath.toFile());
        ImageIO.write(actual, "png", actPath.toFile());
        ImageIO.write(differenceImage(expected, actual), "png", IMAGE_DIR.resolve(filePrefix + "_diff.png").toFile());
        assertEquals(expected.getWidth(), actual.getWidth());
        assertEquals(expected.getHeight(), actual.getHeight());
        for (int y = 0; y < expected.getHeight(); y++) {
            for (int x = 0; x < expected.getHeight(); x++) {
                assertEquals("color mismatch at (%d, %d)".formatted(x, y), expected.getRGB(x, y), actual.getRGB(x, y));
            }
        }
    }

    public static int diffRGB(int c1, int c2) {
        int r = (c1 & 0xff0000 >> 16) - (c2 & 0xff0000 >> 16);
        int g = (c1 & 0xff00 >> 8) - (c2 & 0xff00 >> 8);
        int b = (c1 & 0xff) - (c2 & 0xff);
        return 0xff << 24 | Math.abs(r) << 16 | Math.abs(g) << 8 | Math.abs(b);
    }

    public static BufferedImage differenceImage(BufferedImage a, BufferedImage b) {
        BufferedImage diff = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_RGB);
        int[] colorA = a.getRGB(0, 0, a.getWidth(), a.getHeight(), null, 0, a.getWidth());
        int[] colorB = b.getRGB(0, 0, b.getWidth(), b.getHeight(), null, 0, b.getWidth());
        int[] colorDiff = new int[colorA.length];
        assert colorA.length == colorB.length;
        for (int i = 0; i < colorA.length; i++) {
            colorDiff[i] = diffRGB(colorA[i], colorB[i]);
        }
        diff.setRGB(0, 0, a.getWidth(), a.getHeight(), colorDiff, 0, a.getWidth());
        return diff;
    }
}

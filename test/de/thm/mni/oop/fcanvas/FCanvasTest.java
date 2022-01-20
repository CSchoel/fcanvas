package de.thm.mni.oop.fcanvas;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.SwingUtilities;

public class FCanvasTest {

    private static final Path IMAGE_DIR = Path.of(System.getProperty("user.dir")).resolve("testimg");

    record ImageSetup(BufferedImage image, Graphics2D graphics) {}

    @BeforeClass
    public static void createImageDir() {
        if (!IMAGE_DIR.toFile().exists()) {
            IMAGE_DIR.toFile().mkdir();
        }
    }

    @Before
    public void setupCanvas() {
        FCanvas.show();
    }

    @After
    public void teardownCanvas() {
        FCanvas.reset();
    }

    /**
     * Invokes a no-op runnable on the event dispatch thread
     * to ensure that all previous events have been processed.
     * @throws InterruptedException
     */
    public void waitForEDT() throws InterruptedException {
        // waits for execution of empty runnable
        // => ensure that all events that have been previously
        // scheduled have already been completed
        try {
            SwingUtilities.invokeAndWait(() -> {});
        } catch (InvocationTargetException e) {
            /* this can never happen since our runnable is a no-op */
        }
    }

    /**
     * Test hypothesis: {@link FCanvas#drawRectangle(int, int, int, int)} might fail to display
     * the rectangle, or one of the parameters might be handled incorrectly (e.g. swapped axes).
     * @throws IOException when test image cannot be saved
     * @throws InterruptedException when test is interrupted
     */
    @Test
    public void testRectangle() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawRect(50, 10, 20, 100);
        FCanvas.drawRectangle(50, 10, 20, 100);
        waitForEDT();
        assertFCanvasEqualsImage(setup.image, "rectangle");
    }

    /**
     * Test hypothesis: {@link FCanvas#drawLine(int, int, int, int)} might fail to display
     * the line, or one of the parameters might be handled incorrectly (e.g. swapped axes).
     * @throws IOException when test image cannot be saved
     * @throws InterruptedException when test is interrupted
     */
    @Test
    public void testLine() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawLine(50, 10, 20, 100);
        FCanvas.drawLine(50, 10, 20, 100);
        waitForEDT();
        assertFCanvasEqualsImage(setup.image, "line");
    }

    /**
     * Test hypothesis: {@link FCanvas#drawOval(int, int, int, int)} might fail to display
     * the oval, or one of the parameters might be handled incorrectly (e.g. swapped axes).
     * @throws IOException when test image cannot be saved
     * @throws InterruptedException when test is interrupted
     */
    @Test
    public void testOval() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawOval(50, 10, 20, 100);
        FCanvas.drawOval(50, 10, 20, 100);
        waitForEDT();
        assertFCanvasEqualsImage(setup.image, "oval");
    }

    /**
     * Test hypothesis: {@link FCanvas#drawPolygon(int[], int[])} might fail to display
     * the polygon, or one of the parameters might be handled incorrectly (e.g. swapped axes).
     * @throws IOException when test image cannot be saved
     * @throws InterruptedException when test is interrupted
     */
    @Test
    public void testPolygon() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        int[] x = {100,110,150,110,100, 90, 50,90};
        int[] y = { 50, 90,100,110,150,110,100,90};
        setup.graphics.drawPolygon(x, y, x.length);
        FCanvas.drawPolygon(x, y);
        waitForEDT();
        assertFCanvasEqualsImage(setup.image, "polygon");
    }

    /**
     * Test hypothesis: {@link FCanvas#drawText(String, int, int)} might fail to display
     * the text, or one of the parameters might be handled incorrectly (e.g. swapped axes).
     * @throws IOException when test image cannot be saved
     * @throws InterruptedException when test is interrupted
     */
    @Test
    public void testText() throws IOException, InterruptedException {
        Thread.sleep(100); // canvas must have finished first draw before we can get dimensions
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawString("foo", 50, 100);
        FCanvas.drawText("foo", 50, 100);
        waitForEDT();
        assertFCanvasEqualsImage(setup.image, "text");
    }

    /**
     * Creates an {@link ImageSetup} object that allows to draw on the image with the
     * same default settings as used by FCanvas (including panel dimensions).
     * @return setup that can be used to draw images comparable to FCanvas content
     */
    public static ImageSetup createFCanvasImageSetup() {
        BufferedImage bi = new BufferedImage(FCanvas.getCanvasWidth(), FCanvas.getCanvasHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.setBackground(Color.WHITE);
        g.setPaint(Color.BLACK);
        return new ImageSetup(bi, g);
    }

    /**
     * <p>Tests that the BufferedImage passed as argument is visually identical to the image currently
     * displayed by FCanvas.</p>
     * 
     * <p>This method will also store the actual and the reference image in {@link #IMAGE_DIR} as
     * well as a difference image that highlights regions where there are differences between both images.</p>
     * 
     * @param expected a reference image showing how the canvas should currently look like
     * @param filePrefix prefix used to store actual and reference image for debugging
     * @throws IOException if one of the test images cannot be saved
     */
    public static void assertFCanvasEqualsImage(BufferedImage expected, String filePrefix) throws IOException {
        BufferedImage ref = FCanvas.gui.getPanel().toImage();
        assertImageEquals(expected, ref, filePrefix);
    }

    /**
     * <p>Asserts that the two given images have identical pixel values.</p>
     * 
     * <p>This method will also store the actual and the reference image in {@link #IMAGE_DIR} as
     * well as a difference image that highlights regions where there are differences between both images.</p>
     * 
     * @param expected the expected image
     * @param actual the actual image
     * @param filePrefix prefix used for file name of test images
     * @throws IOException if one of the test images cannot be saved
     */
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
                assertEquals("color mismatch at (%d, %d)".formatted(x, y), new Color(expected.getRGB(x, y)), new Color(actual.getRGB(x, y)));
            }
        }
    }

    /**
     * Creates a difference color in (A)RGB where each color channel is
     * the absolute difference between the same color channels in the input colors.
     * @param c1 first (A)RGB color to compare
     * @param c2 second (A)RGB color to compare
     * @return (A)RGB color highlighting the differences between c1 and c2 per color channel
     */
    public static int diffRGB(int c1, int c2) {
        int r = (c1 & 0xff0000 >> 16) - (c2 & 0xff0000 >> 16);
        int g = (c1 & 0xff00 >> 8) - (c2 & 0xff00 >> 8);
        int b = (c1 & 0xff) - (c2 & 0xff);
        return 0xff << 24 | Math.abs(r) << 16 | Math.abs(g) << 8 | Math.abs(b);
    }

    /**
     * <p>Creates a difference image that highlights differences between the two input images
     * per color channel.</p>
     * @param a first image to compare
     * @param b second image to compare
     * @return image that highlights differences between a and b per color channel
     * @see #diffRGB(int, int)
     */
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

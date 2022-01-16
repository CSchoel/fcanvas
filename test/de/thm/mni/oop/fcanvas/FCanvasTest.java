package de.thm.mni.oop.fcanvas;

import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics2D;

public class FCanvasTest {

    private static final Path IMAGE_DIR = Path.of(".");

    record ImageSetup(BufferedImage image, Graphics2D graphics) {}

    @Test
    public void testRectangle() throws IOException, InterruptedException {
        FCanvas.show();
        Thread.sleep(100);
        ImageSetup setup = createFCanvasImageSetup();
        setup.graphics.drawRect(10, 10, 100, 100);
        FCanvas.drawRectangle(10, 10, 100, 100);
        Thread.sleep(1000);
        BufferedImage ref = FCanvas.gui.getPanel().toImage();
        assertImageEquals(setup.image, ref, "rectangle");
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

    public static void assertImageEquals(BufferedImage expected, BufferedImage actual, String filePrefix) throws IOException {
        Path expPath = IMAGE_DIR.resolve(filePrefix + "_exp.png");
        Path actPath = IMAGE_DIR.resolve(filePrefix + "_act.png");
        ImageIO.write(expected, "png", expPath.toFile());
        ImageIO.write(actual, "png", actPath.toFile());
        ImageIO.write(differenceImage(expected, actual), "png", IMAGE_DIR.resolve(filePrefix + "_diff.png").toFile());
        String msg = "Images %s and %s are not equal.".formatted(expPath, actPath);
        assertEquals(msg, expected, actual);
    }

    public static BufferedImage differenceImage(BufferedImage a, BufferedImage b) {
        BufferedImage diff = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_RGB);
        // int[] colorA = a.getRGB(0, 0, a.getWidth(), a.getHeight(), null, 0, 0);
        // int[] colorB = b.getRGB(0, 0, b.getWidth(), b.getHeight(), null, 0, 0);
        // int[] colorDiff = new int[colorA.length];
        // assert colorA.length == colorB.length;
        // for (int i = 0; i < colorA.length; i++) {
        //     colorDiff[i] = colorA[i] - colorB[i];
        // }
        // diff.setRGB(0, 0, a.getWidth(), a.getHeight(), colorDiff, 0, 0);
        for (int y = 0; y < diff.getHeight(); y++) {
            for (int x = 0; x < diff.getHeight(); x++) {
                diff.setRGB(x, y, a.getRGB(x, y) - b.getRGB(x, y));
            }
        }
        return diff;
    }
}

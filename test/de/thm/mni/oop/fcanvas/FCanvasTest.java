package de.thm.mni.oop.fcanvas;

import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.io.IOException;

public class FCanvasTest {

    private static final Path IMAGE_DIR = Path.of(".");

    @Test
    public void testRectangle() throws IOException, InterruptedException {
        FCanvas.show();
        BufferedImage bi = new BufferedImage(FCanvas.getCanvasWidth(), FCanvas.getCanvasHeight(), BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawRect(10, 10, 100, 100);
        FCanvas.drawRectangle(10, 10, 100, 100);
        Thread.sleep(1000);
        BufferedImage ref = FCanvas.gui.getPanel().toImage();
        System.out.println(ref);
        System.out.println(bi);
        saveImage(bi, IMAGE_DIR.resolve("bi.png"));
        saveImage(ref, IMAGE_DIR.resolve("ref.png"));
        assertEquals(bi, ref);
    }

    public static void saveImage(BufferedImage bi, Path dest) throws IOException {
        ImageIO.write(bi, "png", dest.toFile());
    }
}

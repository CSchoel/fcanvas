import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.image.BufferedImage;

public class FCanvasTest {

    @Test
    public void testRectangle() {
        BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        System.out.println("Muh");
        assertEquals(bi, bi);
    }
}

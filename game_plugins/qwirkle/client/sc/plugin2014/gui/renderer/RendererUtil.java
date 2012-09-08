package sc.plugin2014.gui.renderer;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class RendererUtil {
    public static Image getImage(String filename) {
        URL url = RendererUtil.class.getClassLoader().getResource(filename);

        if (url == null) {
            return null;
        }

        return (new ImageIcon(url)).getImage();
    }
}

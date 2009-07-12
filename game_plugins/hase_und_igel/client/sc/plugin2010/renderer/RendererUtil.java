package sc.plugin2010.renderer;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RendererUtil
{
	private static final Logger	logger	= LoggerFactory
												.getLogger(RendererUtil.class);

	public static Image getImage(String filename)
	{
		URL url = RendererUtil.class.getClassLoader().getResource(filename);
		logger.debug("Loading Image {} from {}", filename, url);
		return Toolkit.getDefaultToolkit().getImage(url);
	}
}

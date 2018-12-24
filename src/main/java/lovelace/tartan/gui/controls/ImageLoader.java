package lovelace.tartan.gui.controls;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * A class to encapsulate a method to hide the details of loading an image from the
 * classpath from callers.
 *
 * @author Jonathan Lovelace
 */
public class ImageLoader {
	private ImageLoader() {
	}

	public static Image loadImage(final String filename) throws IOException {
		final InputStream stream =
				Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(filename);
		if (stream == null) {
			throw new FileNotFoundException(
					String.format("File %s not found on the classpath", filename));
		} else {
			return ImageIO.read(stream);
		}
	}

	// TODO: Provide a factory method that tries to create an ImageButton but falls back to JButton
}

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
public final class ImageLoader {
	private ImageLoader() {
	}

	public static Image loadImage(final String filename) throws IOException {
		try (final InputStream stream =
					Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(filename)) {
			if (stream == null) {
				throw new FileNotFoundException(
						"File %s not found on the classpath".formatted(filename));
			} else {
				return ImageIO.read(stream);
			}
		}
	}

	// TODO: Provide a factory method to try to create an ImageButton w/ JButton fallback
}

package lovelace.tartan.gui.controls;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JButton;
import org.jetbrains.annotations.NotNull;

/**
 * A button with an image on it.
 *
 * @author Jonathan Lovelace
 */
public final class ImageButton extends JButton {
	private final @NotNull Image image;
	@SuppressWarnings("MagicNumber")
	public ImageButton(final @NotNull Image image) {
		this.image = image;
		setMaximumSize(new Dimension(60, 60));
		setPreferredSize(new Dimension(40, 40));
		setMinimumSize(new Dimension(20, 20));
	}

	@Override
	protected void paintComponent(final @NotNull Graphics pen) {
		pen.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}

	@Override
	public String toString() {
		return "ImageButton for %dx%d image"
				       .formatted(image.getWidth(null), image.getHeight(null));
	}
}

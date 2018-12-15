package lovelace.tartan.gui.controls;

import java.awt.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

/**
 * A button with an image on it.
 *
 * @author Jonathan Lovelace
 */
public class ImageButton extends JButton {
	@NotNull private final Image image;
	public ImageButton(@NotNull Image image) {
		this.image = image;
		setMaximumSize(new Dimension(60, 60));
		setPreferredSize(new Dimension(40, 40));
		setMinimumSize(new Dimension(20, 20));
	}

	@Override
	protected void paintComponent(@NotNull final Graphics pen) {
		pen.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}
}

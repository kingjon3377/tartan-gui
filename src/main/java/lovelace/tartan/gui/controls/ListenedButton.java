package lovelace.tartan.gui.controls;

import java.awt.event.ActionListener;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link JButton} taking its listener in its constructor.
 *
 * @author Jonathan Lovelace
 */
public class ListenedButton extends JButton {
	/**
	 * @param text the text of the button
	 * @param action what to do when the button is pressed
	 */
	public ListenedButton(@NotNull final String text, @NotNull final ActionListener action) {
		super(text);
		addActionListener(action);
	}
}

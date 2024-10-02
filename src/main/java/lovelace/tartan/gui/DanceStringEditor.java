package lovelace.tartan.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lovelace.tartan.gui.controls.BorderedPanel;
import lovelace.tartan.gui.controls.ImageButton;
import lovelace.tartan.gui.controls.ImageLoader;
import org.jetbrains.annotations.NotNull;

/**
 * A panel to let the user edit movements in dances that are represented as just text, not
 * figures.
 *
 * @author Jonathan Lovelace
 */
public final class DanceStringEditor extends JPanel {
	private final @NotNull JTextField field;
	private @NotNull String string;
	private final @NotNull Consumer<@NotNull String> consumer;
	private final @NotNull Runnable cancel;

	private void okListener(final ActionEvent ignored) {
		final String text = field.getText();
		string = text;
		consumer.accept(text);
	}

	private void cancelListener(final ActionEvent ignored) {
		field.setText(string);
		cancel.run();
	}

	@SuppressWarnings("EmptyMethod")
	private static void noop() { /* deliberate no-op */ }

	public DanceStringEditor(final String string,
	                         final Consumer<@NotNull String> consumer) {
		this(string, consumer, DanceStringEditor::noop);
	}

	public DanceStringEditor(final @NotNull String string,
	                         final @NotNull Consumer<@NotNull String> consumer,
	                         final @NotNull Runnable cancel) {
		super(new BorderLayout());
		this.string = string;
		field = new JTextField(string, 26);
		field.addActionListener(this::okListener);
		this.cancel = cancel;
		this.consumer = consumer;
		JButton okButton;
		try {
			okButton = new ImageButton(
					ImageLoader.loadImage(
							// TODO: Check image path once port back to Java complete
							"lovelace/tartan/gui/Green-Check-Mark-Icon-300px.png"));
		} catch (final IOException e) {
			okButton = new JButton("OK");
		}
		okButton.addActionListener(this::okListener);
		JButton cancelButton;
		try {
			cancelButton =
					new ImageButton(ImageLoader.loadImage(
							// TODO: Check image path once port back to Java complete
							"lovelace/tartan/gui/Red-X-Icon-300px.png"));
		} catch (final IOException e) {
			cancelButton = new JButton("Cancel");
		}
		cancelButton.addActionListener(this::cancelListener);
		add(field, BorderLayout.LINE_START);
		add(BorderedPanel.horizontalLine(okButton, null, cancelButton),
				BorderLayout.LINE_END);
	}

	@Override
	public String toString() {
		return "DanceStringEditor editing '%s'".formatted(string);
	}
}

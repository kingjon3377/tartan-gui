package lovelace.tartan.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.*;
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
	@NotNull
	private final JTextField field;
	@NotNull
	private String string;
	@NotNull
	private final Consumer<@NotNull String> consumer;
	@NotNull
	private final Runnable cancel;

	private void okListener(ActionEvent _) {
		String text = field.getText();
		string = text;
		consumer.accept(text);
	}

	private void cancelListener(ActionEvent _) {
		field.setText(string);
		cancel.run();
	}

	private static final void noop() {}

	public DanceStringEditor(String string, Consumer<@NotNull String> consumer) {
		this(string, consumer, DanceStringEditor::noop);
	}
	public DanceStringEditor(String string, Consumer<@NotNull String> consumer,
							 Runnable cancel) {
		super(new BorderLayout());
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
		} catch (IOException e) {
			okButton = new JButton("OK");
		}
		okButton.addActionListener(this::okListener);
		JButton cancelButton;
		try {
			cancelButton =
					new ImageButton(ImageLoader.loadImage(
							// TODO: Check image path once port back to Java complete
								"lovelace/tartan/gui/Red-X-Icon-300px.png"));
		} catch (IOException e) {
			cancelButton = new JButton("Cancel");
		}
		cancelButton.addActionListener(this::cancelListener);
		add(field, BorderLayout.LINE_START);
		add(BorderedPanel.horizontalLine(okButton, null, cancelButton),
				BorderLayout.LINE_END);
	}
}

package lovelace.tartan.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import lovelace.tartan.gui.controls.BorderedPanel;
import lovelace.tartan.gui.controls.ImageButton;
import lovelace.tartan.gui.controls.ImageLoader;
import lovelace.tartan.model.Figure;

/**
 * A panel to allow the user to edit a figure in a dance on the program.
 *
 * @author Jonathan Lovelace
 */
public final class FigureEditor extends JPanel {
	private final Figure figure;
	private final Runnable cancel;
	private final JTextField barsField;
	private final JTextField descriptionField;

	@SuppressWarnings("HardcodedFileSeparator") // '/' is cross-platform in Java!
	public FigureEditor(final Figure figure, final Runnable cancel) {
		super(new BorderLayout());
		this.figure = figure;
		this.cancel = cancel;
		barsField = new JTextField(Objects.toString(figure.getBars(), ""), 6);
		descriptionField = new JTextField(figure.getDescription(), 20);
		add(barsField, BorderLayout.LINE_START);
		add(descriptionField, BorderLayout.CENTER);
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
		barsField.addActionListener(this::okListener);
		descriptionField.addActionListener(this::okListener);
		JButton cancelButton;
		try {
			cancelButton =
					new ImageButton(ImageLoader.loadImage(
							// TODO: Check image path once port back to Java complete
							"lovelace/tartan/gui/Red-X-Icon-300px.png"));
		} catch (final IOException e) {
			cancelButton = new JButton("Cancel");
		}
		final KeyListener escapeListener = new EscapeKeyListener(this::cancelListener);
		barsField.addKeyListener(escapeListener);
		descriptionField.addKeyListener(escapeListener);
		cancelButton.addActionListener(this::cancelListener);
		add(BorderedPanel.horizontalLine(okButton, null, cancelButton),
				BorderLayout.LINE_END);
	}

	public FigureEditor(final Figure figure) {
		this(figure, FigureEditor::noop);
	}

	private void setBars(final String bars) {
		if (bars.isEmpty()) {
			figure.setBars(null);
		} else {
			figure.setBars(bars);
		}
	}

	private void setText(final String text) {
		figure.setDescription(text);
	}

	private void okListener(final ActionEvent ignored) {
		setBars(barsField.getText());
		setText(descriptionField.getText());
		cancel.run();
	}
	private void cancelListener(final AWTEvent ignored) {
		barsField.setText(Objects.toString(figure.getBars(), ""));
		descriptionField.setText(figure.getDescription());
		cancel.run();
	}

	@SuppressWarnings("EmptyMethod")
	private static void noop() { // deliberate no-op
	}

	private static final class EscapeKeyListener extends KeyAdapter {
		private final Consumer<AWTEvent> consumer;
		private EscapeKeyListener(final Consumer<AWTEvent> consumer) {
			this.consumer = consumer;
		}
		@Override
		public void keyTyped(final KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				consumer.accept(event);
			}
		}

		@Override
		public void keyReleased(final KeyEvent event) {
			keyTyped(event);
		}

		@Override
		public String toString() {
			return "EscapeKeyListener";
		}
	}

	@Override
	public String toString() {
		return "FigureEditor for '%s'".formatted(figure);
	}
}

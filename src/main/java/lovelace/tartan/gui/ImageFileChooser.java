package lovelace.tartan.gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import lovelace.tartan.gui.controls.ListenedButton;
import lovelace.tartan.gui.controls.PlatformFileDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class to help the user choose image files supported by LaTeX.
 *
 * TODO: Move to lovelace.tartan.gui.controls? Perhaps make more generic?
 *
 * @author Jonathan Lovelace
 */
public class ImageFileChooser {
	public static final FileFilter IMAGE_FILTER =
			new FileNameExtensionFilter("LaTeX-supported images", "png", "jpg", "pdf");
	private @Nullable File filename = null;
	private final @NotNull Consumer<@Nullable Path> handler;
	private final JTextField chosenFileField = new JTextField(10);
	private final PlatformFileDialog chooser;
	private final JButton button = new ListenedButton("Choose File", this::buttonHandler);

	public @Nullable File getFilename() {
		return filename;
	}
	public void setFilename(final @Nullable File filename) {
		if (filename == null) {
			this.filename = null;
			chosenFileField.setText("");
		} else {
			this.filename = filename;
			chosenFileField.setText(filename.toString());
		}
	}
	public JComponent getField() {
		return chosenFileField;
	}

	private void buttonHandler(final ActionEvent ignored) {
		chooser.showOpenDialog();
		final @Nullable File file = chooser.getFilename();
		if (file == null) {
			filename = null;
			chosenFileField.setText("");
			handler.accept(null);
		} else {
			filename = file;
			chosenFileField.setText(file.toString());
			handler.accept(file.toPath());
		}
	}

	public JComponent getButton() {
		return button;
	}

	public ImageFileChooser(final @NotNull Consumer<@Nullable Path> handler) {
		this(handler, null);
	}

	public ImageFileChooser(final @NotNull Consumer<@Nullable Path> handler,
							final @Nullable Component parent) {
		final @Nullable Frame parentFrame;
		if (parent instanceof Frame) {
			parentFrame = (Frame) parent;
		} else {
			parentFrame = null;
		}
		chooser = new PlatformFileDialog(parentFrame);
		chosenFileField.setEditable(false);
		chooser.setFileFilter(IMAGE_FILTER);
		this.handler = handler;
	}

	@Override
	public String toString() {
		return "ImageFileChooser, currently holding '%s'".formatted(filename);
	}
}

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
	@Nullable private File filename = null;
	private @NotNull final Consumer<@Nullable Path> handler;
	@Nullable
	public File getFilename() {
		return filename;
	}
	public void setFilename(@Nullable final File filename) {
		if (filename == null) {
			this.filename = null;
			chosenFileField.setText("");
		} else {
			this.filename = filename;
			chosenFileField.setText(filename.toString());
		}
	}
	private final JTextField chosenFileField = new JTextField(10);
	public JComponent getField() {
		return chosenFileField;
	}
	private final PlatformFileDialog chooser;

	private void buttonHandler(final ActionEvent ignored) {
		chooser.showOpenDialog();;
		@Nullable File file = chooser.getFilename();
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

	private final JButton button = new ListenedButton("Choose File", this::buttonHandler);

	public JComponent getButton() {
		return button;
	}

	public ImageFileChooser(@NotNull final Consumer<@Nullable Path> handler) {
		this(handler, null);
	}

	public ImageFileChooser(@NotNull final Consumer<@Nullable Path> handler, @Nullable Component parent) {
		@Nullable final Frame parentFrame;
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
}

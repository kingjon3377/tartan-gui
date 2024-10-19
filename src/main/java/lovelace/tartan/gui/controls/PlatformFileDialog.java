package lovelace.tartan.gui.controls;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.util.Locale;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper around {@link FileDialog} on the Mac platform and {@link
 * JFileChooser} on other platforms.
 *
 * @author Jonathan Lovelace
 */
public final class PlatformFileDialog {
	/**
	 * The wrapped dialog. Must be either a {@link FileDialog} or {@link
	 * JFileChooser}.
	 */
	private final @NotNull Component wrapped;
	/**
	 * The parent window for this dialog.
	 */
	private final @Nullable Frame parent;

	/**
	 * Constructor.
	 *
	 * @param parentWindow The parent window for this dialog.
	 */
	public PlatformFileDialog(final @Nullable Frame parentWindow) {
		// Accessing "os.name" property should be safe
		// noinspection AccessOfSystemProperties
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("mac")) {
			wrapped = new FileDialog(parentWindow);
		} else {
			wrapped = new JFileChooser();
		}
		parent = parentWindow;
	}

	/**
	 * @return the file currently selected in the dialog.
	 */
	public @Nullable File getFilename() {
		if (wrapped instanceof FileDialog) {
			final File[] array = ((FileDialog) wrapped).getFiles();
			if (array.length > 0) {
				return array[0];
			} else {
				return null;
			}
		} else {
			return ((JFileChooser) wrapped).getSelectedFile();
		}
	}

	/**
	 * @param filename a file that should be the new default selection
	 */
	public void setFilename(final @Nullable File filename) {
		if (wrapped instanceof FileDialog) {
			((FileDialog) wrapped)
					.setFile(Optional.ofNullable(filename).map(File::getPath).orElse(""));
		} else {
			((JFileChooser) wrapped).setSelectedFile(filename);
		}
	}

	/**
	 * @return the dialog's file filter, which will be either a
	 * {@link FileFilter} or a {@link FilenameFilter}.
	 */
	public Object getFileFilter() {
		if (wrapped instanceof FileDialog) {
			return ((FileDialog) wrapped).getFilenameFilter();
		} else {
			return ((JFileChooser) wrapped).getFileFilter();
		}
	}

	private static FilenameFilter toFilenameFilter(final Object filter) {
		return switch (filter) {
			case final FilenameFilter filenameFilter -> filenameFilter;
			case final FileFilter fileFilter ->
					(dir, name) -> fileFilter.accept(new File(dir, name));
			case null, default -> throw new IllegalArgumentException(
					"filter must be a FilenameFilter or a FileFilter");
		};
	}

	private static FileFilter toFileFilter(final Object filter) {
		return switch (filter) {
			case final FileFilter fileFilter -> fileFilter;
			case final FilenameFilter filenameFilter ->
					new FilenameFilterWrapper(filenameFilter);
			case null, default -> throw new IllegalArgumentException(
					"filter must be a FilenameFilter or a FileFilter");
		};
	}

	/**
	 * @param filter the new file filter for the dialog; must be either a {@link
	 *               FileFilter} or a {@link FilenameFilter}.
	 */
	public void setFileFilter(final Object filter) {
		switch (wrapped) {
			case final FileDialog fileDialog ->
					fileDialog.setFilenameFilter(toFilenameFilter(filter));
			case final JFileChooser chooser ->
					chooser.setFileFilter(toFileFilter(filter));
			default -> throw new IllegalStateException("Impossible file-chooser type");
		}
	}

	/**
	 * Show the "Open" dialog.
	 */
	public void showOpenDialog() {
		if (wrapped instanceof FileDialog) {
			((FileDialog) wrapped).setMode(FileDialog.LOAD);
			wrapped.setVisible(true);
		} else {
			((JFileChooser) wrapped).showOpenDialog(parent);
		}
	}
	/**
	 * Show the "Save As" dialog.
	 */
	public void showSaveAsDialog() {
		if (wrapped instanceof FileDialog) {
			((FileDialog) wrapped).setMode(FileDialog.SAVE);
			wrapped.setVisible(true);
		} else {
			((JFileChooser) wrapped).showSaveDialog(parent);
		}
	}

	/**
	 * A wrapper around a {@link FilenameFilter} to meet the {@link FileFilter} interface.
	 */
	private final static class FilenameFilterWrapper extends FileFilter {
		private final FilenameFilter filter;

		private FilenameFilterWrapper(final FilenameFilter filter) {
			this.filter = filter;
		}

		@Override
		public boolean accept(final File f) {
			return filter.accept(f.getParentFile(), f.getName());
		}

		@Override
		public String getDescription() {
			return "Unknown";
		}

		@Override
		public String toString() {
			return "FilenameFilteerWrapper for " + getDescription();
		}
	}

	@Override
	public String toString() {
		return "PlatformFileDialog wrapping " + wrapped.getClass().getSimpleName();
	}
}

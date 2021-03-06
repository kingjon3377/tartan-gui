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
 * A wrapper around {@link java.awt.FileDialog} on the Mac platform and {@link
 * javax.swing.JFileChooser} on other platforms.
 *
 * @author Jonathan Lovelace
 */
public final class PlatformFileDialog {
	/**
	 * The wrapped dialog. Must be either a {@link java.awt.FileDialog} or {@link
	 * javax.swing.JFileChooser}.
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
	 * {@link javax.swing.filechooser.FileFilter} or a {@link java.io.FilenameFilter}.
	 */
	public Object getFileFilter() {
		if (wrapped instanceof FileDialog) {
			return ((FileDialog) wrapped).getFilenameFilter();
		} else {
			return ((JFileChooser) wrapped).getFileFilter();
		}
	}

	/**
	 * @param filter the new file filter for the dialog; must be either a {@link
	 *               FileFilter} or a {@link FilenameFilter}.
	 */
	public void setFileFilter(final Object filter) {
		if (wrapped instanceof FileDialog) {
			if (filter instanceof FilenameFilter) {
				((FileDialog) wrapped).setFilenameFilter((FilenameFilter) filter);
			} else if (filter instanceof FileFilter) {
				((FileDialog) wrapped).setFilenameFilter(
						(dir, name) -> ((FileFilter) filter).accept(new File(dir,
								name)));
			} else {
				throw new IllegalArgumentException(
						"filter must be a FilenameFilter or a FileFilter");
			}
		} else {
			if (filter instanceof FileFilter) {
				((JFileChooser) wrapped).setFileFilter((FileFilter) filter);
			} else if (filter instanceof FilenameFilter) {
				((JFileChooser) wrapped).setFileFilter(new FilenameFilterWrapper(
					(FilenameFilter) filter));
			}
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
	private static class FilenameFilterWrapper extends FileFilter {
		private final FilenameFilter filter;

		public FilenameFilterWrapper(final FilenameFilter filter) {
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
	}
}

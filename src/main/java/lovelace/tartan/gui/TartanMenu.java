package lovelace.tartan.gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import lovelace.tartan.gui.controls.PlatformFileDialog;
import lovelace.tartan.gui.model.ReorderableListModel;
import lovelace.tartan.latex.LaTeXReader;
import lovelace.tartan.latex.LaTeXWriter;
import lovelace.tartan.model.ProgramElement;
import lovelace.tartan.model.ProgramMetadata;
import lovelace.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Code to provide the menu and handle menu-item selections.
 *
 * @author Jonathan Lovelace
 */
public final class TartanMenu {
	private static final Logger LOGGER = Logger.getLogger(TartanMenu.class.getName());

	private TartanMenu() {
	}

	private static JMenuItem menuItem(final String text, final int mnemonic,
									  final String description,
									  final Runnable handler,
									  final KeyStroke... accelerators) {
		final JMenuItem retval = new JMenuItem(text, mnemonic);
		if (accelerators.length > 0) {
			retval.setAccelerator(accelerators[0]);
		}
		retval.getAccessibleContext().setAccessibleDescription(description);
		retval.addActionListener((ignored) -> handler.run());
		final InputMap inputMap = retval.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		for (final KeyStroke accelerator : accelerators) {
			inputMap.put(accelerator, retval.getAction());
		}
		return retval;
	}

	private static final FileFilter LATEX_FILTER =
			new FileNameExtensionFilter("LaTeX documents", "tex");

	static void readFromFile(@NotNull final ReorderableListModel<ProgramElement> program,
							 @NotNull final ProgramMetadata metadata,
							 @Nullable final MetadataEditingPanel metadataPanel) {
		readFromFile(program, metadata, metadataPanel, null);
	}

	static void readFromFile(@NotNull final ReorderableListModel<ProgramElement> program,
							 @NotNull final ProgramMetadata metadata,
							 @Nullable final MetadataEditingPanel metadataPanel,
							 @Nullable final Component parent) {
		@Nullable final Frame parentFrame;
		if (parent instanceof Frame) {
			parentFrame = (Frame) parent;
		} else {
			parentFrame = null;
		}
		final PlatformFileDialog chooser = new PlatformFileDialog(parentFrame);
		chooser.setFileFilter(LATEX_FILTER);
		chooser.showOpenDialog();
		@Nullable final File filename = chooser.getFilename();
		if (filename == null) {
			return;
		} else if (!filename.canRead()) {
			JOptionPane.showMessageDialog(parent, "Chosen file could not be opened",
					"File Not Found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		final Optional<Pair<@NotNull ProgramMetadata,
								   @NotNull List<@NotNull ProgramElement>>>
				pair = readFromSpecifiedFile(filename.toPath(), parent);
		if (pair.isPresent()) {
			assignMetadata(pair.get().getFirst(), metadata);
			metadata.setFilename(filename.toPath());
			if (metadataPanel != null) {
				metadataPanel.revert();
			}
			program.clear();
			program.addAll(pair.get().getSecond());
		}
	}

	public static Optional<Pair<@NotNull ProgramMetadata,
									   @NotNull List<@NotNull ProgramElement>>> readFromSpecifiedFile(
			@NotNull final Path file) {
		return readFromSpecifiedFile(file, null);
	}

	public static Optional<Pair<@NotNull ProgramMetadata,
									   @NotNull List<@NotNull ProgramElement>>> readFromSpecifiedFile(
			@NotNull final Path file, @Nullable final Component parent) {
		try {
			final List<String> lines = Files.readAllLines(file);
			final StringBuilder builder = new StringBuilder();
			for (final String line : lines) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}
			return Optional.of(new LaTeXReader().readLaTeXProgram(builder.toString()));
		} catch (final IOException | ParseException except) {
			JOptionPane.showMessageDialog(null,
					String.format(
							"Error trying to read LaTeX from %s",
							file.toString()), "Error Reading LaTeX Program",
					JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, "Error reading LaTeX program",
					except);
		}
		return Optional.empty();
	}

	public static void assignMetadata(final ProgramMetadata from,
									  final ProgramMetadata to) {
		// Skip the filename, as we'll assign that immediately after this method anyway.
		to.setGroupCoverName(from.getGroupCoverName());
		to.setGroupTitleName(from.getGroupTitleName());
		to.setEventCoverName(from.getEventCoverName());
		to.setEventTitleName(from.getEventTitleName());
		to.setCoverDate(from.getCoverDate());
		to.setTitleDate(from.getTitleDate());
		to.setCoverLocation(from.getCoverLocation());
		to.setTitleLocation(from.getTitleLocation());
		to.setLocationAddress(from.getLocationAddress());
		to.setTitleTimes(from.getTitleTimes());
		to.setMusicians(from.getMusicians());
		to.setCoverImage(from.getCoverImage());
		to.setTitleOnCover(from.getTitleOnCover());
		to.setPrintAuldLangSyne(from.getPrintAuldLangSyne());
		to.setBackCoverImage(from.getBackCoverImage());
		to.getInsidePostDanceImages().clear();
		to.getInsidePostDanceImages().addAll(from.getInsidePostDanceImages());
	}

	static void saveToFile(
			@NotNull final ReorderableListModel<@NotNull ProgramElement> program,
			@NotNull final ProgramMetadata metadata, @Nullable final Path passedFilename,
			@Nullable final Component parent) {
		final Path filename;
		if (passedFilename != null) {
			filename = passedFilename;
		} else {
			@Nullable final Frame parentFrame;
			if (parent instanceof Frame) {
				parentFrame = (Frame) parent;
			} else {
				parentFrame = null;
			}
			final PlatformFileDialog chooser = new PlatformFileDialog(parentFrame);
			chooser.setFileFilter(LATEX_FILTER);
			chooser.showSaveAsDialog();
			@Nullable final File chosenFile = chooser.getFilename();
			if (chosenFile == null) {
				LOGGER.info("User canceled from save dialog");
				return;
			} else {
				filename = chosenFile.toPath();
			}
		}
		try (final BufferedWriter writer = Files.newBufferedWriter(filename)) {
			new LaTeXWriter().writeLaTeXProgram(writer, program, metadata);
		} catch (final IOException except) {
			JOptionPane.showMessageDialog(parent, "Error writing to " + filename,
					"I/O Error", JOptionPane.ERROR_MESSAGE);
			LOGGER.log(Level.SEVERE, "I/O error writing to " + filename, except);
		}
	}

	public static JMenu fileMenu(
			@NotNull final ReorderableListModel<@NotNull ProgramElement> program,
			@NotNull final ProgramMetadata metadata,
			@NotNull final MetadataEditingPanel metadataPanel) {
		final boolean onMac =
				System.getProperty("os.name").toLowerCase().startsWith("mac");
		final int shortcutMask =
				(onMac) ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
		final JMenu retval = new JMenu("File");
		retval.add(menuItem("Open", KeyEvent.VK_O,
				"Open an existing program for further editing",
				() -> readFromFile(program, metadata, metadataPanel, retval),
				KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutMask)));
		retval.add(menuItem("Save", KeyEvent.VK_S, "Save the program to file",
				() -> saveToFile(program, metadata, metadata.getFilename(), retval),
				KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutMask)));
		retval.add(menuItem("Save AS", KeyEvent.VK_A, "Save the program to a new file",
				() -> saveToFile(program, metadata, null, retval),
				KeyStroke.getKeyStroke(
						KeyEvent.VK_S, shortcutMask | InputEvent.SHIFT_DOWN_MASK)));
		if (onMac) {
			// TODO: Wire the "quit" code up to the Mac quit-handling system
		} else {
			// TODO: Add an "about" dialog
			retval.addSeparator();
			// FIXME: Check whether program or metadata have been modified; if so, ask
			//  whether to save first
			retval.add(
					menuItem("Exit", KeyEvent.VK_X, "Quit the app", () -> System.exit(0),
							KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutMask)));
		}
		return retval;
	}
}

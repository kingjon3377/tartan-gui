package lovelace.tartan.gui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import lovelace.tartan.db.DanceDatabase;
import lovelace.tartan.gui.controls.PlatformFileDialog;
import lovelace.tartan.gui.model.ReorderableListModel;
import lovelace.tartan.gui.model.ReorderableListModelImpl;
import lovelace.tartan.model.ProgramElement;
import lovelace.tartan.model.ProgramMetadata;
import lovelace.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A window to allow the user to edit the Ball program.
 *
 * @author Jonathan Lovelace
 */
public final class ProgramEditingWindow extends JFrame {
	private static final Logger LOGGER =
			Logger.getLogger(ProgramEditingWindow.class.getName());

	private static JComponent programEditingPanel(
			final ReorderableListModel<ProgramElement> program) {
		final JList<ProgramElement> selectedList = new JList<>(program);
		selectedList.setMinimumSize(new Dimension(400, 100));
		selectedList.setTransferHandler(new ProgramElementTransferHandler());
		selectedList.setDropMode(DropMode.INSERT);
		selectedList.setDragEnabled(true);
		final ElementEditingPanel eep = new ElementEditingPanel();
		selectedList.addListSelectionListener((ignored) -> {
			final int index = selectedList.getSelectedIndex();
			if (index >= 0 && index < program.getSize()) {
				eep.setCurrent(program.getElementAt(index));
			} else {
				eep.setCurrent(null);
			}
		});
		final JSplitPane retval =
				new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, selectedList, eep);
		retval.setResizeWeight(0.5);
		return retval;
	}

	public ProgramEditingWindow(final @NotNull DanceDatabase db,
								final ProgramMetadata metadata,
								final @NotNull List<ProgramElement> program) {
		super("Dance Program Editor");
		final Desktop desktop = Desktop.getDesktop();
		final ReorderableListModel<@NotNull ProgramElement> programModel =
				new ReorderableListModelImpl<>(program);
		setMinimumSize(new Dimension(400, 300));
		final JTabbedPane pane =
				new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		setContentPane(pane);
		final DanceSelectionPanel dsp = new DanceSelectionPanel(db, programModel);
		pane.add("Select Dances", dsp);
		final JComponent pep = programEditingPanel(programModel);
		pane.add("Edit Selected Dances", pep);
		final MetadataEditingPanel mep = new MetadataEditingPanel(metadata);
		pane.add("Edit Other Content", mep);
		pack();
		dsp.setDividerLocation(0.5);
		if (pep instanceof JSplitPane) {
			((JSplitPane) pep).setDividerLocation(0.5);
		}
		// TODO: Implement 'modified' flag like the SP map viewer
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(TartanMenu.fileMenu(programModel, metadata, mep));
		if (desktop.isSupported(Action.APP_MENU_BAR)) {
			desktop.setDefaultMenuBar(menuBar);
		} else if (!TartanMenu.isOnMac()) {
			setJMenuBar(menuBar);
		}
	}
	private static DanceDatabase initializeDatabaseNoArgs() {
		final PlatformFileDialog chooser = new PlatformFileDialog(null);
		final FilenameFilter filter =
				(dir, name) -> name.endsWith(".db") || name.endsWith(".sqlite") ||
						name.endsWith(".sqlite3");
		// "os.name" property should be safe
		// noinspection AccessOfSystemProperties
		if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("mac")) {
			chooser.setFileFilter(filter);
		} else {
			chooser.setFileFilter(
					new FileNameExtensionFilter("SQLite Databases", "db", "sqlite",
							"sqlite3"));
		}
		chooser.showOpenDialog();
		final @Nullable File file = chooser.getFilename();
		if (file == null) {
			LOGGER.info("User probably pressed 'cancel'");
			System.exit(0);
			throw new IllegalStateException("Returned from System.exit()");
		} else {
			try {
				return new DanceDatabase(file.toPath());
			} catch (final SQLException except) {
				JOptionPane.showMessageDialog(null,
						String.format(
								"Error trying to open %s as SQLite database",
								file.getName()), "Error Opening Database",
						JOptionPane.ERROR_MESSAGE);
				LOGGER.log(Level.SEVERE, "Error opening SQLite database",
						except);
				System.exit(1);
				// This is an impossible condition, that the compiler isn't smart enough
				// to detect yet
				//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
				throw new IllegalStateException("Returned from System.exit()"); // NOPMD
			}
		}
	}
	private static DanceDatabase initializeDatabaseFromArgs(final String... args) {
		final FilenameFilter filter =
				(dir, name) -> name.endsWith(".db") || name.endsWith(".sqlite") ||
						name.endsWith(".sqlite3");
		for (final String arg : args) {
			final Path file = Paths.get(arg);
			if (file.toFile().canRead() && filter.accept(file.toFile(), arg)) {
				try {
					return new DanceDatabase(file);
				} catch (final SQLException except) {
					JOptionPane.showMessageDialog(null,
							String.format(
									"Error trying to open %s as SQLite database",
									arg), "Error Opening Database",
							JOptionPane.ERROR_MESSAGE);
					LOGGER.log(Level.SEVERE, "Error opening SQLite database",
							except);
					System.exit(1);
					// This is an impossible condition, that the compiler isn't smart
					// enough to detect yet
					//noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
					throw new IllegalStateException("Returned from System.exit()");
				}
			}
		}
		return initializeDatabaseNoArgs();
	}

	// Suppression of "access of system properties" is warranted, as setting these three
	// properties still seems to be the prescribed invocation.
	@SuppressWarnings("AccessOfSystemProperties")
	public static void main(final String... args) {
		System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"SCD Program Editor");
		System.setProperty("apple.awt.application.name", "SCD Program Editor");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		final DanceDatabase db;
		final FilenameFilter filter =
				(dir, name) -> name.endsWith(".db") || name.endsWith(".sqlite") ||
						name.endsWith(".sqlite3");
		if (args.length == 0) {
			db = initializeDatabaseNoArgs();
		} else {
			db = initializeDatabaseFromArgs(args);
		}
		// TODO: If a non-DB argument, read previously-written project from it
		final ProgramMetadata metadata = new ProgramMetadata();
		final List<ProgramElement> initialProgram = new ArrayList<>(args.length);
		for (final String arg : args) {
			if (arg.endsWith(".tex") && new File(arg).canRead()) {
				final Path file = Paths.get(arg);
				final Optional<Pair<@NotNull ProgramMetadata,
							@NotNull List<@NotNull ProgramElement>>>
						maybePair = TartanMenu.readFromSpecifiedFile(file, null);
				if (maybePair.isPresent()) {
					TartanMenu.assignMetadata(maybePair.get().getFirst(), metadata);
					metadata.setFilename(file);
					initialProgram.addAll(maybePair.get().getSecond());
					break;
				} else {
					System.exit(2);
					return;
				}
			}
		}
		new ProgramEditingWindow(db, metadata, initialProgram).setVisible(true);
	}
}

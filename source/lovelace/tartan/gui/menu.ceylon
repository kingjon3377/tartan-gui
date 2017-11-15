import javax.swing {
	JMenu,
	JMenuItem,
	KeyStroke,
	InputMap,
	JComponent,
	JFileChooser,
	JOptionPane
}
import lovelace.tartan.model {
	ProgramElement,
	ProgramMetadata,
	Dance,
	Intermission,
	Figure,
	NamedFigure
}
import lovelace.tartan.gui.model {
	MutableListModel
}
import java.awt.event {
	InputEvent,
	KeyEvent
}
import javax.swing.filechooser {
	FileFilter
}
import java.io {
	JFile=File
}
import java.awt {
	Component
}
import ceylon.file {
	parsePath,
	Nil,
	File
}
import lovelace.tartan.latex {
	writeLaTeXProgram,
	readLaTeXProgram
}
import ceylon.collection {
	ArrayList,
	MutableList
}
JMenuItem menuItem(String text, Integer mnemonic, String description,
		Anything() handler, KeyStroke* accelerators) {
	JMenuItem retval = JMenuItem(text, mnemonic);
	if (exists accelerator = accelerators.first) {
		retval.accelerator = accelerator;
	}
	retval.accessibleContext.accessibleDescription = description;
	retval.addActionListener((_) => handler());
	InputMap inputMap = retval.getInputMap(JComponent.whenInFocusedWindow);
	for (accelerator in accelerators) {
		inputMap.put(accelerator, retval.action);
	}
	return retval;
}
object latexFilter extends FileFilter() {
	shared actual Boolean accept(JFile file) => file.name.endsWith(".tex");
	shared actual String description => "LaTeX documents";
}
void readFromFile(MutableListModel<ProgramElement> program, ProgramMetadata metadata,
		Component? parent = null) {
	JFileChooser chooser = JFileChooser();
	chooser.fileFilter = latexFilter;
	chooser.showOpenDialog(parent);
	String input;
	String chosenFilename;
	if (exists filename = chooser.selectedFile?.path, is File file = parsePath(filename).resource) {
		chosenFilename = filename;
		try (reader = file.Reader()) {
			StringBuilder builder = StringBuilder();
			while (exists line = reader.readLine()) {
				builder.append(line);
				builder.appendNewline();
			}
			input = builder.string;
		}
	} else {
		// FIXME: Report this to the user
		return;
	}
	value readingResult = readLaTeXProgram(input);
	if (is ParseException readingResult) {
		// FIXME: Report this to the user
	} else {
		ProgramMetadata returnedMetadata = readingResult.first;
		assignMetadata {
			from = returnedMetadata;
			to = metadata;
		};
		metadata.filename = chosenFilename;
		// TODO: Notify any listeners that the metadata object has changed
		program.clear();
		program.addElements(readingResult.rest);
	}
}
void assignMetadata(ProgramMetadata from, ProgramMetadata to) {
	// Skip the filename, as we'll assign that immediately after this method anyway.
	to.groupCoverName = from.groupCoverName;
	to.groupTitleName = from.groupTitleName;
	to.groupCoverName = from.groupCoverName;
	to.eventTitleName = from.eventTitleName;
	to.coverDate = from.coverDate;
	to.titleDate = from.titleDate;
	to.coverLocation = from.coverLocation;
	to.titleLocation = from.titleLocation;
	to.locationAddress = from.locationAddress;
	to.titleTimes = from.titleTimes;
	to.musicians = from.musicians;
	to.coverImage = from.coverImage;
	to.titleOnCover = from.titleOnCover;
	to.printAuldLangSyne = from.printAuldLangSyne;
	to.backCoverImage = from.backCoverImage;
	to.insidePostDanceImages.clear();
	to.insidePostDanceImages.addAll(from.insidePostDanceImages);
}
// FIXME: Extract actual writing into a method taking Anything(String), or returning String, so we can write tests
void saveToFile(MutableListModel<ProgramElement> program, ProgramMetadata metadata,
		String? passedFilename, Component? parent = null) {
	String filename;
	if (exists passedFilename) {
		filename = passedFilename;
	} else {
		// TODO: Use AWT file-chooser on Mac?
		JFileChooser chooser = JFileChooser();
		chooser.fileFilter = latexFilter;
		chooser.showSaveDialog(parent);
		if (exists chosenFile = chooser.selectedFile) {
			filename = chosenFile.path;
		} else {
			log.info("User canceled from save dialog");
			return;
		}
	}
	value file = parsePath(filename).resource;
	File actualFile;
	switch (file)
	case (is Nil) {
		actualFile = file.createFile();
	}
	case (is File) {
		actualFile = file;
	}
	else {
		JOptionPane.showMessageDialog(parent, "Error: Could not open ``filename`` for writing");
		log.error("``filename`` was neither not present nor an ordinary file");
		return;
	}
	try (writer = actualFile.Overwriter()) {
		writeLaTeXProgram(writer.write, program.asIterable, metadata);
	}
}
JMenu fileMenu(MutableListModel<ProgramElement> program, ProgramMetadata metadata) {
	Boolean onMac = operatingSystem.name == "mac";
	Integer shortcutMask;
	if (onMac) {
		shortcutMask = InputEvent.metaDownMask;
	} else {
		shortcutMask = InputEvent.ctrlDownMask;
	}
	JMenu retval = JMenu("File");
	retval.add(menuItem("Open", KeyEvent.vkO, "Open an existing program for further editing",
		() => readFromFile(program, metadata, retval), KeyStroke.getKeyStroke(KeyEvent.vkO, shortcutMask)));
	retval.add(menuItem("Save", KeyEvent.vkS, "Save the program to file",
		() => saveToFile(program, metadata, metadata.filename, retval),
		KeyStroke.getKeyStroke(KeyEvent.vkS, shortcutMask)));
	retval.add(menuItem("Save As", KeyEvent.vkA, "Save the program to a new file",
		() => saveToFile(program, metadata, null, retval),
		KeyStroke.getKeyStroke(KeyEvent.vkS, shortcutMask.or(InputEvent.shiftDownMask))));
	if (onMac) {
		// TODO: Wire the "quit" code up to the Mac quit-handling system
	} else {
		retval.addSeparator();
		// FIXME: Check whether program or metadata have been modified, and if so ask whether to save first
		retval.add(menuItem("Exit", KeyEvent.vkX, "Quit the app",
			() => process.exit(0), KeyStroke.getKeyStroke(KeyEvent.vkQ, shortcutMask)));
	}
	return retval;
}
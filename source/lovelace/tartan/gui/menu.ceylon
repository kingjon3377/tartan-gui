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
	writeLaTeXProgram
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
		() => nothing, KeyStroke.getKeyStroke(KeyEvent.vkO, shortcutMask)));
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
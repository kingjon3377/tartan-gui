import ceylon.collection {
	MutableList,
	ArrayList
}
import ceylon.file {
	parsePath,
	File
}

import java.awt {
	Dimension
}
import java.io {
	JFile=File
}

import javax.swing {
	JFrame,
	JTabbedPane,
	JFileChooser,
	WindowConstants,
	JSplitPane,
	JMenuBar
}
import javax.swing.filechooser {
	FileFilter
}

import lovelace.tartan.db {
	DanceDatabase
}
import lovelace.tartan.model {
	ProgramElement,
	ProgramMetadata
}
import lovelace.tartan.gui.model {
	MutableListModel,
	ListModelAdapter
}
import ceylon.language.meta.declaration {
	Module,
	Package
}
import ceylon.logging {
	Priority,
	addLogWriter
}
import java.lang {
	System
}
JFrame programEditingWindow(DanceDatabase db, ProgramMetadata metadata, ProgramElement* initialProgram) {
	MutableList<ProgramElement> program = ArrayList<ProgramElement> { *initialProgram };
	MutableListModel<ProgramElement> programModel = ListModelAdapter(program);
	JFrame retval = JFrame("Dance Program Editor");
	retval.setMinimumSize(Dimension(400, 300));
	value pane = JTabbedPane(JTabbedPane.top, JTabbedPane.scrollTabLayout);
	retval.contentPane = pane;
	value dsp = danceSelectionPanel(db, programModel);
	pane.add("Select Dances", dsp);
	value pep = programEditingPanel(programModel);
	pane.add("Edit Selected Dances", pep);
	value mep = metadataEditingPanel(metadata);
	pane.add("Edit Other Content", mep);
	retval.pack();
	if (is JSplitPane dsp) {
		dsp.setDividerLocation(0.5);
	}
	if (is JSplitPane pep) {
		pep.setDividerLocation(0.5);
	}
	retval.defaultCloseOperation = WindowConstants.disposeOnClose;
	JMenuBar menuBar = JMenuBar();
	menuBar.add(fileMenu(programModel, metadata, mep));
	retval.jMenuBar = menuBar;
	return retval;
}
void logWriter(Priority priority, Module|Package mod,
	String message, Throwable? except) {
	process.writeErrorLine("``priority`` (``mod``): ``message``");
	if (exists except) {
		process.writeErrorLine(except.message);
		except.printStackTrace();
	}
}
shared void run() {
	addLogWriter(logWriter);
	System.setProperty("com.apple.mrj.application.apple.menu.about.name",
		"SCD Program Editor");
	System.setProperty("apple.awt.application.name", "SCD Program Editor");
	System.setProperty("apple.laf.useScreenMenuBar", "true");
	DanceDatabase db;
	for (arg in process.arguments) {
		if (parsePath(arg).resource is File) {
			db = DanceDatabase(arg);
			break;
		}
	} else {
		JFileChooser chooser = JFileChooser();
		chooser.fileFilter = object extends FileFilter() {
			shared actual Boolean accept(JFile file) =>
					file.name.endsWith(".db") || file.name.endsWith(".sqlite") || file.name.endsWith(".sqlite3");
			shared actual String description => "SQLite Databases";
		};
		if (chooser.showOpenDialog(null) == JFileChooser.approveOption) {
			db = DanceDatabase(chooser.selectedFile.path);
		} else {
			process.writeLine("User probably pressed 'cancel'");
			return;
		}
	}
	// TODO If a non-DB argument, read previously-written project from it
	ProgramMetadata metadata = ProgramMetadata();
	{ProgramElement*} initialProgram;
	for (arg in process.arguments) {
		if (arg.endsWith(".tex"), is File file = parsePath(arg).resource) {
			if (exists readingResult = readFromSpecifiedFile(file, null)) {
				ProgramMetadata returnedMetadata = readingResult.first;
				assignMetadata {
					from = returnedMetadata;
					to = metadata;
				};
				metadata.filename = arg;
				initialProgram = {*readingResult.rest};
				break;
			}
		}
	} else {
		initialProgram = {};
	}
	programEditingWindow(db, metadata, *initialProgram).visible = true;
}
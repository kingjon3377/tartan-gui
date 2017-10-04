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
	JSplitPane
}
import javax.swing.filechooser {
	FileFilter
}

import lovelace.tartan.db {
	DanceDatabase
}
import lovelace.tartan.model {
	ProgramElement
}
JFrame programEditingWindow(DanceDatabase db) {
	MutableList<ProgramElement> program = ArrayList<ProgramElement>();
	JFrame retval = JFrame("Dance Program Editor");
	retval.setMinimumSize(Dimension(400, 300));
	value pane = JTabbedPane(JTabbedPane.top, JTabbedPane.scrollTabLayout);
	retval.contentPane = pane;
	value dsp = danceSelectionPanel(db, program);
	pane.add("Select Dances", dsp);
	retval.pack();
	if (is JSplitPane dsp) {
		dsp.setDividerLocation(0.5);
	}
	retval.defaultCloseOperation = WindowConstants.disposeOnClose;
	return retval;
}
shared void run() {
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
	programEditingWindow(db).visible = true;
}
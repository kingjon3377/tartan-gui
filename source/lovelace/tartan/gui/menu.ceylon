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
	AuldLangSyne,
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
		writer.writeLine("""\documentclass{tartan}""");
		Boolean auldLangSyne = !program.asIterable.narrow<AuldLangSyne>().empty;
		if (auldLangSyne) {
			writer.writeLine("""\usepackage{verse}""");
		}
		for (pkg in metadata.extraPackages) {
			if ("verse" != pkg || !auldLangSyne) {
				writer.writeLine("\\usePackage{``pkg``}");
			}
		}
		void writeIfNonempty(String string) {
			if (!string.empty) {
				writer.writeLine(string);
			}
		}
		String quoted(String string) => string.replace("&", "\\&").replace("<b>", "\\textbf{").replace("</b>", "}");
		writeIfNonempty(metadata.extraPrologue);
		writer.writeLine("""\begin{document}""");
		writeIfNonempty(metadata.preCoverText);
		writer.writeLine("""\begin{center}""");
		writer.write("""\large""");
		writeIfNonempty(metadata.coverText);
		writer.writeLine("""\normalsize""");
		writer.writeLine("""\end{center}""");
		writeIfNonempty(metadata.postCoverText);
		writer.writeLine("""\clearpage""");
		writer.writeLine("""\listofdances""");
		writer.writeLine("""\clearpage""");
		writeIfNonempty(metadata.preDancesText);
		for (item in program.asIterable) {
			switch (item)
			case (is Dance) {
				writer.write("\\begin{scdance}{``item.title``}{``item.source``}");
				writer.writeLine("{``item.tempo``}{``item.times``x``item.length``}{``item.formation``}");
				void writeSimpleFigure(Figure figure) {
					writer.write("""\scfigure""");
					if (exists bars = figure.bars) {
						writer.write("[``bars``]");
					}
					// TODO: Other quoting/HTML-to-TeX conversion stuff
					writer.writeLine("{``quoted(figure.description)``}");
				}
				for (figure in item.contents) {
					switch (figure)
					case (is Figure) {
						writeSimpleFigure(figure);
					}
					case (is NamedFigure) {
						writer.write("""\namedfigure{""");
						for (subfigure in figure.contents) {
							switch (subfigure)
							case (is Figure) {
								writeSimpleFigure(subfigure);
							}
							case (is String) {
								writer.write(subfigure);
							}
						}
						writer.writeLine("}");
					}
					case (is String) {
						writer.write(figure);
					}
				}
				writer.writeLine("""\end{scdance}""");
			}
			case (is Intermission) {
				String text = item.description;
				writer.write("""\intermission""");
				if ("Intermission" != text || text.empty) {
					writer.writeLine();
				} else {
					writer.writeLine("[``text``]");
				}
			}
			case (is AuldLangSyne) {
				// TODO: Actually use the "description" attribute
				// TODO: Make printing of text of the song optional
				// TODO: IN practice, this (the command to print the text) shouldn't be part of the "program" at all
				writer.writeLine("""\auldlangsyne""");
			}
		}
		writeIfNonempty(metadata.postDancesText);
		writer.writeLine("""\end{document}""");
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
		retval.add(menuItem("Exit", KeyEvent.vkX, "Quit the app",
			() => nothing, KeyStroke.getKeyStroke(KeyEvent.vkQ, shortcutMask)));
	}
	return retval;
}
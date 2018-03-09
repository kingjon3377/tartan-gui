import lovelace.tartan.model {
	ProgramElement,
	ProgramMetadata,
	Dance,
	NamedFigure,
	Figure,
	Intermission
}
String quoted(String string) => string.replace("&", "\\&").replace("{", """\{""").replace("}","""\}""")
		.replace("<b>", "\\textbf{").replace("</b>", "}").replace("½", """\nicefrac{1}{2}""")
		.replace("¾", """\nicefrac{3}{4}""").replace("“", """``""").replace("”", """''""")
		.replace("–", "---").replace("’", "'").replace("„", """``""").replace("‟", """''""")
		.replace("‘", "'").replace("‗", """`""").replace("\f", "").replace("¼", """\nicefrac{1}{4}""")
		.replace("‑", "--").replace("─", "---").replace("—", "---").replace(";", ";")
		.replace("⅔", """\nicefrac{2}{3}""").replace("⅜", """\nicefrac{3}{8}""")
		.replace("⅞", """\nicefrac{7}{8}""");
void writePrologue(Anything(String) ostream, ProgramMetadata metadata) {
	void writePrologueLine(String command, String arg) {
		if (!arg.trimmed.empty) {
			ostream("\\``command``{``quoted(arg)``}\n");
		}
	}
	// TODO: Don't write starred forms when the same as unstarred
	writePrologueLine("tartangroupname", metadata.groupCoverName);
	writePrologueLine("tartangroupname*", metadata.groupTitleName);
	writePrologueLine("tartanballname", metadata.eventCoverName);
	writePrologueLine("tartanballname*", metadata.eventTitleName);
	writePrologueLine("tartanballdate", metadata.coverDate);
	writePrologueLine("tartanballdate*", metadata.titleDate);
	writePrologueLine("tartanhall", metadata.coverLocation);
	writePrologueLine("tartanhall*", metadata.titleLocation);
	writePrologueLine("tartanhalladdress", metadata.locationAddress);
	writePrologueLine("tartantimes", metadata.titleTimes.replace("\n", "\\\\*\n"));
	writePrologueLine("tartanmusicians", metadata.musicians);
}
"Write LaTeX code representing the given Ball program to the given stream."
// TODO: Split this method to reduce its length and complexity.
shared void writeLaTeXProgram(Anything(String) ostream, {ProgramElement*} program, ProgramMetadata metadata) {
	void writeLine(String string) => ostream("``string``\n");
	void writeSimpleCommand(String command, String? arg = null) {
		if (exists arg) {
			writeLine("\\``command``{``arg``}");
		} else {
			writeLine("\\``command``");
		}
	}
	writeLine("% This LaTeX file was produced by the tartan-gui graphical editor;");
	writeLine("% if you edit it by hand, that editor may not be able to read it again.");
	writeSimpleCommand("documentclass", "tartan");
	writePrologue(ostream, metadata);
	writeSimpleCommand("begin", "document");
	String latexImage(String imageFilename) {
		for (extension in {".png", ".jpg", ".pdf"}) { // only extensions graphicx supports
			if (imageFilename.endsWith(extension)) {
				return imageFilename.removeTerminal(extension);
			}
		}
		return imageFilename;
	}
	if (exists coverImage = metadata.coverImage) {
		if ({metadata.groupCoverName, metadata.eventCoverName, metadata.coverDate,
				metadata.coverLocation}.every(String.empty)) {
			writeSimpleCommand("tartanimage", latexImage(coverImage));
		} else {
			writeSimpleCommand("tartanimagecover", latexImage(coverImage));
		}
	} else if (!{metadata.groupCoverName, metadata.eventCoverName, metadata.coverDate,
			metadata.coverLocation}.every(String.empty)) {
		writeSimpleCommand("tartancover");
	}
	if (!{metadata.groupTitleName, metadata.eventTitleName, metadata.titleDate, metadata.titleLocation,
			metadata.locationAddress, metadata.titleTimes, metadata.musicians}.every(String.empty)) {
		if (metadata.titleOnCover) {
			writeSimpleCommand("clearpage");
		} else {
			writeSimpleCommand("cleardoublepage");
		}
		writeSimpleCommand("maketartantitle");
	}
	writeSimpleCommand("clearpage");
	writeSimpleCommand("listofdances");
	writeSimpleCommand("clearpage");
	for (item in program) {
		switch (item)
		case (is Dance) {
			ostream("\\begin{scdance}{``item.title``}{``item.source``}");
			ostream("{``item.tempo``}{``item.times``x``item.length``}{``item.formation``}\n");
			void writeSimpleFigure(Figure figure) {
				ostream("""\scfigure""");
				if (exists bars = figure.bars) {
					ostream("[``bars``]");
				}
				ostream("{``quoted(figure.description)``}\n");
			}
			for (figure in item.contents) {
				switch (figure)
				case (is Figure) {
					writeSimpleFigure(figure);
				}
				case (is NamedFigure) {
					ostream("""\namedfigure{""");
					for (subfigure in figure.contents) {
						switch (subfigure)
						case (is Figure) {
							writeSimpleFigure(subfigure);
						}
						case (is String) {
							ostream(subfigure);
						}
					}
					ostream("}\n");
				}
				case (is String) {
					ostream(figure);
				}
			}
			writeLine("""\end{scdance}""");
		}
		case (is Intermission) {
			String text = item.description;
			ostream("""\intermission""");
			if ("Intermission" == text || text.empty) {
				ostream("\n");
			} else {
				writeLine("[``text``]");
			}
		}
	}
	for (image in metadata.insidePostDanceImages) {
		writeSimpleCommand("clearpage");
		writeSimpleCommand("tartanimage", latexImage(image));
	}
	if (metadata.printAuldLangSyne) {
		writeSimpleCommand("clearpage");
		writeSimpleCommand("auldlangsyne");
	}
	if (exists image = metadata.backCoverImage) {
		writeSimpleCommand("cleartoverso");
		writeSimpleCommand("tartanimage", latexImage(image));
	}
	writeLine("""\end{document}""");
}
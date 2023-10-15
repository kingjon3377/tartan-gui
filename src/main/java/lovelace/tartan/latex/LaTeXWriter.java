package lovelace.tartan.latex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.DanceMember;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.Intermission;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.NamedFigureMember;
import lovelace.tartan.model.ProgramElement;
import lovelace.tartan.model.ProgramMetadata;
import lovelace.tartan.model.SimplestMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Code to write a Ball program to LaTeX.
 *
 * @author Jonathan Lovelace
 */
public final class LaTeXWriter {
	/**
	 * The extensions the graphicx package supports.
	 */
	private static final List<String> SUPPORTED_IMAGE_EXTENSIONS =
			List.of(".png", ".jpg", ".pdf");

	private static String quoted(final @NotNull String string) {
		return string.replace("&", "\\&").replace("{", "\\{").replace("}", "\\}")
					   .replace("<b>", "\\textbf{").replace("</b>", "}")
					   .replace("<i>", "\\textit{").replace("</i>", "}")
					   .replace("½", "\\nicefrac{1}{2}")
					   .replace("<sup>", "\\textsuperscript").replace("</sup>", "}")
					   .replace("¾", "\\nicefrac{3}{4}").replace("“", "``")
					   .replace("”", "''").replace("–", "---").replace("’", "'")
					   .replace("„", "``").replace("‟", "''").replace("‘", "'")
					   .replace("‗", "`").replace("\f", "")
					   .replace("¼", "\\nicefrac{1}{4}").replace("‑", "--")
					   .replace("─", "---").replace("—", "---").replace(";", ";")
					   .replace("⅔", "\\nicefrac{2}{3}").replace("⅜", "\\nicefrac{3}{8}")
					   .replace("⅞", "\\nicefrac{7}{8}");
	}

	private static void writePrologueLine(final @NotNull Appendable ostream,
										  final @NotNull String command,
										  final @NotNull String arg)
			throws IOException {
		if (!arg.trim().isEmpty()) {
			ostream.append("\\");
			ostream.append(command);
			ostream.append("{");
			ostream.append(quoted(arg).trim());
			ostream.append("}\n");
		}
	}

	private static void writePrologue(final @NotNull Appendable ostream,
									  final @NotNull ProgramMetadata metadata)
			throws IOException {
		// TODO: Do't write starred forms when the same as unstarred
		writePrologueLine(ostream, "tartangroupname", metadata.getGroupCoverName());
		writePrologueLine(ostream, "tartangroupname*", metadata.getGroupTitleName());
		writePrologueLine(ostream, "tartanballname", metadata.getEventCoverName());
		writePrologueLine(ostream, "tartanballname*", metadata.getEventTitleName());
		writePrologueLine(ostream, "tartanballdate", metadata.getCoverDate());
		writePrologueLine(ostream, "tartanballdate*", metadata.getTitleDate());
		writePrologueLine(ostream, "tartanhall", metadata.getCoverLocation());
		writePrologueLine(ostream, "tartanhall*", metadata.getTitleLocation());
		writePrologueLine(ostream, "tartanhalladdress", metadata.getLocationAddress());
		writePrologueLine(ostream, "tartantimes", metadata.getTitleTimes()
														  .replace("\n", "\\\\*\n"));
		writePrologueLine(ostream, "tartanmusicians", metadata.getMusicians());
	}

	private static void writeLine(final @NotNull Appendable ostream,
								  final @NotNull String string) throws IOException {
		ostream.append(string);
		ostream.append('\n');
	}

	private static void writeSimpleCommand(final @NotNull Appendable ostream,
										   final @NotNull String command) throws IOException {
		writeSimpleCommand(ostream, command, null);
	}

	private static void writeSimpleCommand(final @NotNull Appendable ostream,
										   final @NotNull String command,
										   final @Nullable String arg) throws IOException {
		ostream.append('\\');
		ostream.append(command);
		if (arg != null) {
			ostream.append('{');
			ostream.append(arg);
			ostream.append("}\n");
		}
	}

	private static String latexImage(final @NotNull Path imageFilename) {
		final String asString = imageFilename.toString();
		for (final String extension : SUPPORTED_IMAGE_EXTENSIONS) {
			if (asString.endsWith(extension)) {
				return asString.substring(0, asString.length() - extension.length());
			}
		}
		return asString;
	}

	private static void writeSimpleFigure(final @NotNull Appendable ostream,
										  final @NotNull Figure figure)
			throws IOException {
		ostream.append("\\scfigure");
		final @Nullable String bars = figure.getBars();
		if (bars != null) {
			ostream.append('[');
			ostream.append(bars);
			ostream.append(']');
		}
		ostream.append('{');
		ostream.append(quoted(figure.getDescription()));
		ostream.append("}\n");
	}

	/**
	 * Write LaTeX code representing the given Ball program to the given stream.
	 *
	 * @param out      the stream to write to
	 * @param program  the dances etc. that make up the program
	 * @param metadata the metadata to write in the prologue
	 * @throws IOException on I/O error while writing
	 */
	public void writeLaTeXProgram(final @NotNull Appendable out,
								  final @NotNull List<@NotNull ProgramElement> program,
								  final @NotNull ProgramMetadata metadata)
			throws IOException {
		writeLine(out,
				"% This LaTeX file was produced by the tartan-gui graphical editor;");
		writeLine(out,
				"% if you edit it by hand, that editor may not be able to read it " +
						"again.");
		writeSimpleCommand(out, "documentclass", "tartan");
		writePrologue(out, metadata);
		writeSimpleCommand(out, "begin", "document");
		final @Nullable Path coverImage = metadata.getCoverImage();
		if (Objects.nonNull(coverImage)) {
			// TODO: Add helper "allCoverFieldsEmpty() method to ProgramMetadata
			if (metadata.getGroupCoverName().isEmpty() &&
						metadata.getEventCoverName().isEmpty() &&
						metadata.getCoverDate().isEmpty() &&
						metadata.getCoverLocation().isEmpty()) {
				writeSimpleCommand(out, "tartanimage", latexImage(coverImage));
			} else {
				writeSimpleCommand(out, "tartanimagecover", latexImage(coverImage));
			}
		} else if (!metadata.getGroupCoverName().isEmpty() ||
						   !metadata.getEventCoverName().isEmpty() ||
						   !metadata.getCoverDate().isEmpty() ||
						   !metadata.getCoverLocation().isEmpty()) {
			writeSimpleCommand(out, "tartancover");
		}
		// TODO: Add helper method "allTitleFieldsEmpty()" to ProgramMetadata
		if (!metadata.getGroupTitleName().isEmpty() ||
					!metadata.getEventTitleName().isEmpty() ||
					!metadata.getTitleDate().isEmpty() ||
					!metadata.getTitleLocation().isEmpty() ||
					!metadata.getLocationAddress().isEmpty() ||
					!metadata.getTitleTimes().isEmpty() ||
					!metadata.getMusicians().isEmpty()) {
			if (metadata.getTitleOnCover()) {
				writeSimpleCommand(out, "clearpage");
			} else {
				writeSimpleCommand(out, "cleardoublepage");
			}
			writeSimpleCommand(out, "maketartantitle");
		}
		writeSimpleCommand(out, "clearpage");
		writeSimpleCommand(out, "vspace*", "\\fill");
		writeSimpleCommand(out, "listofdances");
		writeSimpleCommand(out, "vspace", "\\fill");
		writeSimpleCommand(out, "clearpage");
		for (final ProgramElement item : program) {
			if (item instanceof Dance dance) {
				out.append(String.format("\\begin{scdance}{%s}{%s}{%s}{%dx%d}{%s}%n",
						((Dance) item).getTitle(), ((Dance) item).getSource(),
						((Dance) item).getTempo(), ((Dance) item).getTimes(),
						((Dance) item).getLength(), ((Dance) item).getFormation()));
				for (final DanceMember figure : dance.getContents()) {
					if (figure instanceof Figure) {
						writeSimpleFigure(out, (Figure) figure);
					} else if (figure instanceof NamedFigure) {
						out.append("\\namedfigure{");
						for (final NamedFigureMember subfigure : ((NamedFigure) figure)
																   .getContents()) {
							if (subfigure instanceof Figure) {
								writeSimpleFigure(out, (Figure) subfigure);
							} else if (subfigure instanceof SimplestMember) {
								out.append(((SimplestMember) subfigure).getString());
							} else {
								throw new IllegalStateException("Impossible NamedFigureMember");
							}
						}
						out.append("}\n");
					} else if (figure instanceof SimplestMember) {
						out.append(((SimplestMember) figure).getString());
					} else {
						throw new IllegalStateException("Impossible DanceMember");
					}
				}
				writeLine(out, "\\end{scdance}");
			} else if (item instanceof Intermission) {
				final String text = ((Intermission) item).getDescription();
				if ("Intermission".equals(text) || text.isEmpty()) {
					writeSimpleCommand(out, "intermission");
				} else {
					writeSimpleCommand(out, "intermission");
					out.append('[');
					out.append(text);
					out.append(']');
				}
			}
		}
		for (final Path image : metadata.getInsidePostDanceImages()) {
			writeSimpleCommand(out, "clearpage");
			writeSimpleCommand(out, "tartanimage", latexImage(image));
		}
		if (metadata.getPrintAuldLangSyne()) {
			writeSimpleCommand(out, "clearpage");
			writeSimpleCommand(out, "auldlangsyne");
		}
		final @Nullable Path image = metadata.getBackCoverImage();
		if (image != null) {
			writeSimpleCommand(out, "cleartoverso");
			writeSimpleCommand(out, "tartanimage", latexImage(image));
		}
		writeLine(out, "\\end{document}");
	}
}

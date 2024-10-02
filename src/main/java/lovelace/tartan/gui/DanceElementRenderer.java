package lovelace.tartan.gui;

import java.awt.Component;
import java.util.Objects;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.NamedFigureMember;
import lovelace.tartan.model.SimplestMember;

/**
 * A renderer for the table (list) of figures in a dance.
 *
 * @author Jonathan Lovelace
 */
public class DanceElementRenderer extends DefaultTableCellRenderer {
	private static final String FIGURE_FORMAT =
			"<html><table><tr><td width=\"10%%\">%s</td><td>%s</td></tr></table></html>";
	private static final Logger LOGGER =
			Logger.getLogger(DanceElementRenderer.class.getName());

	@Override
	public Component getTableCellRendererComponent(final JTable table,
												   final Object value,
												   final boolean isSelected,
												   final boolean hasFocus,
												   final int row, final int column) {
		switch (value) {
			case final SimplestMember simplestMember -> {
				return super.getTableCellRendererComponent(table,
						simplestMember.getString(), isSelected, hasFocus, row,
						column);
			}
			case final Figure figure -> {
				return super.getTableCellRendererComponent(table,
						String.format(FIGURE_FORMAT,
								Objects.toString(figure.getBars(), ""),
								figure.getDescription()), isSelected, hasFocus,
						row, column);
			}
			case final NamedFigure namedFigure -> {
				final StringBuilder builder = new StringBuilder();
				builder.append("<html><table>");
				for (final NamedFigureMember movement : namedFigure.getContents()) {
					renderFigureMovement(movement, builder);
				}
				builder.append("</table></html>");
				return super.getTableCellRendererComponent(table, builder.toString(),
						isSelected, hasFocus, row, column);
			}
			case null, default -> {
				return super.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);
			}
		}
	}

	private static void renderFigureMovement(final NamedFigureMember movement,
	                                         final StringBuilder builder) {
		builder.append("<tr><td width=\"10%\">");
		switch (movement) { // TODO: Extract method
			case final Figure figure -> {
				builder.append(
						Objects.toString(figure.getBars(), "&nbsp;"));
				builder.append("</td><td>");
				builder.append(figure.getDescription());
			}
			case final SimplestMember simplestMember -> {
				builder.append("&nbsp;</td><td>");
				builder.append(simplestMember.getString());
			}
			default -> {
				LOGGER.warning("Unexpected type of member of named figure");
				builder.append(movement);
			}
		}
		builder.append("</td></tr>");
	}
}

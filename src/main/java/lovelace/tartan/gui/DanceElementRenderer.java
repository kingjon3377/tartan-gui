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
		if (value instanceof SimplestMember) {
			return super.getTableCellRendererComponent(table,
					((SimplestMember) value).getString(), isSelected, hasFocus, row,
					column);
		} else if (value instanceof Figure) {
			return super.getTableCellRendererComponent(table,
					String.format(FIGURE_FORMAT,
							Objects.toString(((Figure) value).getBars(), ""),
							((Figure) value).getDescription()), isSelected, hasFocus,
					row, column);
		} else if (value instanceof NamedFigure) {
			final StringBuilder builder = new StringBuilder();
			builder.append("<html><table>");
			for (NamedFigureMember movement : ((NamedFigure) value).getContents()) {
				builder.append("<tr><td width=\"10%\">");
				if (movement instanceof Figure) {
					builder.append(
							Objects.toString(((Figure) movement).getBars(), "&nbsp;"));
					builder.append("</td><td>");
					builder.append(((Figure) movement).getDescription());
				} else if (movement instanceof SimplestMember) {
					builder.append("&nbsp;</td><td>");
					builder.append(((SimplestMember) movement).getString());
				} else {
					LOGGER.warning("Unexpected type of member of named figure");
					builder.append(movement);
				}
				builder.append("</td></tr>");
			}
			builder.append("</table></html>");
			return super.getTableCellRendererComponent(table, builder.toString(),
					isSelected, hasFocus, row, column);
		} else {
			return super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
		}
	}
}

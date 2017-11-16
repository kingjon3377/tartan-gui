import javax.swing {
	JTable
}
import java.awt {
	Component
}
import javax.swing.table {
	DefaultTableCellRenderer
}
import java.lang {
	Types
}
import lovelace.tartan.model {
	Figure,
	NamedFigure
}
object danceElementRenderer extends DefaultTableCellRenderer() {
	shared actual Component getTableCellRendererComponent(JTable table, Object item, Boolean isSelected,
			Boolean hasFocus, Integer rowIndex, Integer columnIndex) {
		switch (item)
		case (is String) {
			return super.getTableCellRendererComponent(table, Types.nativeString(item), isSelected, hasFocus,
				rowIndex, columnIndex);
		}
		case (is Figure) {
			return super.getTableCellRendererComponent(table, Types.nativeString(
					"<html><table><tr><td width=\"10%\">``item.bars else ""``</td><td>``
						item.description``</td></tr></table></html>"), isSelected, hasFocus,
				rowIndex, columnIndex);
		}
		case (is NamedFigure) {
			StringBuilder builder = StringBuilder();
			builder.append("<html><table>");
			for (movement in item.contents) {
				builder.append("""<tr><td width="10%">""");
				if (is Figure movement) {
					if (exists bars = movement.bars) {
						builder.append(bars);
					} else {
						builder.append("&nbsp;");
					}
					builder.append("</td><td>");
					builder.append(movement.description);
				} else {
					builder.append("&nbsp;</td><td>");
					builder.append(movement);
				}
				builder.append("</td></tr>");
			}
			return super.getTableCellRendererComponent(table, Types.nativeString(builder.string), isSelected,
				hasFocus, rowIndex, columnIndex);
		}
		else {
			return super.getTableCellRendererComponent(table, item, isSelected, hasFocus, rowIndex, columnIndex);
		}
	}
}

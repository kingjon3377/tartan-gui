package lovelace.tartan.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.SimplestMember;
import org.jetbrains.annotations.Nullable;

/**
 * A component to allow the user to edit movements in a dance.
 *
 * @author Jonathan Lovelace
 */
public class DanceElementEditor implements TableCellEditor {
	private final List<CellEditorListener> listeners = new ArrayList<>();
	private final TableCellEditor defaultEditor =
			new DefaultCellEditor(new JTextField());
	private @Nullable Object current = null;

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value,
												 final boolean isSelected, final int row,
												 final int column) {
		current = value;
		return switch (value) {
			case final NamedFigure namedFigure -> new NamedFigureEditor(namedFigure, this::stopCellEditing);
			case final SimplestMember simplestMember -> new DanceStringEditor(simplestMember.getString(),
					simplestMember::setString, this::stopCellEditing);
			case final Figure figure -> new FigureEditor(figure, this::stopCellEditing);
			case null, default -> defaultEditor
					.getTableCellEditorComponent(table, value, isSelected, row,
							column);
		};
	}

	@Override
	public @Nullable Object getCellEditorValue() {
		return current;
	}

	@Override
	public boolean isCellEditable(final EventObject event) {
		return defaultEditor.isCellEditable(event);
	}

	@Override
	public boolean shouldSelectCell(final EventObject event) {
		return defaultEditor.shouldSelectCell(event);
	}

	@Override
	public boolean stopCellEditing() {
		final ChangeEvent stopEvent = new ChangeEvent(this);
		for (final CellEditorListener listener : new ArrayList<>(listeners)) {
			listener.editingStopped(stopEvent);
		}
		return true;
	}

	@Override
	public void cancelCellEditing() {
		final ChangeEvent cancelEvent = new ChangeEvent(this);
		for (final CellEditorListener listener : new ArrayList<>(listeners)) {
			listener.editingCanceled(cancelEvent);
		}
	}

	@Override
	public void addCellEditorListener(final CellEditorListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeCellEditorListener(final CellEditorListener listener) {
		listeners.remove(listener);
	}
}

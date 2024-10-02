package lovelace.tartan.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.stream.IntStream;
import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import lovelace.tartan.gui.controls.BorderedPanel;
import lovelace.tartan.gui.controls.ListenedButton;
import lovelace.tartan.gui.model.SingleColumnTableModel;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.DanceMember;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.ProgramElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A panel to hold the figures in a dance to let the user edit them.
 *
 * @author Jonathan Lovelace
 */
public final class ElementEditingPanel extends JPanel {
	private @Nullable ProgramElement current = null;

	private @NotNull SingleColumnTableModel<DanceMember> tableModel =
			new SingleColumnTableModel<>(new ArrayList<>(),
					DanceMember.class, "Directions");
	private final JTable table = new JTable(tableModel);

	private final DanceDetailsPanel detailsPanel = new DanceDetailsPanel();

	public @Nullable ProgramElement getCurrent() {
		return current;
	}

	private void fixHeights(final @Nullable Object ignored) {
		for (int row = 0; row < table.getRowCount(); row++) {
			final Component renderer =
					table.prepareRenderer(table.getCellRenderer(row, 0), row, 0);
			final Component editor =
					table.prepareEditor(table.getCellEditor(row, 0), row, 0);
			table.setRowHeight(row, IntStream.of(renderer.getPreferredSize().height,
					renderer.getMinimumSize().height, editor.getMinimumSize().height,
					editor.getPreferredSize().height).max().getAsInt());
		}
	}

	public void setCurrent(final @Nullable ProgramElement current) {
		this.current = current;
		detailsPanel.setCurrent(current);
		if (current instanceof Dance) {
			final @NotNull SingleColumnTableModel<DanceMember> model =
					new SingleColumnTableModel<>(
							((Dance) current).getContents(), DanceMember.class,
							"Directions");
			model.addTableModelListener(this::fixHeights);
			tableModel = model;
			table.setModel(model);
			fixHeights(null);
		} else {
			final @NotNull SingleColumnTableModel<DanceMember> model =
					new SingleColumnTableModel<>(new ArrayList<>(), DanceMember.class,
							"Directions");
			tableModel = model;
			table.setModel(model);
		}
	}

	private void addFigure(
			final Object ignored) { // TODO: Disable the button when current not a dance
		if (current instanceof Dance) {
			tableModel.add(new Figure("Figure To Be Entered", "Bars TBD"));
		}
	}

	private void addNamedFigure(
			final Object ignored) { // TODO: Disable the button when not a dance
		if (current instanceof Dance) {
			tableModel.add(new NamedFigure(
					new Figure("First Movement of Figure", "Bars TBD")));
			fixHeights(ignored);
		}
	}

	private void removeFigure(final Object ignored) {
		// TODO: Disable the button when not a dance or no figure selected
		final int selection = table.getSelectedRow();
		if (current instanceof Dance && selection >= 0 &&
				    selection < table.getRowCount()) {
			tableModel.remove(selection);
		}
	}

	public ElementEditingPanel() {
		super(new BorderLayout());
		table.setTransferHandler(new FigureTransferHandler());
		table.setDropMode(DropMode.INSERT);
		table.setDragEnabled(true);
		table.setDefaultEditor(Object.class, new DanceElementEditor());
		table.setDefaultRenderer(Object.class, new DanceElementRenderer());
		add(detailsPanel, BorderLayout.PAGE_START);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(BorderedPanel
					.horizontalLine(new ListenedButton("Add Figure", this::addFigure),
							new ListenedButton("Add Multi-Movement Figure",
									this::addNamedFigure),
							new ListenedButton("Remove Selected Figure",
									this::removeFigure)), BorderLayout.PAGE_END);
	}

	@Override
	public String toString() {
		if (current instanceof Dance d) {
			return "ElementEditingPanel editing '%s'".formatted(d.getTitle());
		} else if (current == null) {
			return "ElementEditingPanel (empty)";
		} else {
			return "ElementEditingPanel editing %s".formatted(current.getClass().getSimpleName());
		}
	}
}

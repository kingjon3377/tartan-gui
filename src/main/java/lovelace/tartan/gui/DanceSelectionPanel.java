package lovelace.tartan.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import lovelace.tartan.db.DanceDatabase;
import lovelace.tartan.db.DanceRow;
import lovelace.tartan.db.DatabaseAdapter;
import lovelace.tartan.gui.controls.BorderedPanel;
import lovelace.tartan.gui.controls.BoxPanel;
import lovelace.tartan.gui.controls.BoxPanel.BoxDirection;
import lovelace.tartan.gui.controls.ImageButton;
import lovelace.tartan.gui.controls.ImageLoader;
import lovelace.tartan.gui.controls.ListenedButton;
import lovelace.tartan.gui.model.ReorderableListModel;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.Intermission;
import lovelace.tartan.model.ProgramElement;
import org.jetbrains.annotations.Nullable;

/**
 * A panel to let the user choose which dances are on the program.
 *
 * @author Jonathan Lovelace
 */
public class DanceSelectionPanel extends JSplitPane {
	private final DanceSearchResultsListModel danceListModel;
	private final JList<DanceRow> danceList;
	private final JTextField filterField = new JTextField(15);
	private final JList<ProgramElement> selectedList;
	private final ReorderableListModel<ProgramElement> program;
	private final DanceDatabase db;

	private void filterDanceList(ActionEvent ignored) {
		final String search = filterField.getText().trim();
		if (search.isEmpty()) {
			danceListModel.search(null);
		} else {
			danceListModel.search(search);
		}
		danceList.repaint();
	}

	private void addDance() {
		@Nullable DanceRow selection = danceList.getSelectedValue();
		if (selection != null && program.stream().filter(Dance.class::isInstance)
										 .map(Dance.class::cast).map(Dance::getTitle)
										 .noneMatch(selection.getName()::equals)) {
			final Dance dance =
					DatabaseAdapter.convertDance(selection, db.cribText(selection));
			final int target = selectedList.getSelectedIndex();
			if (target >= 0) {
				program.add(target, dance);
			} else {
				program.add(dance);
			}
		}
	}

	public DanceSelectionPanel(DanceDatabase db,
							   ReorderableListModel<ProgramElement> program) {
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		this.program = program;
		this.db = db;
		JButton rightButton;
		try {
			rightButton =
					new ImageButton(ImageLoader.loadImage(
							// TODO: Check image path once port back to Java complete
							"lovelace/tartan/gui/arrow-right-300px.png"));
		} catch (IOException e) {
			rightButton = new JButton("Add");
		}
		JButton leftButton;
		try {
			leftButton =
					new ImageButton(ImageLoader.loadImage(
							// TODO: Check image path once port back to Java complete
							"lovelace/tartan/gui/arrow-left-300px.png"));
		} catch (IOException e) {
			leftButton = new JButton("Add");
		}
		final JPanel inner =
				new BoxPanel(BoxDirection.PageAxis, BoxPanel.GLUE, rightButton,
						new BoxPanel.BoxStrut(5), leftButton, BoxPanel.GLUE);
		inner.setMaximumSize(new Dimension(60, 4096));
		inner.setPreferredSize(new Dimension(40, 480));
		inner.setMinimumSize(new Dimension(20, 45));

		danceListModel = new DanceSearchResultsListModel(db);
		danceList = new JList<>(danceListModel);

		final JPanel filterPanel = BorderedPanel.horizontalLine(null, filterField,
				new ListenedButton("Search", this::filterDanceList));
		final JPanel left =
				BorderedPanel.verticalLine(filterPanel, new JScrollPane(danceList),
						null);
		filterField.addActionListener(this::filterDanceList);
		selectedList = new JList<>(program);
		selectedList.setMinimumSize(new Dimension(400, 100));
		selectedList.setTransferHandler(new ProgramElementTransferHandler());
		selectedList.setDropMode(DropMode.INSERT);
		selectedList.setDragEnabled(true);
		rightButton.addActionListener((evt) -> addDance());
		danceList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					addDance();
				}
			}

			@Override
			public void keyPressed(final KeyEvent evt) {
				keyTyped(evt);
			}
		});
		danceList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				if (evt.getClickCount() == 2 || evt.getClickCount() == 3) {
					addDance();
				}
			}
		});
		leftButton.addActionListener((evt) -> {
			final int selection = selectedList.getSelectedIndex();
			if (selection >= 0) {
				program.remove(selection);
			}
		});
		JPanel rightPanel =
				BorderedPanel.verticalLine(null, new JScrollPane(selectedList),
						new ListenedButton("Add Break", (ignored) -> {
							final int selection = selectedList.getSelectedIndex();
							if (selection >= 0) {
								program.add(selection, new Intermission());
							} else {
								program.add(new Intermission());
							}
						}));
		final JSplitPane secondSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, inner, rightPanel);
		setLeftComponent(left);
		setRightComponent(secondSplitPane);
		secondSplitPane.setResizeWeight(0.0);
		setResizeWeight(0.5);
	}
}

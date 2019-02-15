package lovelace.tartan.gui;

import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import lovelace.tartan.gui.controls.BorderedPanel;
import lovelace.tartan.gui.controls.ListenedButton;
import lovelace.tartan.model.Dance;
import lovelace.tartan.model.Intermission;
import lovelace.tartan.model.ProgramElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A panel to let the user edit the details of (metadata about) a dance.
 *
 * @author Jonathan Lovelace
 */
public class DanceDetailsPanel extends JPanel {
	private final JLabel titleLabel = new JLabel("Dance Title:");
	private final JTextField titleField = new JTextField();
	private final JTextField sourceField = new JTextField();
	private final JComboBox<String> tempoBox =
			new JComboBox<>( // TODO: Take tempos from database
					new String[]{"Jig", "Reel", "Strathspey", "Medley", "Waltz",
							"Hornpipe",
							"Step", "Quadrille", "March", "Polka", "Unknown", "Other"});
	private final SpinnerNumberModel timesModel = new SpinnerNumberModel(1, 1, 20, 1);
	private final JSpinner timesBox = new JSpinner(timesModel);
	private final SpinnerNumberModel barsModel = new SpinnerNumberModel(0, 0, 256, 4);
	private final JSpinner barsBox = new JSpinner(barsModel);
	private final JComboBox<String> formationBox = new JComboBox<>(
			new String[]{"2C (3C set)", "2C (4C set)", "3C (4C set)", "3C set",
					"3C Triangle", "4C set", "Sq Set", "5C Set"});

	private void applyToDance(final Dance dance) {
		dance.setTitle(titleField.getText().trim());
		dance.setSource(sourceField.getText().trim());
		dance.setTempo(Objects.toString(tempoBox.getSelectedItem(), "none")); // Avoiding casting
		dance.setTimes(timesModel.getNumber().intValue());
		dance.setLength(barsModel.getNumber().intValue());
		dance.setFormation(Objects.toString(formationBox.getSelectedItem(), "none"));
	}

	private void applyToIntermission(final Intermission intermission) {
		intermission.setDescription(titleField.getText().trim());
	}

	private void noop() {
	}

	@NotNull
	private Runnable apply = this::noop;

	private void revertDance(final Dance dance) {
		titleLabel.setText("Dance Title:");
		titleField.setText(dance.getTitle());
		sourceField.setText(dance.getSource());
		tempoBox.setSelectedItem(dance.getTempo());
		timesModel.setValue(dance.getTimes());
		barsModel.setValue(dance.getLength());
		formationBox.setSelectedItem(dance.getFormation());
	}

	private void revertIntermission(final Intermission intermission) {
		titleLabel.setText("Break Description:");
		titleField.setText(intermission.getDescription());
		sourceField.setText("");
		tempoBox.setSelectedItem("");
		timesModel.setValue(1);
		barsModel.setValue(0);
		formationBox.setSelectedItem("");
	}

	@NotNull
	private Runnable revert = this::noop;

	private final JButton applyButton =
			new ListenedButton("Apply", (ignored) -> apply.run());
	private final JButton revertButton =
			new ListenedButton("Revert", (ignored) -> revert.run());

	public DanceDetailsPanel() {
		super(new BorderLayout());
		final JPanel topPanel = BorderedPanel.verticalLine(
				BorderedPanel.horizontalLine(titleLabel, titleField, null), null,
				BorderedPanel.horizontalLine(new JLabel("Source/Deviser:"), sourceField,
						null));
		tempoBox.setEditable(false);
		formationBox.setEditable(false);
		final JPanel bottomPanel = BorderedPanel.verticalLine(
				BorderedPanel.horizontalLine(
						BorderedPanel.horizontalLine(new JLabel("Tempo:"), null,
								tempoBox), null,
						BorderedPanel.horizontalLine(new JLabel("Times Through:"), null,
								timesBox)),
				BorderedPanel.horizontalLine(
						BorderedPanel.horizontalLine(new JLabel("Bars Long:"), null,
								barsBox), null,
						BorderedPanel.horizontalLine(new JLabel("Formation:"), null,
								formationBox)),
				BorderedPanel.horizontalLine(applyButton, null, revertButton));
		add(topPanel, BorderLayout.PAGE_START);
		add(bottomPanel, BorderLayout.PAGE_END);
	}

	@Nullable
	private ProgramElement current = null;

	@Nullable
	public ProgramElement getCurrent() {
		return current;
	}

	public void setCurrent(@Nullable final ProgramElement current) {
		this.current = current;
		if (current == null) {
			titleField.setEnabled(false);
			sourceField.setEnabled(false);
			tempoBox.setEnabled(false);
			timesBox.setEnabled(false);
			barsBox.setEnabled(false);
			formationBox.setEnabled(false);
			applyButton.setEnabled(false);
			revertButton.setEnabled(false);
			apply = this::noop;
			revert = this::noop;
		} else if (current instanceof Dance) {
			titleField.setEnabled(true);
			sourceField.setEnabled(true);
			tempoBox.setEnabled(true);
			timesBox.setEnabled(true);
			barsBox.setEnabled(true);
			formationBox.setEnabled(true);
			applyButton.setEnabled(true);
			revertButton.setEnabled(true);
			apply = () -> applyToDance((Dance) current);
			revert = () -> revertDance((Dance) current);
		} else if (current instanceof Intermission) {
			titleField.setEnabled(true);
			sourceField.setEnabled(false);
			tempoBox.setEnabled(false);
			timesBox.setEnabled(false);
			barsBox.setEnabled(false);
			formationBox.setEnabled(false);
			applyButton.setEnabled(true);
			revertButton.setEnabled(true);
			apply = () -> applyToIntermission((Intermission) current);
			revert = () -> revertIntermission((Intermission) current);
		} else {
			throw new IllegalArgumentException(
					"Dance details panel can only show details of dance or intermission");
		}
		revert.run();
	}
}

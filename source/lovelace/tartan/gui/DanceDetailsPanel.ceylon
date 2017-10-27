import javax.swing {
	JPanel,
	JLabel,
	JTextField,
	JComboBox,
	JSpinner,
	SpinnerNumberModel
}
import lovelace.tartan.model {
	ProgramElement,
	AuldLangSyne,
	Intermission,
	Dance
}
import java.awt {
	BorderLayout
}
import java.lang {
	IllegalArgumentException,
	JInteger=Integer,
	Types
}
import lovelace.tartan.gui.controls {
	ListenedButton,
	BorderedPanel
}
class DanceDetailsPanel() extends JPanel(BorderLayout()) {
	JLabel titleLabel = JLabel("Dance Title:");
	JTextField titleField = JTextField();
	JTextField sourceField = JTextField();
	JPanel topPanel = BorderedPanel.verticalLine(BorderedPanel.horizontalLine(titleLabel, titleField, null),
		null, BorderedPanel.horizontalLine(JLabel("Source/Deviser:"), sourceField, null));
	JComboBox<String> tempoBox = JComboBox(Array<String?> { "Jig", "Reel", "Strathspey",
		"Medley", "Waltz", "Hornpipe", "Step", "Quadrille", "March", "Polka", "Unknown", "Other" });
	tempoBox.editable = false;
	SpinnerNumberModel timesModel = SpinnerNumberModel(1, 1, 20, 1);
	JSpinner timesBox = JSpinner(timesModel);
	SpinnerNumberModel barsModel = SpinnerNumberModel(0, 0, 256, 4);
	JSpinner barsBox = JSpinner(barsModel);
	JComboBox<String> formationBox = JComboBox(Array<String?> { "2C (3C set)", "2C (4C set)", "3C (4C set)",
		"3C set", "3C Triangle", "4C set", "Sq Set" });
	formationBox.editable = true;
	void applyToDance(Dance dance) {
		dance.title = titleField.text.trimmed;
		dance.source = sourceField.text.trimmed;
		// A JComboBox's selectedItem will be either a Ceylon String or a Java String, but
		// in my experience which one isn't reliable, so we'll just use the 'string' attribute.
		dance.tempo = tempoBox.selectedItem.string;
		dance.times = timesModel.number.intValue();
		dance.length = barsModel.number.intValue();
		dance.formation = formationBox.selectedItem.string;
	}
	void applyToIntermission(Intermission interm) => interm.description = titleField.text.trimmed;
	void applyToAuldLangSyne(AuldLangSyne als) => als.description = titleField.text.trimmed;
	variable Anything() apply = noop;
	void revertDance(Dance dance) {
		titleLabel.text = "Dance Title:";
		titleField.text = dance.title;
		sourceField.text = dance.source;
		tempoBox.selectedItem = dance.tempo;
		// We tried to call the setValue(int) method, but the compiler always called the setValue(Object) method,
		// resulting in exceptions when a Ceylon Integer was passed. So we have to get the Java Integer-class
		// equivalent.
		try {
			timesModel.\ivalue = JInteger.valueOf(dance.times);
		} catch (IllegalArgumentException except) {
			log.error("Illegal value for times: ``dance.times``");
		}
		try {
			barsModel.\ivalue = JInteger.valueOf(dance.length);
		} catch (IllegalArgumentException except) {
			log.error("Illegal value for length: ``dance.length``");
		}
		formationBox.selectedItem = dance.formation;
	}
	void revertIntermission(Intermission interm) {
		titleLabel.text = "Break Description:";
		titleField.text = interm.description;
		sourceField.text = "";
		tempoBox.selectedItem = "";
		timesModel.\ivalue = JInteger.valueOf(1);
		barsModel.\ivalue = JInteger.valueOf(0);
		formationBox.selectedItem = "";
	}
	void revertAuldLangSyne(AuldLangSyne als) {
		titleLabel.text = "ALS Description:";
		titleField.text = als.description;
		sourceField.text = "";
		tempoBox.selectedItem = "";
		timesModel.\ivalue = JInteger.valueOf(1);
		barsModel.\ivalue = JInteger.valueOf(0);
		formationBox.selectedItem = "";
	}
	variable Anything() revert = noop;
	value applyButton = ListenedButton("Apply", (_) => apply());
	value revertButton = ListenedButton("Revert", (_) => revert());

	JPanel bottomPanel = BorderedPanel.verticalLine(
		BorderedPanel.horizontalLine(BorderedPanel.horizontalLine(JLabel("Tempo:"), null, tempoBox), null,
			BorderedPanel.horizontalLine(JLabel("Times Through:"), null, timesBox)),
		BorderedPanel.horizontalLine(BorderedPanel.horizontalLine(JLabel("Bars Long:"), null, barsBox), null,
			BorderedPanel.horizontalLine(JLabel("Formation:"), null, formationBox)),
		BorderedPanel.horizontalLine(applyButton, null, revertButton));

	add(topPanel, Types.nativeString(BorderLayout.pageStart));
	add(bottomPanel, Types.nativeString(BorderLayout.pageEnd));

	variable ProgramElement? _current = null;
	shared ProgramElement? current => _current;
	assign current {
		_current = current;
		switch (current)
		case (null) {
			titleField.enabled = false;
			sourceField.enabled = false;
			tempoBox.enabled = false;
			timesBox.enabled = false;
			barsBox.enabled = false;
			formationBox.enabled = false;
			applyButton.enabled = false;
			revertButton.enabled = false;
			apply = noop;
			revert = noop;
		}
		case (is Dance) {
			titleField.enabled = true;
			sourceField.enabled = true;
			tempoBox.enabled = true;
			timesBox.enabled = true;
			barsBox.enabled = true;
			formationBox.enabled = true;
			applyButton.enabled = true;
			revertButton.enabled = true;
			apply = () => applyToDance(current);
			revert = () => revertDance(current);
			revert();
		}
		case (is Intermission) {
			titleField.enabled = true;
			sourceField.enabled = false;
			tempoBox.enabled = false;
			timesBox.enabled = false;
			barsBox.enabled = false;
			formationBox.enabled = false;
			applyButton.enabled = true;
			revertButton.enabled = true;
			apply = () => applyToIntermission(current);
			revert = () => revertIntermission(current);
			revert();
		}
		case (is AuldLangSyne) {
			titleField.enabled = true;
			sourceField.enabled = false;
			tempoBox.enabled = false;
			timesBox.enabled = false;
			barsBox.enabled = false;
			formationBox.enabled = false;
			applyButton.enabled = true;
			revertButton.enabled = true;
			apply = () => applyToAuldLangSyne(current);
			revert = () => revertAuldLangSyne(current);
			revert();
		}
	}
}
import lovelace.tartan.model {
	ProgramMetadata
}
import javax.swing {
	JPanel,
	JTextField,
	JLabel,
	GroupLayout,
	JTextArea,
	JSeparator,
	JFileChooser,
	JCheckBox,
	JComponent,
	JButton,
	JScrollPane
}
import java.awt {
	Component
}
import lovelace.tartan.gui.controls {
	ListenedButton
}
import javax.swing.filechooser {
	FileFilter
}
import java.io {
	JFile=File
}
import javax.swing.event {
	DocumentListener,
	DocumentEvent
}
import javax.swing.text {
	JTextComponent
}
interface IMetadataConsumer {
	shared formal void revert();
}
object imageFilter extends FileFilter() {
	shared actual Boolean accept(JFile file) {
		String name = file.name.lowercased;
		return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".pdf");
	}
	shared actual String description => "LaTeX-supported images";
}
// TODO: move to lovelace.tartan.gui.controls? perhaps make more generic?
class ImageFileChooser(Anything(String?) handler, Component? parent = null) {
	variable String? _filename = null;
	shared String? filename => _filename;
	value chosenFileField = JTextField(10);
	chosenFileField.editable = false;
	shared JComponent field => chosenFileField;
	// TODO: Use AWT FileChooser on Mac
	JFileChooser chooser = JFileChooser();
	assign filename {
		_filename = filename;
		if (exists filename) {
			chooser.selectedFile = JFile(filename);
			chosenFileField.text = filename;
		} else {
			chooser.selectedFile = null;
			chosenFileField.text = "";
		}
	}
	chooser.fileFilter = imageFilter;
	JButton _button = ListenedButton("Choose File", (_) {
		chooser.showOpenDialog(parent);
		if (exists file = chooser.selectedFile) {
			_filename = file.path;
			chosenFileField.text = file.path;
			handler(file.path);
		} else {
			_filename = null;
			chosenFileField.text = "";
			handler(null);
		}
	});
	shared JComponent button => _button;
}
class TextBoxChangeListener(JTextComponent field, Anything(String) assignee) satisfies DocumentListener {
	shared actual void changedUpdate(DocumentEvent event) => assignee(field.text);
	shared actual void insertUpdate(DocumentEvent event) => assignee(field.text);
	shared actual void removeUpdate(DocumentEvent event) => assignee(field.text);
}
JPanel&IMetadataConsumer metadataEditingPanel(ProgramMetadata metadata) {
	void setupTextFieldListener(JTextComponent field, Anything(String) handler) {
			field.document.addDocumentListener(TextBoxChangeListener(field, handler));
	}
	variable Anything() placeholder = noop;
	object retval extends JPanel() satisfies IMetadataConsumer {
		shared actual void revert() => placeholder();
	}
	value layout = GroupLayout(retval);
	// TODO: Once we add lovelace.util as a dependency, use its version of this trick
	GroupLayout.Group createParallelGroup(GroupLayout.Alignment alignment, <Component|GroupLayout.Group>* members) {
		variable GroupLayout.Group retval = layout.createParallelGroup(alignment);
		for (member in members) {
			switch (member)
			case (is Component) {
				retval = retval.addComponent(member);
			}
			case (is GroupLayout.Group) {
				retval = retval.addGroup(member);
			}
		}
		return retval;
	}
	GroupLayout.Group createSequentialGroup(<Component|GroupLayout.Group>* members) {
		variable GroupLayout.Group retval = layout.createSequentialGroup();
		for (member in members) {
			switch (member)
			case (is Component) {
				retval = retval.addComponent(member);
			}
			case (is GroupLayout.Group) {
				retval = retval.addGroup(member);
			}
		}
		return retval;
	}
	retval.layout = layout;
	value coverHeaderLabel = JLabel("On Cover:");
	value titleHeaderLabel = JLabel("On Title Page:");
	value groupNameLabel = JLabel("Host (Group) Name:");
	value groupNameCoverBox = JTextField(15);
	setupTextFieldListener(groupNameCoverBox, (str) => metadata.groupCoverName = str);
	value groupNameTitleBox = JTextField(15);
	setupTextFieldListener(groupNameTitleBox, (str) => metadata.groupTitleName = str);
	value eventNameLabel = JLabel("Event Name:");
	value eventNameCoverBox = JTextField(15);
	setupTextFieldListener(eventNameCoverBox, (str) => metadata.eventCoverName = str);
	value eventNameTitleBox = JTextField(15);
	setupTextFieldListener(eventNameTitleBox, (str) => metadata.eventTitleName = str);
	value dateLabel = JLabel("Event Date:");
	value dateCoverBox = JTextField(15);
	setupTextFieldListener(dateCoverBox, (str) => metadata.coverDate = str);
	value dateTitleBox = JTextField(15);
	setupTextFieldListener(dateTitleBox, (str) => metadata.titleDate = str);
	value locationLabel = JLabel("Event Location:");
	value locationCoverBox = JTextField(15);
	setupTextFieldListener(locationCoverBox, (str) => metadata.coverLocation = str);
	value locationTitleBox = JTextField(15);
	setupTextFieldListener(locationTitleBox, (str) => metadata.titleLocation = str);
	value locationAddressLabel = JLabel("Location Address:");
	value locationAddressBox = JTextField(15);
	setupTextFieldListener(locationAddressBox, (str) => metadata.locationAddress = str);
	value timesLabel = JLabel("Event time(s):");
	value timesArea = JTextArea(3, 15);
	setupTextFieldListener(timesArea, (str) => metadata.titleTimes = str);
	JScrollPane timesAreaWrapped = JScrollPane(timesArea);
	value musiciansLabel = JLabel("Musicians:");
	value musiciansBox = JTextField(15);
	setupTextFieldListener(musiciansBox, (str) => metadata.musicians = str);
	value firstSeparator = JSeparator();
	value coverImageLabel = JLabel("Cover image:");
	value coverImageSelector = ImageFileChooser((str) => metadata.coverImage = str, retval);
	value backImageLabel = JLabel("Back cover image:");
	value backImageSelector = ImageFileChooser((str) => metadata.backCoverImage = str, retval);
	value titleOnCoverLabel = JLabel("Print title page on back of cover?");
	value titleOnCoverField = JCheckBox();
	titleOnCoverField.addChangeListener((_) => metadata.titleOnCover = titleOnCoverField.selected);
	value auldLangSyneLabel = JLabel("Print Auld Lang Syne inside back cover?");
	value auldLangSyneField = JCheckBox();
	auldLangSyneField.addChangeListener((_) => metadata.printAuldLangSyne = auldLangSyneField.selected);
	void reset() {
		groupNameCoverBox.text = metadata.groupCoverName;
		groupNameTitleBox.text = metadata.groupTitleName;
		eventNameCoverBox.text = metadata.eventCoverName;
		eventNameTitleBox.text = metadata.eventTitleName;
		dateCoverBox.text = metadata.coverDate;
		dateTitleBox.text = metadata.titleDate;
		locationCoverBox.text = metadata.coverLocation;
		locationTitleBox.text = metadata.titleLocation;
		locationAddressBox.text = metadata.locationAddress;
		timesArea.text = metadata.titleTimes;
		musiciansBox.text = metadata.musicians;
		coverImageSelector.filename = metadata.coverImage;
		backImageSelector.filename = metadata.backCoverImage;
		titleOnCoverField.selected = metadata.titleOnCover;
		auldLangSyneField.selected = metadata.printAuldLangSyne;
	}
	reset();
	placeholder = reset;
	// TODO: images between last dance and Auld Lang Syne or the back cover
	layout.autoCreateGaps = true;
	layout.autoCreateContainerGaps = true;
	layout.setVerticalGroup(createSequentialGroup(
		createParallelGroup(GroupLayout.Alignment.baseline, coverHeaderLabel, titleHeaderLabel),
		createParallelGroup(GroupLayout.Alignment.baseline, groupNameLabel, groupNameCoverBox, groupNameTitleBox),
		createParallelGroup(GroupLayout.Alignment.baseline, eventNameLabel, eventNameCoverBox, eventNameTitleBox),
		createParallelGroup(GroupLayout.Alignment.baseline, dateLabel, dateCoverBox, dateTitleBox),
		createParallelGroup(GroupLayout.Alignment.baseline, locationLabel, locationCoverBox, locationTitleBox),
		createParallelGroup(GroupLayout.Alignment.baseline, locationAddressLabel, locationAddressBox),
		createParallelGroup(GroupLayout.Alignment.baseline, timesLabel, timesAreaWrapped),
		createParallelGroup(GroupLayout.Alignment.baseline, musiciansLabel, musiciansBox), firstSeparator,
		createParallelGroup(GroupLayout.Alignment.baseline, coverImageLabel, coverImageSelector.field,
			coverImageSelector.button),
		createParallelGroup(GroupLayout.Alignment.baseline, backImageLabel, backImageSelector.field,
			backImageSelector.button),
		createParallelGroup(GroupLayout.Alignment.center, titleOnCoverLabel, titleOnCoverField),
		createParallelGroup(GroupLayout.Alignment.center, auldLangSyneLabel, auldLangSyneField)));
	layout.setHorizontalGroup(createParallelGroup(GroupLayout.Alignment.leading, firstSeparator,
		createSequentialGroup(
			createParallelGroup(GroupLayout.Alignment.leading, groupNameLabel, eventNameLabel, dateLabel,
				locationLabel, locationAddressLabel, timesLabel, musiciansLabel, coverImageLabel,
				backImageLabel, titleOnCoverLabel, auldLangSyneLabel),
			createParallelGroup(GroupLayout.Alignment.leading, coverHeaderLabel, groupNameCoverBox,
				eventNameCoverBox, dateCoverBox, locationCoverBox,
				createSequentialGroup(coverImageSelector.field, coverImageSelector.button),
				createSequentialGroup(backImageSelector.field, backImageSelector.button),
				titleOnCoverField, auldLangSyneField),
			createParallelGroup(GroupLayout.Alignment.leading, titleHeaderLabel, groupNameTitleBox,
				eventNameTitleBox, dateTitleBox, locationTitleBox, locationAddressBox, timesAreaWrapped,
				musiciansBox))));
	return retval;
}
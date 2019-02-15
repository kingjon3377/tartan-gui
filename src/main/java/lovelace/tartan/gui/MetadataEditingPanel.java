package lovelace.tartan.gui;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import lovelace.tartan.gui.controls.ListenedButton;
import lovelace.tartan.gui.controls.PlatformFileDialog;
import lovelace.tartan.gui.model.ReorderableListModel;
import lovelace.tartan.model.ProgramMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A panel to let the user edit the parts of the program document other than the dances.
 *
 * @author Jonathan Lovelace
 */
public final class MetadataEditingPanel extends JPanel {
	// TODO: Make a JTextBox subclass taking both a Consumer<String> and a
	//  Supplier<String>, and has revert() as an instance method
	private static final class TextBoxChangeListener implements DocumentListener {
		private final @NotNull Consumer<String> assignee;
		private final @NotNull JTextComponent field;

		public TextBoxChangeListener(@NotNull final JTextComponent field,
									 @NotNull final Consumer<String> assignee) {
			this.assignee = assignee;
			this.field = field;
		}

		@Override
		public void insertUpdate(final DocumentEvent ignored) {
			assignee.accept(field.getText());
		}

		@Override
		public void removeUpdate(final DocumentEvent ignored) {
			assignee.accept(field.getText());
		}

		@Override
		public void changedUpdate(final DocumentEvent ignored) {
			assignee.accept(field.getText());
		}
	}

	private static void setupTextFieldListener(@NotNull final JTextComponent field,
											   @NotNull final Consumer<String> handler) {
		field.getDocument()
				.addDocumentListener(new TextBoxChangeListener(field, handler));
	}

	private final GroupLayout groupLayout;

	private GroupLayout.Group createParallelGroup(final GroupLayout.Alignment alignment,
												  final Object... members) {
		@NotNull GroupLayout.Group retval = groupLayout.createParallelGroup(alignment);
		for (Object member : members) {
			if (member instanceof Component) {
				retval = retval.addComponent((Component) member);
			} else if (member instanceof GroupLayout.Group) {
				retval = retval.addGroup((GroupLayout.Group) member);
			} else {
				throw new IllegalArgumentException(
						"Can only add a component or a group to a group");
			}
		}
		return retval;
	}

	private GroupLayout.Group createSequentialGroup(final Object... members) {
		@NotNull GroupLayout.Group retval = groupLayout.createSequentialGroup();
		for (Object member : members) {
			if (member instanceof Component) {
				retval = retval.addComponent((Component) member);
			} else if (member instanceof GroupLayout.Group) {
				retval = retval.addGroup((GroupLayout.Group) member);
			} else {
				throw new IllegalArgumentException(
						"Can only add a component or a group to a group");
			}
		}
		return retval;
	}

	private final JTextField groupNameCoverBox = new JTextField(15);
	private final JTextField groupNameTitleBox = new JTextField(15);
	private final JTextField eventNameCoverBox = new JTextField(15);
	private final JTextField eventNameTitleBox = new JTextField(15);
	private final JTextField dateCoverBox = new JTextField(15);
	private final JTextField dateTitleBox = new JTextField(15);
	private final JTextField locationCoverBox = new JTextField(15);
	private final JTextField locationTitleBox = new JTextField(15);
	private final JTextField locationAddressBox = new JTextField(15);
	private final JTextArea timesArea = new JTextArea(3, 15);
	private final JTextField musiciansBox = new JTextField(15);
	private final ImageFileChooser coverImageSelector;
	private final ImageFileChooser backImageSelector;
	private final JCheckBox titleOnCoverField = new JCheckBox();
	private final JCheckBox auldLangSyneField = new JCheckBox();
	private final @NotNull ProgramMetadata metadata;

	public void revert() {
		groupNameCoverBox.setText(metadata.getGroupCoverName());
		groupNameTitleBox.setText(metadata.getGroupTitleName());
		eventNameCoverBox.setText(metadata.getEventCoverName());
		eventNameTitleBox.setText(metadata.getEventTitleName());
		dateCoverBox.setText(metadata.getCoverDate());
		dateTitleBox.setText(metadata.getTitleDate());
		locationCoverBox.setText(metadata.getCoverLocation());
		locationTitleBox.setText(metadata.getTitleLocation());
		locationAddressBox.setText(metadata.getLocationAddress());
		timesArea.setText(metadata.getTitleTimes());
		musiciansBox.setText(metadata.getMusicians());
		coverImageSelector.setFilename(
				Optional.ofNullable(metadata.getCoverImage()).map(Path::toFile)
						.orElse(null));
		backImageSelector.setFilename(
				Optional.ofNullable(metadata.getBackCoverImage()).map(Path::toFile)
						.orElse(null));
		titleOnCoverField.setSelected(metadata.getTitleOnCover());
		auldLangSyneField.setSelected(metadata.getPrintAuldLangSyne());
	}

	public MetadataEditingPanel(@NotNull final ProgramMetadata metadata) {
		groupLayout = new GroupLayout(this);
		setLayout(groupLayout);
		this.metadata = metadata;
		final JLabel coverHeaderLabel = new JLabel("On Cover:");
		final JLabel titleHeaderLabel = new JLabel("On Title Page:");
		final JLabel groupNameLabel = new JLabel("Host (Group) Name:");
		setupTextFieldListener(groupNameCoverBox, metadata::setGroupCoverName);
		setupTextFieldListener(groupNameTitleBox, metadata::setGroupTitleName);
		final JLabel eventNameLabel = new JLabel("Event Name:");
		setupTextFieldListener(eventNameCoverBox, metadata::setEventCoverName);
		setupTextFieldListener(eventNameTitleBox, metadata::setEventTitleName);
		final JLabel dateLabel = new JLabel("Event Date:");
		setupTextFieldListener(dateCoverBox, metadata::setCoverDate);
		setupTextFieldListener(dateTitleBox, metadata::setTitleDate);
		final JLabel locationLabel = new JLabel("Event Location:");
		setupTextFieldListener(locationCoverBox, metadata::setCoverLocation);
		setupTextFieldListener(locationTitleBox, metadata::setTitleLocation);
		final JLabel locationAddressLabel = new JLabel("Location Address:");
		setupTextFieldListener(locationAddressBox, metadata::setLocationAddress);
		final JLabel timesLabel = new JLabel("Event Time(s):");
		timesArea.setMaximumSize(timesArea.getPreferredSize());
		setupTextFieldListener(timesArea, metadata::setTitleTimes);
		final JScrollPane timesAreaWrapped = new JScrollPane(timesArea);
		final JLabel musiciansLabel = new JLabel("Musicians:");
		setupTextFieldListener(musiciansBox, metadata::setMusicians);
		final JSeparator firstSeparator = new JSeparator();
		final JLabel coverImageLabel = new JLabel("Cover Image:");
		coverImageSelector = new ImageFileChooser(metadata::setCoverImage, this);
		final JLabel backImageLabel = new JLabel("Back Cover Image:");
		backImageSelector = new ImageFileChooser(metadata::setBackCoverImage, this);
		final JLabel fillerImageLabel = new JLabel("Images after last dance:");
		final ReorderableListModel<Path> fillerImageListModel =
				new ReorderableListModel<>(metadata.getInsidePostDanceImages());
		final JList<Path> fillerImageList =
				new JList<>(fillerImageListModel); // TODO: Need a renderer
		final JScrollPane fillerImageListWrapped = new JScrollPane(fillerImageList);
		final PlatformFileDialog fillerChooser = new PlatformFileDialog(null);
		fillerChooser.setFileFilter(ImageFileChooser.IMAGE_FILTER);
		final JButton fillerImageAdd = new ListenedButton("Add Image",
				(ignored) -> { // TODO: convert from lambda to class method?
					fillerChooser.showOpenDialog();
					@Nullable final File file = fillerChooser.getFilename();
					if (file != null) {
						fillerImageListModel.add(file.toPath());
					}
				});
		final JButton fillerImageRemove = new ListenedButton("Remove Image",
				(ignored) -> { // TODO: convert from lambda to class method?
					int index = fillerImageList.getSelectedIndex();
					if (index >= 0) {
						fillerImageListModel.remove(index);
					}
				});
		final JLabel titleOnCoverLabel =
				new JLabel("Print title page on back of cover?");
		titleOnCoverField.addChangeListener(
				(ignored) -> metadata.setTitleOnCover(titleOnCoverField.isSelected()));
		final JLabel auldLangSyneLabel =
				new JLabel("Print Auld Lang Syne inside back cover?");
		auldLangSyneField.addChangeListener((ignored) -> metadata.setPrintAuldLangSyne(
				auldLangSyneField.isSelected()));
		revert();
		// TODO: images between last dance and Auld Lang Syne or the back cover
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setVerticalGroup(
				createSequentialGroup(
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								coverHeaderLabel, titleHeaderLabel),
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								groupNameLabel, groupNameCoverBox, groupNameTitleBox),
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								eventNameLabel, eventNameCoverBox, eventNameTitleBox),
						createParallelGroup(GroupLayout.Alignment.BASELINE, dateLabel,
								dateCoverBox, dateTitleBox),
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								locationLabel,
								locationCoverBox, locationTitleBox),
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								locationAddressLabel, locationAddressBox),
						createParallelGroup(GroupLayout.Alignment.BASELINE, timesLabel,
								timesAreaWrapped),
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								musiciansLabel, musiciansBox),
						firstSeparator,
						fillerImageLabel,
						createParallelGroup(GroupLayout.Alignment.LEADING,
								createSequentialGroup(
										createParallelGroup(
												GroupLayout.Alignment.BASELINE,
												coverImageLabel,
												coverImageSelector.getField(),
												coverImageSelector.getButton()),
										createParallelGroup(
												GroupLayout.Alignment.BASELINE,
												backImageLabel,
												backImageSelector.getField(),
												backImageSelector.getButton()),
										createParallelGroup(GroupLayout.Alignment.CENTER,
												titleOnCoverLabel, titleOnCoverField),
										createParallelGroup(GroupLayout.Alignment.CENTER,
												auldLangSyneLabel, auldLangSyneField)),
								fillerImageListWrapped),
						createParallelGroup(GroupLayout.Alignment.BASELINE,
								fillerImageAdd, fillerImageRemove)));
		groupLayout.setHorizontalGroup(
				createParallelGroup(GroupLayout.Alignment.LEADING, firstSeparator,
						createSequentialGroup(
								createParallelGroup(GroupLayout.Alignment.LEADING,
										groupNameLabel, eventNameLabel, dateLabel,
										locationLabel, locationAddressLabel, timesLabel,
										musiciansLabel, coverImageLabel,
										backImageLabel, titleOnCoverLabel,
										auldLangSyneLabel),
								createParallelGroup(GroupLayout.Alignment.LEADING,
										coverHeaderLabel, groupNameCoverBox,
										eventNameCoverBox, dateCoverBox,
										locationCoverBox,
										createSequentialGroup(
												coverImageSelector.getField(),
												coverImageSelector.getButton()),
										createSequentialGroup(
												backImageSelector.getField(),
												backImageSelector.getButton()),
										titleOnCoverField, auldLangSyneField),
								createParallelGroup(GroupLayout.Alignment.LEADING,
										titleHeaderLabel, groupNameTitleBox,
										eventNameTitleBox, dateTitleBox,
										locationTitleBox,
										locationAddressBox, timesAreaWrapped,
										musiciansBox, fillerImageLabel,
										fillerImageListWrapped,
										createSequentialGroup(fillerImageAdd,
												fillerImageRemove)))));
	}
}

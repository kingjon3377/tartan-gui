package lovelace.tartan.gui;

import java.awt.GridLayout;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;

import lovelace.tartan.gui.controls.BorderedPanel;
import lovelace.tartan.gui.controls.ListenedButton;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.NamedFigureMember;
import lovelace.tartan.model.SimplestMember;

/// A class to let the user edit a named figure.
///
/// TODO: Need a way to allow drag-and-drop within a named figure.
///
/// @author Jonathan Lovelace
public final class NamedFigureEditor extends JPanel {
	private static final Logger LOGGER =
			Logger.getLogger(NamedFigureEditor.class.getName());

	public NamedFigureEditor(final NamedFigure namedFigure,
	                         final Runnable stopOperation) {
		super(new GridLayout(0, 1));
		for (final NamedFigureMember member : namedFigure.getContents()) {
			switch (member) {
				case final Figure figure -> add(new FigureEditor(figure));
				case final SimplestMember simplestMember -> add(
						new DanceStringEditor(simplestMember.getString(), (str) -> {
							if (str.isEmpty()) {
								// TODO: Remove this editor as well, & in non-editing view
								namedFigure.getContents().remove(member);
							} else {
								simplestMember.setString(str);
								// TODO: Make sure the non-editing view is updated?
							}
						}));
				default -> LOGGER.info("Unexpected NamedFigureMember implementation");
			}
		}
		final JButton addButton = new JButton("Add movement");
		final JPanel buttonPanel = BorderedPanel.horizontalLine(addButton, null,
				new ListenedButton("Done Editing", (ignored) -> stopOperation.run()));
		addButton.addActionListener(
				(ignored) -> createNewFigure(namedFigure, buttonPanel));
		add(buttonPanel);
	}

	private void createNewFigure(final NamedFigure namedFigure,
	                             final JPanel buttonPanel) {
		final Figure newFigure = new Figure("description of movement", "bars");
		namedFigure.getContents().add(newFigure);
		remove(buttonPanel);
		add(new FigureEditor(newFigure));
		add(buttonPanel);
	}
}

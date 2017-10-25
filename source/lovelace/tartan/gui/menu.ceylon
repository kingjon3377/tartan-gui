import javax.swing {
	JMenu,
	JMenuItem,
	KeyStroke,
	InputMap,
	JComponent
}
import lovelace.tartan.model {
	ProgramElement
}
import lovelace.tartan.gui.model {
	MutableListModel
}
import java.awt.event {
	InputEvent,
	KeyEvent
}
JMenuItem menuItem(String text, Integer mnemonic, String description,
		Anything() handler, KeyStroke* accelerators) {
	JMenuItem retval = JMenuItem(text, mnemonic);
	if (exists accelerator = accelerators.first) {
		retval.accelerator = accelerator;
	}
	retval.accessibleContext.accessibleDescription = description;
	retval.addActionListener((_) => handler());
	InputMap inputMap = retval.getInputMap(JComponent.whenInFocusedWindow);
	for (accelerator in accelerators) {
		inputMap.put(accelerator, retval.action);
	}
	return retval;
}
JMenu fileMenu(MutableListModel<ProgramElement> program) {
	Boolean onMac = operatingSystem.name == "mac";
	Integer shortcutMask;
	if (onMac) {
		shortcutMask = InputEvent.metaDownMask;
	} else {
		shortcutMask = InputEvent.ctrlDownMask;
	}
	JMenu retval = JMenu("File");
	retval.add(menuItem("Open", KeyEvent.vkO, "Open an existing program for further editing",
		() => nothing, KeyStroke.getKeyStroke(KeyEvent.vkO, shortcutMask)));
	retval.add(menuItem("Save", KeyEvent.vkS, "Save the program to file",
		() => nothing, KeyStroke.getKeyStroke(KeyEvent.vkS, shortcutMask)));
	retval.add(menuItem("Save As", KeyEvent.vkA, "Save the program to a new file",
		() => nothing, KeyStroke.getKeyStroke(KeyEvent.vkS, shortcutMask.or(InputEvent.shiftDownMask))));
	if (onMac) {
		// TODO: Wire the "quit" code up to the Mac quit-handling system
	} else {
		retval.addSeparator();
		retval.add(menuItem("Exit", KeyEvent.vkX, "Quit the app",
			() => nothing, KeyStroke.getKeyStroke(KeyEvent.vkQ, shortcutMask)));
	}
	return retval;
}
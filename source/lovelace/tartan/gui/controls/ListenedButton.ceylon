import java.awt.event {
	ActionEvent
}
import javax.swing {
	JButton
}
shared class ListenedButton(
		"The text of the button"
		String text,
		"What to do when the button is pressed"
		Anything(ActionEvent) action) extends JButton(text) {
	addActionListener(action);
}
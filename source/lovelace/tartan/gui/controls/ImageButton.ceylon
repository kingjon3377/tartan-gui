import java.awt {
	Graphics,
	Image,
	Dimension
}
import javax.swing {
	JButton
}
shared class ImageButton(Image image) extends JButton() {
	shared actual void paintComponent(Graphics pen) => pen.drawImage(image, 0, 0, width, height, null);
	maximumSize = Dimension(60, 60);
	preferredSize = Dimension(40, 40);
	minimumSize = Dimension(20, 20);
}
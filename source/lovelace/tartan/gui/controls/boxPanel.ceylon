import javax.swing {
	JComponent,
	JPanel,
	BoxLayout,
	Box
}
import java.awt {
	Component
}
"An interface for our marker objects for glue and struts."
shared interface BoxElement of boxGlue | BoxStrut {}
shared object boxGlue satisfies BoxElement {}
shared class BoxStrut(shared Integer size) satisfies BoxElement {}
shared class BoxDirection of pageAxis | lineAxis {
	shared Integer constant;
	shared Component glue();
	shared Component strut(Integer size);
	shared new pageAxis {
		constant = BoxLayout.pageAxis;
		glue = Box.createVerticalGlue;
		strut = Box.createVerticalStrut;
	}
	shared new lineAxis {
		constant = BoxLayout.lineAxis;
		glue = Box.createHorizontalGlue;
		strut = Box.createHorizontalStrut;
	}
}
shared JPanel boxPanel(BoxDirection direction, <JComponent|BoxElement>* contents) {
	value retval = JPanel();
	retval.layout = BoxLayout(retval, direction.constant);
	for (item in contents) {
		switch (item)
		case (boxGlue) {
			retval.add(direction.glue());
		}
		case (is BoxStrut) {
			retval.add(direction.strut(item.size));
		}
		case (is JComponent) {
			retval.add(item);
		}
	}
	return retval;
}
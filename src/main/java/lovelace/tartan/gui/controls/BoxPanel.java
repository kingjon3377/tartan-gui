package lovelace.tartan.gui.controls;

import java.awt.Component;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

/**
 * A panel laid out with {@link javax.swing.BoxLayout} adding as much convenience for
 * callers as possible.
 *
 * @author Jonathan Lovelace
 */
public class BoxPanel extends JPanel {
	public static final BoxGlue GLUE = new BoxGlue();

	public enum BoxDirection {
		PageAxis(BoxLayout.PAGE_AXIS, Box::createVerticalGlue, Box::createVerticalStrut),
		LineAxis(BoxLayout.LINE_AXIS, Box::createHorizontalGlue,
				Box::createHorizontalStrut);
		private final int constant;
		private final @NotNull Supplier<Component> glue;
		private final @NotNull IntFunction<Component> strut;

		public int getConstant() {
			return constant;
		}

		public Component createGlue() {
			return glue.get();
		}

		public Component createStrut(final int size) {
			return strut.apply(size);
		}

		BoxDirection(final int constant, final @NotNull Supplier<Component> glue,
					 final @NotNull IntFunction<Component> strut) {
			this.constant = constant;
			this.glue = glue;
			this.strut = strut;
		}
	}

	public static final class BoxGlue {
		protected BoxGlue() { // constructor provided for access specifier only
		}
	}

	public static final class BoxStrut {
		private final int size;

		public int getSize() {
			return size;
		}

		public BoxStrut(final int size) {
			this.size = size;
		}
	}

	public BoxPanel(final BoxDirection direction, final @NotNull Object... contents) {
		//noinspection MagicConstant
		setLayout(new BoxLayout(this, direction.getConstant()));
		for (final Object item : contents) {
			if (item instanceof BoxGlue) {
				add(direction.createGlue());
			} else if (item instanceof BoxStrut) {
				add(direction.createStrut(((BoxStrut) item).getSize()));
			} else if (item instanceof Component) {
				add((Component) item);
			} else {
				throw new IllegalArgumentException(
						"All BoxPanel contents must be Components, BoxGlue, or BoxStruts");
			}
		}
	}
}

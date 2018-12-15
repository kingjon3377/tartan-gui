package lovelace.tartan.gui.controls;

import java.awt.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

/**
 * A panel laid out with {@link javax.swing.BoxLayout} adding as much convenience for
 * callers as possible.
 *
 * @author Jonathan Lovelace
 */
public class BoxPanel extends JPanel {
	public enum BoxDirection {
		PageAxis(BoxLayout.PAGE_AXIS, Box::createVerticalGlue, Box::createVerticalStrut),
		LineAxis(BoxLayout.LINE_AXIS, Box::createHorizontalGlue,
				Box::createHorizontalStrut);
		private final int constant;
		@NotNull
		private final Supplier<Component> glue;
		@NotNull
		private final IntFunction<Component> strut;

		public int getConstant() {
			return constant;
		}

		public Component createGlue() {
			return glue.get();
		}

		public Component createStrut(final int size) {
			return strut.apply(size);
		}

		BoxDirection(final int constant, @NotNull final Supplier<Component> glue,
					 @NotNull final IntFunction<Component> strut) {
			this.constant = constant;
			this.glue = glue;
			this.strut = strut;
		}
	}

	public static final class BoxGlue {
		protected BoxGlue() {
		}
	}

	public static final BoxGlue GLUE = new BoxGlue();

	public static final class BoxStrut {
		private final int size;

		public int getSize() {
			return size;
		}

		public BoxStrut(final int size) {
			this.size = size;
		}
	}

	public BoxPanel(BoxDirection direction, @NotNull Object... contents) {
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

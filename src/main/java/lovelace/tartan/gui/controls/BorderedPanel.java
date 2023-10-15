package lovelace.tartan.gui.controls;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

/**
 * A panel laid out with {@link java.awt.BorderLayout}, made more convenient for callers.
 *
 * @author Jonathan Lovelace
 */
public class BorderedPanel extends JPanel {
	private @Nullable Component center;
	private @Nullable Component pageStart;
	private @Nullable Component pageEnd;
	private @Nullable Component lineStart;
	private @Nullable Component lineEnd;

	public final @Nullable Component getCenter() {
		return center;
	}

	public final void setCenter(final @Nullable Component center) {
		if (Objects.nonNull(center)) {
			add(center, BorderLayout.CENTER);
		} else if (Objects.nonNull(this.center)) {
			remove(this.center);
		}
		this.center = center;
	}

	public final @Nullable Component getPageStart() {
		return pageStart;
	}

	public final void setPageStart(final @Nullable Component pageStart) {
		if (Objects.nonNull(pageStart)) {
			add(pageStart, BorderLayout.PAGE_START);
		} else if (Objects.nonNull(this.pageStart)) {
			remove(this.pageStart);
		}
		this.pageStart = pageStart;
	}

	public final @Nullable Component getPageEnd() {
		return pageEnd;
	}

	public final void setPageEnd(final @Nullable Component pageEnd) {
		if (Objects.nonNull(pageEnd)) {
			add(pageEnd, BorderLayout.PAGE_END);
		} else if (Objects.nonNull(this.pageEnd)) {
			remove(this.pageEnd);
		}
		this.pageEnd = pageEnd;
	}

	public final @Nullable Component getLineStart() {
		return lineStart;
	}

	public final void setLineStart(final @Nullable Component lineStart) {
		if (Objects.nonNull(lineStart)) {
			add(lineStart, BorderLayout.LINE_START);
		} else if (Objects.nonNull(this.lineStart)) {
			remove(this.lineStart);
		}
		this.lineStart = lineStart;
	}

	public final @Nullable Component getLineEnd() {
		return lineEnd;
	}

	public final void setLineEnd(final @Nullable Component lineEnd) {
		if (Objects.nonNull(lineEnd)) {
			add(lineEnd, BorderLayout.LINE_END);
		} else if (Objects.nonNull(this.lineEnd)) {
			remove(this.lineEnd);
		}
		this.lineEnd = lineEnd;
	}
	public BorderedPanel(final @Nullable Component center,
						 final @Nullable Component pageStart,
						 final @Nullable Component pageEnd,
						 final @Nullable Component lineStart,
						 final @Nullable Component lineEnd) {
		super(new BorderLayout());
		setCenter(center);
		setPageStart(pageStart);
		setPageEnd(pageEnd);
		setLineStart(lineStart);
		setLineEnd(lineEnd);
	}
	public static BorderedPanel horizontalLine(final @Nullable Component lineStart,
											   final @Nullable Component center,
											   final @Nullable Component lineEnd) {
		return new BorderedPanel(center, null, null, lineStart, lineEnd);
	}
	public static BorderedPanel verticalLine(final @Nullable Component pageStart,
											 final @Nullable Component center,
											 final @Nullable Component pageEnd) {
		return new BorderedPanel(center, pageStart, pageEnd, null, null);
	}
}

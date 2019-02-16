package lovelace.tartan.gui.controls;

import java.awt.BorderLayout;
import java.awt.Component;
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

	public @Nullable Component getCenter() {
		return center;
	}

	public void setCenter(final @Nullable Component center) {
		if (center != null) {
			add(center, BorderLayout.CENTER);
		} else if (this.center != null) {
			remove(this.center);
		}
		this.center = center;
	}

	public @Nullable Component getPageStart() {
		return pageStart;
	}

	public void setPageStart(final @Nullable Component pageStart) {
		if (pageStart != null) {
			add(pageStart, BorderLayout.PAGE_START);
		} else if (this.pageStart != null) {
			remove(this.pageStart);
		}
		this.pageStart = pageStart;
	}

	public @Nullable Component getPageEnd() {
		return pageEnd;
	}

	public void setPageEnd(final @Nullable Component pageEnd) {
		if (pageEnd != null) {
			add(pageEnd, BorderLayout.PAGE_END);
		} else if (this.pageEnd != null) {
			remove(this.pageEnd);
		}
		this.pageEnd = pageEnd;
	}

	public @Nullable Component getLineStart() {
		return lineStart;
	}

	public void setLineStart(final @Nullable Component lineStart) {
		if (lineStart != null) {
			add(lineStart, BorderLayout.LINE_START);
		} else if (this.lineStart != null) {
			remove(this.lineStart);
		}
		this.lineStart = lineStart;
	}

	public @Nullable Component getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(final @Nullable Component lineEnd) {
		if (lineEnd != null) {
			add(lineEnd, BorderLayout.LINE_END);
		} else if (this.lineEnd != null) {
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
	public static BorderedPanel horizontalLine(final @Nullable Component lineStart, final @Nullable Component center, final @Nullable Component lineEnd) {
		return new BorderedPanel(center, null, null, lineStart, lineEnd);
	}
	public static BorderedPanel verticalLine(final @Nullable Component pageStart, final @Nullable Component center, final @Nullable Component pageEnd) {
		return new BorderedPanel(center, pageStart, pageEnd, null, null);
	}
}

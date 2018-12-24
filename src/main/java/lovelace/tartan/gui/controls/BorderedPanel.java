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
	@Nullable private Component center;
	@Nullable private Component pageStart;
	@Nullable private Component pageEnd;
	@Nullable private Component lineStart;
	@Nullable private Component lineEnd;

	@Nullable
	public Component getCenter() {
		return center;
	}

	public void setCenter(@Nullable final Component center) {
		if (center != null) {
			add(center, BorderLayout.CENTER);
		} else if (this.center != null) {
			remove(this.center);
		}
		this.center = center;
	}

	@Nullable
	public Component getPageStart() {
		return pageStart;
	}

	public void setPageStart(@Nullable final Component pageStart) {
		if (pageStart != null) {
			add(pageStart, BorderLayout.PAGE_START);
		} else if (this.pageStart != null) {
			remove(this.pageStart);
		}
		this.pageStart = pageStart;
	}

	@Nullable
	public Component getPageEnd() {
		return pageEnd;
	}

	public void setPageEnd(@Nullable final Component pageEnd) {
		if (pageEnd != null) {
			add(pageEnd, BorderLayout.PAGE_END);
		} else if (this.pageEnd != null) {
			remove(this.pageEnd);
		}
		this.pageEnd = pageEnd;
	}

	@Nullable
	public Component getLineStart() {
		return lineStart;
	}

	public void setLineStart(@Nullable final Component lineStart) {
		if (lineStart != null) {
			add(lineStart, BorderLayout.LINE_START);
		} else if (this.lineStart != null) {
			remove(this.lineStart);
		}
		this.lineStart = lineStart;
	}

	@Nullable
	public Component getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(@Nullable final Component lineEnd) {
		if (lineEnd != null) {
			add(lineEnd, BorderLayout.LINE_END);
		} else if (this.lineEnd != null) {
			remove(this.lineEnd);
		}
		this.lineEnd = lineEnd;
	}
	public BorderedPanel(@Nullable final Component center, @Nullable final Component pageStart, @Nullable final Component pageEnd, @Nullable final Component lineStart, @Nullable final Component lineEnd) {
		super(new BorderLayout());
		setCenter(center);
		setPageStart(pageStart);
		setPageEnd(pageEnd);
		setLineStart(lineStart);
		setLineEnd(lineEnd);
	}
	public static BorderedPanel horizontalLine(@Nullable final Component lineStart, @Nullable final Component center, @Nullable final Component lineEnd) {
		return new BorderedPanel(center, null, null, lineStart, lineEnd);
	}
	public static BorderedPanel verticalLine(@Nullable final Component pageStart, @Nullable final Component center, @Nullable final Component pageEnd) {
		return new BorderedPanel(center, pageStart, pageEnd, null, null);
	}
}

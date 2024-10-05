package lovelace.tartan.gui.controls;

import java.awt.Component;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for a container laid out with {@link java.awt.BorderLayout}, made more
 * convenient for callers. In practice this interface will rarely be useful itself, since
 * any code calling any of these methods will also need to know that the object is a Swing
 * component or container, but the interface is provided for those rare cases when that is
 * <em>not</em> the case, and to appease static analysis warnings.
 *
 * @author Jonathan Lovelace
 */
public interface BorderedContainer {
	@Nullable Component getCenter();

	void setCenter(@Nullable Component center);

	@Nullable Component getPageStart();

	void setPageStart(@Nullable Component pageStart);

	@Nullable Component getPageEnd();

	void setPageEnd(@Nullable Component pageEnd);

	@Nullable Component getLineStart();

	void setLineStart(@Nullable Component lineStart);

	@Nullable Component getLineEnd();

	void setLineEnd(@Nullable Component lineEnd);
}

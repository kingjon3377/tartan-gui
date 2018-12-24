package lovelace.tartan.gui.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Transferable} implementation transferring a single integer.
 *
 * @author Jonathan Lovelace
 */
public final class IntTransferable implements Transferable {
	@NotNull
	private final DataFlavor flavor;
	private final int payload;

	public IntTransferable(@NotNull final DataFlavor flavor, final int payload) {
		this.flavor = flavor;
		this.payload = payload;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{flavor};
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return this.flavor.equals(flavor);
	}

	@NotNull
	@Override
	public Object getTransferData(final DataFlavor flavor)
			throws UnsupportedFlavorException {
		if (this.flavor.equals(flavor)) {
			return payload;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public String toString() {
		return "IntTransferable carrying " + payload;
	}

	@Override
	public int hashCode() {
		return payload;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof IntTransferable &&
					   payload == ((IntTransferable) obj).payload &&
					   flavor.equals(((IntTransferable) obj).flavor);
	}
}

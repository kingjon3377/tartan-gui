package lovelace.tartan.gui;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import lovelace.tartan.gui.model.IntTransferable;
import lovelace.tartan.gui.model.Reorderable;
import lovelace.tartan.model.Dance;
import org.jetbrains.annotations.NotNull;

/**
 * A transfer-handler to let the user drag items in the list of program elements.
 *
 * @author Jonathan Lovelace
 */
public class ProgramElementTransferHandler extends TransferHandler {
	private static final DataFlavor FLAVOR =
			new DataFlavor(Dance.class, "ProgramElement");
	private static final Logger LOGGER =
			Logger.getLogger(ProgramElementTransferHandler.class.getName());

	/**
	 * @param flag   a bit
	 * @param number a number
	 * @return whether that bit is set in that number
	 */
	private static boolean isBitSet(int flag, int number) {
		return (flag & number) == flag;
	}

	/**
	 * A drag-and-drop operation is supported iff it is using a supported flavor and
	 * it is
	 * or can be coerced to be a move operation.
	 */
	@Override
	public boolean canImport(final TransferSupport support) {
		if (support.isDrop() && support.isDataFlavorSupported(FLAVOR) &&
					isBitSet(TransferHandler.MOVE, support.getSourceDropActions())) {
			support.setDropAction(TransferHandler.MOVE);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a wrapper to transfer contents of the given component, which must be a
	 * {@link JList}.
	 *
	 * @param component the component whose contents will be transferred.
	 */
	@Override
	@NotNull
	public final Transferable createTransferable(JComponent component) {
		if (component instanceof JList<?>) {
			return new IntTransferable(FLAVOR,
					((JList<?>) component).getSelectedIndex());
		} else {
			throw new IllegalArgumentException(
					"Tried to create transferable from non-list");
		}
	}

	/**
	 * @return that we only support move operations
	 */
	@Override
	public int getSourceActions(final JComponent component) {
		return TransferHandler.MOVE;
	}

	/**
	 * Handle a drop.
	 */
	@Override
	public boolean importData(final TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		final Component component = support.getComponent();
		final DropLocation dropLocation = support.getDropLocation();
		final Transferable transfer = support.getTransferable();
		int payload;
		try {
			Object temp = transfer.getTransferData(FLAVOR);
			payload = (Integer) temp;
		} catch (UnsupportedFlavorException | IOException except) {
			LOGGER.log(Level.INFO, "Transfer failure", except);
			return false;
		}
		if (component instanceof JList<?>) {
			final ListModel model = ((JList<?>) component).getModel();
			if (model instanceof Reorderable && dropLocation instanceof JList.DropLocation) {
				final int index = ((JList.DropLocation) dropLocation).getIndex();
				((Reorderable) model).reorder(payload, index);
				return true;
			}
		}
		return false;
	}
}

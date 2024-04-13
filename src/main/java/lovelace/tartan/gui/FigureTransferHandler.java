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
import javax.swing.JTable;
import javax.swing.TransferHandler;
import lovelace.tartan.gui.model.IntTransferable;
import lovelace.tartan.gui.model.Reorderable;
import lovelace.tartan.model.Figure;

/**
 * A transfer-handler to let the user drag and drop figures within a dance.
 *
 * @author Jonathan Lovelace
 */
public class FigureTransferHandler extends TransferHandler {
	private static final DataFlavor FLAVOR = new DataFlavor(Figure.class,
			"DanceElement");
	private static final Logger LOGGER =
			Logger.getLogger(FigureTransferHandler.class.getName());

	/**
	 * A drag/drop operation is supported iff it is a supported flavor and it is or
	 * can be
	 * coerced to be a move operation.
	 */
	@Override
	public boolean canImport(final TransferSupport support) {
		if (support.isDrop() && support.isDataFlavorSupported(FLAVOR) &&
					(TransferHandler.MOVE & support.getSourceDropActions()) ==
							TransferHandler.MOVE) {
			support.setDropAction(TransferHandler.MOVE);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a wrapper to transfer contents of the given component, which must be a
	 * {@link JList} or {@link JTable}.
	 */
	@Override
	public Transferable createTransferable(final JComponent component) {
		if (component instanceof JList<?>) {
			return new IntTransferable(FLAVOR,
					((JList<?>) component).getSelectedIndex());
		} else if (component instanceof JTable) {
			return new IntTransferable(FLAVOR, ((JTable) component).getSelectedRow());
		} else {
			throw new IllegalArgumentException(
					"Can only create transferable from table or list");
		}
	}

	/**
	 * We only allow move operations.
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
		final int payload;
		try {
			payload = (Integer) transfer.getTransferData(FLAVOR);
		} catch (final UnsupportedFlavorException | IOException except) {
			LOGGER.log(Level.INFO, "Transfer failure", except);
			return false;
		}
		if (component instanceof JList<?> &&
					((JList<?>) component).getModel() instanceof Reorderable &&
					dropLocation instanceof JList.DropLocation) {
			((Reorderable) ((JList<?>) component).getModel())
					.reorder(payload, ((JList.DropLocation) dropLocation).getIndex());
			return true;
		} else if (component instanceof JTable &&
				((JTable) component).getModel() instanceof Reorderable &&
					dropLocation instanceof JTable.DropLocation) {
			((Reorderable) ((JTable) component).getModel())
					.reorder(payload, ((JTable.DropLocation) dropLocation).getRow());
			return true;
		} else {
			return false;
		}
	}
}

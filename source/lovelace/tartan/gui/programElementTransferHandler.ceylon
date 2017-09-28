import java.awt.datatransfer {
	DataFlavor,
	Transferable,
	UnsupportedFlavorException
}
import javax.swing {
	TransferHandler,
	SwingList=JList,
	JComponent
}
import lovelace.tartan.model {
	Dance
}
import java.lang {
	IllegalArgumentException
}
import java.io {
	IOException
}
import java.awt {
	Component
}
import ceylon.logging {
	Logger,
	logger
}
"Logger."
Logger log = logger(`module lovelace.tartan.gui`);
"A transfer-handler to let the user drag items in the list of program elements."
object programElementTransferHandler extends TransferHandler() {
	DataFlavor flavor = DataFlavor(`Dance`, "ProgramElement");
	"A drag/drop operation is supported iff it is a supported flavor and it is or
	 can be coerced to be a move operation."
	shared actual Boolean canImport(TransferSupport support) {
		if (support.drop, support.isDataFlavorSupported(flavor),
			TransferHandler.move.and(support.sourceDropActions) ==
					TransferHandler.move) {
			support.dropAction = TransferHandler.move;
			return true;
		} else {
			return false;
		}
	}
	"Create a wrapper to transfer contents of the given component, which must be a [[SwingList]]."
	shared actual Transferable createTransferable(JComponent component) {
		if (is SwingList<out Anything> component) {
			return IntTransferable(flavor, component.selectedIndex);
		} else {
			throw IllegalArgumentException("Tried to create transferable from non-list");
		}
	}
	"We only allow move operations."
	shared actual Integer getSourceActions(JComponent component) => TransferHandler.move;
	"Handle a drop."
	shared actual Boolean importData(TransferSupport support) {
		if (!support.drop) {
			return false;
		}
		Component component = support.component;
		DropLocation dropLocation = support.dropLocation;
		Transferable transfer = support.transferable;
		Integer payload;
		try {
			assert (is Integer temp = transfer.getTransferData(flavor));
			payload = temp;
		} catch (UnsupportedFlavorException|IOException except) {
			log.debug("Transfer failure", except);
			return false;
		}
		if (is SwingList<out Anything> component,
			is Reorderable model = component.model,
			is SwingList<out Anything>.DropLocation dropLocation) {
			Integer index = dropLocation.index;
			model.reorder(payload, index);
			return true;
		} else {
			return false;
		}
	}
}
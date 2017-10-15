import java.awt {
	BorderLayout,
	Component
}
import javax.swing {
	JPanel
}
import java.lang {
	Types
}
shared class BorderedPanel extends JPanel {
	variable Component? localCenter;
	variable Component? localNorth;
	variable Component? localSouth;
	variable Component? localEast;
	variable Component? localWest;
	shared new (Component? center = null, Component? pageStart = null, Component? pageEnd = null,
			Component? lineStart = null, Component? lineEnd = null) extends JPanel(BorderLayout()) {
		localCenter = center;
		localNorth = pageStart;
		localSouth = pageEnd;
		localWest = lineStart;
		localEast = lineEnd;
		if (exists center) {
			add(center, Types.nativeString(BorderLayout.center));
		}
		if (exists pageStart) {
			add(pageStart, Types.nativeString(BorderLayout.pageStart));
		}
		if (exists pageEnd) {
			add(pageEnd, Types.nativeString(BorderLayout.pageEnd));
		}
		if (exists lineStart) {
			add(lineStart, Types.nativeString(BorderLayout.lineStart));
		}
		if (exists lineEnd) {
			add(lineEnd, Types.nativeString(BorderLayout.lineEnd));
		}
	}
	shared new horizontalLine(Component? lineStart, Component? center, Component? lineEnd)
			extends BorderedPanel(center, null, null, lineStart, lineEnd) {}
	shared new verticalLine(Component? pageStart, Component? center, Component? pageEnd)
			extends BorderedPanel(center, pageStart, pageEnd) {}
	shared Component? center => localCenter;
	shared Component? pageStart => localNorth;
	shared Component? pageEnd => localSouth;
	shared Component? lineStart => localWest;
	shared Component? lineEnd => localEast;
	assign center {
		if (exists center) {
			add(center, Types.nativeString(BorderLayout.center));
			localCenter = center;
		} else if (exists temp = localCenter) {
			remove(temp);
		}
	}
	assign pageStart {
		if (exists pageStart) {
			add(pageStart, Types.nativeString(BorderLayout.pageStart));
			localNorth = pageStart;
		} else if (exists temp = localNorth) {
			remove(temp);
		}
	}
	assign pageEnd {
		if (exists pageEnd) {
			add(pageEnd, Types.nativeString(BorderLayout.pageEnd));
			localSouth = pageEnd;
		} else if (exists temp = localSouth) {
			remove(temp);
		}
	}
	assign lineStart {
		if (exists lineStart) {
			add(lineStart, Types.nativeString(BorderLayout.lineStart));
			localWest = lineStart;
		} else if (exists temp = localWest) {
			remove(temp);
		}
	}
	assign lineEnd {
		if (exists lineEnd) {
			add(lineEnd, Types.nativeString(BorderLayout.lineEnd));
			localEast = lineEnd;
		} else if (exists temp = localEast) {
			remove(temp);
		}
	}
}
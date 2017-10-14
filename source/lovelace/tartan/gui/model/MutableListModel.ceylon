import javax.swing {
	ListModel
}

shared interface MutableListModel<Element>
		satisfies ListModel<Element>&Reorderable&Correspondence<Integer,Element>
		given Element satisfies Object {
	shared formal void addElement(Element element);
	shared formal void removeElement(Integer|Element element);
	shared actual Element? get(Integer index) {
		if ((0:size).contains(index)) {
			return getElementAt(index);
		} else {
			return null;
		}
	}
	shared actual Boolean defines(Integer index) => (0:size).contains(index);
	shared formal Iterable<Element> asIterable;
}
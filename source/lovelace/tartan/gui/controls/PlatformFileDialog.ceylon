import java.awt {
	AWTFileDialog=FileDialog,
	Frame
}
import javax.swing {
	JFileChooser
}
import javax.swing.filechooser {
	FileFilter
}
import java.io {
	JFile=File,
	FilenameFilter
}
"A wrapper around [[AWTFileDialog]] on the Mac platform and [[JFileChooser]] on other platforms."
shared class PlatformFileDialog(Frame? parentWindow) {
	JFileChooser|AWTFileDialog wrapped;
	if (operatingSystem.name == "mac") {
		wrapped = AWTFileDialog(parentWindow);
	} else {
		wrapped = JFileChooser();
	}
	shared String? filename {
		switch (wrapped)
		case (is JFileChooser) {
			return wrapped.selectedFile.path;
		}
		case (is AWTFileDialog) {
			return wrapped.file;
		}
	}
	assign filename {
		switch (wrapped)
		case (is JFileChooser) {
			wrapped.selectedFile = JFile(filename);
		}
		case (is AWTFileDialog) {
			wrapped.file = filename;
		}
	}
	shared FileFilter|FilenameFilter fileFilter {
		switch (wrapped)
		case (is JFileChooser) {
			return wrapped.fileFilter;
		}
		case (is AWTFileDialog) {
			return wrapped.filenameFilter;
		}
	}
	assign fileFilter {
		switch (wrapped)
		case (is JFileChooser) {
			if (is FileFilter fileFilter) {
				wrapped.fileFilter = fileFilter;
			} else {
				wrapped.fileFilter = object extends FileFilter() {
					shared actual Boolean accept(JFile file) =>
							fileFilter.accept(file.parentFile, file.name);
					shared actual String description => "Unknown";
				};
			}
		}
		case (is AWTFileDialog) {
			if (is FilenameFilter fileFilter) {
				wrapped.filenameFilter = fileFilter;
			} else {
				wrapped.filenameFilter = object satisfies FilenameFilter {
					shared actual Boolean accept(JFile dir, String name) => fileFilter.accept(JFile(dir, name));
				};
			}
		}
	}
	shared void showOpenDialog() {
		switch (wrapped)
		case (is JFileChooser) {
			wrapped.showOpenDialog(parentWindow);
		}
		case (is AWTFileDialog) {
			wrapped.mode = AWTFileDialog.load;
			wrapped.setVisible(true);
		}
	}
	shared void showSaveAsDialog() {
		switch (wrapped)
		case (is JFileChooser) {
			wrapped.showSaveDialog(parentWindow);
		}
		case (is AWTFileDialog) {
			wrapped.mode = AWTFileDialog.save;
			wrapped.setVisible(true);
		}
	}
}
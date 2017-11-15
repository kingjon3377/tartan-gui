"Code to convert our model objects to and from LaTeX."
native("jvm") // TODO: Drop this if we can
module lovelace.tartan.latex "1.0.0" {
	shared import lovelace.tartan.model "1.0.0";
	import ceylon.logging "1.3.3";
	import ceylon.file "1.3.3";
}

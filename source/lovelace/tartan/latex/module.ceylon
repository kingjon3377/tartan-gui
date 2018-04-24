"Code to convert our model objects to and from LaTeX."
native("jvm") // TODO: Drop this if we can
module lovelace.tartan.latex "1.0.0" {
	value ceylonVersion = "1.3.3";
	shared import lovelace.tartan.model "1.0.0";
	import ceylon.logging ceylonVersion;
	import ceylon.file ceylonVersion;
	import ceylon.test ceylonVersion;
}

"The module to provide the GUI for the application."
native ("jvm")
module lovelace.tartan.gui "1.0.0" {
	value ceylonVersion = "1.3.3";
	value javaVersion = "8";
	value tartanVersion = "1.0.0";
	import lovelace.tartan.model tartanVersion;
	import lovelace.tartan.db tartanVersion;
	import java.desktop javaVersion;
	import java.base javaVersion;
	import ceylon.interop.java ceylonVersion;
	import ceylon.file ceylonVersion;
	import ceylon.logging ceylonVersion;
	import lovelace.tartan.latex tartanVersion;
}

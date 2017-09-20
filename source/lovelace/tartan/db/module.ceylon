"API to get dances and information from the Scottish Country Dance Database."
native ("jvm")
module lovelace.tartan.db "1.0.0" {
	import ceylon.dbc "1.3.3";
	shared import lovelace.tartan.model "1.0.0";
	import maven:"org.xerial:sqlite-jdbc" "3.19.3";
	import java.base "8";
}

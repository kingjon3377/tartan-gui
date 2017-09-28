"A break between dances."
shared class Intermission(description = "Intermission") {
	"How to describe the break in the program."
	shared String description;
	shared actual String string => description;
}
"The singing of Auld Lang Syne."
shared class AuldLangSyne(description = "Auld Lang Syne") {
	"How to describe this in the program."
	shared String description;
	shared actual String string => description;
}
shared alias ProgramElement => Dance|Intermission|AuldLangSyne;
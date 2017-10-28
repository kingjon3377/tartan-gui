"A break between dances."
shared class Intermission(description = "Intermission") {
	"How to describe the break in the program."
	shared variable String description;
	shared actual String string => description;
}
shared alias ProgramElement => Dance|Intermission;
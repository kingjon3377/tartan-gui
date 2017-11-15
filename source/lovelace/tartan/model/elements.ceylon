"A break between dances."
shared class Intermission(description = "Intermission") {
	"How to describe the break in the program."
	shared variable String description;
	shared actual String string => description;
	shared actual Boolean equals(Object other) {
		if (is Intermission other) {
			return other.description == description;
		} else {
			return false;
		}
	}
	shared actual Integer hash => description.hash;
}
shared alias ProgramElement => Dance|Intermission;
package lovelace.tartan.latex;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import lovelace.tartan.model.DanceImpl;
import lovelace.tartan.model.Figure;
import lovelace.tartan.model.Intermission;
import lovelace.tartan.model.NamedFigure;
import lovelace.tartan.model.ProgramElement;
import lovelace.tartan.model.ProgramMetadata;
import lovelace.util.Pair;
import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Some tests for LaTeX reading and writing code.
 *
 * @author Jonathan Lovelace
 */
@RunWith(Parameterized.class)
public class LaTeXTest {
	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Parameter
	public boolean titleOnCover;
	@SuppressWarnings("InstanceVariableMayNotBeInitialized")
	@Parameter(1)
	public boolean printAuldLangSyne;

	@Parameters(name = "{index}: [{0}, {1}]")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{{true, true}, {true, false}, {false, true},
				{false, false}});
	}

	public LaTeXTest() {
		Arrays.stream(LogManager.getLogManager().getLogger("").getHandlers())
				.forEach(h -> h.setLevel(Level.FINEST));
	}

	@Test
	public void noImageTest() throws Exception {
		final List<ProgramElement> startingProgram = Arrays.asList(
				new DanceImpl("Fiddler's Choice", "M. Morgan", "Jig", 8, 32,
						"2C (4C set)",
						new Figure("1s & 2s advance and retire and dance back to back",
								"1-8"),
						new Figure(
								"1s and 2s dance Right Hands across and Left Hands back",
								"9-16"),
						new Figure(
								"""
										1s lead down the middle & up to 2d place (2s \
										step up on 19-20""",
								"17-24"),
						new Figure("2s & 1s circle 4H around to the left and back",
								"25-32")),
				new DanceImpl("Random Reel", "A. Mouse", "Reel", 6, 40, "2C (3C set)",
						new Figure("1s & 2s dance the Targe:", "1-8"), new NamedFigure(
						new Figure(
								"""
										1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 \
										round anticlockwise""",
								"1-2"),
						new Figure("1M with 2L & 1L with 2M full turn", "3-4"),
						new Figure(
								"""
										1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 \
										way round anticlockwise""",
								"5-6"),
						new Figure("1M with 2L & 1L with 2M full turn", "7-8")),
						new Figure("remainder of dance description here")),
				new Intermission(),
				new DanceImpl("Odd Example", "Bk -1", "Strathspey", 3, 32, "3C Triangle",
						new Figure("dance description here")),
				new DanceImpl("Unsourced Medley", "", "Medley", 1, 64, "Sq. Set",
						new Figure("dance description here"), new Figure("xyzzy")),
				new Intermission("Break"));
		final ProgramMetadata startingMetadata = makeTestMetadata();
		startingMetadata.setTitleOnCover(titleOnCover);
		startingMetadata.setPrintAuldLangSyne(printAuldLangSyne);
		final StringBuilder builder = new StringBuilder(
				LaTeXWriter.estimateSize(startingProgram, startingMetadata));
		LaTeXWriter.writeLaTeXProgram(builder, startingProgram, startingMetadata);
		final String serialized = builder.toString();
		final Pair<@NonNull ProgramMetadata, @NonNull List<@NonNull ProgramElement>>
				deserializationResults = new LaTeXReader().readLaTeXProgram(serialized);
		final ProgramMetadata readMetadata = deserializationResults.getFirst();
		final List<ProgramElement> readProgram = deserializationResults.getSecond();
		assertThat("Metadata should be (de)serialized correctly", readMetadata,
				is(startingMetadata));
		assertThat("Dances should be (de)serialized correctly", readProgram,
				is(startingProgram));
	}

	@NonNull
	private static ProgramMetadata makeTestMetadata() {
		final ProgramMetadata startingMetadata = new ProgramMetadata();
		startingMetadata.setGroupCoverName("groupCoverName");
		startingMetadata.setGroupTitleName("groupTitleName");
		startingMetadata.setEventCoverName("eventCoverName");
		startingMetadata.setEventTitleName("eventTitleName");
		startingMetadata.setCoverDate("coverDate");
		startingMetadata.setTitleDate("titleDate");
		startingMetadata.setCoverLocation("coverLocation");
		startingMetadata.setTitleLocation("titleLocation");
		startingMetadata.setLocationAddress("locationAddress");
		startingMetadata.setTitleTimes("""
				titleTimes
				secondLine""");
		startingMetadata.setMusicians("musicians");
		return startingMetadata;
	}

	@Test
	@SuppressWarnings("HardcodedFileSeparator") // '/' is cross-platform in Java!
	public void withImageTest() throws Exception {
		final List<ProgramElement> startingProgram = Arrays.asList(
				new DanceImpl("Fiddler's Choice", "M. Morgan", "Jig", 8, 32,
						"2C (4C set)",
						new Figure("1s & 2s advance and retire and dance back to back",
								"1-8"),
						new Figure(
								"1s and 2s dance Right Hands across and Left Hands back",
								"9-16"),
						new Figure(
								"""
										1s lead down the middle & up to 2d place (2s \
										step up on 19-20""",
								"17-24"),
						new Figure("2s & 1s circle 4H around to the left and back",
								"25-32")),
				new DanceImpl("Random Reel", "A. Mouse", "Reel", 6, 40, "2C (3C set)",
						new Figure("1s & 2s dance the Targe:", "1-8"),
						new NamedFigure(new Figure(
								"""
										1L & 2L turn RH 3/4 <b>while</b> Men dance 1/4 \
										round anticlockwise""",
								"1-2"),
								new Figure("1M with 2L & 1L with 2M full turn", "3-4"),
								new Figure(
										"""
												1L & 2L turn RH 3/4 <b>while</b> Men \
												dance 1/4 way round anticlockwise""",
										"5-6"),
								new Figure("1M with 2L & 1L with 2M full turn", "7-8")),
						new Figure("remainder of dance description here")),
				new Intermission(),
				new DanceImpl("Odd Example", "Bk -1", "Strathspey", 3, 32, "3C Triangle",
						new Figure("dance description here")),
				new DanceImpl("Unsourced Medley", "", "Medley", 1, 64, "Sq. Set",
						new Figure("dance description here"), new Figure("xyzzy")),
				new Intermission("Break")
		);
		final ProgramMetadata startingMetadata = makeTestMetadata();
		startingMetadata.setCoverImage(Paths.get("path/to/coverImage"));
		startingMetadata.setBackCoverImage(Paths.get("path/to/backCover"));
		Collections.addAll(startingMetadata.getInsidePostDanceImages(),
				Paths.get("firstExtra"), Paths.get("secondExtra"));
		startingMetadata.setTitleOnCover(titleOnCover);
		startingMetadata.setPrintAuldLangSyne(printAuldLangSyne);
		final StringBuilder builder = new StringBuilder(
				LaTeXWriter.estimateSize(startingProgram, startingMetadata));
		LaTeXWriter.writeLaTeXProgram(builder, startingProgram, startingMetadata);
		final String serialized = builder.toString();
		final Pair<@NonNull ProgramMetadata, @NonNull List<@NonNull ProgramElement>>
				deserializationResults = new LaTeXReader().readLaTeXProgram(serialized);
		final ProgramMetadata readMetadata = deserializationResults.getFirst();
		final List<ProgramElement> readProgram = deserializationResults.getSecond();
		assertThat("Metadata should be (de)serialized correctly", readMetadata,
				is(startingMetadata));
		assertThat("Dances should be (de)serialized correctly", readProgram,
				is(startingProgram));
	}

	@Override
	public String toString() {
		return "LaTeXTest";
	}
}

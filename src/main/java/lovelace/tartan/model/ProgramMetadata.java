package lovelace.tartan.model;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class to represent information related to the ball program as exported or imported,
 * beyond the list of dances and their directions.
 *
 * @author Jonathan Lovelace
 */
public final class ProgramMetadata {
	/**
	 * The filename this program was loaded from or should be saved to.
	 */
	@Nullable
	private Path filename = null;
	/**
	 * The name of the group putting on the event, as it should appear on the cover.
	 */
	@NotNull
	private String groupCoverName = "";
	/**
	 * The name of the group putting on the event, as it should appear on the title page.
	 */
	@NotNull
	private String groupTitleName = "";
	/**
	 * The name of the event as it should appear on the cover.
	 */
	@NotNull
	private String eventCoverName = "";
	/**
	 * The name of the event as it should appear on the title page.
	 */
	@NotNull
	private String eventTitleName = "";
	/**
	 * The date of the event, as it should appear on the cover.
	 */
	@NotNull
	private String coverDate = "";
	/**
	 * The date of the event, as it should appear on the title page.
	 */
	@NotNull
	private String titleDate = "";
	/**
	 * The location of the event, as it should appear on the cover.
	 */
	@NotNull
	private String coverLocation = "";
	/**
	 * The location of the event, as it should appear on the title page.
	 */
	@NotNull
	private String titleLocation = "";
	/**
	 * The address of the event, if it should appear on the title page.
	 */
	@NotNull
	private String locationAddress = "";
	/**
	 * The time(s) of the event (e.g. "Gather 6 p.m., Dinner 6:30 p.m., Dance 7:30 p.m."
	 * Newlines will replaced with hard-linebreak commands for LaTeX.
	 */
	@NotNull
	private String titleTimes = "";
	/**
	 * The name(s) of the musician(s) providing music for the event. If provided, this
	 * will be typeset on the title page.
	 */
	@NotNull
	private String musicians = "";
	/**
	 * The filename of an image to put on the cover.
	 */
	@Nullable
	private Path coverImage = null;
	/**
	 * Whether to put the title page on the back of the cover.
	 */
	private boolean titleOnCover = false;
	/**
	 * Whether to print the text of Auld Lang Syne after the last dance's crib.
	 */
	private boolean printAuldLangSyne = false;
	/**
	 * The filename of an image to put on the back cover (or, rather, the last page).
	 */
	@Nullable
	private Path backCoverImage = null;
	/**
	 * Filenames of images to put after the last dance's crib, before Auld Lang Syne (or
	 * before the back cover if Auld Lang Syne is not included).
	 */
	private final List<@NotNull Path> insidePostDanceImages = new ArrayList<>();

	/**
	 * @return The filename this program was loaded from or should be saved to.
	 */
	@Nullable
	public Path getFilename() {
		return filename;
	}

	/**
	 * @return The name of the group putting on the event, as it should appear on the
	 * cover.
	 */
	@NotNull
	public String getGroupCoverName() {
		return groupCoverName;
	}

	/**
	 * @return The name of the group putting on the event, as it should appear on the
	 * title page.
	 */
	@NotNull
	public String getGroupTitleName() {
		return groupTitleName;
	}

	/**
	 * @return The name of the event as it should appear on the cover.
	 */
	@NotNull
	public String getEventCoverName() {
		return eventCoverName;
	}

	/**
	 * @return The name of the event as it should appear on the title page.
	 */
	@NotNull
	public String getEventTitleName() {
		return eventTitleName;
	}

	/**
	 * @return The date of the event, as it should appear on the cover.
	 */
	@NotNull
	public String getCoverDate() {
		return coverDate;
	}

	/**
	 * @return The date of the event, as it should appear on the title page.
	 */
	@NotNull
	public String getTitleDate() {
		return titleDate;
	}

	/**
	 * @return The location of the event, as it should appear on the cover.
	 */
	@NotNull
	public String getCoverLocation() {
		return coverLocation;
	}

	/**
	 * @return The location of the event, as it should appear on the title page.
	 */
	@NotNull
	public String getTitleLocation() {
		return titleLocation;
	}

	/**
	 * @return The address of the event, if it should appear on the title page.
	 */
	@NotNull
	public String getLocationAddress() {
		return locationAddress;
	}

	/**
	 * @return The time(s) of the event
	 */
	@NotNull
	public String getTitleTimes() {
		return titleTimes;
	}

	/**
	 * @return The name(s) of the musician(s) providing music for the event.
	 */
	@NotNull
	public String getMusicians() {
		return musicians;
	}

	/**
	 * @return The filename of an image to put on the cover, or null if none specified
	 */
	@Nullable
	public Path getCoverImage() {
		return coverImage;
	}

	/**
	 * @return Whether to put the title page on the back of the cover.
	 */
	public boolean getTitleOnCover() {
		return titleOnCover;
	}

	/**
	 * @return Whether to print the text of Auld Lang Syne after the last dance's crib.
	 */
	public boolean getPrintAuldLangSyne() {
		return printAuldLangSyne;
	}

	/**
	 * @return The filename of an image to put on the back cover/last page, or null if
	 * none specified
	 */
	@Nullable
	public Path getBackCoverImage() {
		return backCoverImage;
	}

	/**
	 * @return the list of filenames of images to put after the last dance's crib, before
	 * Auld Lang Syne (or before the back cover if Auld Lang Syne is not included).
	 */
	@NotNull
	public final List<@NotNull Path> getInsidePostDanceImages() {
		return insidePostDanceImages;
	}
	/**
	 * @param filename The filename this program was loaded from or should be saved to.
	 */
	public void setFilename(@Nullable final Path filename) {
		this.filename = filename;
	}

	/**
	 * @param name The name of the group putting on the event, as it should appear on the
	 * cover.
	 */
	public void setGroupCoverName(@NotNull final String name) {
		groupCoverName = name;
	}

	/**
	 * @param name The name of the group putting on the event, as it should appear on the
	 * title page.
	 */
	public void setGroupTitleName(@NotNull final String name) {
		groupTitleName = name;
	}

	/**
	 * @param name The name of the event as it should appear on the cover.
	 */
	public void setEventCoverName(@NotNull final String name) {
		eventCoverName = name;
	}

	/**
	 * @param name The name of the event as it should appear on the title page.
	 */
	public void setEventTitleName(@NotNull final String name) {
		eventTitleName = name;
	}

	/**
	 * @param date The date of the event, as it should appear on the cover.
	 */
	public void setCoverDate(@NotNull final String date) {
		coverDate = date;
	}

	/**
	 * @param date The date of the event, as it should appear on the title page.
	 */
	public void setTitleDate(@NotNull final String date) {
		titleDate = date;
	}

	/**
	 * @param location The location of the event, as it should appear on the cover.
	 */
	public void setCoverLocation(@NotNull final String location) {
		coverLocation = location;
	}

	/**
	 * @param location The location of the event, as it should appear on the title page.
	 */
	public void setTitleLocation(@NotNull final String location) {
		titleLocation = location;
	}

	/**
	 * @param address The address of the event, if it should appear on the title page.
	 */
	public void setLocationAddress(@NotNull final String address) {
		locationAddress = address;
	}

	/**
	 * @param times The time(s) of the event
	 */
	public void setTitleTimes(@NotNull final String times) {
		titleTimes = times;
	}

	/**
	 * @param musicians The name(s) of the musician(s) providing music for the event.
	 */
	public void setMusicians(@NotNull final String musicians) {
		this.musicians = musicians;
	}

	/**
	 * @param image The filename of an image to put on the cover, or null to specify none
	 */
	public void setCoverImage(@Nullable final Path image) {
		coverImage = image;
	}

	/**
	 * @param enabled Whether to put the title page on the back of the cover.
	 */
	public void setTitleOnCover(final boolean enabled) {
		titleOnCover = enabled;
	}

	/**
	 * @param enabled Whether to print the text of Auld Lang Syne after the last dance's crib.
	 */
	public void setPrintAuldLangSyne(final boolean enabled) {
		printAuldLangSyne = enabled;
	}

	/**
	 * @param image The filename of an image to put on the back cover/last page, or null
	 *                if none specified
	 */
	public void setBackCoverImage(@Nullable final Path image) {
		backCoverImage = image;
	}
	/**
	 * @return a String representation of this object, mainly for debugging use.
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		for (final Field field : ProgramMetadata.class.getDeclaredFields()) {
			builder.append("- ");
			builder.append(field.getName());
			builder.append(": ");
			try {
				builder.append(field.get(this));
			} catch (final IllegalAccessException except) {
				builder.append("[threw exception]");
			}
			builder.append(System.lineSeparator());
		}
		return builder.toString();
	}

	/**
	 * @param other another object
	 * @return if it is identical to this one
	 */
	@Override
	public boolean equals(final Object other) {
		if (other instanceof ProgramMetadata) {
			final ProgramMetadata that = (ProgramMetadata) other;
			return titleOnCover == that.titleOnCover &&
						   printAuldLangSyne == that.printAuldLangSyne &&
						   groupCoverName.equals(that.groupCoverName) &&
						   groupTitleName.equals(that.groupTitleName) &&
						   eventCoverName.equals(that.eventCoverName) &&
						   eventTitleName.equals(that.eventTitleName) &&
						   coverDate.equals(that.coverDate) &&
						   titleDate.equals(that.titleDate) &&
						   coverLocation.equals(that.coverLocation) &&
						   titleLocation.equals(that.titleLocation) &&
						   locationAddress.equals(that.locationAddress) &&
						   titleTimes.equals(that.titleTimes) &&
						   musicians.equals(that.musicians) &&
						   Objects.equals(coverImage, that.coverImage) &&
						   Objects.equals(backCoverImage, that.backCoverImage) &&
						   insidePostDanceImages.equals(that.insidePostDanceImages);
		} else {
			return false;
		}
	}

	/**
	 * @return a hash value for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(groupCoverName, groupTitleName, eventCoverName,
				eventTitleName,
				coverDate, titleDate, coverLocation, titleLocation, locationAddress,
				titleTimes, musicians, coverImage, titleOnCover, printAuldLangSyne,
				backCoverImage, insidePostDanceImages);
	}
}

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
	private @Nullable Path filename = null;
	/**
	 * The name of the group putting on the event, as it should appear on the cover.
	 */
	private @NotNull String groupCoverName = "";
	/**
	 * The name of the group putting on the event, as it should appear on the title page.
	 */
	private @NotNull String groupTitleName = "";
	/**
	 * The name of the event as it should appear on the cover.
	 */
	private @NotNull String eventCoverName = "";
	/**
	 * The name of the event as it should appear on the title page.
	 */
	private @NotNull String eventTitleName = "";
	/**
	 * The date of the event, as it should appear on the cover.
	 */
	private @NotNull String coverDate = "";
	/**
	 * The date of the event, as it should appear on the title page.
	 */
	private @NotNull String titleDate = "";
	/**
	 * The location of the event, as it should appear on the cover.
	 */
	private @NotNull String coverLocation = "";
	/**
	 * The location of the event, as it should appear on the title page.
	 */
	private @NotNull String titleLocation = "";
	/**
	 * The address of the event, if it should appear on the title page.
	 */
	private @NotNull String locationAddress = "";
	/**
	 * The time(s) of the event (e.g. "Gather 6 p.m., Dinner 6:30 p.m., Dance 7:30 p.m."
	 * Newlines will replaced with hard-linebreak commands for LaTeX.
	 */
	private @NotNull String titleTimes = "";
	/**
	 * The name(s) of the musician(s) providing music for the event. If provided, this
	 * will be typeset on the title page.
	 */
	private @NotNull String musicians = "";
	/**
	 * The filename of an image to put on the cover.
	 */
	private @Nullable Path coverImage = null;
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
	private @Nullable Path backCoverImage = null;
	/**
	 * Filenames of images to put after the last dance's crib, before Auld Lang Syne (or
	 * before the back cover if Auld Lang Syne is not included).
	 */
	private final List<@NotNull Path> insidePostDanceImages = new ArrayList<>();

	/**
	 * @return The filename this program was loaded from or should be saved to.
	 */
	public @Nullable Path getFilename() {
		return filename;
	}

	/**
	 * @return The name of the group putting on the event, as it should appear on the
	 * cover.
	 */
	public @NotNull String getGroupCoverName() {
		return groupCoverName;
	}

	/**
	 * @return The name of the group putting on the event, as it should appear on the
	 * title page.
	 */
	public @NotNull String getGroupTitleName() {
		return groupTitleName;
	}

	/**
	 * @return The name of the event as it should appear on the cover.
	 */
	public @NotNull String getEventCoverName() {
		return eventCoverName;
	}

	/**
	 * @return The name of the event as it should appear on the title page.
	 */
	public @NotNull String getEventTitleName() {
		return eventTitleName;
	}

	/**
	 * @return The date of the event, as it should appear on the cover.
	 */
	public @NotNull String getCoverDate() {
		return coverDate;
	}

	/**
	 * @return The date of the event, as it should appear on the title page.
	 */
	public @NotNull String getTitleDate() {
		return titleDate;
	}

	/**
	 * @return The location of the event, as it should appear on the cover.
	 */
	public @NotNull String getCoverLocation() {
		return coverLocation;
	}

	/**
	 * @return The location of the event, as it should appear on the title page.
	 */
	public @NotNull String getTitleLocation() {
		return titleLocation;
	}

	/**
	 * @return The address of the event, if it should appear on the title page.
	 */
	public @NotNull String getLocationAddress() {
		return locationAddress;
	}

	/**
	 * @return The time(s) of the event
	 */
	public @NotNull String getTitleTimes() {
		return titleTimes;
	}

	/**
	 * @return The name(s) of the musician(s) providing music for the event.
	 */
	public @NotNull String getMusicians() {
		return musicians;
	}

	/**
	 * @return The filename of an image to put on the cover, or null if none specified
	 */
	public @Nullable Path getCoverImage() {
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
	public @Nullable Path getBackCoverImage() {
		return backCoverImage;
	}

	/**
	 * @return the list of filenames of images to put after the last dance's crib, before
	 * Auld Lang Syne (or before the back cover if Auld Lang Syne is not included).
	 */
	public final @NotNull List<@NotNull Path> getInsidePostDanceImages() {
		return insidePostDanceImages;
	}
	/**
	 * @param filename The filename this program was loaded from or should be saved to.
	 */
	public void setFilename(final @Nullable Path filename) {
		this.filename = filename;
	}

	/**
	 * @param name The name of the group putting on the event, as it should appear on the
	 * cover.
	 */
	public void setGroupCoverName(final @NotNull String name) {
		groupCoverName = name;
	}

	/**
	 * @param name The name of the group putting on the event, as it should appear on the
	 * title page.
	 */
	public void setGroupTitleName(final @NotNull String name) {
		groupTitleName = name;
	}

	/**
	 * @param name The name of the event as it should appear on the cover.
	 */
	public void setEventCoverName(final @NotNull String name) {
		eventCoverName = name;
	}

	/**
	 * @param name The name of the event as it should appear on the title page.
	 */
	public void setEventTitleName(final @NotNull String name) {
		eventTitleName = name;
	}

	/**
	 * @param date The date of the event, as it should appear on the cover.
	 */
	public void setCoverDate(final @NotNull String date) {
		coverDate = date;
	}

	/**
	 * @param date The date of the event, as it should appear on the title page.
	 */
	public void setTitleDate(final @NotNull String date) {
		titleDate = date;
	}

	/**
	 * @param location The location of the event, as it should appear on the cover.
	 */
	public void setCoverLocation(final @NotNull String location) {
		coverLocation = location;
	}

	/**
	 * @param location The location of the event, as it should appear on the title page.
	 */
	public void setTitleLocation(final @NotNull String location) {
		titleLocation = location;
	}

	/**
	 * @param address The address of the event, if it should appear on the title page.
	 */
	public void setLocationAddress(final @NotNull String address) {
		locationAddress = address;
	}

	/**
	 * @param times The time(s) of the event
	 */
	public void setTitleTimes(final @NotNull String times) {
		titleTimes = times;
	}

	/**
	 * @param musicians The name(s) of the musician(s) providing music for the event.
	 */
	public void setMusicians(final @NotNull String musicians) {
		this.musicians = musicians;
	}

	/**
	 * @param image The filename of an image to put on the cover, or null to specify none
	 */
	public void setCoverImage(final @Nullable Path image) {
		coverImage = image;
	}

	/**
	 * @param enabled Whether to put the title page on the back of the cover.
	 */
	public void setTitleOnCover(final boolean enabled) {
		titleOnCover = enabled;
	}

	/**
	 * @param enabled Whether to print the text of Auld Lang Syne after the last dance's
	 *                   crib.
	 */
	public void setPrintAuldLangSyne(final boolean enabled) {
		printAuldLangSyne = enabled;
	}

	/**
	 * @param image The filename of an image to put on the back cover/last page, or null
	 *                if none specified
	 */
	public void setBackCoverImage(final @Nullable Path image) {
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

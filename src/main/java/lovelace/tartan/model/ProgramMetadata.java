package lovelace.tartan.model;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

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
	private String groupCoverName = "";
	/**
	 * The name of the group putting on the event, as it should appear on the title page.
	 */
	private String groupTitleName = "";
	/**
	 * The name of the event as it should appear on the cover.
	 */
	private String eventCoverName = "";
	/**
	 * The name of the event as it should appear on the title page.
	 */
	private String eventTitleName = "";
	/**
	 * The date of the event, as it should appear on the cover.
	 */
	private String coverDate = "";
	/**
	 * The date of the event, as it should appear on the title page.
	 */
	private String titleDate = "";
	/**
	 * The location of the event, as it should appear on the cover.
	 */
	private String coverLocation = "";
	/**
	 * The location of the event, as it should appear on the title page.
	 */
	private String titleLocation = "";
	/**
	 * The address of the event, if it should appear on the title page.
	 */
	private String locationAddress = "";
	/**
	 * The time(s) of the event (e.g. "Gather 6 p.m., Dinner 6:30 p.m., Dance 7:30 p.m.")
	 * Newlines will be replaced with hard-linebreak commands for LaTeX.
	 */
	private String titleTimes = "";
	/**
	 * The name(s) of the musician(s) providing music for the event. If provided, this
	 * will be typeset on the title page.
	 */
	private String musicians = "";
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
	private final List<Path> insidePostDanceImages = new ArrayList<>();

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
	public String getGroupCoverName() {
		return groupCoverName;
	}

	/**
	 * @return The name of the group putting on the event, as it should appear on the
	 * title page.
	 */
	public String getGroupTitleName() {
		return groupTitleName;
	}

	/**
	 * @return The name of the event as it should appear on the cover.
	 */
	public String getEventCoverName() {
		return eventCoverName;
	}

	/**
	 * @return The name of the event as it should appear on the title page.
	 */
	public String getEventTitleName() {
		return eventTitleName;
	}

	/**
	 * @return The date of the event, as it should appear on the cover.
	 */
	public String getCoverDate() {
		return coverDate;
	}

	/**
	 * @return The date of the event, as it should appear on the title page.
	 */
	public String getTitleDate() {
		return titleDate;
	}

	/**
	 * @return The location of the event, as it should appear on the cover.
	 */
	public String getCoverLocation() {
		return coverLocation;
	}

	/**
	 * @return The location of the event, as it should appear on the title page.
	 */
	public String getTitleLocation() {
		return titleLocation;
	}

	/**
	 * @return The address of the event, if it should appear on the title page.
	 */
	public String getLocationAddress() {
		return locationAddress;
	}

	/**
	 * @return The time(s) of the event
	 */
	public String getTitleTimes() {
		return titleTimes;
	}

	/**
	 * @return The name(s) of the musician(s) providing music for the event.
	 */
	public String getMusicians() {
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
	public final List<Path> getInsidePostDanceImages() {
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
	public void setGroupCoverName(final String name) {
		groupCoverName = name;
	}

	/**
	 * @param name The name of the group putting on the event, as it should appear on the
	 * title page.
	 */
	public void setGroupTitleName(final String name) {
		groupTitleName = name;
	}

	/**
	 * @param name The name of the event as it should appear on the cover.
	 */
	public void setEventCoverName(final String name) {
		eventCoverName = name;
	}

	/**
	 * @param name The name of the event as it should appear on the title page.
	 */
	public void setEventTitleName(final String name) {
		eventTitleName = name;
	}

	/**
	 * @param date The date of the event, as it should appear on the cover.
	 */
	public void setCoverDate(final String date) {
		coverDate = date;
	}

	/**
	 * @param date The date of the event, as it should appear on the title page.
	 */
	public void setTitleDate(final String date) {
		titleDate = date;
	}

	/**
	 * @param location The location of the event, as it should appear on the cover.
	 */
	public void setCoverLocation(final String location) {
		coverLocation = location;
	}

	/**
	 * @param location The location of the event, as it should appear on the title page.
	 */
	public void setTitleLocation(final String location) {
		titleLocation = location;
	}

	/**
	 * @param address The address of the event, if it should appear on the title page.
	 */
	public void setLocationAddress(final String address) {
		locationAddress = address;
	}

	/**
	 * @param times The time(s) of the event
	 */
	public void setTitleTimes(final String times) {
		titleTimes = times;
	}

	/**
	 * @param musicians The name(s) of the musician(s) providing music for the event.
	 */
	public void setMusicians(final String musicians) {
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
		final StringBuilder builder =
				new StringBuilder(ProgramMetadata.class.getDeclaredFields().length * 20);
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
	 * @param obj another object
	 * @return if it is identical to this one
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof final ProgramMetadata that) {
			//noinspection OverlyComplexBooleanExpression
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

	/**
	 * TODO: Should probably use isBlank() instead.
	 *
	 * @return whether any of the fields that would be included on the cover is non-empty.
	 */
	public boolean hasCoverContent() {
		return !groupCoverName.isEmpty() || !eventCoverName.isEmpty() ||
				       !coverDate.isEmpty() || !coverLocation.isEmpty();
	}

	/**
	 * TODO: Should probably use isBlank() instead
	 *
	 * @return whether any of the fields that would be included on the title page is
	 * non-empty.
	 */
	public boolean hasTitlePageContent() {
		return !groupTitleName.isEmpty() || !eventTitleName.isEmpty() ||
				       !titleDate.isEmpty() || !titleLocation.isEmpty() ||
				       !locationAddress.isEmpty() || !titleTimes.isEmpty() ||
				       !musicians.isEmpty();
	}
}

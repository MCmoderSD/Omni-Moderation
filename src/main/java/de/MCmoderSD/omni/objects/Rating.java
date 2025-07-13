package de.MCmoderSD.omni.objects;

import com.openai.models.moderations.Moderation;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.HashMap;

import static de.MCmoderSD.omni.objects.Rating.Category.*;

@SuppressWarnings("ALL")
public class Rating implements Serializable {

    // Type
    private final boolean flagged;

    // Attributes
    private final HashMap<Category, Flag> flags;

    /**
     * Constructs a Rating object from a given Moderation result.
     *
     * @param moderation The moderation result provided by the OpenAI API.
     */
    public Rating(Moderation moderation) {

        // Extract Data
        var flags = moderation.categories();
        var scores = moderation.categoryScores();

        // Set Type
        flagged = moderation.flagged();

        // Initialize
        ArrayList<Flag> flagList = new ArrayList<>();

        // Set Flags
        flagList.add(new Flag(HARASSMENT, flags.harassment(), scores.harassment()));
        flagList.add(new Flag(HARASSMENT_THREATENING, flags.harassmentThreatening(), scores.harassmentThreatening()));
        flagList.add(new Flag(HATE, flags.hate(), scores.hate()));
        flagList.add(new Flag(HATE_THREATENING, flags.hateThreatening(), scores.hateThreatening()));
        flagList.add(new Flag(ILLICIT, flags.illicit().orElse(false), flags.illicit().isPresent() ? scores.illicit() : 0d));
        flagList.add(new Flag(ILLICIT_VIOLENT, flags.illicitViolent().orElse(false), flags.illicitViolent().isPresent() ? scores.illicitViolent() : 0d));
        flagList.add(new Flag(SELF_HARM, flags.selfHarm(), scores.selfHarm()));
        flagList.add(new Flag(SELF_HARM_INTENT, flags.selfHarmIntent(), scores.selfHarmIntent()));
        flagList.add(new Flag(SELF_HARM_INSTRUCTIONS, flags.selfHarmInstructions(), scores.selfHarmInstructions()));
        flagList.add(new Flag(SEXUAL, flags.sexual(), scores.sexual()));
        flagList.add(new Flag(SEXUAL_MINORS, flags.sexualMinors(), scores.sexualMinors()));
        flagList.add(new Flag(VIOLENCE, flags.violence(), scores.violence()));
        flagList.add(new Flag(VIOLENCE_GRAPHIC, flags.violenceGraphic(), scores.violenceGraphic()));

        // Create HashMap
        this.flags = new HashMap<>();
        for (Flag flag : flagList) this.flags.put(flag.category, flag);
    }

    /**
     * Enumeration of moderation categories with human-readable name and description.
     */
    public enum Category implements Serializable {

        // Categories
        HARASSMENT("Harassment", "Content that expresses, incites, or promotes harassing language towards any target."),
        HARASSMENT_THREATENING("Harassment Threatening", "Harassment content that also includes violence or serious harm towards any target."),
        HATE("Hate", "Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual orientation, disability status, or caste. Hateful content aimed at non-protected groups (e.g., chess players) is harassment."),
        HATE_THREATENING("Hate Threatening", "Hateful content that also includes violence or serious harm towards the targeted group based on race, gender, ethnicity, religion, nationality, sexual orientation, disability status, or caste."),
        ILLICIT("Illicit", "Content that gives advice or instruction on how to commit illicit acts. A phrase like \"how to shoplift\" would fit this category."),
        ILLICIT_VIOLENT("Illicit Violent", "The same types of content flagged by the illicit category, but also includes references to violence or procuring a weapon."),
        SELF_HARM("Self Harm", "Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders."),
        SELF_HARM_INTENT("Self Harm Intent", "Content where the speaker expresses that they are engaging or intend to engage in acts of self-harm, such as suicide, cutting, and eating disorders."),
        SELF_HARM_INSTRUCTIONS("Self Harm Instructions", "Content that encourages performing acts of self-harm, such as suicide, cutting, and eating disorders, or that gives instructions or advice on how to commit such acts."),
        SEXUAL("Sexual", "Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual services (excluding sex education and wellness)."),
        SEXUAL_MINORS("Sexual Minors", "Sexual content that includes an individual who is under 18 years old."),
        VIOLENCE("Violence", "Content that depicts death, violence, or physical injury."),
        VIOLENCE_GRAPHIC("Violence Graphic", "Content that depicts death, violence, or physical injury in graphic detail.");

        // Attributes
        private final String name;
        private final String description;

        /**
         * Constructs a Category enum value.
         *
         * @param name        The display name of the category.
         * @param description The detailed description of the category.
         */
        Category(String name, String description) {
            this.name = name;
            this.description = description;
        }

        /**
         * Gets the display name of the category.
         *
         * @return The name of the category.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the description of the category.
         *
         * @return The description of the category.
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Record representing a single moderation flag and score.
     *
     * @param category The category of the flag.
     * @param flagged  Whether the content is flagged under this category.
     * @param score    The confidence score of the flag.
     */
    public record Flag(Category category, boolean flagged, double score) implements Serializable {

        /**
         * Converts the confidence score to a percentage with specified scale.
         *
         * @param scale Number of decimal places to round to.
         * @return Score as percentage.
         */
        public BigDecimal asPercentage(int scale) {
            return BigDecimal.valueOf(score).movePointRight(2).setScale(scale, RoundingMode.HALF_UP);
        }
    }

    /**
     * Checks whether the content is flagged by the moderation system.
     *
     * @return true if flagged, false otherwise.
     */
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Retrieves all flags from the moderation result.
     *
     * @return A list of all flags.
     */
    public ArrayList<Flag> getFlags() {
        return new ArrayList<>(flags.values());
    }

    /**
     * Retrieves flags based on their flagged status.
     *
     * @param flagged Whether to retrieve flagged or unflagged categories.
     * @return A list of flags matching the specified status.
     */
    public ArrayList<Flag> getFlags(boolean flagged) {
        ArrayList<Flag> result = new ArrayList<>();
        for (Flag flag : flags.values()) if (flag.flagged == flagged) result.add(flag);
        return result;
    }

    /**
     * Retrieves flags with a score above a specified threshold.
     *
     * @param threshold The minimum score to filter flags.
     * @return A list of flags with scores above the threshold.
     */
    public ArrayList<Flag> getFlags(double threshold) {
        ArrayList<Flag> result = new ArrayList<>();
        for (Flag flag : flags.values()) if (flag.score >= threshold) result.add(flag);
        return result;
    }

    /**
     * Retrieves flags based on their flagged status and a score threshold.
     *
     * @param flagged   Whether to retrieve flagged or unflagged categories.
     * @param threshold The minimum score to filter flags.
     * @return A list of flags matching the specified status and score threshold.
     */
    public ArrayList<Flag> getFlags(boolean flagged, double threshold) {
        ArrayList<Flag> result = new ArrayList<>();
        for (Flag flag : flags.values()) if (flag.flagged == flagged && flag.score >= threshold) result.add(flag);
        return result;
    }

    /**
     * Retrieves a specific flag based on its category.
     *
     * @param category The category of the flag to retrieve.
     * @return The flag corresponding to the specified category, or null if not found.
     */
    public Flag getFlag(Category category) {
        return flags.get(category);
    }

    public Flag getHarassment() {
        return flags.get(HARASSMENT);
    }

    public Flag getHarassmentThreatening() {
        return flags.get(HARASSMENT_THREATENING);
    }

    public Flag getHate() {
        return flags.get(HATE);
    }

    public Flag getHateThreatening() {
        return flags.get(HATE_THREATENING);
    }

    public Flag getIllicit() {
        return flags.get(ILLICIT);
    }

    public Flag getIllicitViolent() {
        return flags.get(ILLICIT_VIOLENT);
    }

    public Flag getSelfHarm() {
        return flags.get(SELF_HARM);
    }

    public Flag getSelfHarmIntent() {
        return flags.get(SELF_HARM_INTENT);
    }

    public Flag getSelfHarmInstructions() {
        return flags.get(SELF_HARM_INSTRUCTIONS);
    }

    public Flag getSexual() {
        return flags.get(SEXUAL);
    }

    public Flag getSexualMinors() {
        return flags.get(SEXUAL_MINORS);
    }

    public Flag getViolence() {
        return flags.get(VIOLENCE);
    }

    public Flag getViolenceGraphic() {
        return flags.get(VIOLENCE_GRAPHIC);
    }

    /**
     * Returns a string representation of the moderation flags and scores.
     *
     * @param data The type of data to include (ALL, POSITIVE, or NEGATIVE).
     * @return A formatted string of flagged categories and scores.
     */
    public String getData(Data data) {
        StringBuilder result = new StringBuilder();
        for (Category category : Category.values()) {
            Flag flag = flags.get(category);
            if (data.equals(Data.ALL) || (data.equals(Data.POSITIVE) && flag.flagged) || data.equals(Data.NEGATIVE))
                result.append(String.format("%s: %s", flag.category.getName(), flag.asPercentage(2))).append("%\n");
        }
        return result.toString().trim();
    }

    /**
     * Defines the type of data to retrieve from the moderation results.
     */
    public enum Data implements Serializable {
        ALL, POSITIVE, NEGATIVE
    }
}
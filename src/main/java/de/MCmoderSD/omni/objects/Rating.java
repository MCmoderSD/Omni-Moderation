package de.MCmoderSD.omni.objects;

import com.openai.models.moderations.Moderation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static de.MCmoderSD.omni.objects.Rating.Data.*;

@SuppressWarnings("ALL")
public class Rating implements Serializable {

    // Type
    private final boolean flagged;

    // Flags
    private final Flag harassment;
    private final Flag harassmentThreatening;
    private final Flag hate;
    private final Flag hateThreatening;
    private final Flag illicit;
    private final Flag illicitViolent;
    private final Flag selfHarm;
    private final Flag selfHarmInstructions;
    private final Flag selfHarmIntent;
    private final Flag sexual;
    private final Flag sexualMinors;
    private final Flag violence;
    private final Flag violenceGraphic;

    // Constructor
    public Rating(Moderation moderation) {

        // Extract Data
        var flags = moderation.categories();
        var scores = moderation.categoryScores();

        // Set Type
        flagged = moderation.flagged();

        // Set Flags
        harassment = new Flag(flags.harassment(), scores.harassment());
        harassmentThreatening = new Flag(flags.harassmentThreatening(), scores.harassmentThreatening());
        hate = new Flag(flags.hate(), scores.hate());
        hateThreatening = new Flag(flags.hateThreatening(), scores.hateThreatening());
        illicit = new Flag(flags.illicit().orElse(false), flags.illicit().isPresent() ? scores.illicit() : 0d);
        illicitViolent = new Flag(flags.illicitViolent().orElse(false), flags.illicitViolent().isPresent() ? scores.illicitViolent() : 0d);
        selfHarm = new Flag(flags.selfHarm(), scores.selfHarm());
        selfHarmInstructions = new Flag(flags.selfHarmInstructions(), scores.selfHarmInstructions());
        selfHarmIntent = new Flag(flags.selfHarmIntent(), scores.selfHarmIntent());
        sexual = new Flag(flags.sexual(), scores.sexual());
        sexualMinors = new Flag(flags.sexualMinors(), scores.sexualMinors());
        violence = new Flag(flags.violence(), scores.violence());
        violenceGraphic = new Flag(flags.violenceGraphic(), scores.violenceGraphic());
    }

    // Flag Record
    public record Flag(boolean flagged, double score) implements Serializable {
        public BigDecimal asPercentage(int scale) {
            return BigDecimal.valueOf(score).movePointRight(2).setScale(scale, RoundingMode.HALF_UP);
        }
    }

    // Getter
    public boolean isFlagged() {
        return flagged;
    }

    // Flags
    public Flag getHarassment() {
        return harassment;
    }

    public Flag getHarassmentThreatening() {
        return harassmentThreatening;
    }

    public Flag getHate() {
        return hate;
    }

    public Flag getHateThreatening() {
        return hateThreatening;
    }

    public Flag getIllicit() {
        return illicit;
    }

    public Flag getIllicitViolent() {
        return illicitViolent;
    }

    public Flag getSelfHarm() {
        return selfHarm;
    }

    public Flag getSelfHarmInstructions() {
        return selfHarmInstructions;
    }

    public Flag getSelfHarmIntent() {
        return selfHarmIntent;
    }

    public Flag getSexual() {
        return sexual;
    }

    public Flag getSexualMinors() {
        return sexualMinors;
    }

    public Flag getViolence() {
        return violence;
    }

    public Flag getViolenceGraphic() {
        return violenceGraphic;
    }

    // Print Data
    public String getData(Data data) {
        StringBuilder output = new StringBuilder();
        if (inScope(harassment, data)) output.append("Harassment: ").append(harassment.asPercentage(2)).append("%\n");
        if (inScope(harassmentThreatening, data)) output.append("Harassment Threatening: ").append(harassmentThreatening.asPercentage(2)).append("%\n");
        if (inScope(hate, data)) output.append("Hate: ").append(hate.asPercentage(2)).append("%\n");
        if (inScope(hateThreatening, data)) output.append("Hate Threatening: ").append(hateThreatening.asPercentage(2)).append("%\n");
        if (inScope(illicit, data)) output.append("Illicit: ").append(illicit.asPercentage(2)).append("%\n");
        if (inScope(illicitViolent, data)) output.append("Illicit Violent: ").append(illicitViolent.asPercentage(2)).append("%\n");
        if (inScope(selfHarm, data)) output.append("Self Harm: ").append(selfHarm.asPercentage(2)).append("%\n");
        if (inScope(selfHarmInstructions, data)) output.append("Self Harm Instructions: ").append(selfHarmInstructions.asPercentage(2)).append("%\n");
        if (inScope(selfHarmIntent, data)) output.append("Self Harm Intent: ").append(selfHarmIntent.asPercentage(2)).append("%\n");
        if (inScope(sexual, data)) output.append("Sexual: ").append(sexual.asPercentage(2)).append("%\n");
        if (inScope(sexualMinors, data)) output.append("Sexual Minors: ").append(sexualMinors.asPercentage(2)).append("%\n");
        if (inScope(violence, data)) output.append("Violence: ").append(violence.asPercentage(2)).append("%\n");
        if (inScope(violenceGraphic, data)) output.append("Violence Graphic: ").append(violenceGraphic.asPercentage(2)).append("%\n");
        return output.toString();
    }

    // Helper Method
    private static boolean inScope(Flag flag, Data data) {
        return data.equals(ALL) || (data.equals(POSITIVE) && flag.flagged) || data.equals(NEGATIVE);
    }

    // Data Enum
    public enum Data {
        ALL, POSITIVE, NEGATIVE
    }

    @Override
    public int hashCode() {
        return Objects.hash(flagged, harassment, harassmentThreatening, hate, hateThreatening, illicit, illicitViolent, selfHarm, selfHarmInstructions, selfHarmIntent, sexual, sexualMinors, violence, violenceGraphic);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass() && hashCode() == obj.hashCode();
    }
}
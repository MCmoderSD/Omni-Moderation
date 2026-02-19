package de.MCmoderSD.omni;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.moderations.*;
import com.openai.services.blocking.ModerationService;
import de.MCmoderSD.imageloader.tools.ImageEncoder;
import de.MCmoderSD.omni.objects.Rating;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;

import static com.openai.models.moderations.ModerationModel.*;
import static de.MCmoderSD.imageloader.enums.Extension.*;

@SuppressWarnings("unused")
public class Moderator {

    // Constants
    public static final ModerationModel MODEL = OMNI_MODERATION_LATEST;

    // Attributes
    private final ModerationService service;

    // Constructor
    public Moderator(String apiKey, @Nullable String organizationId, @Nullable String projectId, @Nullable String baseUrl) {
        var builder = OpenAIOkHttpClient.builder().apiKey(apiKey);
        if (organizationId != null && !organizationId.isBlank()) builder.organization(organizationId);
        if (projectId != null && !projectId.isBlank()) builder.project(projectId);
        if (baseUrl != null && !baseUrl.isBlank()) builder.baseUrl(baseUrl);
        service = builder.build().moderations();
    }

    public Moderator(String apiKey, String organizationId, String projectId) {
        this(apiKey, organizationId, projectId, null);
    }

    public Moderator(String apiKey, String baseUrl) {
        this(apiKey, null, null, baseUrl);
    }

    public Moderator(String apiKey) {
        this(apiKey, null, null);
    }

    // Encode Inputs
    private static ModerationMultiModalInput encode(String text) {
        return ModerationMultiModalInput.ofText(ModerationTextInput
                .builder()
                .text(text)
                .build()
        );
    }

    private static ModerationMultiModalInput encode(BufferedImage image) {
        return ModerationMultiModalInput.ofImageUrl(ModerationImageUrlInput
                .builder()
                .imageUrl(ModerationImageUrlInput
                        .ImageUrl
                        .builder()
                        .url(ImageEncoder.toBase64(image, PNG))
                        .build()
                ).build()
        );
    }

    // Build Parameters
    private static ModerationCreateParams buildParams(ModerationMultiModalInput... inputs) {
        return ModerationCreateParams.builder()
                .model(MODEL)
                .inputOfModerationMultiModalArray(List.of(inputs))
                .build();
    }

    private static ModerationCreateParams buildParams(String... input) {
        return ModerationCreateParams.builder()
                .model(MODEL)
                .inputOfStrings(List.of(input))
                .build();
    }

    // Moderation Text
    public Rating moderate(String text) {
        return moderate(new String[]{ text }).get(text);
    }

    // Moderation Multiple Texts
    public LinkedHashMap<String, Rating> moderate(String... text) {

        // Create HashMap to store ratings
        LinkedHashMap<String, Rating> ratings = new LinkedHashMap<>(text.length);

        // Create Moderation Request
        var request = buildParams(text);

        // Call Moderation Service
        var response = service.create(request);

        // Iterate through results and store in HashMap
        for (var i = 0; i < response.results().size(); i++) ratings.put(text[i], new Rating(response.results().get(i)));

        // Return the HashMap containing ratings
        return ratings;
    }

    // Moderation Image
    public Rating moderate(BufferedImage image) {

        // Create Moderation Request
        var request = buildParams(encode(image));

        // Call Moderation Service
        var response = service.create(request);

        // Parse Rating and return
        return new Rating(response.results().getFirst());
    }

    // Moderation Multi-Modal
    public Rating moderate(BufferedImage image, String... text) {

        // Encode inputs
        ModerationMultiModalInput[] inputs = new ModerationMultiModalInput[text.length + 1];
        for (var i = 0; i < text.length; i++) inputs[i + 1] = encode(text[i]);
        inputs[0] = encode(image);

        // Create Moderation Request
        var request = buildParams(inputs);

        // Call Moderation Service
        var response = service.create(request);

        // Parse Rating and return
        return new Rating(response.results().getFirst());
    }
}
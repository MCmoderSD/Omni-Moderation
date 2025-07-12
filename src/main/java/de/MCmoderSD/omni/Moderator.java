package de.MCmoderSD.omni;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.moderations.ModerationCreateParams;
import com.openai.models.moderations.ModerationImageUrlInput;
import com.openai.models.moderations.ModerationMultiModalInput;
import com.openai.models.moderations.ModerationTextInput;
import com.openai.models.moderations.ModerationModel;
import com.openai.services.blocking.ModerationService;

import de.MCmoderSD.imageloader.enums.Extension;
import de.MCmoderSD.imageloader.tools.ImageEncoder;
import de.MCmoderSD.omni.objects.Rating;

import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class Moderator {

    // Constants
    public static final ModerationModel MODEL = ModerationModel.OMNI_MODERATION_LATEST;

    // Attributes
    private final ModerationService service;

    /**
     * Constructs a Moderator using a given OpenAIClient.
     *
     * @param client the OpenAI client instance to use
     */
    public Moderator(OpenAIClient client) {
        service = client.moderations();
    }

    /**
     * Constructs a Moderator using only an API key.
     *
     * @param apiKey the OpenAI API key
     */
    public Moderator(String apiKey) {
        this(apiKey, null, null);
    }

    /**
     * Constructs a Moderator with optional project and organization.
     *
     * @param apiKey       the OpenAI API key
     * @param project      optional project ID
     * @param organization optional organization ID
     */
    public Moderator(String apiKey, @Nullable String project, @Nullable String organization) {
        var builder = OpenAIOkHttpClient.builder().apiKey(apiKey);
        if (project != null && !project.isBlank()) builder.project(project);
        if (organization != null && !organization.isBlank()) builder.organization(organization);
        service = builder.build().moderations();
    }

    /**
     * Encodes a plain text into a ModerationMultiModalInput.
     *
     * @param text the text to encode
     * @return a ModerationMultiModalInput representing the text
     */
    private static ModerationMultiModalInput encode(String text) {
        return ModerationMultiModalInput.ofText(ModerationTextInput
                .builder()
                .text(text)
                .build()
        );
    }

    /**
     * Encodes an image into a ModerationMultiModalInput.
     *
     * @param image the BufferedImage to encode
     * @return a ModerationMultiModalInput representing the image
     * @throws IOException if encoding the image fails
     */
    private static ModerationMultiModalInput encode(BufferedImage image) throws IOException {
        return ModerationMultiModalInput.ofImageUrl(ModerationImageUrlInput
                .builder()
                .imageUrl(ModerationImageUrlInput
                        .ImageUrl
                        .builder()
                        .url(ImageEncoder.toBase64(image, Extension.PNG))
                        .build()
                ).build()
        );
    }

    /**
     * Creates a ModerationCreateParams.Input from multiple multi-modal inputs.
     *
     * @param inputs an array of ModerationMultiModalInput
     * @return the constructed ModerationCreateParams.Input
     */
    private static ModerationCreateParams.Input createInput(ModerationMultiModalInput... inputs) {
        return ModerationCreateParams.Input.ofModerationMultiModalArray(Arrays.asList(inputs));
    }

    /**
     * Creates a ModerationCreateParams using a given input.
     *
     * @param input the input to use for moderation
     * @return the created ModerationCreateParams
     */
    private static ModerationCreateParams createParams(ModerationCreateParams.Input input) {
        return ModerationCreateParams.builder().model(MODEL).input(input).build();
    }

    /**
     * Creates a ModerationCreateParams using multiple input strings.
     *
     * @param input the input strings to moderate
     * @return the created ModerationCreateParams
     */
    private static ModerationCreateParams createParams(String... input) {
        return ModerationCreateParams.builder().model(MODEL).inputOfStrings(Arrays.stream(input).toList()).build();
    }

    /**
     * Creates a ModerationCreateParams using a single input string.
     *
     * @param input the input string to moderate
     * @return the created ModerationCreateParams
     */
    private static ModerationCreateParams createParams(String input) {
        return ModerationCreateParams.builder().model(MODEL).input(input).build();
    }

    /**
     * Moderates a single text input and returns the result as a Rating.
     *
     * @param text the text to moderate
     * @return the moderation result as a Rating
     */
    public Rating moderate(String text) {

        // Create Moderation Request
        var request = createParams(text);

        // Call Moderation Service
        var response = service.create(request);

        // Parse Rating and return
        return new Rating(response.results().getFirst());
    }

    /**
     * Moderates multiple text inputs and returns the results in a HashMap.
     *
     * @param text the array of texts to moderate
     * @return a HashMap of text to corresponding Rating
     */
    public HashMap<String, Rating> moderate(String... text) {

        // Create HashMap to store ratings
        HashMap<String, Rating> ratings = new HashMap<>();

        // Create Moderation Request
        var request = createParams(text);

        // Call Moderation Service
        var response = service.create(request);

        // Iterate through results and store in HashMap
        for (var i = 0; i < response.results().size(); i++) ratings.put(text[i], new Rating(response.results().get(i)));

        // Return the HashMap containing ratings
        return ratings;
    }

    /**
     * Moderates an image input and returns the result as a Rating.
     *
     * @param image the image to moderate
     * @return the moderation result as a Rating
     * @throws IOException if encoding the image fails
     */
    public Rating moderate(BufferedImage image) throws IOException {

        // Create Moderation Request
        var request = createParams(createInput(encode(image)));

        // Call Moderation Service
        var response = service.create(request);

        // Parse Rating and return
        return new Rating(response.results().getFirst());
    }

    /**
     * Moderates both text and image input and returns the result as a Rating.
     *
     * @param text  the text to moderate
     * @param image the image to moderate
     * @return the moderation result as a Rating
     * @throws IOException if encoding the image fails
     */
    public Rating moderate(String text, BufferedImage image) throws IOException {

        // Create Moderation Request
        var request = createParams(createInput(encode(text), encode(image)));

        // Call Moderation Service
        var response = service.create(request);

        // Parse Rating and return
        return new Rating(response.results().getFirst());
    }
}
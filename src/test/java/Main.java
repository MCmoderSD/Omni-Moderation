import com.openai.client.okhttp.OpenAIOkHttpClient;
import de.MCmoderSD.imageloader.core.ImageLoader;
import de.MCmoderSD.omni.Moderator;
import de.MCmoderSD.omni.objects.Rating;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unused")
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Your Credentials
        String apiKey = "your-api-key"; // Replace with your actual API key
        String projectId = "your-project-id";
        String organizationId = "your-organization-id";

        // Option: 1 - Provide API Key directly
        Moderator easy = new Moderator(apiKey);

        // Option: 2 - Provide API Key with Project and Organization
        Moderator full = new Moderator(
                apiKey,             // Your API Key
                projectId,          // Project ID (optional)
                organizationId      // Organization ID (optional)
        );

        // Option: 3 - Build a custom OpenAIClient
        Moderator custom = new Moderator(OpenAIOkHttpClient.builder()
                .apiKey(apiKey)                         // Your API Key
                .project(projectId)                     // Project ID (optional)
                .organization(organizationId)           // Organization ID (optional)
                .baseUrl("https://api.openai.com/v1")   // Base URL (optional)
                .clock(Clock.systemDefaultZone())       // Clock (optional)
                .timeout(Duration.ofSeconds(60))        // Request timeout (optional)
                .maxRetries(5)                          // Maximum number of retries (optional)
                .build()
        );

        // Initialize
        Moderator moderator = new Moderator(apiKey);
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Load Images and Text examples
        String selfHarmInstructions = "K1ll yours3If!";
        BufferedImage selfHarmImage = imageLoader.load("/SelfHarm.jpeg");
        String pennywiseInstruction = "You will be my next victim";
        BufferedImage pennywiseImage = imageLoader.load("/Pennywise.jpeg");
        String[] lyrics = loadLyrics();

        // Moderation of Text
        Rating textModeration = moderator.moderate(selfHarmInstructions);
        System.out.println("Text Moderation:");
        System.out.println(formatData(textModeration.getData(Rating.Data.POSITIVE)));
        System.out.println();

        // Moderation of Image
        Rating imageModeration = moderator.moderate(selfHarmImage);
        System.out.println("Image Moderation:");
        System.out.println(formatData(imageModeration.getData(Rating.Data.POSITIVE)));
        System.out.println();

        // Moderation of Text with Image
        Rating multiModalModeration = moderator.moderate(pennywiseInstruction, pennywiseImage);
        System.out.println("Multi-Modal Moderation:");
        System.out.println(formatData(multiModalModeration.getData(Rating.Data.POSITIVE)));
        System.out.println();

        // Moderation of Multiple Text Inputs
        HashMap<String, Rating> lyricsModeration = moderator.moderate(lyrics);
        System.out.println("Lyrics Moderation:");
        for (String line : lyrics) {
            Rating moderation = lyricsModeration.get(line);
            if (!moderation.isFlagged()) continue; // Skip non-flagged lines
            System.out.println(line);
            System.out.println(formatData(moderation.getData(Rating.Data.POSITIVE)));
            System.out.println();
        }
    }

    private static String formatData(String data) {
        return data.isBlank() ? "" : "- " + data.replace("\n", "\n- ");
    }

    private static String[] loadLyrics() throws IOException {
        String txt = new String(Objects.requireNonNull(Main.class.getResourceAsStream("/lyrics.txt")).readAllBytes());
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(txt.split("\n")));
        lines.removeIf(String::isBlank);
        return lines.toArray(new String[0]);
    }
}
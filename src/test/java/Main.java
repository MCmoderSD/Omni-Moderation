import de.MCmoderSD.imageloader.core.ImageLoader;

import de.MCmoderSD.omni.Moderator;
import de.MCmoderSD.omni.objects.Rating;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import static de.MCmoderSD.omni.objects.Rating.Data.*;

void main() {

    // Your Credentials
    String apiKey = "your-api-key"; // Replace with your actual API key
    String organizationId = "your-organization-id";
    String projectId = "your-project-id";
    String baseUrl = "https://api.openai.com/v1";

    // Initialize Moderator
    Moderator moderator = new Moderator(
            apiKey,         // Your API Key
            organizationId, // Project ID (optional)
            projectId,      // Organization ID (optional)
            baseUrl         // Base URL (optional)
    );

    // Load Images and Text examples
    ImageLoader imageLoader = ImageLoader.getInstance();
    String pennywiseInstruction = "You will be my next victim";
    BufferedImage pennywiseImage = imageLoader.loadResource("/Pennywise.jpeg");

    // Text Moderation
    Rating textModeration = moderator.moderate("K1ll yours3If!");
    IO.println("Text Moderation:");
    IO.println(formatData(textModeration.getData(POSITIVE)) + "\n");

    // Image Moderation
    Rating imageModeration = moderator.moderate(imageLoader.loadResource("/SelfHarm.jpeg"));
    IO.println("Image Moderation:");
    IO.println(formatData(imageModeration.getData(POSITIVE)) + "\n");

    // Multi-Modal Moderation
    Rating multiModalModeration = moderator.moderate(
            pennywiseImage,         // Image Input
            pennywiseInstruction    // Text Input
    );
    IO.println("Multi-Modal Moderation:");
    IO.println(formatData(multiModalModeration.getData(POSITIVE)) + "\n");

    // Moderation of Multiple Text Inputs
    LinkedHashMap<String, Rating> lyricsModeration = moderator.moderate(loadLyrics());
    IO.println("Lyrics Moderation:");
    for (var line : lyricsModeration.keySet()) {
        Rating moderation = lyricsModeration.get(line);
        if (!moderation.isFlagged()) continue; // Skip non-flagged lines
        IO.println(line);
        IO.println(formatData(moderation.getData(POSITIVE)) + "\n");
    }
}

// Helper Methods
private static String formatData(String data) {
    return data.isBlank() ? "" : "- " + data.trim().replace("\n", "\n- ");
}

private static String[] loadLyrics() {
    try {
        ArrayList<String> lines = new ArrayList<>(Files.readAllLines(Path.of("src/test/resources/lyrics.txt")));
        ArrayList<String> lyrics = new ArrayList<>();
        for (String line : lines) if (!line.isBlank()) lyrics.add(line);
        return lyrics.toArray(new String[0]);
    }  catch (IOException e) {
        throw new RuntimeException("Failed to load lyrics", e);
    }
}
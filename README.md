# Omni Moderation

## Description
Omni Moderation is a **Java Wrapper** for the OpenAI Moderation API, designed to simplify the process of moderating text and images using OpenAI's powerful AI models.
It allows developers to easily integrate content moderation capabilities into their applications and manage potentially harmful content effectively. <br>

## Requirements

You need to obtain an API key from [OpenAI](https://platform.openai.com/signup) to use this tool. <br>
The Project ID and Organization ID are optional but recommended for better tracking and management of your API usage. <br>
The IDs can be obtained from the [OpenAI dashboard](https://platform.openai.com/settings/organization/general). <br>
The OpenAI Moderation API is free to use, but there are usage limits. Please refer to the [OpenAI Documentation](https://platform.openai.com/docs/models/omni-moderation-latest).

## Supported Features
- **Text Moderation**: Analyze and moderate text content for harmful or inappropriate language.
- **Image Moderation**: Analyze and moderate images for harmful or inappropriate content.
- **Multi-Modal Moderation**: Analyze and moderate both text and images together.
- **Batch Moderation**: Moderate multiple text inputs in a single request.


## Moderation Categories
| Category                 | Description                                                                                                                                                                                                                                    | Inputs          |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|
| `harassment`             | Content that expresses, incites, or promotes harassing language towards any target.                                                                                                                                                            | Text only       |
| `harassment/threatening` | Harassment content that also includes violence or serious harm towards any target.                                                                                                                                                             | Text only       |
| `hate`                   | Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual orientation, disability status, or caste. Hateful content aimed at non-protected groups (e.g., chess players) is harassment. | Text only       |
| `hate/threatening`       | Hateful content that also includes violence or serious harm towards the targeted group based on race, gender, ethnicity, religion, nationality, sexual orientation, disability status, or caste.                                               | Text only       |
| `illicit`                | Content that gives advice or instruction on how to commit illicit acts. A phrase like "how to shoplift" would fit this category.                                                                                                               | Text only       |
| `illicit/violent`        | The same types of content flagged by the `illicit` category, but also includes references to violence or procuring a weapon.                                                                                                                   | Text only       |
| `self-harm`              | Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders.                                                                                                                               | Text and images |
| `self-harm/intent`       | Content where the speaker expresses that they are engaging or intend to engage in acts of self-harm, such as suicide, cutting, and eating disorders.                                                                                           | Text and images |
| `self-harm/instructions` | Content that encourages performing acts of self-harm, such as suicide, cutting, and eating disorders, or that gives instructions or advice on how to commit such acts.                                                                         | Text and images |
| `sexual`                 | Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual services (excluding sex education and wellness).                                                                                | Text and images |
| `sexual/minors`          | Sexual content that includes an individual who is under 18 years old.                                                                                                                                                                          | Text only       |
| `violence`               | Content that depicts death, violence, or physical injury.                                                                                                                                                                                      | Text and images |
| `violence/graphic`       | Content that depicts death, violence, or physical injury in graphic detail.                                                                                                                                                                    | Text and images |


## Usage

### Maven
Make sure you have my Sonatype Nexus OSS repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>Nexus</id>
        <name>Sonatype Nexus</name>
        <url>https://mcmodersd.de/nexus/repository/maven-releases/</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>de.MCmoderSD</groupId>
    <artifactId>Omni-Moderation</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Usage Example
```java
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
```

### Chat Example
```java
import de.MCmoderSD.omni.objects.Rating;
import de.MCmoderSD.omni.Moderator;

void main() {

    // Initialize Moderator
    Moderator moderator = new Moderator("your-api-key"); // Replace with your actual API key

    // Input
    String input;

    IO.println("User Input:");
    while (!(input = IO.readln()).equalsIgnoreCase("exit")) {

        // Prompt
        Rating rating = moderator.moderate(input);

        // Print Rating
        IO.println(formatData(rating.getData(Rating.Data.POSITIVE)));

        // User Input
        IO.println("\nUser Input:");
    }
}

// Helper Method
private static String formatData(String data) {
    return data.isBlank() ? "" : "- " + data.trim().replace("\n", "\n- ");
}
```
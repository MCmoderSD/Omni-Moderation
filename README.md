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
    <version>1.0.1</version>
</dependency>
```

### Usage Example
```java
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
```

### Chat Example
```java
import de.MCmoderSD.omni.Moderator;
import de.MCmoderSD.omni.objects.Rating;

import java.util.Scanner;

public class ChatExample {

    public static void main(String[] args) {

        // Initialize Moderator
        Moderator moderator = new Moderator("your-api-key"); // Replace with your actual API key

        // Scanner for user input
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("User Input:");
        while (!(input = scanner.nextLine()).equalsIgnoreCase("exit")) {

            // Prompt
            Rating rating = moderator.moderate(input);

            // Print Rating
            System.out.println(formatData(rating.getData(Rating.Data.POSITIVE)));

            // User Input
            System.out.println("\nUser Input:");
        }
    }

    private static String formatData(String data) {
        return data.isBlank() ? "" : "- " + data.replace("\n", "\n- ");
    }
}
```
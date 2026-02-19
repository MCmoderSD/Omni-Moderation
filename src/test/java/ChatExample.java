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
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
import java.util.Random;
import java.util.Scanner;

public class Proj1 {

    public static void main(String[] args) {
        // Create an instance of the Random class
        Random random = new Random();
        
        // Generate a random number between 1 and 100 (inclusive)
        int randomNumber = random.nextInt(100) + 1;  // Random number between 1 and 100
        
        // Create a Scanner object to get user input
        Scanner scanner = new Scanner(System.in);
        
        // Variables for user input and number of attempts
        int guess;
        int numberOfAttempts = 0;
        int maxAttempts = 10;  // Limit to 10 attempts for this example

        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("I'm thinking of a number between 1 and 100. Can you guess it?");
        
        // Game loop: continue until the correct number is guessed or attempts run out
        while (numberOfAttempts < maxAttempts) {
            System.out.print("Enter your guess: ");
            guess = scanner.nextInt();
            numberOfAttempts++;

            // Check if the guess is correct
            if (guess == randomNumber) {
                System.out.println("Congratulations! You guessed the number in " + numberOfAttempts + " attempts.");
                break;
            } else if (guess > randomNumber) {
                System.out.println("Too high! Try again.");
            } else {
                System.out.println("Too low! Try again.");
            }
        }

        // If the number was not guessed in the given attempts, reveal the number
        if (numberOfAttempts == maxAttempts) {
            System.out.println("Sorry, you've used all your attempts. The correct number was " + randomNumber + ".");
        }

        // Close the scanner to prevent resource leak
        scanner.close();
    }
}


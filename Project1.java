import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class Project1 {
    private static SecretKey aesKey;

    static {
        try {
            // Load existing AES key if available, otherwise generate a new one
            if (new File("aes.key").exists()) {
                byte[] keyBytes = Files.readAllBytes(Paths.get("aes.key"));
                aesKey = new SecretKeySpec(keyBytes, "AES");
            } else {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                aesKey = keyGen.generateKey();
                saveKey(); // Save the generated key for future use
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Encryption-Decryption Tool ===");
            System.out.println("1. Encrypt a file using Caesar Cipher");
            System.out.println("2. Decrypt a file using Caesar Cipher");
            System.out.println("3. Encrypt a file using AES");
            System.out.println("4. Decrypt a file using AES");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1 -> processFile("encrypt", "caesar", scanner);
                case 2 -> processFile("decrypt", "caesar", scanner);
                case 3 -> processFile("encrypt", "aes", scanner);
                case 4 -> processFile("decrypt", "aes", scanner);
                case 5 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Try again!");
            }
        } while (choice != 5);

        scanner.close();
    }

    private static void processFile(String operation, String algorithm, Scanner scanner) {
        System.out.print("Enter the input file path: ");
        String inputPath = scanner.nextLine();

        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.out.println("Error: Input file does not exist or is not a valid file.");
            return;
        }

        // Generate output file path
        String outputPath = generateOutputFilePath(inputPath, operation, algorithm);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = switch (algorithm) {
                    case "caesar" -> {
                        System.out.print("Enter the Caesar Cipher key (integer): ");
                        int key = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline
                        yield operation.equals("encrypt") ? CaesarCipher.encrypt(line, key) : CaesarCipher.decrypt(line, key);
                    }
                    case "aes" -> {
                        yield operation.equals("encrypt") ? AES.encrypt(line) : AES.decrypt(line);
                    }
                    default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
                };
                writer.write(processedLine);
                writer.newLine();
            }
            System.out.println("Operation completed successfully. Output saved to: " + outputPath);

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static String generateOutputFilePath(String inputPath, String operation, String algorithm) {
        String extension = operation.equals("encrypt") ? ".enc" : ".dec";
        return inputPath + "_" + algorithm + extension;
    }

    // Caesar Cipher Implementation
    static class CaesarCipher {
        public static String encrypt(String text, int key) {
            StringBuilder result = new StringBuilder();
            for (char c : text.toCharArray()) {
                if (Character.isLetter(c)) {
                    char base = Character.isLowerCase(c) ? 'a' : 'A';
                    result.append((char) ((c - base + key) % 26 + base));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }

        public static String decrypt(String text, int key) {
            return encrypt(text, 26 - key);
        }
    }

    // AES Encryption and Decryption Implementation
    static class AES {
        public static String encrypt(String text) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        public static String decrypt(String encryptedText) throws Exception {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        }
    }

    // Save AES key to a file for future use
    private static void saveKey() throws IOException {
        try (FileOutputStream fos = new FileOutputStream("aes.key")) {
            fos.write(aesKey.getEncoded());
        }
    }
}

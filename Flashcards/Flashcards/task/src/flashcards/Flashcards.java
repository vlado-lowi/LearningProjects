package flashcards;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class Flashcards {
    private final LinkedHashMap<String, String> termsAndDefinitions;
    private final Scanner scanner;

    public Flashcards() {
        this.termsAndDefinitions = new LinkedHashMap<>();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        Action userAction = null;
        while(!Objects.equals(userAction, Action.EXIT)) {
            userAction = getActionFromUser();
            switch (userAction) {
                case ADD:
                    addCard();
                    break;
                case REMOVE:
                    removeCard();
                    break;
                case IMPORT:
                    importCardsFromFile();
                    break;
                case EXPORT:
                    exportCardsToFile();
                    break;
                case ASK:
                    askUserAboutCards();
                    break;
                default: // Exit
                    scanner.close();
                    System.out.println("Bye bye!");
                    break;
            }
        }
    }

    private void askUserAboutCards() {
        System.out.println("How many times to ask?");
        int timesToAsk = Integer.parseInt(scanner.nextLine().trim());
        String cardTerm;
        String cardDefinition;
        String userAnswer;
        ArrayList<String> helperArray = new ArrayList<>(termsAndDefinitions.keySet());
        Random random = new Random();
        int randomCardNumber;

        for (int i = 0; i < timesToAsk; i++) {
            randomCardNumber = random.nextInt(termsAndDefinitions.size()); // random number of card
            cardTerm = helperArray.get(randomCardNumber); // get the term from helper array (containing keys)
            cardDefinition = termsAndDefinitions.get(cardTerm); // get the definition of term
            System.out.printf("Print the definition of \"%s\":%n", cardTerm);
            userAnswer = scanner.nextLine().trim();
            if (Objects.equals(cardDefinition, userAnswer)) {
                System.out.println("Correct answer.");
            } else if (termsAndDefinitions.containsValue(userAnswer)) {
                System.out.printf("Wrong answer. The correct one is \"%s\", you've just" +
                                " written the definition of \"%s\".\n",
                        cardDefinition, getKey(termsAndDefinitions, userAnswer));
            } else {
                System.out.printf("Wrong answer. The correct one is \"%s\".\n",
                        cardDefinition);
            }
        }
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /*
    Exported file has the following format:
    first line: number n -> number of cards in the file
    next 2 * n lines: terms and definitions on separate lines eg.:
    ----------------------
    15
    card term 1
    card definition 1
    ...
    card term 15
    card definition 15
    ----------------------
     */
    private void exportCardsToFile() {
        System.out.println("File name:");
        String fileName = scanner.nextLine().trim();
        try (PrintWriter printWriter = new PrintWriter(fileName)) {
            printWriter.println(termsAndDefinitions.size());
            termsAndDefinitions.forEach((cardTerm,cardDefinition) -> {
                printWriter.println(cardTerm);
                printWriter.println(cardDefinition);
            });
            System.out.println(termsAndDefinitions.size() + " cards have been saved.");
        } catch (Exception e) {
            System.err.printf("Error while trying to export cards to file: '%s'.%n",fileName);
            System.err.println("Error msg: " + e.getMessage());
        }
    }

    /*
    Imported file must have following format:
    first line: number n -> number of cards in the file
    next 2 * n lines: terms and definitions on separate lines eg.:
    ----------------------
    15
    card term 1
    card definition 1
    ...
    card term 15
    card definition 15
    ----------------------
     */
    private void importCardsFromFile() {
        System.out.println("File name:");
        String fileName = scanner.nextLine().trim();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int cardsToRead = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < cardsToRead; i++) {
                String cardTerm = br.readLine().trim();
                String cardDefinition = br.readLine().trim();
                termsAndDefinitions.put(cardTerm, cardDefinition);
            }
            System.out.println(cardsToRead + " cards have been loaded.");
        } catch (Exception e) {
            System.out.println("File not found.");
            System.err.printf("Error while trying to import cards from file: '%s'.%n",fileName);
            System.err.println("Error msg: " + e.getMessage());
        }
    }

    private void removeCard() {
        System.out.println("The card:");
        String cardToDelete = scanner.nextLine().trim();
        if (termsAndDefinitions.containsKey(cardToDelete)) {
            termsAndDefinitions.remove(cardToDelete);
            System.out.println("The card has been removed.");
        } else {
            System.out.printf("Can't remove \"%s\": there is no such card.%n", cardToDelete);
        }
    }

    public void addCard() {
        System.out.println("The card:");
        String cardTerm = scanner.nextLine().trim();
        if (termsAndDefinitions.containsKey(cardTerm)) {
            System.out.printf("The card \"%s\" already exists.%n", cardTerm);
            return;
        }
        System.out.println("The definition:");
        String cardDefinition = scanner.nextLine().trim();
        if (termsAndDefinitions.containsValue(cardDefinition)) {
            System.out.printf("The definition \"%s\" already exists.%n", cardDefinition);
            return;
        }
        termsAndDefinitions.put(cardTerm, cardDefinition);
        System.out.printf("The pair (\"%s\":\"%s\") has been added.%n", cardTerm, cardDefinition);
    }

    private Action getActionFromUser() {
        boolean validAction = false;
        Action userAction = null;
        while (!validAction) {
            System.out.println("Input the action (add, remove, import, export, ask, exit):");
            String userInput = scanner.nextLine().trim().toLowerCase();
            switch (userInput) {
                case "add":
                    userAction = Action.ADD;
                    validAction = true;
                    break;
                case "remove":
                    userAction = Action.REMOVE;
                    validAction = true;
                    break;
                case "ask":
                    userAction = Action.ASK;
                    validAction = true;
                    break;
                case "import":
                    userAction = Action.IMPORT;
                    validAction = true;
                    break;
                case "export":
                    userAction = Action.EXPORT;
                    validAction = true;
                    break;
                case "exit":
                    userAction = Action.EXIT;
                    validAction = true;
                    break;
                default:
                    System.out.printf("Invalid action \"%s\" please try again.\n", userInput);
                    break;
            }
        }
        return userAction;
    }
}


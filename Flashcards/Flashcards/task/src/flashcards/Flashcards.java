package flashcards;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class Flashcards {
    private final LinkedHashMap<String, String> termsAndDefinitions;
    private final LinkedHashMap<String, Integer> cardsAndErrors;
    private final Scanner scanner;
    private final ArrayList<String> logsArray;

    public Flashcards() {
        this.termsAndDefinitions = new LinkedHashMap<>();
        this.cardsAndErrors = new LinkedHashMap<>();
        this.scanner = new Scanner(System.in);
        this.logsArray = new ArrayList<>();
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
                case LOG:
                    exportLogToFile();
                    break;
                case HARDEST_CARD:
                    displayHardestCards();
                    break;
                case RESET_STATS:
                    resetStats();
                    break;
                default: // Exit
                    scanner.close();
                    printlnAndLogIt("Bye bye!");
                    break;
            }
        }
    }

    private void resetStats() {
        cardsAndErrors.forEach((card, errors) -> cardsAndErrors.put(card, 0));
        printlnAndLogIt("Card statistics has been reset.");
    }

    private void displayHardestCards() {
        ArrayList<String> hardestCards = new ArrayList<>();
        int maxErrors = 1;
        for (Map.Entry<String, Integer> entry : cardsAndErrors.entrySet()) {
            if (entry.getValue() > maxErrors) {
                maxErrors = entry.getValue();
                hardestCards.clear();
                hardestCards.add(entry.getKey());
            } else if (entry.getValue() == maxErrors) {
                hardestCards.add(entry.getKey());
            }
        }
        if (hardestCards.isEmpty()) { // no cards with errors
            printlnAndLogIt("There are no cards with errors.");
        } else if (hardestCards.size() == 1) { // 1 card with max errors
            printlnAndLogIt(String.format("The hardest card is \"%s\". You have %d errors answering it.",
                    hardestCards.get(0), cardsAndErrors.get(hardestCards.get(0))));
        } else {
            StringBuilder sb = new StringBuilder();
            hardestCards.forEach(e -> sb.append(String.format("\"%s\", ", e)));
            int lastComma = sb.lastIndexOf(",");
            sb.replace(lastComma, sb.length(), "");
            String cards = sb.toString();
            printlnAndLogIt(String.format("The hardest cards are %s. You have %d errors answering them.",
                    cards, cardsAndErrors.get(hardestCards.get(0)))); // they have same num of errors
        }
    }

    private void exportLogToFile() {
        printlnAndLogIt("File name:");
        String fileName = nextLineAndLogIt().trim();
        try (PrintWriter pw = new PrintWriter(fileName)) {
            logsArray.forEach(pw::println);
            printlnAndLogIt("The log has been saved.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void askUserAboutCards() {
        printlnAndLogIt("How many times to ask?");
        int timesToAsk = Integer.parseInt(nextLineAndLogIt().trim());
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
            printlnAndLogIt(String.format("Print the definition of \"%s\":%n", cardTerm));
            userAnswer = nextLineAndLogIt().trim();
            if (Objects.equals(cardDefinition, userAnswer)) {
                printlnAndLogIt("Correct answer.");
            } else if (termsAndDefinitions.containsValue(userAnswer)) {
                printlnAndLogIt(String.format("Wrong answer. The correct one is \"%s\", you've just" +
                                " written the definition of \"%s\".\n",
                        cardDefinition, getKey(termsAndDefinitions, userAnswer)));
                cardsAndErrors.put(cardTerm, cardsAndErrors.get(cardTerm) + 1);
            } else {
                printlnAndLogIt(String.format("Wrong answer. The correct one is \"%s\".\n",
                        cardDefinition));
                cardsAndErrors.put(cardTerm, cardsAndErrors.get(cardTerm) + 1);
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
    next 3 * n lines:
        card term
        card definition
        number of errors guessing this card
    EXAMPLE:
    ----------------------
    15
    card 1 term
    card 1 definition
    card 1 errors
    ...
    card 15 term
    card 15 definition
    card 15 error
    ----------------------
     */
    private void exportCardsToFile() {
        printlnAndLogIt("File name:");
        String fileName = nextLineAndLogIt().trim();
        try (PrintWriter printWriter = new PrintWriter(fileName)) {
            printWriter.println(termsAndDefinitions.size());
            termsAndDefinitions.forEach((cardTerm,cardDefinition) -> {
                printWriter.println(cardTerm);
                printWriter.println(cardDefinition);
                printWriter.println(cardsAndErrors.getOrDefault(cardTerm, 0));
            });
            printlnAndLogIt(termsAndDefinitions.size() + " cards have been saved.");
        } catch (Exception e) {
            System.err.printf("Error while trying to export cards to file: '%s'.%n",fileName);
            System.err.println("Error msg: " + e.getMessage());
        }
    }

    /*
    Imported file must have following format:
    first line: number n -> number of cards in the file
    next 3 * n lines:
        card term
        card definition
        number of errors guessing this card
    EXAMPLE:
    ----------------------
    15
    card 1 term
    card 1 definition
    card 1 errors
    ...
    card 15 term
    card 15 definition
    card 15 error
    ----------------------
     */
    private void importCardsFromFile() {
        printlnAndLogIt("File name:");
        String fileName = nextLineAndLogIt().trim();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int cardsToRead = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < cardsToRead; i++) {
                String cardTerm = br.readLine().trim();
                String cardDefinition = br.readLine().trim();
                termsAndDefinitions.put(cardTerm, cardDefinition);
                String cardErrors = br.readLine().trim();
                cardsAndErrors.put(cardTerm, Integer.parseInt(cardErrors));
            }
            printlnAndLogIt(cardsToRead + " cards have been loaded.");
        } catch (Exception e) {
            printlnAndLogIt("File not found.");
            System.err.printf("Error while trying to import cards from file: '%s'.%n",fileName);
            System.err.println("Error msg: " + e.getMessage());
        }
    }

    private void removeCard() {
        printlnAndLogIt("The card:");
        String cardToDelete = nextLineAndLogIt().trim();
        if (termsAndDefinitions.containsKey(cardToDelete)) {
            termsAndDefinitions.remove(cardToDelete);
            cardsAndErrors.remove(cardToDelete);
            printlnAndLogIt("The card has been removed.");
        } else {
            printlnAndLogIt(String.format("Can't remove \"%s\": there is no such card.%n", cardToDelete));
        }
    }

    public void addCard() {
        printlnAndLogIt("The card:");
        String cardTerm = nextLineAndLogIt().trim();
        if (termsAndDefinitions.containsKey(cardTerm)) {
            printlnAndLogIt(String.format("The card \"%s\" already exists.%n", cardTerm));
            return;
        }
        printlnAndLogIt("The definition:");
        String cardDefinition = nextLineAndLogIt().trim();
        if (termsAndDefinitions.containsValue(cardDefinition)) {
            printlnAndLogIt(String.format("The definition \"%s\" already exists.%n", cardDefinition));
            return;
        }
        termsAndDefinitions.put(cardTerm, cardDefinition);
        cardsAndErrors.put(cardTerm, 0);
        printlnAndLogIt(String.format("The pair (\"%s\":\"%s\") has been added.%n", cardTerm, cardDefinition));
    }

    private Action getActionFromUser() {
        boolean validAction = false;
        Action userAction = null;
        while (!validAction) {
            printlnAndLogIt("Input the action (add, remove, import, export, ask," +
                    " exit, log, hardest card, reset stats):");
            String userInput = nextLineAndLogIt().trim().toLowerCase();
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
                case "hardest card":
                    userAction = Action.HARDEST_CARD;
                    validAction = true;
                    break;
                case "log":
                    userAction = Action.LOG;
                    validAction = true;
                    break;
                case "reset stats":
                    userAction = Action.RESET_STATS;
                    validAction = true;
                    break;
                default:
                    printlnAndLogIt(String.format("Invalid action \"%s\" please try again.\n", userInput));
                    break;
            }
        }
        return userAction;
    }

    private String nextLineAndLogIt() {
        String input = scanner.nextLine();
        logsArray.add(input);
        return input;
    }

    private void printlnAndLogIt(String line) {
        System.out.println(line);
        logsArray.add(line);
    }

}


package flashcards;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try(Scanner in = new Scanner(System.in)) {
            System.out.println("Input the number of cards:");
            int numberOfCards = Integer.parseInt(in.nextLine());

            LinkedHashMap<String, String> termsAndDefinitions =
                    new LinkedHashMap<>(numberOfCards);

            for(int i = 0 ; i < numberOfCards; i++) { // Let user input cards
                System.out.printf("The card #%d:\n", i);
                String term = in.nextLine();
                while (termsAndDefinitions.containsKey(term)) {
                    System.out.printf("The card \"%s\" already exists. Try again:\n", term);
                    term = in.nextLine();
                }
                System.out.printf("The definition of the card #%d:\n", i);
                String definition = in.nextLine();
                while (termsAndDefinitions.containsValue(definition)) {
                    System.out.printf("The definition \"%s\" already exists. Try again:\n",
                            definition);
                    definition = in.nextLine();
                }
                termsAndDefinitions.put(term, definition);
            }

            termsAndDefinitions.keySet().forEach(term -> { // Ask user about cards
                System.out.printf("Print the definition of \"%s\":\n", term);
                String answer = in.nextLine();
                if (Objects.equals(termsAndDefinitions.get(term), answer)) {
                    System.out.println("Correct answer.");
                } else if (termsAndDefinitions.containsValue(answer)) {

                    System.out.printf("Wrong answer. The correct one is \"%s\", you've just" +
                            " written the definition of \"%s\".\n",
                            termsAndDefinitions.get(term), getKey(termsAndDefinitions, answer));
                } else {
                    System.out.printf("Wrong answer. The correct one is \"%s\".\n",
                            termsAndDefinitions.get(term));
                }
            });
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
}

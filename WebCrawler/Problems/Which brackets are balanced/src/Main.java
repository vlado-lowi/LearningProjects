import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Deque<String> deque = new ArrayDeque<>();
            String string = scanner.nextLine().trim();
            for (String ch : string.split("")) {
                if (ch.equals("[") || ch.equals("{") || ch.equals("(")) {
                    deque.addFirst(ch);
                } else {
                    String fromDeque;
                    if((fromDeque = deque.pollFirst()) == null) {
                        System.out.println(false);
                        return;
                    }
                    switch (fromDeque) {
                        case "[":
                            if (!Objects.equals("]", ch)) {
                                System.out.println(false);
                                System.exit(0);
                            }
                            break;
                        case "{":
                            if (!Objects.equals("}", ch)) {
                                System.out.println(false);
                                System.exit(0);
                            }
                            break;
                        default:
                            if (!Objects.equals(")", ch)) {
                                System.out.println(false);
                                System.exit(0);
                            }
                            break;
                    }
                }
            }
            System.out.println(deque.isEmpty());
        }
    }
}
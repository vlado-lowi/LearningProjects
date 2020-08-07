import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Deque<Integer> deque = new ArrayDeque<>();
        try (Scanner scanner = new Scanner(System.in)) {
            int inputSize = scanner.nextInt();
            for (int index = 0; index < inputSize; index++) {
                deque.addFirst(scanner.nextInt());
            }
            for (int index = 0; index < inputSize; index++) {
                System.out.println(deque.removeFirst());
            }
        }
    }
}
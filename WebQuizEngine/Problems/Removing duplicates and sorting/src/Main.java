import java.util.*;

class Main {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int setSize = in.nextInt();
            Set<String> set = new TreeSet<>();
            for (int i = 0; i < setSize; i++) {
                set.add(in.next());
            }
            set.forEach(System.out::println);
        }
    }
}
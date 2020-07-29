import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int barLength = in.nextInt();
            int barHeight = in.nextInt();
            int requiredBarSize = in.nextInt();
            if (requiredBarSize % barLength == 0) {
                int requiredMultiplier = requiredBarSize / barLength;
                if (barHeight >= requiredMultiplier) System.out.println("YES");
                else System.out.println("NO");
            } else if (requiredBarSize % barHeight == 0) {
                int requiredMultiplier = requiredBarSize / barHeight;
                if (barLength >= requiredMultiplier) System.out.println("YES");
                else System.out.println("NO");
            } else System.out.println("NO");
        }
    }
}
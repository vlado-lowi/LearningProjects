import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            String[] list = in.nextLine().split(" ");
            Arrays.stream(list).forEach(e -> arrayList.add(Integer.valueOf(e)));
            int N = in.nextInt();

            int smallestDifference = Integer.MAX_VALUE;
            ArrayList<Integer> resultList = new ArrayList<>();

            for (Integer integer : arrayList) {
                if (Math.abs(integer - N) < smallestDifference) {
                    // new smallest difference
                    smallestDifference = Math.abs(integer - N);
                    resultList.clear();
                    resultList.add(integer);
                } else if (Math.abs(integer - N) == smallestDifference) {
                    resultList.add(integer);
                }
            }
            Collections.sort(resultList);
            resultList.forEach(e -> System.out.print(e + " "));
        }
    }
}
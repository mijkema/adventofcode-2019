import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Day4 {

    private static int lower = 165432;
    private static int higher = 707912;

    public static void main(String[] args) {
        // part 1
        int n = 0;
        for (int i = lower; i < higher; i++) {
            if (validPart1(i)) {
                n++;
            }

        }
        System.out.println("total (part 1): " +n);

        // part 2
        n = 0;
        for (int i = lower; i < higher; i++) {
            if (validPart2(i)) {
                n++;
            }

        }
        System.out.println("total (part 2): " +n);
    }

    private static boolean validPart1(int i) {
        char[] list = String.valueOf(i).toCharArray();
        // check for double
        boolean hasDouble = false;
        for (int j = 1; j < list.length; j++) {
            if (list[j] == list[j - 1]) {
                hasDouble = true;
                break;
            }
        }

        boolean increasing = true;
        // check for increasing
        for (int j = 1; j < list.length; j++) {
            if (list[j] < list[j - 1]) {
                increasing = false;
                break;
            }
        }
        return hasDouble && increasing;
    }

    private static boolean validPart2(int i) {
        char[] list = String.valueOf(i).toCharArray();
        // check for double
        Multiset<Character> adjacents = HashMultiset.create();
        boolean hasDouble = false;
        for (int j = 1; j < list.length; j++) {
            if (list[j] == list[j-1]) {
                hasDouble = true;
                adjacents.add(list[j]);
            }
        }

        // check that there is an explicit double
        boolean hasExplicitDouble = false;
        for (char c : adjacents) {
            if (adjacents.count(c) == 1) {
                hasExplicitDouble = true;
            }
        }

        boolean increasing = true;
        // check for increasing
        for (int j = 1; j < list.length; j++) {
            if (list[j] < list[j - 1]) {
                increasing = false;
                break;
            }
        }
        return increasing && hasDouble && hasExplicitDouble;
    }
}

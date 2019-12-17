import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Arrays;

public class Day16 {

    public static void main(String[] args) throws IOException {
        int[] inputSignal = getInput();

        // part 1
        int[] signalAfterPhases = getSignal(inputSignal);
        System.out.println(Arrays.toString(signalAfterPhases));

        // part 2
        int lengthMultiplier = 10000;
        int[] copy = Arrays.copyOf(inputSignal, inputSignal.length * lengthMultiplier);
        for (int i = 0; i < lengthMultiplier; i++) {
            System.arraycopy(copy, 0, copy, inputSignal.length * i, inputSignal.length);
        }
        int digitIndex =
                Integer.parseInt(
                        Arrays.stream(inputSignal)
                                .limit(7)
                                .mapToObj(String::valueOf)
                                .reduce(String::concat)
                                .orElseThrow());
        System.out.println(digitIndex);
        signalAfterPhases = getSignalPart2(copy, digitIndex);
        System.out.println(
                Arrays.stream(signalAfterPhases, digitIndex, digitIndex + 8)
                        .mapToObj(String::valueOf)
                        .reduce(String::concat));
    }

    private static int[] getInput() throws IOException {
        return Resources.toString(Resources.getResource("day16"), Charsets.UTF_8)
                .chars()
                .map(c -> Integer.parseInt("" + (char) c))
                .toArray();
    }

    private static int[] getSignal(int[] inputSignal) {
        int[] basePattern = new int[] {0, 1, 0, -1};
        int[] currentSignal = Arrays.copyOf(inputSignal, inputSignal.length);
        for (int iteration = 0; iteration < 100; iteration++) {
            // 1 iteration
            int[] newSignal = new int[currentSignal.length];
            for (int i = 0; i < currentSignal.length; i++) {
                int c = 0;
                for (int j = i; j < currentSignal.length; j++) {
                    int multiplier = basePattern[((j + 1) / (i + 1)) % basePattern.length];
                    if (multiplier != 0) {
                        c += currentSignal[j] * multiplier;
                    }
                }
                newSignal[i] = Math.abs(c) % 10;
            }
            currentSignal = newSignal;
        }
        return currentSignal;
    }

    private static int[] getSignalPart2(int[] inputSignal, int digitIndex) {
        int[] currentSignal = Arrays.copyOf(inputSignal, inputSignal.length);
        for (int iteration = 0; iteration < 100; iteration++) {
            int[] newSignal = new int[inputSignal.length];
            System.out.println("iteration " + iteration);
            newSignal[digitIndex] =
                    Arrays.stream(currentSignal, digitIndex, currentSignal.length).sum() % 10;
            for (int i = digitIndex + 1; i < inputSignal.length; i++) {
                newSignal[i] = (10 + newSignal[i-1] - currentSignal[i-1]) % 10;
            }
            currentSignal = newSignal;
        }

        return currentSignal;
    }
}

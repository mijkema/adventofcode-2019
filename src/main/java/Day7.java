import static java.util.stream.Collectors.toList;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day7 {

    public static void main(String[] args) throws IOException {
        List<BigDecimal> operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day7"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());

        BigDecimal maxValue = new BigDecimal(Integer.MIN_VALUE);
        List<Integer> maxInputs = new ArrayList<>();
        for (int i = 0; i < 99999; i++) {
            List<Integer> inputs =
                    String.format("%05d", i)
                            .chars()
                            .mapToObj(c -> Integer.parseInt(String.valueOf((char) c)))
                            .collect(toList());
            if (inputs.stream().anyMatch(j -> j < 5) || hasDuplicate(inputs)) {
                continue;
            }
            List<BigDecimal> newInputs = inputs.stream().map(BigDecimal::valueOf).collect(toList());
            BigDecimal output = runAmplificationFeedback(new ArrayList<>(operations), newInputs);
            if (output.compareTo(maxValue) > 0) {
                maxValue = output;
                maxInputs = inputs;
            }
        }
        System.out.printf("max value %s for %s\n", maxValue, maxInputs);
    }

    private static boolean hasDuplicate(List<Integer> inputs) {
        for (int i = 5; i < 10; i++) {
            int finalI = i;
            if (inputs.stream().filter(j -> j == finalI).count() > 1) {
                return true;
            }
        }
        return false;
    }

    private static BigDecimal runAmplificationFeedback(
            List<BigDecimal> operations, List<BigDecimal> inputs) {
        List<IntCodeState> amplifiers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<BigDecimal> amplifierInputs = new ArrayList<>();
            amplifierInputs.add(inputs.get(i));
            if (i == 0) {
                amplifierInputs.add(new BigDecimal(0));
            }
            amplifiers.add(
                    new IntCodeState(
                            0, new ArrayList<>(operations), amplifierInputs, new BigDecimal(0)));
        }

        int i = 0;
        while (!amplifiers.stream().allMatch(IntCodeState::isDone)) {
            IntCoder.runWithOutput(amplifiers.get(i), true);
            BigDecimal output = amplifiers.get(i).getOutput();
            i = (i + 1) % 5;
            amplifiers.get(i).addInput(output);
        }

        return amplifiers.get(4).getOutput();
    }
}

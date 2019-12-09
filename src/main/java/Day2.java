import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 {

    private static final String INPUT =
            "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,13,1,19,1,6,19,23,2,"
                    + "23,6,27,1,5,27,31,1,10,31,35,2,6,35,39,1,39,13,43,1,43,9,47,2,47,10,51,1,5,51,55,1"
                    + ",55,10,59,2,59,6,63,2,6,63,67,1,5,67,71,2,9,71,75,1,75,6,79,1,6,79,83,2,83,9,87,2,87,"
                    + "13,91,1,10,91,95,1,95,13,99,2,13,99,103,1,103,10,107,2,107,10,111,1,111,9,115,1,115,"
                    + "2,119,1,9,119,0,99,2,0,14,0";

    public static void main(String[] args) {
        List<BigDecimal> inputTokens =
                Arrays.stream(INPUT.split(",")).map(BigDecimal::new).collect(toList());
        ArrayList<BigDecimal> part1 = new ArrayList<>(inputTokens);
        part1.set(1, new BigDecimal(12));
        part1.set(2, new BigDecimal(2));
        IntCodeState result =
                IntCoder.runWithOutput(new IntCodeState(0, part1, new ArrayList<>(), new BigDecimal(0)));

        System.out.println("program: " + result.getOperations().get(0));

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                ArrayList<BigDecimal> part2 = new ArrayList<>(inputTokens);
                part2.set(1, new BigDecimal(i));
                part2.set(2, new BigDecimal(j));
                result =
                        IntCoder.runWithOutput(
                                new IntCodeState(
                                        0, part2, new ArrayList<>(1), new BigDecimal(0)));
                if (result.getOperations().get(0).equals(new BigDecimal(19690720))) {
                    System.out.printf("found result for %d and %d\n", i, j);
                }
            }
        }
    }
}

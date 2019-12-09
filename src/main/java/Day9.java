import static java.util.stream.Collectors.toList;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day9 {

    public static void main(String[] args) throws IOException {
        List<BigDecimal> operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day9"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());

        List<BigDecimal> input = new ArrayList<>();
        input.add(new BigDecimal(2));
        IntCoder.runWithOutput(
                new IntCodeState(0, operations, input, new BigDecimal(0)));
    }
}

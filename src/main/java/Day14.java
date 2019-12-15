import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.stream.Collectors.toMap;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Day14 {

    private static Map<String, Long> inventory = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Map<String, Reaction> reactions =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day14"), Charsets.UTF_8)
                                        .split("\n"))
                        .map(Reaction::fromString)
                        .collect(toMap(r -> r.output, r -> r));

        // part 1
        System.out.println("Need ore: " + triggerReaction("FUEL", 1, reactions));

        // part 2
        long attempt = 100000;
        long delta = attempt;
        long target = 1000000000000L;
        long previousResult = 0;
        long result;
        while (Math.abs(delta) > 0) {
            result = triggerReaction("FUEL", attempt, reactions);
            System.out.printf(
                    "result: %d, previous result: %d, attempt: %d, delta: %d\n",
                    result, previousResult, attempt, delta);
            if (result > target && previousResult < target) {
                delta *= -0.5;
            } else if (result < target && previousResult > target) {
                delta = Math.abs(delta);
                delta *= 0.5;
            }
            attempt += delta;
            previousResult = result;
        }
        inventory.clear();
    }

    private static long triggerReaction(
            String chemical, long quantity, Map<String, Reaction> reactions) {
        AtomicLong ore = new AtomicLong();
        Reaction r = reactions.get(chemical);
        long multiplier = (long) Math.ceil(quantity / (double) r.outputQuantity);
        r.inputs.forEach(
                (k, v) -> {
                    long newQuantity = v * multiplier;
                    if (k.equals("ORE")) {
                        ore.addAndGet(newQuantity);
                    } else {
                        long current = inventory.getOrDefault(k, 0L);
                        if (current < newQuantity) {
                            ore.addAndGet(triggerReaction(k, newQuantity - current, reactions));
                        }

                        inventory.put(k, inventory.getOrDefault(k, 0L) - newQuantity);
                    }
                });
        inventory.put(
                r.output, inventory.getOrDefault(r.output, 0L) + (multiplier * r.outputQuantity));
        return ore.get();
    }

    private static class Reaction {

        ImmutableMap<String, Integer> inputs;

        String output;
        long outputQuantity;

        public Reaction(ImmutableMap<String, Integer> inputs, String output, long outputQuantity) {
            this.inputs = inputs;
            this.output = output;
            this.outputQuantity = outputQuantity;
        }

        private static Reaction fromString(String s) {
            String[] split1 = s.split(" => ");
            return new Reaction(
                    Arrays.stream(split1[0].split(", "))
                            .collect(
                                    toImmutableMap(
                                            e -> e.split(" ")[1],
                                            e -> Integer.parseInt(e.split(" ")[0]))),
                    split1[1].split(" ")[1],
                    Integer.parseInt(split1[1].split(" ")[0]));
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("inputs", inputs)
                    .add("output", output)
                    .add("outputQuantity", outputQuantity)
                    .toString();
        }
    }
}

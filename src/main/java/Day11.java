import static java.util.stream.Collectors.toList;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day11 {

    private enum Direction {
        UP,
        LEFT,
        RIGHT,
        DOWN;

        Direction next(boolean left) {
            switch (this) {
                case UP:
                    return left ? LEFT : RIGHT;
                case LEFT:
                    return left ? DOWN : UP;
                case RIGHT:
                    return left ? UP : DOWN;
                case DOWN:
                    return left ? RIGHT : LEFT;
            }
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        List<BigDecimal> operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day11"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());

        Map<Point, Boolean> paintedPanels = new HashMap<>();
        Point currentPoint = new Point(0, 0);
        Direction direction = Direction.UP;
        ArrayList<BigDecimal> input = new ArrayList<>();
        input.add(new BigDecimal(1));
        IntCodeState state = new IntCodeState(0, operations, input, new BigDecimal(0));
        // part 1
        while (!state.isDone()) {
            IntCoder.runWithOutput(state, true);
            paintedPanels.put(currentPoint, state.getOutput().equals(new BigDecimal(1)));
            IntCoder.runWithOutput(state, true);
            direction = direction.next(state.getOutput().equals(new BigDecimal(0)));
            currentPoint = currentPoint.next(direction);
            input.add(new BigDecimal(paintedPanels.getOrDefault(currentPoint, false) ? 1 : 0));
        }
        System.out.println(paintedPanels);
        System.out.println(paintedPanels.size());

        // part 2
        for (int y = 10; y > -10; y--) {
            for (int x = -50; x < 50; x++) {
                if (paintedPanels.getOrDefault(new Point(x, y), false)) {
                    System.out.print('#');
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
    }

    private static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point next(Direction direction) {
            switch (direction) {
                case UP:
                    return new Point(x, y + 1);
                case LEFT:
                    return new Point(x - 1, y);
                case RIGHT:
                    return new Point(x + 1, y);
                case DOWN:
                    return new Point(x, y - 1);
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x, y);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("x", x).add("y", y).toString();
        }
    }
}

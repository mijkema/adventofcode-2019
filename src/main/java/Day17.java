import static java.util.stream.Collectors.toList;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.io.Resources;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Day17 {

    public static void main(String[] args) throws IOException {
        List<BigDecimal> operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day17"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());
        IntCodeState state = new IntCodeState(0, operations, new ArrayList<>(), null);
        Map<Point, Type> map = new HashMap<>();
        int x = 0, y = 0;
        while (!state.isDone()) {
            IntCoder.runWithOutput(state, true);
            char output = (char) state.getOutput().intValue();
            System.out.print((char) state.getOutput().intValue());
            if (output == '\n') {
                y++;
                x = 0;
                continue;
            } else {
                map.put(new Point(x, y), Type.of(output));
                x++;
            }
        }
        int sum = 0;
        final AtomicReference<Point> robotPoint = new AtomicReference<>();
        for (Map.Entry<Point, Type> e : map.entrySet()) {
            if (e.getValue() == Type.ROBOT) {
                robotPoint.set(e.getKey());
            }
            if (e.getValue() != Type.SCAFFOLD) {
                continue;
            }
            boolean intersection = true;
            for (Direction d : Direction.values()) {
                if (get(map, e.getKey().move(d)) != Type.SCAFFOLD) {
                    intersection = false;
                }
            }
            if (!intersection) {
                continue;
            }
            sum += e.getKey().x * e.getKey().y;
        }
        System.out.println("sum: " + sum);

        boolean done = false;
        Direction previousDirection = Direction.NORTH;
        List<Path> paths = new ArrayList<>();
        // part 2
        while (!done) {
            Direction finalPreviousDirection = previousDirection;
            Optional<Direction> newDirection =
                    Arrays.stream(Direction.values())
                            .filter(d -> get(map, robotPoint.get().move(d)) == Type.SCAFFOLD)
                            .filter(d -> d != finalPreviousDirection.opposite())
                            .findFirst();
            if (newDirection.isEmpty()) {
                done = true;
                break;
            }
            Point newPoint = robotPoint.get().move(newDirection.get());
            int distance = 0;
            while (get(map, newPoint) != Type.SPACE) {
                newPoint = newPoint.move(newDirection.get());
                distance++;
            }
            paths.add(new Path(robotPoint.get(), newDirection.get(), previousDirection, distance));
            robotPoint.set(newPoint.move(newDirection.get().opposite()));
            previousDirection = newDirection.get();
        }
        operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day17"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());
        operations.set(0, new BigDecimal(2));
        state =
                new IntCodeState(
                        0,
                        operations,
                        "A,C,A,C,B,B,C,A,C,B\nL,12,L,6,L,8,R,6\nL,12,R,6,L,8\nL,8,L,8,R,4,R,6,R,6\ny\n"
                                .chars()
                                .mapToObj(BigDecimal::new)
                                .collect(toList()),
                        null);
        done = false;
        while (!state.isDone()) {
            IntCoder.runWithOutput(state, true);
            System.out.println("output: " + state.getOutput());
        }
    }

    private static Type get(Map<Point, Type> map, Point point) {
        return map.getOrDefault(point, Type.SPACE);
    }

    private static class Path {
        private Point startingPoint;
        private boolean left = false;
        private int length;

        public Path(
                Point startingPoint, Direction direction, Direction previousDirection, int length) {
            this.startingPoint = startingPoint;
            switch (direction) {
                case NORTH:
                    left = previousDirection == Direction.EAST;
                    break;
                case SOUTH:
                    left = previousDirection == Direction.WEST;
                    break;
                case WEST:
                    left = previousDirection == Direction.NORTH;
                    break;
                case EAST:
                    left = previousDirection == Direction.SOUTH;
                    break;
            }
            this.length = length;
        }

        @Override
        public String toString() {
            return (left ? "L" : "R") + "," + length;
        }
    }

    private enum Type {
        SCAFFOLD('#'),
        SPACE('.'),
        ROBOT('^');

        private final char c;

        Type(char c) {
            this.c = c;
        }

        static Type of(char code) {
            return Arrays.stream(values()).filter(t -> t.c == code).findFirst().orElseThrow();
        }
    }

    private enum Direction {
        NORTH(1),
        SOUTH(2),
        WEST(3),
        EAST(4);

        private final int code;

        Direction(int code) {
            this.code = code;
        }

        public Direction opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
                case EAST:
                    return WEST;
            }
            throw new RuntimeException();
        }
    }

    private static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point move(Direction direction) {
            switch (direction) {
                case NORTH:
                    return new Point(x, y - 1);
                case SOUTH:
                    return new Point(x, y + 1);
                case WEST:
                    return new Point(x - 1, y);
                case EAST:
                    return new Point(x + 1, y);
            }
            throw new RuntimeException();
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
            return String.format("[%d, %d]", x, y);
        }
    }
}

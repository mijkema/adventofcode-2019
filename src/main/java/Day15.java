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

public class Day15 {

    private static IntCodeState stateAtStation = null;
    private static Point stationLocation = null;

    public static void main(String[] args) throws IOException {
        List<BigDecimal> operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day15"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());

        Map<Point, Integer> distanceMap = new HashMap<>();
        distanceMap.put(new Point(0, 0), 0);
        Map<Point, Type> areaMap = new HashMap<>();
        IntCodeState state = new IntCodeState(0, operations, new ArrayList<>(), null);
        int shortestPath = findShortestPath(state, areaMap, new Point(0, 0), distanceMap, 0);
        System.out.println(shortestPath);
        printMap(areaMap);
        distanceMap = new HashMap<>();
        distanceMap.put(stationLocation, 0);
        System.out.println(
                findOxygenFillTime(stateAtStation, areaMap, stationLocation, distanceMap, 0));
    }

    private static int findShortestPath(
            IntCodeState state,
            Map<Point, Type> areaMap,
            Point currentPosition,
            Map<Point, Integer> distanceMap,
            int distanceTravelled) {
        int shortest = Integer.MAX_VALUE;
        for (Direction direction : Direction.values()) {
            IntCodeState stateCopy = state.copy();
            stateCopy.setInputSupplier(() -> new BigDecimal(direction.code));
            IntCoder.runWithOutput(stateCopy, true);
            Type output = Type.of(stateCopy.getOutput().intValue());
            Point newPosition = currentPosition.move(direction);
            areaMap.put(newPosition, output);
            if (output == Type.REPAIR_STATION && distanceTravelled + 1 < shortest) {
                shortest = distanceTravelled + 1;
                stateAtStation = stateCopy.copy();
                stationLocation = newPosition;
            }
            if (output == Type.EMPTY) {
                int oldDistance = distanceMap.getOrDefault(newPosition, Integer.MAX_VALUE);
                int newDistance = distanceTravelled + 1;
                if (newDistance < oldDistance) {
                    distanceMap.put(newPosition, newDistance);
                    int distanceToStation =
                            findShortestPath(
                                    stateCopy,
                                    areaMap,
                                    newPosition,
                                    distanceMap,
                                    distanceTravelled + 1);
                    if (distanceToStation < shortest) {
                        shortest = distanceToStation;
                    }
                }
            }
        }
        return shortest;
    }

    private static int findOxygenFillTime(
            IntCodeState state,
            Map<Point, Type> areaMap,
            Point currentPosition,
            Map<Point, Integer> distanceMap,
            int distanceTravelled) {
        int longestPath = distanceTravelled;
        for (Direction direction : Direction.values()) {
            Point newPosition = currentPosition.move(direction);
            if (areaMap.get(newPosition) == Type.WALL) {
                continue;
            }
            IntCodeState stateCopy = state.copy();
            stateCopy.setInputSupplier(() -> new BigDecimal(direction.code));
            IntCoder.runWithOutput(stateCopy, true);
            Type output = Type.of(stateCopy.getOutput().intValue());
            if (output == Type.EMPTY) {
                int oldDistance = distanceMap.getOrDefault(newPosition, Integer.MAX_VALUE);
                int newDistance = distanceTravelled + 1;
                if (newDistance < oldDistance) {
                    distanceMap.put(newPosition, newDistance);
                    longestPath =
                            findOxygenFillTime(
                                    stateCopy,
                                    areaMap,
                                    newPosition,
                                    distanceMap,
                                    distanceTravelled + 1);
                }
            }
        }
        return distanceMap.values().stream().mapToInt(i -> i).max().orElseThrow();
    }

    private static void printMap(Map<Point, Type> areaMap) {
        int minX = areaMap.keySet().stream().mapToInt(p -> p.x).min().orElseThrow();
        int maxX = areaMap.keySet().stream().mapToInt(p -> p.x).max().orElseThrow();
        int minY = areaMap.keySet().stream().mapToInt(p -> p.y).min().orElseThrow();
        int maxY = areaMap.keySet().stream().mapToInt(p -> p.y).max().orElseThrow();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == 0 && y == 0) {
                    System.out.print('S');
                } else {
                    System.out.print(areaMap.getOrDefault(new Point(x, y), Type.EMPTY).symbol);
                }
            }
            System.out.println();
        }
    }

    private enum Type {
        WALL(0, '#'),
        EMPTY(1, ' '),
        REPAIR_STATION(2, 'R');

        private final int code;
        private final char symbol;

        Type(int code, char symbol) {
            this.code = code;
            this.symbol = symbol;
        }

        static Type of(int code) {
            return Arrays.stream(values()).filter(t -> t.code == code).findFirst().orElseThrow();
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
                    return new Point(x, y + 1);
                case SOUTH:
                    return new Point(x, y - 1);
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

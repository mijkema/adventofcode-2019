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
import java.util.concurrent.atomic.AtomicReference;

public class Day13 {

    private static Point nextBall = null;

    public static void main(String[] args) throws IOException {
        List<BigDecimal> operations =
                Arrays.stream(
                                Resources.toString(Resources.getResource("day13"), Charsets.UTF_8)
                                        .split(","))
                        .map(BigDecimal::new)
                        .collect(toList());
        operations.set(0, new BigDecimal(2));
        IntCodeState state = new IntCodeState(0, operations, new ArrayList<>(), null);
        Map<Point, Type> points = new HashMap<>();
        long score = 0;
        AtomicReference<Point> ballPoint = new AtomicReference<>();
        AtomicReference<Point> paddlePoint = new AtomicReference<>();
        AtomicReference<Boolean> ballDown = new AtomicReference<>(true);
        AtomicReference<Boolean> ballRight = new AtomicReference<>(true);
        state.setInputSupplier(
                () ->
                        getDirection(
                                ballPoint.get(), paddlePoint.get(), ballDown, ballRight, points));
        while (!state.isDone()) {
            int x = IntCoder.runWithOutput(state, true).getOutput().intValue();
            int y = IntCoder.runWithOutput(state, true).getOutput().intValue();
            int type = IntCoder.runWithOutput(state, true).getOutput().intValue();
            if (x == -1 && y == 0) {
                score = type;
                System.out.println("score: " + score);
            } else {
                Type t = Type.of(type);
                Point p = new Point(x, y);
                if (t == Type.BALL) ballPoint.set(p);
                if (t == Type.PADDLE) paddlePoint.set(p);
                Type oldType = points.getOrDefault(new Point(x, y), Type.EMPTY);
                if (t == Type.EMPTY && oldType == Type.BLOCK) {
                    System.out.printf("%s now empty (was %s)\n", new Point(x, y), oldType);
                }
                points.put(new Point(x, y), t);
            }
        }

        System.out.println(
                "blocks: " + points.values().stream().filter(Type.BLOCK::equals).count());
    }

    private static BigDecimal getDirection(
            Point ball,
            Point paddle,
            AtomicReference<Boolean> down,
            AtomicReference<Boolean> right,
            Map<Point, Type> points) {
        if (nextBall != null && !ball.equals(nextBall)) {
            System.out.println("wrong, ball at: " + ball + ", expected: " + nextBall);
            System.exit(1);
        }
        int res = 0;
        // Determine what the paddle should do if the ball is at the paddle.
        if (ball.y == 23) {
            if (ball.x < paddle.x) {
                res = -1;
            } else if (ball.x > paddle.x) {
                res = 1;
            }
            right.set(!right.get());
            printBallState(ball, down, right, points);
            Point nextD =
                    new Point(ball.x + (down.get() ? 1 : -1), ball.y + (right.get() ? 1 : -1));
            System.out.printf("Next ball position:%s(%s)\n", nextD, getType(nextD, points));
            nextBall = nextD;
            return new BigDecimal(res);
        }

        // determine what the next position of the ball would be
        printBallState(ball, down, right, points);
        Point nextX = new Point(ball.x + (down.get() ? 1 : -1), ball.y);
        Point nextY = new Point(ball.x, ball.y + (right.get() ? 1 : -1));
        Type xType = getType(nextX, points);
        Type yType = getType(nextY, points);
        if (xType != Type.EMPTY) {
            down.set(!down.get());
        }
        if (yType != Type.EMPTY) {
            right.set(!right.get());
        }
        Point nextD = new Point(ball.x + (down.get() ? 1 : -1), ball.y + (right.get() ? 1 : -1));
        Type dType = getType(nextD, points);
        if (dType != Type.EMPTY) {
            if (xType != Type.WALL && yType != Type.WALL) {
                down.set(!down.get());
            }
            right.set(!right.get());
        }
        nextD = new Point(ball.x + (down.get() ? 1 : -1), ball.y + (right.get() ? 1 : -1));
        dType = getType(nextD, points);
        if (dType != Type.EMPTY) {
            down.set(!down.get());
            right.set(!right.get());
        }
        nextD = new Point(ball.x + (down.get() ? 1 : -1), ball.y + (right.get() ? 1 : -1));
        System.out.printf("Next ball position:%s(%s)\n", nextD, getType(nextD, points));
        nextBall = nextD;
        if (nextD.x < paddle.x) {
            res = -1;
        } else if (nextD.x > paddle.x) {
            res = 1;
        }
        return new BigDecimal(res);
    }

    private static void printBallState(
            Point ball,
            AtomicReference<Boolean> down,
            AtomicReference<Boolean> right,
            Map<Point, Type> points) {
        System.out.printf("Ball state: %s\n", ball);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                char dirChar = ' ';
                if (i == (down.get() ? 1 : -1) && j == (right.get() ? 1 : -1)) {
                    dirChar = right.get() ? '>' : '<';
                }
                if (dirChar == '>') System.out.print(dirChar);
                System.out.print(getType(new Point(ball.x + i, ball.y + j), points).signal);
                if (dirChar != '>') System.out.print(dirChar);
            }
            System.out.println();
        }
    }

    private static Type getType(Point p, Map<Point, Type> points) {
        return points.getOrDefault(p, Type.EMPTY);
    }

    private static void printBoard(Map<Point, Type> points) {
        int minXValue = points.keySet().stream().mapToInt(Point::getX).min().getAsInt();
        int minYalue = points.keySet().stream().mapToInt(Point::getY).min().getAsInt();
        int maxXValue = points.keySet().stream().mapToInt(Point::getX).max().getAsInt();
        int maxYalue = points.keySet().stream().mapToInt(Point::getY).max().getAsInt();
        for (int i = minXValue; i <= maxXValue; i++) {
            for (int j = minYalue; j <= maxYalue; j++) {
                char toDraw = points.getOrDefault(new Point(i, j), Type.EMPTY).signal;
                System.out.print(toDraw);
            }
            System.out.println();
        }
    }

    private enum Type {
        EMPTY(0, ' '),
        WALL(1, '#'),
        BLOCK(2, '!'),
        PADDLE(3, '|'),
        BALL(4, 'O');

        private final int type;
        private final char signal;

        Type(int type, char signal) {
            this.type = type;
            this.signal = signal;
        }

        public static Type of(int type) {
            for (Type t : values()) {
                if (t.type == type) return t;
            }
            return null;
        }
    }

    private static class Point {
        private int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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

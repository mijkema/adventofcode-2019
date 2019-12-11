import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class Day10 {

    public static void main(String[] args) throws IOException {
        String input = Resources.toString(Resources.getResource("day10"), Charsets.UTF_8);
        Scanner s = new Scanner(input);
        Set<Point> asteroids = new HashSet<>();
        int y = 0;
        while (s.hasNext()) {
            String row = s.next();
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                if (c == '#') {
                    asteroids.add(new Point(i, y));
                }
            }
            y++;
        }

        // part 1
        int best = 0;
        Point bestPoint = null;
        Map<Point, Double> bestAngles = new HashMap<>();
        for (Point c : asteroids) {
            Map<Double, Point> angles = new HashMap<>();
            Map<Point, Double> asteroidAngles = new HashMap<>();
            for (Point o : asteroids) {
                if (o.equals(c)) {
                    continue;
                }
                double angle;
                if (c.y == o.y && c.x > o.x) {
                    angle = 270d;
                } else if (c.y == o.y && c.x < o.x) {
                    angle = 90;
                } else if (c.x == o.x && c.y > o.y) {
                    angle = 0;
                } else if (c.x == o.x && c.y < o.y) {
                    angle = 180;
                } else {
                    double d1 = (double) o.y - c.y;
                    double d2 = (double) o.x - c.x;
                    angle = Math.toDegrees(Math.atan(d1 / d2));
                    if (c.x > o.x) {
                        angle += 270;
                    } else if (c.x < o.x) {
                        angle += 90;
                    }
                }

                asteroidAngles.put(o, angle);
                angles.put(angle, o);
            }
            int angleCount = angles.size();
            if (angleCount > best) {
                best = angleCount;
                bestAngles = new HashMap<>(asteroidAngles);
                bestPoint = c;
            }
            asteroidAngles.clear();
        }

        // part 2
        double laserAngle = -1;
        Point finalBestPoint = bestPoint;
        int index = 1;
        while (!bestAngles.isEmpty()) {
            double angleOfNextAsteroid = findNextAngle(bestAngles, laserAngle);
            Map<Point, Double> asteroidCandidates =
                    Maps.filterValues(bestAngles, d -> d == angleOfNextAsteroid);
            Point asteroidToVaporize =
                    asteroidCandidates.keySet().stream()
                            .min(Comparator.comparing(p -> p.distanceTo(finalBestPoint)))
                            .orElseThrow();
            bestAngles.remove(asteroidToVaporize);
            System.out.printf("%d Vaporizing %s\n", index, asteroidToVaporize);
            index++;
            laserAngle = angleOfNextAsteroid;
        }
    }

    private static double findNextAngle(Map<Point, Double> bestAngles, double laserAngle) {
        double difference = Double.MAX_VALUE, angle = Double.MAX_VALUE;
        for (Map.Entry<Point, Double> e : bestAngles.entrySet()) {
            double current = e.getValue() - laserAngle;
            if (current <= 0) current += 360;
            if (current < difference && current > 0) {
                difference = current;
                angle = e.getValue();
            }
        }
        return angle;
    }

    private static class Point {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public double distanceTo(Point p) {
            return Math.abs(x - p.x) + Math.abs(y - p.y);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("x", x).add("y", y).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x, y);
        }
    }
}

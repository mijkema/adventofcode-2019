import static java.util.stream.Collectors.toList;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day12 {

    public static void main(String[] args) throws IOException {
        List<Moon> part1 = getMoons();
        // calculate gravity
        for (int i = 0; i < 1000; i++) {
            updateMoons(part1);
        }
        System.out.printf(
                "Total energy of all moons: %d\n",
                part1.stream().mapToInt(Moon::getTotalEnergy).sum());

        // find when all x parts are the same
        long rotationX = findCycleTime(m -> Stream.of(m.x, m.velocityX));
        long rotationY = findCycleTime(m -> Stream.of(m.y, m.velocityY));
        long rotationZ = findCycleTime(m -> Stream.of(m.z, m.velocityZ));

        System.out.printf(
                "cycles: %d, %d, %d -> %d\n",
                rotationX, rotationY, rotationZ, lcm(rotationX, rotationY, rotationZ));
    }

    private static long findCycleTime(Function<Moon, Stream<Integer>> f) throws IOException {
        List<Moon> initialState = getMoons();
        List<Moon> part2 = getMoons();
        boolean done = false;
        List<Integer> stateToMatch = initialState.stream().flatMap(f).collect(toList());
        int i = 0;
        while (!done) {
            updateMoons(part2);
            i++;
            if (part2.stream().flatMap(f).collect(toList()).equals(stateToMatch)) {
                done = true;
            }
        }
        return i;
    }

    private static long gcd(long x, long y) {
        return (y == 0) ? x : gcd(y, x % y);
    }

    public static long lcm(long... numbers) {
        return Arrays.stream(numbers).reduce(1, (x, y) -> x * (y / gcd(x, y)));
    }

    private static List<Moon> getMoons() throws IOException {
        return Arrays.stream(
                        Resources.toString(Resources.getResource("day12"), Charsets.UTF_8)
                                .split("\n"))
                .map(Moon::fromInputString)
                .collect(toList());
    }

    private static void updateMoons(List<Moon> moons) {
        for (Moon moon : moons) {
            for (Moon other : moons) {
                if (other == moon) continue;
                moon.updateVelocity(other);
            }
        }
        // apply gravity
        for (Moon moon : moons) {
            moon.applyGravity();
        }
    }

    private static class Moon {
        private int x, y, z;
        private int velocityX, velocityY, velocityZ;

        public Moon(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private static Moon fromInputString(String s) {
            String[] coordinates = s.replaceAll("<", "").replaceAll(">", "").split(",");
            return new Moon(
                    Integer.parseInt(coordinates[0].split("=")[1]),
                    Integer.parseInt(coordinates[1].split("=")[1]),
                    Integer.parseInt(coordinates[2].split("=")[1]));
        }

        private int getPotentialEnergy() {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }

        private int getKineticEnergy() {
            return Math.abs(velocityX) + Math.abs(velocityY) + Math.abs(velocityZ);
        }

        private int getTotalEnergy() {
            return getPotentialEnergy() * getKineticEnergy();
        }

        public void updateVelocity(Moon other) {
            velocityX += Integer.compare(other.x, x);
            velocityY += Integer.compare(other.y, y);
            velocityZ += Integer.compare(other.z, z);
        }

        public void applyGravity() {
            x += velocityX;
            y += velocityY;
            z += velocityZ;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("x", x)
                    .add("y", y)
                    .add("z", z)
                    .add("velocityX", velocityX)
                    .add("velocityY", velocityY)
                    .add("velocityZ", velocityZ)
                    .add("potentialEnergy", getPotentialEnergy())
                    .add("kineticEnergy", getKineticEnergy())
                    .add("totalEnergy", getTotalEnergy())
                    .toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Moon moon = (Moon) o;
            return x == moon.x
                    && y == moon.y
                    && z == moon.z
                    && velocityX == moon.velocityX
                    && velocityY == moon.velocityY
                    && velocityZ == moon.velocityZ;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}

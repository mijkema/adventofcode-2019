import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Day6 {

    public static void main(String[] args) throws IOException {
        List<String> input =
                new Scanner(Resources.toString(Resources.getResource("day6"), Charsets.UTF_8))
                        .tokens()
                        .collect(Collectors.toList());
        Map<String, Node> nodes = new HashMap<>();
        for (String s : input) {
            String[] nodeNames = s.split("\\)");
            Node node1 = nodes.get(nodeNames[0]);
            if (node1 == null) {
                node1 = new Node(nodeNames[0], null);
            }
            nodes.put(nodeNames[0], node1);
            Node node2 = nodes.get(nodeNames[1]);
            if (node2 == null) {
                node2 = new Node(nodeNames[1], node1);
                nodes.put(nodeNames[1], node2);
            } else if (node2.orbit == null) {
                node2.orbit = node1;
            } else {
                System.out.printf(
                        "node %s already orbits something, but now also orbits %s",
                        node2, nodeNames[0]);
            }
            node1.addOrbited(node2);
        }

        // part 1
        int totalOrbits = 0;
        for (Entry<String, Node> n : nodes.entrySet()) {
            Node orbit = n.getValue().orbit;
            int orbits = 0;
            while (orbit != null) {
                orbits++;
                orbit = orbit.orbit;
            }
            totalOrbits += orbits;
        }
        System.out.println("total: " + totalOrbits);

        // part 2
        Node you = nodes.get("YOU");
        Node santa = nodes.get("SAN");
        // minus 2, since the source and destination don't count
        System.out.println(findShortestPath(you, santa, new HashSet<>(), 0) - 2);
    }

    private static int findShortestPath(
            Node you, Node santa, Set<Node> visited, int distanceTravelled) {
        Set<Node> hops = you.findOrbits();
        if (hops.contains(santa)) {
            return distanceTravelled + 1;
        } else {
            int distance = Integer.MAX_VALUE;
            Set<Node> newHops = Sets.difference(hops, visited);
            for (Node hop : newHops) {
                Set<Node> newVisited = new HashSet<>(visited);
                newVisited.add(you);
                int newDistance = findShortestPath(hop, santa, newVisited, distanceTravelled + 1);
                if (newDistance < distance) {
                    distance = newDistance;
                }
            }
            return distance;
        }
    }

    private static class Node {
        String name;
        Node orbit;
        List<Node> orbited = new ArrayList<>();

        public Node(String name, Node orbit) {
            this.name = name;
            this.orbit = orbit;
        }

        public void addOrbited(Node orbit) {
            orbited.add(orbit);
        }

        public Set<Node> findOrbits() {
            HashSet<Node> res = new HashSet<>(orbited);
            if (orbit != null) {
                res.add(orbit);
            }
            return res;
        }
    }
}

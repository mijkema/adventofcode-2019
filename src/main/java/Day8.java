import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day8 {

    public static void main(String[] args) throws IOException {
        String input = Resources.toString(Resources.getResource("day8"), Charsets.UTF_8);
        int totalSize = 25 * 6;
        List<List<Integer>> digitLayers = new ArrayList<>();
        int i = 0;
        List<Integer> currentLayer = new ArrayList<>();
        for (char c : input.toCharArray()) {
            if (i % totalSize == 0) {
                currentLayer = new ArrayList<>();
                digitLayers.add(currentLayer);
            }
            currentLayer.add(Integer.parseInt(String.valueOf(c)));
            i++;
        }

        // part 1
        List<Integer> resultLayer = digitLayers.get(0);
        int count = Integer.MAX_VALUE;
        for (List<Integer> layer : digitLayers) {
            int newCount = (int) layer.stream().filter(d -> d == 0).count();
            if (newCount < count) {
                count = newCount;
                resultLayer = layer;
            }
        }
        System.out.println(
                resultLayer.stream().filter(d -> d == 1).count()
                        * resultLayer.stream().filter(d -> d == 2).count());

        // part 2
        List<Integer> finalLayer = new ArrayList<>();
        for (int j = 0; j < resultLayer.size(); j++) {
            for (int k = 0; k < digitLayers.size(); k++) {
                if (digitLayers.get(k).get(j) != 2) {
                    finalLayer.add(digitLayers.get(k).get(j));
                    break;
                }
            }
        }
        for (int j = 0; j < finalLayer.size(); j++) {
            int value = finalLayer.get(j);
            if (j % 25 == 0) {
                System.out.println();
            }
            System.out.print(value == 1 ? "#" : " ");
        }
    }
}

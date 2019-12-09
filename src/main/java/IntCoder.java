import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntCoder {

    private enum ParameterType {
        POSITION,
        IMMEDIATE,
        RELATIVE
    }

    static IntCodeState runWithOutput(IntCodeState state) {
        return runWithOutput(state, false);
    }

    static IntCodeState runWithOutput(IntCodeState state, boolean stopAtOutput) {
        while (!state.isDone()) {
            String opString = String.valueOf(state.getOperations().get(state.getIndex()));
            int operation = Integer.parseInt(opString);
            if (opString.length() >= 2) {
                operation = Integer.parseInt(opString.substring(opString.length() - 2));
            }
            if (operation == 1) {
                List<BigDecimal> vals = getOperands(state, 2, operation);
                state.setValue(vals.get(2).intValue(), vals.get(0).add(vals.get(1)));
                state.setIndex(state.getIndex() + 4);
            } else if (operation == 2) {
                List<BigDecimal> vals = getOperands(state, 2, operation);
                state.setValue(vals.get(2).intValue(), vals.get(0).multiply(vals.get(1)));
                state.setIndex(state.getIndex() + 4);
            } else if (operation == 3) {
                List<BigDecimal> vals = getOperands(state, 0, operation);
                System.out.printf("input %s\n", state.getInput().get(0));
                state.setValue(vals.get(0).intValue(), state.getInput().remove(0));
                state.setIndex(state.getIndex() + 2);
            } else if (operation == 4) {
                List<BigDecimal> vals = getOperands(state, 1, operation);
                state.setOutput(vals.get(0));
                System.out.printf("output %s\n", state.getOutput());
                state.setIndex(state.getIndex() + 2);
                if (stopAtOutput) {
                    return state;
                }
            } else if (operation == 5) {
                List<BigDecimal> vals = getOperands(state, 2, operation);
                if (!vals.get(0).equals(new BigDecimal(0))) {
                    state.setIndex(vals.get(1).intValue());
                    continue;
                }
                state.setIndex(state.getIndex() + 3);
            } else if (operation == 6) {
                List<BigDecimal> vals = getOperands(state, 2, operation);
                if (vals.get(0).equals(new BigDecimal(0))) {
                    state.setIndex(vals.get(1).intValue());
                    continue;
                }
                state.setIndex(state.getIndex() + 3);
            } else if (operation == 7) {
                List<BigDecimal> vals = getOperands(state, 2, operation);
                state.setValue(
                        vals.get(2).intValue(),
                        vals.get(0).compareTo(vals.get(1)) < 0
                                ? new BigDecimal(1)
                                : new BigDecimal(0));
                state.setIndex(state.getIndex() + 4);
            } else if (operation == 8) {
                List<BigDecimal> vals = getOperands(state, 2, operation);
                state.setValue(
                        vals.get(2).intValue(),
                        vals.get(0).equals(vals.get(1)) ? new BigDecimal(1) : new BigDecimal(0));
                state.setIndex(state.getIndex() + 4);
            } else if (operation == 9) {
                List<BigDecimal> vals = getOperands(state, 1, operation);
                state.setRelativeBase(state.getRelativeBase() + vals.get(0).intValue());
                state.setIndex(state.getIndex() + 2);
            } else if (operation == 99) {
                state.setDone(true);
                System.out.println("done");
            } else {
                throw new IllegalArgumentException("invalid operation " + operation);
            }
        }
        return state;
    }

    private static List<BigDecimal> getOperands(IntCodeState state, int parameters, int operation) {
        String opString = String.valueOf(state.getOperations().get(state.getIndex()));
        ParameterType[] parameterTypes = new ParameterType[parameters + 1];
        Arrays.fill(parameterTypes, ParameterType.POSITION);
        if (Integer.parseInt(opString) != operation) {
            List<Character> parameterModes =
                    Lists.reverse(
                            opString.substring(0, opString.length() - 2)
                                    .chars()
                                    .mapToObj(c -> (char) c)
                                    .collect(toList()));
            for (int j = 0; j < parameterModes.size(); j++) {
                if (parameterModes.get(j) == '1') {
                    parameterTypes[j] = ParameterType.IMMEDIATE;
                } else if (parameterModes.get(j) == '2') {
                    parameterTypes[j] = ParameterType.RELATIVE;
                }
            }
        }

        List<BigDecimal> result = new ArrayList<>();
        // input parameters
        for (int i = 1; i <= parameters; i++) {
            if (parameterTypes[i - 1] == ParameterType.IMMEDIATE) {

                result.add(state.getOperationAt(state.getIndex() + i));
            } else if (parameterTypes[i - 1] == ParameterType.RELATIVE) {
                int relIndex =
                        state.getRelativeBase()
                                + state.getOperations().get(state.getIndex() + i).intValue();
                result.add(state.getOperationAt(relIndex));
            } else {
                result.add(
                        state.getOperationAt(
                                state.getOperations().get(state.getIndex() + i).intValue()));
            }
        }
        // output parameter
        if (parameterTypes[parameters] == ParameterType.POSITION) {
            result.add(state.getOperationAt(state.getIndex() + parameters + 1));
        } else if (parameterTypes[parameters] == ParameterType.RELATIVE) {
            result.add(
                    new BigDecimal(state.getRelativeBase())
                            .add(state.getOperationAt(state.getIndex() + parameters + 1)));
        }
        return result;
    }
}

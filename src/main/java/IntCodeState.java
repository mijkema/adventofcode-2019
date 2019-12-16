import com.google.common.base.MoreObjects;
import com.google.common.base.Supplier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntCodeState {
    private int index;
    private List<BigDecimal> operations;
    private Map<Integer, BigDecimal> memory = new HashMap<>();
    private List<BigDecimal> input;
    private Supplier<BigDecimal> inputSupplier = null;
    private BigDecimal output;
    private boolean done = false;
    private int relativeBase = 0;

    public IntCodeState(
            int index, List<BigDecimal> operations, List<BigDecimal> input, BigDecimal output) {
        this.index = index;
        this.operations = operations;
        this.input = input;
        this.output = output;
    }

    private IntCodeState(
            int index,
            List<BigDecimal> operations,
            Map<Integer, BigDecimal> memory,
            List<BigDecimal> input,
            Supplier<BigDecimal> inputSupplier,
            BigDecimal output,
            boolean done,
            int relativeBase) {
        this.index = index;
        this.operations = operations;
        this.memory = memory;
        this.input = input;
        this.inputSupplier = inputSupplier;
        this.output = output;
        this.done = done;
        this.relativeBase = relativeBase;
    }

    void addInput(BigDecimal newInput) {
        input.add(newInput);
    }

    public BigDecimal getOutput() {
        return output;
    }

    public boolean isDone() {
        return done;
    }

    public List<BigDecimal> getOperations() {
        return operations;
    }

    public int getIndex() {
        return index;
    }

    public void setInputSupplier(Supplier<BigDecimal> inputSupplier) {
        this.inputSupplier = inputSupplier;
    }

    public BigDecimal getInput() {
        return inputSupplier == null ? input.remove(0) : inputSupplier.get();
    }

    public void setOutput(BigDecimal output) {
        this.output = output;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getRelativeBase() {
        return relativeBase;
    }

    public void setRelativeBase(int relativeBase) {
        this.relativeBase = relativeBase;
    }

    public BigDecimal getOperationAt(int i) {
        if (i >= operations.size()) {
            return memory.getOrDefault(i, new BigDecimal(0));
        }
        return operations.get(i);
    }

    public void setValue(int index, BigDecimal value) {
        if (index < operations.size()) {
            operations.set(index, value);
        } else {
            memory.put(index, value);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("index", index)
                .add("operations", operations)
                .add("input", input)
                .add("output", output)
                .add("relativeBase", relativeBase)
                .toString();
    }

    public IntCodeState copy() {
        return new IntCodeState(
                this.index,
                new ArrayList<>(this.operations),
                new HashMap<>(this.memory),
                new ArrayList<>(this.input),
                this.inputSupplier,
                this.output,
                this.done,
                this.relativeBase);
    }
}

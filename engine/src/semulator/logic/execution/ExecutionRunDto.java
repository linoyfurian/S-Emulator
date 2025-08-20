package semulator.logic.execution;

import semulator.logic.program.ProgramDto;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecutionRunDto {
    private final long runNumber;
    private final int expansionDegree;
    private final long result;
    private final long[] inputs;
    private final int cycles;
    private final LinkedHashMap<String, Long> variables;

    ExecutionRunDto(long runNumber, int expansionDegree, long result, long[] inputs, int cycles, Map<String, Long> variables) {
        this.runNumber = runNumber;
        this.expansionDegree = expansionDegree;
        this.result = result;
        this.inputs = inputs;
        this.cycles = cycles;
        this.variables = sortVarsForDisplay(variables);
    }

    private static LinkedHashMap<String, Long> sortVarsForDisplay(Map<String, Long> vars) {
        Map<String, Long> copy = new HashMap<>(vars);
        copy.putIfAbsent("y", 0L);
        return copy.entrySet().stream()
                .sorted(Comparator
                        .comparingInt((Map.Entry<String, Long> e) -> groupOf(e.getKey()))   // y -> 0, x* -> 1, z* -> 2, others -> 3
                        .thenComparingInt(e -> numericIndex(e.getKey()))                         // numeric index (x2 < x10)
                        .thenComparing(Map.Entry::getKey))                                  // stable tie-breaker
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private static int numericIndex(String k) {
        if ("y".equals(k)) return 0;
        return Integer.parseInt(k.substring(1)); // safe under your assumption
    }

    private static int groupOf(String k) {
        char c = k.charAt(0);
        if (c == 'y' && k.length() == 1) return 0;
        if (c == 'x') return 1;
        if (c == 'z') return 2;
        return 3;
    }

    public long getResult() {
        return result;
    }

    public int getCycles() {
        return cycles;
    }

    public LinkedHashMap<String, Long> getVariables() {
        return variables;
    }

    public long getRunNumber() {
        return runNumber;
    }

    public int getExpansionDegree() {
        return expansionDegree;
    }

    public long[] getInputs() {
        return inputs;
    }
}
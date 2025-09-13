package semulator.api.dto;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.program.Program;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugContextDto {
    private final long previousInstructionNumber;
    private final long nextInstructionNumber;
    private final LinkedHashMap<String, Long> currentVariablesValues;
    private final LinkedHashMap<String, Long> previousVariablesValues;
    private final int cycles;
    private final DebugContextDto prevDebugContext;
    private final Map<String, Long> originalInputs;

    public DebugContextDto(Program debuggedProgram, ExecutionContext context, long previousInstructionNumber, long nextInstructionNumber, int cycles, Map<String, Long> previousVariablesValues, DebugContextDto prevDebugContext, Map<String, Long> originalInputs) {
        this.previousInstructionNumber = previousInstructionNumber;
        this.nextInstructionNumber = nextInstructionNumber;
        this.currentVariablesValues = sortVarsForDisplay(context.getAllValues());
        this.previousVariablesValues = sortVarsForDisplay(previousVariablesValues);
        this.cycles = cycles;
        this.prevDebugContext = prevDebugContext;
        this.originalInputs = originalInputs;
    }

    public LinkedHashMap<String, Long> getCurrentVariablesValues() {
        return currentVariablesValues;
    }

    public int getCycles() {
        return cycles;
    }

    public long getNextInstructionNumber() {
        return nextInstructionNumber;
    }

    public long getPreviousInstructionNumber() {
        return previousInstructionNumber;
    }

    public LinkedHashMap<String, Long> getPreviousVariablesValues() {
        return previousVariablesValues;
    }

    public DebugContextDto getPrevDebugContext() {
        return prevDebugContext;
    }

    public Map<String, Long> getOriginalInputs() {
        return originalInputs;
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
}

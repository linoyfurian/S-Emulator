package dto;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.program.Program;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugContextDto {
    private final String userName;
    private final long previousInstructionNumber;
    private final long nextInstructionNumber;
    private final LinkedHashMap<String, Long> currentVariablesValues;
    private final LinkedHashMap<String, Long> previousVariablesValues;
    private final int cycles;
    private final int PrevCycles;
    private final DebugContextDto prevDebugContext;
    private final Map<String, Long> originalInputs;
    private final int currentInstructionsCycles;
    private final boolean isSuccess;
    private double averageCredits;

    public DebugContextDto(boolean isSuccess, int currentInstructionsCycles, String userName, Program debuggedProgram, ExecutionContext context, long previousInstructionNumber, long nextInstructionNumber, int cycles, Map<String, Long> previousVariablesValues, DebugContextDto prevDebugContext, Map<String, Long> originalInputs, int PrevCycles) {
        this.previousInstructionNumber = previousInstructionNumber;
        this.nextInstructionNumber = nextInstructionNumber;
        this.currentVariablesValues = sortVarsForDisplay(context.getAllValues());
        this.previousVariablesValues = sortVarsForDisplay(previousVariablesValues);
        this.cycles = cycles;
        this.prevDebugContext = prevDebugContext;
        this.originalInputs = originalInputs;
        this.PrevCycles = PrevCycles;
        this.userName = userName;
        this.currentInstructionsCycles = currentInstructionsCycles;
        this.isSuccess = isSuccess;
        this.averageCredits = 0;
    }

    public DebugContextDto(boolean isSuccess, int currentInstructionsCycles, String userName, Program debuggedProgram, Map<String, Long> currentVariablesValues, long previousInstructionNumber, long nextInstructionNumber, int cycles, Map<String, Long> previousVariablesValues, DebugContextDto prevDebugContext, Map<String, Long> originalInputs, int PrevCycles) {
        this.previousInstructionNumber = previousInstructionNumber;
        this.nextInstructionNumber = nextInstructionNumber;
        this.currentVariablesValues = sortVarsForDisplay(currentVariablesValues);
        this.previousVariablesValues = sortVarsForDisplay(previousVariablesValues);
        this.cycles = cycles;
        this.prevDebugContext = prevDebugContext;
        this.originalInputs = originalInputs;
        this.PrevCycles = PrevCycles;
        this.userName = userName;
        this.currentInstructionsCycles = currentInstructionsCycles;
        this.isSuccess = isSuccess;
        this.averageCredits = 0;
    }

    public DebugContextDto (boolean isSuccess, DebugContextDto debugContextDto) {
        this.isSuccess = isSuccess;
        this.userName = debugContextDto.getUserName();
        this.previousInstructionNumber = debugContextDto.getPreviousInstructionNumber();
        this.nextInstructionNumber = debugContextDto.getNextInstructionNumber();
        this.currentVariablesValues = debugContextDto.getCurrentVariablesValues();
        this.previousVariablesValues = debugContextDto.getPreviousVariablesValues();
        this.cycles = debugContextDto.getCycles();
        this.PrevCycles = debugContextDto.getPrevCycles();
        this.originalInputs = debugContextDto.getOriginalInputs();
        this.currentInstructionsCycles = debugContextDto.getCurrentInstructionsCycles();
        this.prevDebugContext = debugContextDto.getPrevDebugContext();
        this.averageCredits = 0;
    }

    public double getAverageCredits(){
        return averageCredits;
    }

    public void setAverageCredits(double averageCredits){
        this.averageCredits = averageCredits;
    }

    public LinkedHashMap<String, Long> getCurrentVariablesValues() {
        return currentVariablesValues;
    }

    public int getCycles() {
        return cycles;
    }

    public int getPrevCycles() {
        return PrevCycles;
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

    public String getUserName() {
        return userName;
    }

    public int getCurrentInstructionsCycles() {
        return currentInstructionsCycles;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}

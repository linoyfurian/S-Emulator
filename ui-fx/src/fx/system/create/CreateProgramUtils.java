package fx.system.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateProgramUtils {
    private CreateProgramUtils() {}

    public static List<String> getInstructionsNameOptions() {
        List<String> options = new ArrayList<>();
        options.add("INCREASE");
        options.add("DECREASE");
        options.add("JUMP_NOT_ZERO");
        options.add("NEUTRAL");
        options.add("JUMP_ZERO");
        options.add("ZERO_VARIABLE");
        options.add("GOTO_LABEL");
        options.add("ASSIGNMENT");
        options.add("CONSTANT_ASSIGNMENT");
        options.add("JUMP_EQUAL_CONSTANT");
        options.add("JUMP_EQUAL_VARIABLE");
        return options;
    }

    public static List<String> getVariablesTypeOptions(){
        List<String> options = new ArrayList<>();
        options.add("y");
        options.add("x");
        options.add("z");
        return options;
    }


    public static List<String> getLabelTypeOptionsPlus(){
        List<String> options = new ArrayList<>();
        options.add("no label");
        options.add("L");
        options.add("EXIT");
        return options;
    }

    public static List<String> getLabelTypeOptions(){
        List<String> options = new ArrayList<>();
        options.add("L");
        options.add("EXIT");
        return options;
    }
}

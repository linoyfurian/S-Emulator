package semulator.core.loader;

import semulator.core.loader.jaxb.schema.version2.generated.*;

import semulator.logic.Function.Function;
import semulator.logic.instruction.*;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;
import java.util.List;

public class XmlProgramMapperV2 {
    public static Program fromSProgramToProgramImpl(SProgram sProgram) {
        String programName = sProgram.getName().trim();
        Program program = new ProgramImpl(programName, 0);

        SInstructions instructions = sProgram.getSInstructions();
        long instructionCounter = 1;

        List<SInstruction> instructionsList = instructions.getSInstruction();
        for (SInstruction instruction : instructionsList) {
            Instruction mapInstruction;
            mapInstruction = instructionMapper(instruction, instructionCounter);
            program.addInstruction(mapInstruction);
            instructionCounter++;
        }

        List<SFunction> functions = sProgram.getSFunctions().getSFunction();

        for (SFunction function : functions) {
            Program newProgram = fromSFunctionToFunction(function);
            if(program instanceof ProgramImpl programImpl) {
                programImpl.addFunction(newProgram);
            }
        }

        ProgramImpl programImpl = (ProgramImpl) program;
        int maxDepth = programImpl.findMaxDepth();

        for(int i = 0; i < maxDepth; i++) {
            setMaxDegreeOfExpansionForComplexInstruction(program);
        }
        return program;
    }


    private static Instruction instructionMapper(SInstruction instruction, long instructionNumber){
        Instruction instructionToReturn = null;
        String instructionName = instruction.getName().trim();
        instructionName = instructionName.toUpperCase();
        String instructionVariable = instruction.getSVariable();
        if(instructionVariable!=null)
            instructionVariable = instructionVariable.trim();
        Variable variable = variableMapper(instructionVariable);
        String instructionLabel = instruction.getSLabel();
        if(instructionLabel!=null)
            instructionLabel = instructionLabel.trim();
        Label label = labelMapper(instructionLabel);

        switch (instructionName) {
            case "INCREASE":
                instructionToReturn = new IncreaseInstruction(variable, label, instructionNumber, null);
                break;
            case "DECREASE":
                instructionToReturn = new DecreaseInstruction(variable, label, instructionNumber, null);
                break;
            case "JUMP_NOT_ZERO":
                Label jnzlabel;
                jnzlabel = getTargetLabel(instruction.getSInstructionArguments().getSInstructionArgument(), "JNZLabel");
                instructionToReturn = new JumpNotZeroInstruction(variable, jnzlabel, label, instructionNumber, null);
                break;
            case "NEUTRAL":
                instructionToReturn =  new NeutralInstruction(variable, label, instructionNumber, null);
                break;
            case "ZERO_VARIABLE":
                instructionToReturn = new ZeroVariableInstruction(variable, label, instructionNumber, null);
                break;
            case "GOTO_LABEL":
                Label gotoLabel;
                gotoLabel = getTargetLabel(instruction.getSInstructionArguments().getSInstructionArgument(), "gotoLabel");
                instructionToReturn = new GoToLabelInstruction(variable, label, gotoLabel, instructionNumber, null);
                break;
            case "ASSIGNMENT":
                Variable assignedVariable;
                assignedVariable = getTargetVariable(instruction.getSInstructionArguments().getSInstructionArgument(), "assignedVariable");
                instructionToReturn = new AssignmentInstruction(variable, label, instructionNumber, assignedVariable, null);
                break;
            case "CONSTANT_ASSIGNMENT":
                long constantValue;
                constantValue = getConstantValue(instruction.getSInstructionArguments().getSInstructionArgument(), "constantValue");
                instructionToReturn = new ConstantAssignmentInstruction(variable, label, constantValue, instructionNumber, null);
                break;
            case "JUMP_ZERO":
                Label jzlabel;
                jzlabel = getTargetLabel(instruction.getSInstructionArguments().getSInstructionArgument(), "JZLabel");
                instructionToReturn = new JumpZeroInstruction(variable, jzlabel, label, instructionNumber, null);
                break;
            case "JUMP_EQUAL_CONSTANT":
                long JEConstantValue;
                Label JEConstantLabel;
                List<SInstructionArgument> JEConstantArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                JEConstantValue = getConstantValue(JEConstantArguments,  "constantValue");
                JEConstantLabel = getTargetLabel(JEConstantArguments, "JEConstantLabel");
                instructionToReturn = new JumpEqualConstantInstruction(variable, label, JEConstantValue, JEConstantLabel, instructionNumber, null);
                break;
            case "JUMP_EQUAL_VARIABLE":
                Variable variableName;
                Label JEVariableLabel;
                List<SInstructionArgument> JEVariableArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                variableName = getTargetVariable(JEVariableArguments, "variableName");
                JEVariableLabel = getTargetLabel(JEVariableArguments,  "JEVariableLabel");
                instructionToReturn = new JumpEqualVariableInstruction(variable, label, JEVariableLabel, variableName, instructionNumber, null);
                break;
            case "QUOTE":
                String functionName, functionArguments;
                List<SInstructionArgument> quoteInstructionArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                functionName = getSpecificArgumentByName(quoteInstructionArguments, "functionName");
                functionArguments = getSpecificArgumentByName(quoteInstructionArguments, "functionArguments");
                instructionToReturn = new QuoteInstruction(variable, functionName, functionArguments, label, instructionNumber, null);
                break;
            case "JUMP_EQUAL_FUNCTION":
                String jumpFunctionName, jumpFunctionArguments;
                Label JEFunctionLabel;
                List<SInstructionArgument> JEFunctionArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                jumpFunctionName = getSpecificArgumentByName(JEFunctionArguments, "functionName");
                jumpFunctionArguments = getSpecificArgumentByName(JEFunctionArguments, "functionArguments");
                JEFunctionLabel = getTargetLabel(JEFunctionArguments, "JEFunctionLabel");
                instructionToReturn = new JumpEqualFunctionInstruction(variable, jumpFunctionName, jumpFunctionArguments, label, JEFunctionLabel, instructionNumber, null);
                break;
        }

        return instructionToReturn;
    }

    public static Variable variableMapper(String sVariable) {
        Variable variableToReturn = null;
        char type;
        if(sVariable==null){
            return null;
        }
        type = sVariable.charAt(0);
        int number;

        if(type == 'x' || type == 'X'){
            number = Integer.parseInt(sVariable.substring(1));
            variableToReturn = new VariableImpl(VariableType.INPUT, number, sVariable);
        }
        else if(type == 'y' || type == 'Y'){
            variableToReturn = Variable.RESULT;
        }
        else if(type == 'z' || type == 'Z'){
            number = Integer.parseInt(sVariable.substring(1));
            variableToReturn = new VariableImpl(VariableType.WORK, number, sVariable);
        }
        else if(type == '('){
            variableToReturn = new VariableImpl(VariableType.FUNCTION, 0, sVariable);
        }

        return variableToReturn;
    }

    public static Label labelMapper(String sLabel) {
        Label labelToReturn = null;

        if(sLabel == null){
            return FixedLabel.EMPTY;
        }

        if (sLabel.equalsIgnoreCase("EXIT")) {
            return FixedLabel.EXIT;
        }

        if (sLabel.isEmpty()) {
            return FixedLabel.EMPTY;
        }

        if (sLabel.charAt(0) == 'L' || sLabel.charAt(0) == 'l') {
            int number = Integer.parseInt(sLabel.substring(1));
            return new LabelImpl(number);
        }

        return  labelToReturn;
    }


    private static long getConstantValue(List<SInstructionArgument> arguments, String nameOfArgument) {
        for (SInstructionArgument argument : arguments) {
            if((argument.getName().trim()).equalsIgnoreCase(nameOfArgument)){
                return Long.parseLong(argument.getValue().trim());
            }
        }
        return 0;
    }

    private static String getSpecificArgumentByName(List<SInstructionArgument> arguments, String nameOfArgument){
        for (SInstructionArgument argument : arguments) {
            if((argument.getName().trim()).equalsIgnoreCase(nameOfArgument)){
                return argument.getValue().trim();
            }
        }
        return "";
    }

    private static Label getTargetLabel(List<SInstructionArgument> arguments, String nameOfArgument){
        Label labelToReturn = null;
        for (SInstructionArgument argument : arguments) {
            if((argument.getName().trim()).equalsIgnoreCase(nameOfArgument)){
                labelToReturn = labelMapper(argument.getValue().trim());
                break;
            }
        }
        return labelToReturn;
    }

    private static Variable getTargetVariable(List<SInstructionArgument> arguments, String nameOfArgument){
        Variable variableToReturn = null;
        for (SInstructionArgument argument : arguments) {
            if((argument.getName().trim()).equalsIgnoreCase(nameOfArgument)){
                variableToReturn = variableMapper(argument.getValue().trim());
                break;
            }
        }
        return variableToReturn;
    }


    public static Program fromSFunctionToFunction(SFunction sFunction) {
        String functionName = sFunction.getName().trim();
        String userString = sFunction.getUserString().trim();
        Program program = new Function(functionName, userString, 0);

        SInstructions instructions = sFunction.getSInstructions();
        long instructionCounter = 1;

        List<SInstruction> instructionsList = instructions.getSInstruction();
        for (SInstruction instruction : instructionsList) {
            Instruction mapInstruction;
            mapInstruction = instructionMapper(instruction, instructionCounter);
            program.addInstruction(mapInstruction);
            instructionCounter++;
        }
        return program;
    }

    private static void setMaxDegreeOfExpansionForComplexInstruction(Program program) {
        List<Instruction> instructions = program.getInstructions();
        ProgramImpl programImpl = (ProgramImpl) program;
        List<Program> functions = programImpl.getFunctions();

        for (Program function : functions) {
            List<Instruction> functionInstructions = function.getInstructions();
            for (Instruction instruction : functionInstructions) {
                if(instruction instanceof ComplexInstruction complexInstruction)
                    complexInstruction.updateDegreeOfExpansion(program);
            }
        }

        for (Instruction instruction : instructions) {
            if(instruction instanceof ComplexInstruction complexInstruction)
                complexInstruction.updateDegreeOfExpansion(program);
        }

    }
}

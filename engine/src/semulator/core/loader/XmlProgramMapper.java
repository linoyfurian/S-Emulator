package semulator.core.loader;

import semulator.core.loader.jaxb.schema.generated.*;
import semulator.logic.instruction.*;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.List;

public class XmlProgramMapper {

    public static Program fromSProgramToProgramImpl(SProgram sProgram) {
        String programName = sProgram.getName();
        Program program = new ProgramImpl(programName);

        SInstructions instructions = sProgram.getSInstructions();
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

    private static Instruction instructionMapper(SInstruction instruction, long instructionNumber){
        Instruction instructionToReturn = null;
        String instructionName = instruction.getName();
        instructionName = instructionName.toUpperCase();
        String instructionVariable = instruction.getSVariable();  //TODO GOTO NULL
        Variable variable = variableMapper(instructionVariable);
        String instructionLabel = instruction.getSLabel();
        Label label = labelMapper(instructionLabel);

        switch (instructionName) {
            case "INCREASE":
                instructionToReturn = new IncreaseInstruction(variable, label, instructionNumber);
                break;
            case "DECREASE":
                instructionToReturn = new DecreaseInstruction(variable, label, instructionNumber);
                break;
            case "JUMP_NOT_ZERO":
                Label jnzlabel;
                jnzlabel = getTargetLabel(instruction.getSInstructionArguments().getSInstructionArgument(), "jnzLabel");
                instructionToReturn = new JumpNotZeroInstruction(variable, jnzlabel, label, instructionNumber);
                break;
            case "NEUTRAL":
                instructionToReturn =  new NeutralInstruction(variable, label, instructionNumber);
                break;
            case "ZERO_VARIABLE":
                instructionToReturn = new ZeroVariableInstruction(variable, label, instructionNumber);
                break;
            case "GOTO_LABEL":
                Label gotoLabel;
                gotoLabel = getTargetLabel(instruction.getSInstructionArguments().getSInstructionArgument(), "gotoLabel");
                instructionToReturn = new GoToLabelInstruction(variable, label, gotoLabel, instructionNumber);
                break;
            case "ASSIGNMENT":
                Variable assignedVariable;
                assignedVariable = getTargetVariable(instruction.getSInstructionArguments().getSInstructionArgument(), "assignedVariable");
                instructionToReturn = new AssignmentInstruction(variable, label, instructionNumber, assignedVariable);
                break;
            case "CONSTANT_ASSIGNMENT":
                long constantValue;
                constantValue = getConstantValue(instruction.getSInstructionArguments().getSInstructionArgument(), "constantValue");
                instructionToReturn = new ConstantAssignmentInstruction(variable, label, constantValue, instructionNumber);
                break;
            case "JUMP_ZERO":
                Label jzlabel;
                jzlabel = getTargetLabel(instruction.getSInstructionArguments().getSInstructionArgument(), "jzLabel");
                instructionToReturn = new JumpZeroInstruction(variable, jzlabel, label, instructionNumber);
                break;
            case "JUMP_EQUAL_CONSTANT":
                long JEConstantValue;
                Label JEConstantLabel;
                List<SInstructionArgument> JEConstantArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                JEConstantValue = getConstantValue(JEConstantArguments,  "JEConstantValue");
                JEConstantLabel = getTargetLabel(JEConstantArguments, "JEConstantLabel");
                instructionToReturn = new JumpEqualConstantInstruction(variable, label, JEConstantValue, JEConstantLabel, instructionNumber);
                break;
            case "JUMP_EQUAL_VARIABLE":
                Variable variableName;
                Label JEVariableLabel;
                List<SInstructionArgument> JEVariableArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                variableName = getTargetVariable(JEVariableArguments, "variableName");
                JEVariableLabel = getTargetLabel(JEVariableArguments,  "JEVariableLabel");
                instructionToReturn = new JumpEqualVariableInstruction(variable, label, JEVariableLabel, variableName, instructionNumber);
                break;
        }

        return instructionToReturn;
    }

    private static Variable variableMapper(String sVariable) {
        Variable variableToReturn = null;
        char type = sVariable.charAt(0);
        int number = Integer.parseInt(sVariable.substring(1));

        switch (type) {
            case 'x':
                variableToReturn = new VariableImpl(VariableType.INPUT, number);
                break;
            case 'y':
                variableToReturn = Variable.RESULT;
                break;
            case 'z':
                variableToReturn = new VariableImpl(VariableType.WORK, number);
                break;
        }

        return variableToReturn;
    }

    private static Label labelMapper(String sLabel) {
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
            if(argument.getName().equalsIgnoreCase(nameOfArgument)){
                return Long.parseLong(argument.getValue());
            }
        }
        return 0;
    }

    private static Label getTargetLabel(List<SInstructionArgument> arguments, String nameOfArgument){
        Label labelToReturn = null;
        for (SInstructionArgument argument : arguments) {
            if(argument.getName().equalsIgnoreCase(nameOfArgument)){
                labelToReturn = labelMapper(argument.getValue());
                break;
            }
        }
        return labelToReturn;
    }

    private static Variable getTargetVariable(List<SInstructionArgument> arguments, String nameOfArgument){
        Variable variableToReturn = null;
        for (SInstructionArgument argument : arguments) {
            if(argument.getName().equalsIgnoreCase(nameOfArgument)){
                variableToReturn = variableMapper(argument.getValue());
                break;
            }
        }
        return variableToReturn;
    }
}
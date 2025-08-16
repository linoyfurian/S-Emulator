package semulator.core.loader;

import semulator.core.loader.jaxb.schema.generated.*;
import semulator.logic.instruction.*;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.List;

public class XmlProgramMapper {

    public static ProgramImpl fromSProgramToProgramImpl(SProgram sProgram) {
        String programName = sProgram.getName();
        ProgramImpl program = new ProgramImpl(programName);

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
        String instructionType = instruction.getType();
        String instructionVariable = instruction.getSVariable();
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
                String argumentValue = instruction.getSInstructionArguments().getSInstructionArgument().get(0).getValue();
                jnzlabel = labelMapper(argumentValue);
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
                String gotoLabelValue = instruction.getSInstructionArguments().getSInstructionArgument().get(0).getValue();
                gotoLabel = labelMapper(gotoLabelValue);
                instructionToReturn = new GoToLabelInstruction(variable, label, gotoLabel, instructionNumber);
                break;
            case "ASSIGNMENT":
                Variable assignedVariable;
                String assignedVariableValue = instruction.getSInstructionArguments().getSInstructionArgument().get(0).getValue();
                assignedVariable = variableMapper(assignedVariableValue);
                instructionToReturn = new AssignmentInstruction(variable, label, instructionNumber, assignedVariable);
                break;
            case "CONSTANT_ASSIGNMENT":
                long constantValue;
                String sConstantValue = instruction.getSInstructionArguments().getSInstructionArgument().get(0).getValue();
                constantValue = Long.parseLong(sConstantValue);
                instructionToReturn = new ConstantAssignmentInstruction(variable, label, constantValue, instructionNumber);
                break;
            case "JUMP_ZERO":
                Label jzlabel;
                String jzArgumentValue = instruction.getSInstructionArguments().getSInstructionArgument().get(0).getValue();
                jzlabel = labelMapper(jzArgumentValue);
                instructionToReturn = new JumpZeroInstruction(variable, jzlabel, label, instructionNumber);
                break;
            case "JUMP_EQUAL_CONSTANT":
                long JEConstantValue;
                Label JEConstantLabel;
                List<SInstructionArgument> JEConstantArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                JEConstantValue = getJEConstantValue(JEConstantArguments);
                JEConstantLabel = getJEConstantLabel(JEConstantArguments);
                instructionToReturn = new JumpEqualConstantInstruction(variable, label, JEConstantValue, JEConstantLabel, instructionNumber);
                break;
            case "JUMP_EQUAL_VARIABLE":
                Variable variableName;
                Label JEVariableLabel;
                List<SInstructionArgument> JEVariableArguments = instruction.getSInstructionArguments().getSInstructionArgument();
                variableName = getVariableName(JEVariableArguments);
                JEVariableLabel = getJEVariableLabel(JEVariableArguments);
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
                variableToReturn = new VariableImpl(VariableType.RESULT, number);
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

    //JEConstantValue = getJEConstantValue(JEConstantArguments);
    private static long getJEConstantValue(List<SInstructionArgument> JEConstantArguments){
        for (SInstructionArgument argument : JEConstantArguments) {
            if(argument.getName().equalsIgnoreCase("constantValue")){
                return Long.parseLong(argument.getValue());
            }
        }
        return 0;
    }

    private static Label getJEConstantLabel(List<SInstructionArgument> JEConstantArguments){
        Label labelToReturn = null;
        for (SInstructionArgument argument : JEConstantArguments) {
            if(argument.getName().equalsIgnoreCase("JEConstantLabel")){
                labelToReturn = labelMapper(argument.getValue());
                break;
            }
        }
        return labelToReturn;
    }

    private static Variable getVariableName(List<SInstructionArgument> JEConstantArguments){
        Variable variableToReturn = null;
        for (SInstructionArgument argument : JEConstantArguments) {
            if(argument.getName().equalsIgnoreCase("variableName")){
                variableToReturn = variableMapper(argument.getValue());
                break;
            }
        }
        return variableToReturn;
    }

    private static Label getJEVariableLabel(List<SInstructionArgument> JEConstantArguments){
        Label labelToReturn = null;
        for (SInstructionArgument argument : JEConstantArguments) {
            if(argument.getName().equalsIgnoreCase("JEVariableLabel")){
                labelToReturn = labelMapper(argument.getValue());
                break;
            }
        }
        return labelToReturn;
    }
}




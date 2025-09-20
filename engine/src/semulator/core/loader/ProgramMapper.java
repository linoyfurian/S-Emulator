package semulator.core.loader;

import semulator.api.dto.InstructionDraft;
import semulator.api.dto.ProgramDraft;
import semulator.core.loader.jaxb.schema.generated.SInstructionArgument;
import semulator.logic.instruction.*;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;

import java.util.List;

public class ProgramMapper {
    private ProgramMapper(){}

    public static Program mapProgram(ProgramDraft program){
        String programName = program.getProgramName();
        Program programToReturn = new ProgramImpl(programName, 0);
        List<InstructionDraft> instructions = program.getInstructions();

        long instructionNumber = 1;

        Instruction newInstruction;
        for(InstructionDraft instruction : instructions){
            newInstruction = mapInstruction(instruction, instructionNumber);
            programToReturn.addInstruction(newInstruction);
            instructionNumber++;
        }

        return programToReturn;
    }

    public static Instruction mapInstruction(InstructionDraft instruction, long instructionNumber){
        Instruction instructionToReturn = null;
        String instructionName = instruction.getName();

        String instructionVariable = instruction.getMainVariable();
        if(instructionVariable!=null)
            instructionVariable = instructionVariable.trim();
        if(instructionVariable.equals(""))
            instructionVariable = "y";
        Variable variable = XmlProgramMapperV2.variableMapper(instructionVariable);
        String instructionLabel = instruction.getMainLabel();
        if(instructionLabel!=null)
            instructionLabel = instructionLabel.trim();
        Label label = XmlProgramMapperV2.labelMapper(instructionLabel);

        switch (instructionName) {
            case "INCREASE":
                instructionToReturn = new IncreaseInstruction(variable, label, instructionNumber, null);
                break;
            case "DECREASE":
                instructionToReturn = new DecreaseInstruction(variable, label, instructionNumber, null);
                break;
            case "JUMP_NOT_ZERO":
                Label jnzlabel;
                jnzlabel = XmlProgramMapperV2.labelMapper(instruction.getAdditionalLabel().trim());
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
                gotoLabel = XmlProgramMapperV2.labelMapper(instruction.getAdditionalLabel().trim());
                instructionToReturn = new GoToLabelInstruction(variable, label, gotoLabel, instructionNumber, null);
                break;
            case "ASSIGNMENT":
                Variable assignedVariable;
                assignedVariable = XmlProgramMapperV2.variableMapper(instruction.getAdditionalVariable().trim());
                instructionToReturn = new AssignmentInstruction(variable, label, instructionNumber, assignedVariable, null);
                break;
            case "CONSTANT_ASSIGNMENT":
                long constantValue;
                constantValue = instruction.getConstantValue();
                instructionToReturn = new ConstantAssignmentInstruction(variable, label, constantValue, instructionNumber, null);
                break;
            case "JUMP_ZERO":
                Label jzlabel;
                jzlabel = XmlProgramMapperV2.labelMapper(instruction.getAdditionalLabel().trim());
                instructionToReturn = new JumpZeroInstruction(variable, jzlabel, label, instructionNumber, null);
                break;
            case "JUMP_EQUAL_CONSTANT":
                long JEConstantValue;
                Label JEConstantLabel;
                JEConstantValue = instruction.getConstantValue();
                JEConstantLabel = XmlProgramMapperV2.labelMapper(instruction.getAdditionalLabel().trim());
                instructionToReturn = new JumpEqualConstantInstruction(variable, label, JEConstantValue, JEConstantLabel, instructionNumber, null);
                break;
            case "JUMP_EQUAL_VARIABLE":
                Variable variableName;
                Label JEVariableLabel;
                variableName = XmlProgramMapperV2.variableMapper(instruction.getAdditionalVariable().trim());
                JEVariableLabel = XmlProgramMapperV2.labelMapper(instruction.getAdditionalLabel().trim());
                instructionToReturn = new JumpEqualVariableInstruction(variable, label, JEVariableLabel, variableName, instructionNumber, null);
                break;
        }

        return instructionToReturn;
    }
}

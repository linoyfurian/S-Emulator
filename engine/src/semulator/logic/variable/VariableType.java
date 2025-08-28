package semulator.logic.variable;

import java.io.Serializable;

public enum VariableType implements Serializable {
    RESULT {
        @Override
        public String getVariableRepresentation(int number) {
            return "y";
        }
    },
    INPUT {
        @Override
        public String getVariableRepresentation(int number) {
            return "x" + number;
        }
    },
    WORK {
        @Override
        public String getVariableRepresentation(int number) {
            return "z" + number;
        }
    };

    public abstract String getVariableRepresentation(int number);
}

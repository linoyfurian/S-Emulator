package dto;

public class ProgramInfo {
   private final String name;
   private final String userName;
   private final int instructionsNumber;
   private final int maxDegree;
   private final double averageCredits;
   private final int runsNumber;

   public ProgramInfo(String name, String userName, int instructionsNumber, int maxDegree, double averageCredits, int runsNumber) {
       this.name = name;
       this.userName = userName;
       this.instructionsNumber = instructionsNumber;
       this.maxDegree = maxDegree;
       this.averageCredits = averageCredits;
       this.runsNumber = runsNumber;
   }

   public String getName() {
       return name;
   }

   public String getUserName() {
       return userName;
   }

   public int getInstructionsNumber() {
       return instructionsNumber;
   }

   public int getMaxDegree() {
       return maxDegree;
   }

   public double getAverageCredits() {
       return averageCredits;
   }

   public int getRunsNumber() {
       return runsNumber;
   }
}

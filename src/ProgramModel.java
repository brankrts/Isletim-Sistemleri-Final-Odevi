
import java.util.List;

public class ProgramModel {

   public String programName;
   public int commandCount;
   public List<SubProcess> subProcesses;

   public ProgramModel(String programName, int commandCount, List<SubProcess> subProcesses) {
      this.programName = programName;
      this.commandCount = commandCount;
      this.subProcesses = subProcesses;
   }

}

public class SubProcess {
    
    private String subProcessName;
    private int subProcessActivateTime;

    public SubProcess(String subProcessName, int subProcessActivateTime) {

        this.subProcessActivateTime = subProcessActivateTime;
        this.subProcessName = subProcessName;

    }

    public String getSubProcessName() {
        return this.subProcessName;
    }

    public int getSubProcessActivateTime() {
        return this.subProcessActivateTime;
    }

}

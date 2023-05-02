import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PCB {

    private int processID;
    private StatusEnum processStatus;
    private int programCounter;
    private Instant createdAt;
    private int cpuUsage;
    private boolean isNotSlice;
    private String processName;
    private int commandCount;
    private int timeSlice;

    private List<SubProcess> subProcesses;
    private CustomQueue<PCB> readyQueue;
    private CustomQueue<PCB> diskQueue;
    private CustomQueue<PCB> screenQueue;
    private CustomQueue<PCB> ethernetQueue;
    private CustomQueue<SubProcess> subProcessQueue = new CustomQueue<SubProcess>();
    private Map<String, CustomQueue<PCB>> queueMap;

    public PCB(ProgramModel model, QueueModel queueModel) {

        this.processID = new Random().nextInt(10000);
        this.processStatus = StatusEnum.New;
        this.programCounter = 1;
        this.timeSlice = 0;
        this.createdAt = Instant.now();
        this.cpuUsage = 0;
        this.processName = model.programName;
        this.commandCount = model.commandCount;
        this.subProcesses = model.subProcesses;
        this.readyQueue = queueModel.readQueue;
        this.screenQueue = queueModel.screenQueue;
        this.diskQueue = queueModel.diskQueue;
        this.ethernetQueue = queueModel.ethernetQueue;
        this.sortSubProcess();
        this.setQueueMap();

    }

    private void sortSubProcess() {
        Comparator<SubProcess> sortByProcessActivationTime = new Comparator<SubProcess>() {
            public int compare(SubProcess s1, SubProcess s2) {
                return s1.getSubProcessActivateTime() - s2.getSubProcessActivateTime();
            }
        };
        this.subProcesses.sort(sortByProcessActivationTime);
        for (SubProcess subProcess : this.subProcesses) {
            this.subProcessQueue.push(subProcess);

        }
    }

    public boolean start() throws InterruptedException {

        this.isNotSlice = true;

        SubProcess subProcess = this.subProcessQueue.pop();

        if (isNotSlice) {

            this.processStatus = StatusEnum.Running;

            while (this.commandCount >= this.programCounter) {
                if (subProcess != null) {
                    if (subProcess.getSubProcessActivateTime() == programCounter) {

                        PcpThread subprocessThread = new PcpThread(
                                () -> {
                                    this.queueMap.get(subProcess.getSubProcessName()).push(this);
                                    this.processStatus = StatusEnum.Waiting;
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    this.queueMap.get(subProcess.getSubProcessName()).pop();
                                    this.readyQueue.push(this);
                                    this.processStatus = StatusEnum.Ready;

                                });
                        subprocessThread.start();

                        return false;

                    }

                }

                Thread.sleep(1000);
                this.incrementCpuUsage();
                this.incrementProgramCounter();
                this.incrementTimeSlice();

                this.isNotSlice = this.timeSlice == 5 ? false : true;
                if (!this.isNotSlice) {
                    this.readyQueue.push(this);
                    this.timeSlice = 0;
                    return false;
                }
                return true;

            }

        }
        this.processStatus = StatusEnum.Terminated;
        return false;

    }

    public int getProcessID() {
        return this.processID;
    }

    public Instant getCratedAt() {
        return this.createdAt;
    }

    public StatusEnum getStatus() {
        return this.processStatus;
    }

    public int getProgramCounter() {
        return this.programCounter;
    }

    public int getCpuUsage() {
        return this.cpuUsage;
    }

    public String getProcessName() {
        return this.processName;
    }

    public int getCommandCount() {
        return this.commandCount;
    }

    public List<SubProcess> getSubProcesses() {
        return this.subProcesses;
    }

    public void incrementProgramCounter() {
        this.programCounter++;
    }

    public void incrementTimeSlice() {
        this.timeSlice++;
    }

    public void incrementCpuUsage() {
        this.cpuUsage++;
    }

    public void setStatus(StatusEnum status) {
        this.processStatus = status;
    }

    private void setQueueMap() {

        this.queueMap = new HashMap<String, CustomQueue<PCB>>();
        this.queueMap.put("disk", this.diskQueue);
        this.queueMap.put("ethernet", this.ethernetQueue);
        this.queueMap.put("ekran", this.screenQueue);

    }

    public void setSlice() {
        this.isNotSlice = false;
    }

    public String getProcessInformation(int time) {

        return "A.exe isimli prosesin " + time + "." + "Saniyedeki PCB bilgileri su sekildedir:\nProses numarasi: "
                + this.getProcessID() + "\nProsess durumu: " + this.getStatus().name() + "\nProgram sayaci: "
                + this.getProgramCounter() + "\nKullanilan CPU miktari: " + this.getCpuUsage() + " saniye\n"
                + "Prosesin yaratilmasindan itibaren gecen sure: "
                + Duration.between(this.createdAt, Instant.now()).getSeconds() + " saniye";

    }

}

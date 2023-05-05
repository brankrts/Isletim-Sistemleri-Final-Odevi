
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cpu {

    private List<ProgramModel> programModels;
    private List<EventModel> eventModels;
    private QueueModel queueModel;
    public static volatile int systemTime;
    private CustomQueue<EventModel> jobQueue = new CustomQueue<EventModel>();
    private List<PCB> activePcbs = new ArrayList<PCB>();
    private List<PCB> pcbList = new ArrayList<PCB>();
    private volatile boolean isSystemActive = true;
    private int systemStopTime;
    private String targetProcess;
    private int processCount;

    public Cpu(List<ProgramModel> programModels, List<EventModel> eventModels, QueueModel queueModel,
            int systemStopTime, String targetProcess) {
        this.systemStopTime = systemStopTime;
        this.targetProcess = targetProcess;
        this.programModels = programModels;
        this.eventModels = eventModels;
        this.queueModel = queueModel;
        this.processCount = eventModels.size();
        this.sortEvents();

    }

    private void init() {
        systemTime = 0;
    }

    public void start() throws InterruptedException {
        init();
        this.startSystemTime();
        this.processingJobQueue();
        this.processingReadyQueue();
    }

    private void startSystemTime() throws InterruptedException {
        PcpThread systemTimeCounter = new PcpThread(() -> {
            while (getSystemTime() <= this.systemStopTime) {

                incrementSystemTime();
                if (getSystemTime() <= this.systemStopTime) {
                    System.out
                            .println(
                                    "\nSystem time : " + getSystemTime() + " and  Jobqueue length : "
                                            + this.getJobQueue().length());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }
            this.isSystemActive = false;
            Constants.isSystemActive = false;
            // Thread.getAllStackTraces().keySet().forEach(Thread::interrupt);

        });
        systemTimeCounter.start();

    }

    private void processingJobQueue() {

        PcpThread cpuJobQueueThread = new PcpThread(() -> {
            while (this.getJobQueue().length() > 0 & Constants.isSystemActive) {

                EventModel currentEvent = this.getJobQueue().peek();
                System.out.println("CPU aktarimina uygun mu ? " + (getSystemTime() == currentEvent.activationTime));

                if (getSystemTime() == currentEvent.activationTime) {

                    currentEvent = this.getJobQueue().pop();
                    for (ProgramModel model : this.programModels) {

                        if (model.programName.equals(currentEvent.eventName)) {
                            PCB newPcb = new PCB(model, queueModel, getSystemTime());
                            this.queueModel.readQueue.push(newPcb);
                            this.activePcbs.add(newPcb);
                            this.pcbList.add(newPcb);
                        }

                    }

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        cpuJobQueueThread.start();

    }

    private void processingReadyQueue() {

        int isDone = 0;
        while (getIsSystemActive()) {
            isDone = 0;

            for (PCB pcb : this.pcbList) {
                if (pcb.getStatus() == StatusEnum.Terminated) {
                    isDone++;
                }

            }
            if (isDone == this.processCount & this.queueModel.readQueue.length() == 0) {
                Constants.workingOnCpu = "";
                break;

            }
            if (this.queueModel.readQueue.length() <= 0) {
                continue;
            }

            if (Constants.isContinue == false) {

                PCB currentProcess = this.queueModel.readQueue.pop();

                try {
                    Constants.workingOnCpu = currentProcess.getProcessName();
                    System.out.println("\n\n----------------------------------------");
                    System.out.println("CPU'ya aktarilan process --> " + currentProcess.getProcessName());
                    System.out.println("----------------------------------------\n");
                    currentProcess.setStatus(StatusEnum.Running);
                    currentProcess.start();

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                if (Constants.isContinue == false && currentProcess.getStatus() == StatusEnum.Terminated) {
                    this.activePcbs.removeIf(p -> p.getStatus() == StatusEnum.Terminated);

                }

            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

        }

        this.queueInformation();
        this.processInformation(this.targetProcess);

    }

    public void processInformation(String process) {

        for (PCB pcb : this.pcbList) {
            if (pcb.getProcessName().equals(process)) {
                System.out.println(pcb.getProcessInformation(this.systemStopTime) + "\n");

            }
        }

    }

    static synchronized void incrementSystemTime() {

        systemTime++;
    }

    static synchronized int getSystemTime() {
        return systemTime;
    }

    private synchronized CustomQueue<EventModel> getJobQueue() {
        return this.jobQueue;
    }

    private synchronized boolean getIsSystemActive() {
        return this.isSystemActive;
    }

    private void sortEvents() {

        Comparator<EventModel> sortByEventActivationTime = new Comparator<EventModel>() {
            public int compare(EventModel e1, EventModel e2) {
                return e1.activationTime - e2.activationTime;
            }
        };
        this.eventModels.sort(sortByEventActivationTime);

        for (EventModel eventModel : this.eventModels) {
            this.getJobQueue().push(eventModel);

        }
        // System.out.println("Job queue is sorted done !");

    }

    private String getProcessOnQueue(CustomQueue<PCB> queue, String queueName) {
        String processes = " ";
        for (PCB pcb : queue.getQueue()) {
            processes += pcb.getProcessName() + " ";

        }
        return queueName + processes;

    }

    private void queueInformation() {
        String currentPCBs = "";
        for (PCB pcb : this.activePcbs) {
            currentPCBs += pcb.getProcessName() + " ";
        }
        String information = "Cpu'da calisan proses: " + Constants.workingOnCpu + "\n"
                + this.getProcessOnQueue(this.queueModel.readQueue, "Ready kuyrugu:") + "\n"
                + this.getProcessOnQueue(this.queueModel.screenQueue, "Ekran kuyrugu:") + "\n"
                + this.getProcessOnQueue(this.queueModel.diskQueue, "Disk kuyrugu:") + "\n"
                + this.getProcessOnQueue(this.queueModel.ethernetQueue, "Ethernet kuyrugu:")
                + "\nPCB'si bulunan Prosesler: " + currentPCBs;

        System.out.println(information);
    }

}

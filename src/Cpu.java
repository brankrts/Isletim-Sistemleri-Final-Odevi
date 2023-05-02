
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cpu {

    private List<ProgramModel> programModels;
    private List<EventModel> eventModels;
    private QueueModel queueModel;
    private static int systemTime = 0;
    private CustomQueue<EventModel> jobQueue = new CustomQueue<EventModel>();
    private List<PCB> activePcbs = new ArrayList<PCB>();
    private boolean isSystemActive = true;
    private int systemStopTime;
    private String targetProcess;

    public Cpu(List<ProgramModel> programModels, List<EventModel> eventModels, QueueModel queueModel,
            int systemStopTime, String targetProcess) {
        this.targetProcess = targetProcess;
        this.systemStopTime = systemStopTime;
        this.programModels = programModels;
        this.eventModels = eventModels;
        this.queueModel = queueModel;
        this.sortEvents();

    }

    public void start() throws InterruptedException {
        PcpThread systemTimeCounter = new PcpThread(() -> {
            while (systemTime != this.systemStopTime) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                incrementSystemTime();
                System.out.println("System time : " + systemTime);

            }
            this.isSystemActive = false;
        });
        systemTimeCounter.start();

        PcpThread cpuJobQueueThread = new PcpThread(() -> {

            while (this.jobQueue.length() != 0 & isSystemActive) {

                EventModel event = this.jobQueue.peek();

                if (event.activationTime == systemTime) {

                    EventModel currentEvent = this.jobQueue.pop();

                    for (ProgramModel model : this.programModels) {

                        if (model.programName.equals(currentEvent.eventName)) {
                            PCB newPcb = new PCB(model, queueModel);
                            this.queueModel.readQueue.push(newPcb);
                            this.activePcbs.add(newPcb);
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

        while (isSystemActive) {
            boolean currentProcessState = true;

            if (this.queueModel.readQueue.length() != 0 ) {
                PCB currentProcess = this.queueModel.readQueue.pop();

                try {
                    System.out.println(currentProcess.getProcessName());
                    currentProcessState = currentProcess.start();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

                if (!currentProcessState & currentProcess.getStatus() == StatusEnum.Terminated) {
                    this.activePcbs.removeIf(p -> p.getStatus() == StatusEnum.Terminated);
                    continue;
                } else {

                    this.queueModel.readQueue.push(currentProcess);
                    continue;
                }

            }
            // incrementSystemTime();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            if (this.activePcbs.size() == 0 & systemTime > 5) {

                break;
            }

        }

        for (PCB activePcb : this.activePcbs) {
            if (activePcb != null) {

                if (activePcb.getProcessName().equals(this.targetProcess)) {
                    System.out.println("Girdi");
                    System.out.println(activePcb.getProcessInformation(systemStopTime));
                }
            }
        }

    }

    static void incrementSystemTime() {

        systemTime++;
    }

    private void sortEvents() {

        Comparator<EventModel> sortByEventActivationTime = new Comparator<EventModel>() {
            public int compare(EventModel e1, EventModel e2) {
                return e1.activationTime - e2.activationTime;
            }
        };
        this.eventModels.sort(sortByEventActivationTime);

        for (EventModel eventModel : this.eventModels) {
            this.jobQueue.push(eventModel);

        }

    }

}

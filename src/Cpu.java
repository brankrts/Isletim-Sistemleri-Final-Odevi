import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cpu {

    private List<ProgramModel> programModels;
    private List<EventModel> eventModels;
    private QueueModel queueModel;
    static int systemTime = 0;
    private CustomQueue<EventModel> jobQueue;
    private List<PCB> activePcbs = new ArrayList<PCB>();

    public Cpu(List<ProgramModel> programModels, List<EventModel> eventModels, QueueModel queueModel) {
        this.programModels = programModels;
        this.eventModels = eventModels;
        this.queueModel = queueModel;
        this.sortEvents();

    }

    public void start() throws InterruptedException {

        while (this.jobQueue.length() != 0) {

            EventModel event = jobQueue.peek();

            if (event.activationTime == systemTime) {

                this.jobQueue.pop();

                for (ProgramModel model : this.programModels) {

                    if (model.programName == event.eventName) {
                        PCB newPcb = new PCB(model, queueModel);
                        this.queueModel.readQueue.push(newPcb);
                        this.activePcbs.add(newPcb);
                    }

                }
            }
        }

        while (true) {

            if (this.queueModel.readQueue.length() != 0) {
                PCB currentProcess = this.queueModel.readQueue.pop();
                boolean currentProcessState = currentProcess.start();

                if (!currentProcessState & currentProcess.getStatus() == StatusEnum.Terminated) {
                    this.activePcbs.removeIf(p -> p.getStatus() == StatusEnum.Terminated);
                } else
                    continue;

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

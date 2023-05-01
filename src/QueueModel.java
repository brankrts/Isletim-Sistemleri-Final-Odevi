
public class QueueModel {

    public CustomQueue<PCB> readQueue;
    public CustomQueue<PCB> screenQueue;
    public CustomQueue<PCB> ethernetQueue;
    public CustomQueue<PCB> diskQueue;

    public QueueModel(CustomQueue<PCB> readQueue, CustomQueue<PCB> screenQueue, CustomQueue<PCB> ethernetQueue,
            CustomQueue<PCB> diskQueue) {

        this.readQueue = readQueue;
        this.screenQueue = screenQueue;
        this.ethernetQueue = ethernetQueue;
        this.diskQueue = diskQueue;

    }

}

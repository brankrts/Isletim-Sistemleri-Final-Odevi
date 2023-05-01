public class PcpThread extends Thread {

    private PCB pcb;

    public PcpThread(PCB pcb) {

        this.pcb = pcb;

    }

    public void run() {

        try {
            this.pcb.start();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

}

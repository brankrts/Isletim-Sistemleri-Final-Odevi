public class PcpThread extends Thread {

    private Runnable function;

    public PcpThread(Runnable function) {

        this.function = function;

    }

    public void run() {

        this.function.run();
    }

}

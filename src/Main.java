import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        List<String> inputTxt = new ArrayList<String>();
        List<String> applications = new ArrayList<String>();
        List<String> events = new ArrayList<String>();
        List<EventModel> eventModels = new ArrayList<EventModel>();
        List<ProgramModel> programModels = new ArrayList<ProgramModel>();
        Scanner fileScanner = new Scanner(new File(Constants.fileName));

        CustomQueue<PCB> readyQueue = new CustomQueue<PCB>();
        CustomQueue<PCB> screenQueue = new CustomQueue<PCB>();
        CustomQueue<PCB> ethernetQueue = new CustomQueue<PCB>();
        CustomQueue<PCB> diskQueue = new CustomQueue<PCB>();

        QueueModel queueModel = new QueueModel(readyQueue, screenQueue, ethernetQueue, diskQueue);

        while (fileScanner.hasNext()) {

            inputTxt.add(fileScanner.nextLine());
        }
        int applicationsIndex = inputTxt.indexOf(Constants.applications);
        int eventIndex = inputTxt.indexOf(Constants.events);

        applications = inputTxt.subList(applicationsIndex + 1, eventIndex);
        events = inputTxt.subList(eventIndex + 1, inputTxt.size());

        for (String event : events) {
            String[] parsingEvent = event.split(" ");
            eventModels.add(new EventModel(parsingEvent[1], Integer.parseInt(parsingEvent[2])));
        }

        for (String application : applications) {
            String[] parsingApplication = application.split(" ");
            String programName = parsingApplication[0];
            int commandCount = Integer.parseInt(parsingApplication[1]);
            List<SubProcess> subProcesses = new ArrayList<SubProcess>();

            for (int i = 2; i < parsingApplication.length; i += 2) {

                subProcesses.add(new SubProcess(parsingApplication[i], Integer.parseInt(parsingApplication[i + 1])));
            }

            programModels.add(new ProgramModel(programName, commandCount, subProcesses));

        }

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("\nLutfen sistemin durumu gormek isrediginiz saniyeyi giriniz.: ");
            int stopTIme = scanner.nextInt();
            scanner.nextLine();
            System.out.print(
                    "\n" + stopTIme + ". saniyededeki PCB'sini goruntulemek istediginiz proses ismini giriniz.: ");
            String targetProcess = scanner.nextLine();
            System.out.println("\n ");

            Cpu cpu = new Cpu(programModels, eventModels, queueModel, stopTIme, targetProcess);
            cpu.start();

        }

    }

}

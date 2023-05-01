import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cpu {
    public static void main(String[] args) throws FileNotFoundException {
        List<String> uygulamalar = new ArrayList<String>();
        List<String> olaylar = new ArrayList<String>();
        Scanner fileScanner = new Scanner(new File(Constants.fileName));

        while(fileScanner.hasNext()) {

            String next = fileScanner.nextLine();
            System.out.println(next);
        }


    }



}

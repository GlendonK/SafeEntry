/*
 * SafeEntryDatabase
 * 
 * CSC 3004 lab assignment
 * 
 * author: Glendon Keh 1901884, Aloysius Goh 1902774
 * 
 * submission date: 18 june 2021 
 * 
 */

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Officer {
    public static void main(String[] args) {
        try {
            Client officer = new Client();
            while (true) {
                int choose;
                System.out.println("choose. 1(set location and time of infected), 2(read all entries)");
                Scanner scan = new Scanner(System.in);          // cant close this as it needs to run constantly in while loop.
                choose = scan.nextInt();

                if (choose == 1) {
                    System.out.println("Input location: ");
                    Scanner officerInput = new Scanner(System.in);
                    String officerLoc = officerInput.nextLine();

                    final String timeExample = LocalDateTime.now().toString();
                    System.out.println("eg: " + timeExample);
                    System.out.println("Input start time (yyy-mm-ddThh:mm:ss): ");
                    String startTime = officerInput.nextLine();

                    System.out.println("Input end time (yyy-mm-ddThh:mm:ss): ");
                    String endTime = officerInput.nextLine();
                    
                    officer.officerUpdateInfectedLocation(officerLoc, startTime, endTime);
                } else if (choose == 2) {
                    officer.officerRead();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

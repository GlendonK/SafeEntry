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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {

    private static String location;
    private static String name;
    private static String NRIC;

    
    public static void main(String[] args) {
        try {
            Client user = new Client();
            while (true) {
                System.out.println("choose. 1(check in) 2(check in with family) 3(check out) 4(read user history)");
                Scanner scan = new Scanner(System.in);      // cannot close as its constantly being used.
                int choose = scan.nextInt();
                
                if (choose == 1) {
                    System.out.println("Enter Location: ");
                    Scanner scanner = new Scanner(System.in);
                    location = scanner.nextLine();

                    System.out.println("Enter Your NRIC: ");
                    NRIC = scanner.nextLine();

                    System.out.println("Enter Your Name");
                    name = scanner.nextLine();
                    user.userCheckIn(location, NRIC, name);
                } else if (choose == 2) {

                    List<String> NRICList = new ArrayList<>();
                    List<String> nameList = new ArrayList<>();

                    System.out.println("location: ");
                    Scanner scanner = new Scanner(System.in);
                    location = scanner.nextLine();
                    

                    System.out.println("Number of additional people (exlude yourself): ");
                    int pax = scanner.nextInt();

                    for (int i = 0; i<=pax; i++) {
                        if (i == 0) {

                            /**
                             * check in the user.
                             * the user who initiate the check in will check out for the familiy.
                             */
                            
                            System.out.println("Your NRIC: ");
                            Scanner scannerUser = new Scanner(System.in);
                            NRIC = scannerUser.nextLine();
                            NRICList.add(NRIC);                

                            System.out.println("Your Name: ");
                            name = scannerUser.nextLine();
                            nameList.add(name);

                            
                            
                            
                        } else if (i > 0 ) {
                            /**
                             * check in family members.
                             */
                            System.out.println("Family member " + i +" NRIC: ");
                            Scanner famScanner = new Scanner(System.in);
                            String famNRIC = famScanner.nextLine();
                            NRICList.add(famNRIC);
                            

                            System.out.println("Family member " + i +" Name: ");
                            String famName = famScanner.nextLine();
                            nameList.add(famName);

                        }

                    }
                    user.userFamCheckIn(location, pax, NRICList, nameList);

                } else if (choose == 3) {
                    user.userCheckOut(NRIC, name, location);

                } else if (choose == 4) {
                    user.userRead(NRIC);
                }
                
            }
        } catch (RemoteException e) {
            System.out.println("\nREMOTE EXCEPTION\n");
        }
    }
}

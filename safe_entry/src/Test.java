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
import java.util.ArrayList;
import java.util.List;

/**
 * Test class
 */
public class Test {

    private final List<Client> clientList1 = new ArrayList<Client>();
    private final List<Client> clientList2 = new ArrayList<Client>();

    private final String[] testNRIC1 = {"s1234567a", "s1234567b", "s1234567c", "s1234567d", "s1234567e"};
    private final String[] testNRIC2 = {"s1234567f", "s1234567g", "s1234567h", "s1234567i", "s1234567j"};
    
    private final String[] testNames1 = {"Alice", "Bob", "Charlie", "Dog", "Elephant"};
    private final String[] testNames2 = { "France", "Glen", "Hilton", "Iceland", "Jack"};

    private final String[] testLocations1 = {"nyp", "tp", "rp", "sp", "sit"};
    private final String[] testLocations2 = {"nyp", "tp", "rp", "sp", "sit"};

    private final List<Client> famClientList = new ArrayList<Client>();

    private final String[] famNRIC1 = {"s1234567k", "s1234567l", "s1234567m", "s1234567n", "s1234567o"};
    private final String[] famNRIC2 = {"s1234567p", "s1234567q", "s1234567r", "s1234567s", "s1234567t"};

    private final String[] famNames1 = {"Khan", "Loh", "Mike", "Nike", "Octopus"};
    private final String[] famNames2 = {"Pines", "Qiang", "Rocco", "Singer", "Top"};

    private final String startTime = LocalDateTime.now().toString();
    private final String endTime = "2021-12-12T12:12:12";

    private static Server server;

    private static List<Long> timeList = new ArrayList<Long>();

    public Test() throws RemoteException, InterruptedException {
        //server = new Server();      // initialise a server

        /**
         * 2 list of clients executing concurrently on 2 different threads.
         */
        for (int i =0; i < 5; i++) {
            Client user = new Client();
            clientList1.add(user);
        }

        for (int i =0; i < 5; i++) {
            Client user = new Client();
            clientList2.add(user);
        }

        /**
         * theads 1 and 2 runs individual user check in of 5 users each.
         * @test: check in is working, thread execute properly, mutex is mainatined, callback confirm message works.
         */
        Thread thread1 = new Thread(new Runnable(){

            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    long startTime = System.currentTimeMillis();
                    clientList1.get(i).userCheckIn(testLocations1[i], testNRIC1[i], testNames1[i]);

                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    System.out.println("\nCheck in time: " + processTime + "\n");
                    timeList.add(processTime);
                }   
            }
        });
        thread1.start();

        Thread thread2 = new Thread(new Runnable(){

            @Override
            public void run() {
                for (int i =0; i < 5; i++) {
                    long startTime = System.currentTimeMillis();

                    clientList2.get(i).userCheckIn(testLocations2[i], testNRIC2[i], testNames2[i]);

                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    System.out.println("\nCheck in time: " + processTime + "\n");
                    timeList.add(processTime);
                }
            }
            
        });
        thread2.start();

        thread1.join();
        thread2.join();

        /**
         * thread 3 and 4 runs individual client check out of 5 clients each.
         * @test: check out works, callback confirm message works, threads execute properly, mutex is maintained. 
         */
        Thread thread3 = new Thread(new Runnable(){

            @Override
            public void run() {
                

                for (int i = 0; i < 5; i++) {
                    long startTime = System.currentTimeMillis();

                    clientList1.get(i).userCheckOut(testNRIC1[i], testNames1[i], testLocations1[i]);

                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    System.out.println("\nCheck out time: " + processTime + "\n");
                    timeList.add(processTime);
                }   
            }
        });
        thread3.start();

        Thread thread4 = new Thread(new Runnable(){

            @Override
            public void run() {
                for (int i =0; i < 5; i++) {
                    long startTime = System.currentTimeMillis();

                    clientList2.get(i).userCheckOut(testNRIC2[i], testNames2[i], testLocations2[i]);

                    long endTime = System.currentTimeMillis();
                    long processTime = endTime - startTime;
                    System.out.println("\nCheck out time: " + processTime + "\n");
                    timeList.add(processTime);
                }   
            }
        });
        thread4.start();

        thread3.join();
        thread4.join();

        /**
         * user read check in history from database.
         * only retrieve his own check in and family check in through callback.
         * @test: callback give user only infomation related to user and user family.
         */
        for (int i = 0; i < famNRIC1.length; i++) {
            clientList1.get(i).userRead(testNRIC1[i]);
            clientList1.get(i).userRead(testNRIC1[i]);
        }

        /**
         * family check in and out.
         * 2 families check in and out on 2 concurrent threads.
         */
        for (int i =0; i < 2; i++) {
            Client user = new Client();
            famClientList.add(user);
        }

        List<String> famNRICList1 = new ArrayList<String>();
        List<String> famNRICList2 = new ArrayList<String>();

        List<String> famNameList1 = new ArrayList<String>();
        List<String> famNameList2 = new ArrayList<String>();

        /**
         * add nric and names of family to list.
         */
        for (int i = 0; i < famNRIC1.length; i++) {
            famNRICList1.add(famNRIC1[i]);
            famNameList1.add(famNames1[i]);

            famNRICList2.add(famNRIC2[i]);
            famNameList2.add(famNames2[i]);
        }

        /**
         * thread 5 and 6 runs family check in concurrently.
         * @test: family check in is working, thread execute properly, mutext is maintained, callback confirm message works.
         */
        Thread thread5 = new Thread(new Runnable(){

            @Override
            public void run() {

                long startTime = System.currentTimeMillis();
                
                famClientList.get(0).userFamCheckIn(testLocations1[0], famNRIC1.length, famNRICList1, famNameList1);

                long endTime = System.currentTimeMillis();
                long processTime = endTime - startTime;
                System.out.println("\nFamily Check in time: " + processTime + "\n");
                timeList.add(processTime);
            }
            
        });
        thread5.start();

        Thread thread6 = new Thread(new Runnable(){

            @Override
            public void run() {

                long startTime = System.currentTimeMillis();

                famClientList.get(1).userFamCheckIn(testLocations1[1], famNRIC2.length, famNRICList2, famNameList2);

                long endTime = System.currentTimeMillis();
                long processTime = endTime - startTime;
                System.out.println("\nFamily Check in time: " + processTime + "\n");
                timeList.add(processTime);
                
            }
            
        });
        thread6.start();

        thread5.join();
        thread6.join();
        
        /**
         * thread 7 and 8 runs family check out concurrently.
         * @test: family check out working, thread execute properly, mutex is maintained, callback confirm message works. 
         */
        Thread thread7 = new Thread(new Runnable(){

            @Override
            public void run() {

                long startTime = System.currentTimeMillis();

                famClientList.get(0).userCheckOut(famNRICList1.get(0), famNameList1.get(0), testLocations1[0]);

                long endTime = System.currentTimeMillis();
                long processTime = endTime - startTime;
                System.out.println("\nFamily Check out time: " + processTime + "\n");
                timeList.add(processTime);
                
            }
            
        });
        thread7.start();

        Thread thread8 = new Thread(new Runnable(){

            @Override
            public void run() {

                long startTime = System.currentTimeMillis();

                famClientList.get(1).userCheckOut(famNRICList2.get(0), famNameList2.get(0), testLocations1[1]);

                long endTime = System.currentTimeMillis();
                long processTime = endTime - startTime;
                System.out.println("\nFamily Check out time: " + processTime + "\n");
                timeList.add(processTime);
                
            }
            
        });
        thread8.start();

        thread7.join();
        thread8.join();

        /**
         * MOH officer.
         */
        Client officer = new Client();
        /**
         * update the infected locations. Clients who been to the location at the time frame
         * will receive notification through callback.
         * @test: update infected location works, callback to clients working.
         */
        officer.officerUpdateInfectedLocation(testLocations1[0], startTime, endTime);

        /**
         * officer get all entries of database through callback.
         * @test: read all entries of database.
         */
        officer.officerRead();

        Thread.sleep(1000);
        System.out.println("\n");

        for (int i = 0; i < timeList.size(); i++) {
            if (i < 10) {
                System.out.println("Check in time: " + timeList.get(i) + " ms");
            } else if (i >= 10 && i < 20) {
                System.out.println("Check out time: " + timeList.get(i) + " ms");
            } else if (i >= 20 && i < 22) {
                System.out.println("Family Check in time: " + timeList.get(i) + " ms");   
            } else {
                System.out.println("Family Check out time: " + timeList.get(i) + " ms");  
            }
        }

    }
    
        

    public static void main(String[] args) throws RemoteException, InterruptedException {
        new Test();
    }

}

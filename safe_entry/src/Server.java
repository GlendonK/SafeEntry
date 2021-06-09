/*
 * Server
 * 
 * CSC 3004 lab assignment
 * 
 * author: Glendon Keh 1901884, Aloysius Goh 1902774
 * 
 * submission date: 18 june 2021 
 * 
*/

import java.rmi.Naming;

/**
 * Server host the remote objects that clients invokes.
 */
public class Server {

    static int port = 1099;                 // port used for rmi
    static String host = "localhost";       // network address of server

    public Server() {
        try {
            Database database = new SafeEntryDatabase();
            Naming.rebind("rmi://" + host + ":" + port + "/database", database);    // server bind to address
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();

    }
}

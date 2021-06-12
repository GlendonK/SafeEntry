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

public class User {
    
    public static void main(String[] args) {
        try {
            Client user = new Client();
            while (true) {
                user.runUser();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

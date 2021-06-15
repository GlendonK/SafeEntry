import java.rmi.RemoteException;

/**
 * run with : java -classpath ".;C:\Users\glend\Desktop\safe\opencsv-5.4.jar;C:\Users\glend\Desktop\safe\commons-lang3-3.12.0.jar" .\Test.java
 */
public class Test {

    private static Server server;
    public Test() throws RemoteException {
        server = new Server();

        Client user = new Client();
        user.userCheckIn("nyp", "s1234567a", "alice");

    }

    public static void main(String[] args) throws RemoteException {
        new Test();
    }

}

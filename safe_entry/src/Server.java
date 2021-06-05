import java.rmi.Naming;

public class Server {

    static int port = 1099;
    
    public Server() {
        try {
            Database database = new SafeEntryDatabase();
            Naming.rebind("rmi://localhost:"+port+"/database", database); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
        
    }
}

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

public class Server implements RMI {

    private String remoteName;
    private Hashtable<String, String> lookupTable;

    public static void main(String[] args) {

        if(args.length != 1) {
            System.out.println("Usage: java Server <remote_object_name>");
            return;
        }
        try {
            Server server = new Server();
            server.start(args);
            RMI server_stub = (RMI) UnicastRemoteObject.exportObject(server,0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(server.remoteName,server_stub);

        }catch (Exception e){
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }


    }

    private void start(String[] args) {
        this.remoteName =args[0];
        this.lookupTable = new Hashtable<>();
    }

    public String respond(String received) {
        System.out.println("Server: " + received);
        String msg = this.buildReply(this.parseMSG(received));
        System.out.println(msg);
        return msg;

    }

    private Integer register(String[] msg) {
        if(this.lookupTable.containsKey(msg[1])) return -1;
        this.lookupTable.put(msg[1],msg[2]);
        return this.lookupTable.size()-1;
    }
    private String lookup(String[] msg) {
        if(!this.lookupTable.containsKey(msg[1])) return "NOT_FOUND";
        return this.lookupTable.get(msg[1]);
    }

    private String buildReply(String msg){
        if(msg.equals("-1"))
            return "ALREADY_IN_TABLE";
        if(msg.contains("."))
            return "SUCCESS - "+ msg;
        if(!msg.equals("ERROR")&&!msg.equals("NOT_FOUND"))
            return "SUCCESS - "+ msg +" previous entries";
        return msg;
    }

    private String parseMSG(String received) {
        String[] rcv = received.split(" ");
        for(int i = 0; i < rcv.length; i++){
            rcv[i] = rcv[i].trim();
        }
        if(rcv[0].equals("REGISTER"))
            return Integer.toString(this.register(rcv));
        if(rcv[0].equals("LOOKUP"))
            return this.lookup(rcv);
        return "ERROR";
    }

}
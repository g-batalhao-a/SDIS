import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private String host;
    private String remote_object_name;
    private String request = "";
    
    public static void main(String[] args) throws IOException{

        if(args.length < 4 || args.length > 5) {
            System.out.println("Usage: java Client <host_name> <remote_object_name> <oper> <opnd>");
            return;
        }
        Client client = new Client();
        if(client.start(args)!=0) return;
        client.run();
    }
    private boolean parseReq(String[] args){
        this.host = args[0];
        this.remote_object_name = args[1];
        String oper = args[2];

       for(int x=0;x<args.length;x++){
           System.out.println(args[x]);
       }

        if(oper.equals("register"))
            this.request = "REGISTER " + args[3] + " " + args[4];
        else if(oper.equals("lookup"))
            this.request = "LOOKUP " + args[3];
        else
            System.out.println("INVALID OP");

        return (request!=null && !request.isEmpty());
    }

    private int start(String[] args){
        if(!this.parseReq(args)) return -1;
        return 0;
    }

    private void run() {
        try {
            Registry registry = LocateRegistry.getRegistry(this.host);
            RMI server_stub = (RMI) registry.lookup(this.remote_object_name);
            String response = server_stub.respond(this.request);
            System.out.println("Client: "+response);
        } catch (Exception e) {
            System.err.println("Client exception: "+e.toString());
            e.printStackTrace();
        }
    }

}
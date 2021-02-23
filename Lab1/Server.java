import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class Server {

    private int port;
    private DatagramSocket socket;
    private Hashtable<String, String> lookupTable;
    private static final int DATAPACKETSIZE = 512;

    public static void main(String[] args) throws IOException {

        if(args.length != 1) {
            System.out.println("Usage: java Server <port number>");
            return;
        }

        Server server = new Server();
        server.start(args);
        while(true){
            server.run();
        }

    }

    private void start(String[] args) throws IOException {
        this.port = Integer.parseInt(args[0]);
        this.socket = new DatagramSocket(this.port);
        this.lookupTable = new Hashtable<>();
    }

    private void run() throws IOException{
        byte[] buffer = new byte[DATAPACKETSIZE];
        DatagramPacket packet = new DatagramPacket(buffer, DATAPACKETSIZE);
        socket.receive(packet);
        this.processPackage(packet);

    }

    private void processPackage(DatagramPacket packet) throws IOException{
        String received = new String(packet.getData());
        System.out.println("Server: " + received);
        String msg = this.buildReply(this.parseMSG(received));
        byte[] buffer = msg.getBytes();
        int clientPort = packet.getPort();
        InetAddress clientAddress = packet.getAddress();
        DatagramPacket newPacket = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
        socket.send(newPacket);
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
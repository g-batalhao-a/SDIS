import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Server{

    private int port, mPort;
    private String mAddr, addr="localhost";
    private DatagramSocket socket;
    private MulticastSocket multicastSocket;
    private Hashtable<String, String> lookupTable;
    private static final int DATAPACKETSIZE = 512;

    public static void main(String[] args) throws IOException {

        if(args.length != 3) {
            System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
            return;
        }

        Server server = new Server();
        server.start(args);

        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(2);


        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                server.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);

        while(true){
            server.execute();

        }

    }

    private void start(String[] args) throws IOException {
        this.port = Integer.parseInt(args[0]);
        this.mPort = Integer.parseInt(args[2]);
        this.mAddr = args[1];
        this.multicastSocket = new MulticastSocket(this.mPort);
        this.multicastSocket.setTimeToLive(1);
        InetAddress group = InetAddress.getByName(this.mAddr);
        this.multicastSocket.joinGroup(group);

        this.socket = new DatagramSocket(this.port);
        this.lookupTable = new Hashtable<>();
    }

    private void execute() throws IOException{
        byte[] buffer = new byte[DATAPACKETSIZE];
        DatagramPacket packet = new DatagramPacket(buffer, DATAPACKETSIZE);
        socket.receive(packet);
        this.processPackage(packet);

    }

    public void run() throws IOException{
        String ad = "multicast: "+this.mAddr+" "+this.mPort+ ": "+this.addr+ " "+this.port;
        byte[] buffer = ad.getBytes();
        DatagramPacket newPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(mAddr), mPort);
        System.out.println(ad);
        this.multicastSocket.send(newPacket);
    }

    private void processPackage(DatagramPacket packet) throws IOException{
        String received = new String(packet.getData());
        String msg = this.buildReply(this.parseMSG(received));
        System.out.println(msg);
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
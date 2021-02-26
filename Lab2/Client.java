import java.io.*;
import java.net.*;

public class Client {

    private String addr;
    private int port;
    private String srvAddr;
    private int srvPort;
    private String request = "";
    private MulticastSocket multicastSocket;
    private DatagramSocket clientSocket;
    private InetAddress group;
    private static final int DATAPACKETSIZE = 512;

    public static void main(String[] args) throws IOException{

        if(args.length < 4 || args.length > 5) {
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd> *");
            return;
        }
        Client client = new Client();
        if(client.start(args)!=0) return;
        client.run();

    }
    private boolean parseReq(String[] args){
        this.addr = args[0];
        this.port = Integer.parseInt(args[1]);
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

    private int start(String[] args) throws IOException{
        if(!this.parseReq(args)) return -1;
        this.multicastSocket = new MulticastSocket(this.port);
        this.group= InetAddress.getByName(this.addr);
        this.multicastSocket.joinGroup(this.group);
        this.clientSocket = new DatagramSocket();
        return 0;
    }

    private void run() throws IOException {
        byte[] bufferRequest = new byte[DATAPACKETSIZE];
        DatagramPacket request = new DatagramPacket(bufferRequest, bufferRequest.length);
        this.multicastSocket.receive(request);

        String req = new String(request.getData(), 0, request.getLength());
        String[] reqArray = req.split(" ");
        this.srvAddr = reqArray[3];
        this.srvPort = Integer.parseInt(reqArray[4]);

        System.out.println("multicast: "+this.addr+" "+this.port+": "+this.srvAddr + " " + this.srvPort);

        InetAddress add = InetAddress.getByName(this.srvAddr);
        byte[] buffer = this.request.getBytes();
        DatagramPacket requestDatagram = new DatagramPacket(buffer, buffer.length, add, this.srvPort);
        this.clientSocket.send(requestDatagram);

        byte[] bufferResponse = new byte[DATAPACKETSIZE];
        DatagramPacket responseDatagram = new DatagramPacket(bufferResponse, bufferResponse.length);
        this.clientSocket.receive(responseDatagram);
        String response = new String(responseDatagram.getData(), 0, responseDatagram.getLength());
        System.out.println(this.request+":: "+ response);

        this.clientSocket.close();
    }

}
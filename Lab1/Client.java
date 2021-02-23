import java.io.*;
import java.net.*;

public class Client {

    private String host;
    private int port;
    private String request = "";
    private DatagramSocket clientSocket;
    private static final int DATAPACKETSIZE = 512;
    
    public static void main(String[] args) throws IOException{

        if(args.length < 4 || args.length > 5) {
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>");
            return;
        }
        Client client = new Client();
        if(client.start(args)!=0) return;
        client.run();
    }
    private boolean parseReq(String[] args){
        this.host = args[0];
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
        this.clientSocket = new DatagramSocket();
        return 0;
    }

    private void run() throws IOException {
        InetAddress add = InetAddress.getByName(this.host);
        byte[] buffer = this.request.getBytes();
        DatagramPacket requestDatagram = new DatagramPacket(buffer, buffer.length, add, this.port);
        this.clientSocket.send(requestDatagram);

        byte[] bufferResponse = new byte[DATAPACKETSIZE];
        DatagramPacket responseDatagram = new DatagramPacket(bufferResponse, bufferResponse.length);
        this.clientSocket.receive(responseDatagram);
        String response = new String(responseDatagram.getData(), 0, responseDatagram.getLength());
        System.out.println("Client: "+this.request+": "+ response);
        this.clientSocket.close();
    }

}
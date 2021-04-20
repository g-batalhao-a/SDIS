import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

public class SSLServer {
    private static Map<String, String> table = new HashMap<>();

    public static void main(String[] args) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        if (args.length < 1) {
            System.out.println("Usage: java SSLServer <srvc_port> <cypher-suite>*");
            return;
        }

        int port = Integer.parseInt(args[0]);

        SSLServerSocket socket;
        SSLServerSocketFactory socketFactory;

        socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        System.setProperty("javax.net.ssl.keyStore","server.keys");
        System.setProperty("javax.net.ssl.keyStoreType","JKS");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");

        socket = (SSLServerSocket) socketFactory.createServerSocket(port);

        socket.setNeedClientAuth(true);
        if(args.length>1) {
            String[] cyphers = Arrays.copyOfRange(args,1,args.length);
            socket.setEnabledCipherSuites(cyphers);
        }



        while(true) {
            Socket clientSocket = socket.accept();

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String request = in.readLine();
            String response = request(request);

            out.println(response);

            out.close();
            in.close();

            clientSocket.close();
        }
    }

    public static String request(String request) {
        String[] command = request.trim().split(" ");
        String response = "-1";

        if (command[0].equalsIgnoreCase("REGISTER")) {
            response = "" + register(command[1], command[2]);
        } else if (command[0].equalsIgnoreCase("LOOKUP")) {
            String ip = lookup(command[1]);
            if (ip != null)
                response = command[1] + " " + ip;
        }

        System.out.println(request + " :: " + response);

        return response;
    }

    private static int register(String name, String ip) {
        String value = table.put(name, ip);

        if (value == null)
            return table.size();

        return -1;
    }

    private static String lookup(String name) {
        return table.get(name);
    }
}
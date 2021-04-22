import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

public class SSLClient {
    public static void main(String[] args) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        if (args.length < 4) {
            System.out.println("Usage: java SSLClient <host_name> <port_number> <oper> <opnd>* <cypher-suite>* ");
            return;
        }

        String command = buildCommand(args);

        int port = Integer.parseInt(args[1]);
        String host = args[0];

        SSLSocket clientSocket;
        SSLSocketFactory socketFactory;
        System.setProperty("javax.net.ssl.keyStore", "clientKeyStore");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");
        System.setProperty("javax.net.ssl.trustStore","truststore");
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");

        socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        clientSocket = (SSLSocket) socketFactory.createSocket(host, port);
        clientSocket.setNeedClientAuth(true);
        if(args.length>=4) {
            String[] cyphers = Arrays.copyOfRange(args,command.length()+1,args.length);
            clientSocket.setEnabledCipherSuites(cyphers);
        }



        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.println(command);

        String received = in.readLine();

        System.out.println("SSLClient: " + command + " : " + received);

        out.close();
        in.close();
        clientSocket.close();

    }

    private static String buildCommand(String[] args) {
        if(args[2].equalsIgnoreCase("register")) {
            String[] ops = new String[3];
            System.arraycopy(args, 2, ops, 0, 3);
            return String.join(" ", ops[0], ops[1], ops[2]);
        } else if(args[2].equalsIgnoreCase("lookup")) {
            String[] ops = new String[2];
            System.arraycopy(args, 2, ops, 0, 2);
            return String.join(" ", ops[0], ops[1]);
        }

        return "";
    }
}
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

public class MITMAttacker {
    private static final int BIT_LENGTH = 512;

    public static void main(String[] args) {
        try (ServerSocket mitmServerSocket = new ServerSocket(6000);
             Socket clientSocket = new Socket("localhost", 5000)) {

            System.out.println("Attacker is listening on port 6000...");

            // Wait for a connection from the client
            Socket attackerSocket = mitmServerSocket.accept();

            DataInputStream inputFromClient = new DataInputStream(attackerSocket.getInputStream());
            DataOutputStream outputToClient = new DataOutputStream(attackerSocket.getOutputStream());

            DataInputStream inputFromServer = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputToServer = new DataOutputStream(clientSocket.getOutputStream());

            // Step 1: Receive public parameters (p, g) from server and pass them to client
            BigInteger p = new BigInteger(inputFromServer.readUTF());
            BigInteger g = new BigInteger(inputFromServer.readUTF());

            outputToClient.writeUTF(p.toString());
            outputToClient.writeUTF(g.toString());

            // Step 2: Receive server's public key and substitute with attacker's key
            BigInteger serverPublicKey = new BigInteger(inputFromServer.readUTF());

            // Generate the attacker's private key
            SecureRandom random = new SecureRandom();
            BigInteger attackerPrivateKey = new BigInteger(BIT_LENGTH, random);
            System.out.println("Attacker's Private Key: " + attackerPrivateKey);

            // Compute the attacker's public key
            BigInteger attackerPublicKey = g.modPow(attackerPrivateKey, p);
            System.out.println("Attacker's Public Key (sent to client): " + attackerPublicKey);

            // Send the attacker's public key to the client
            outputToClient.writeUTF(attackerPublicKey.toString());

            // Step 3: Receive client's public key and substitute with attacker's key
            BigInteger clientPublicKey = new BigInteger(inputFromClient.readUTF());
            System.out.println("Client's Public Key: " + clientPublicKey);

            // Send attacker's public key to the server
            outputToServer.writeUTF(attackerPublicKey.toString());

            // Step 4: Compute the shared secrets with both client and server
            BigInteger sharedSecretWithServer = serverPublicKey.modPow(attackerPrivateKey, p);
            System.out.println("Shared Secret with Server: " + sharedSecretWithServer);

            BigInteger sharedSecretWithClient = clientPublicKey.modPow(attackerPrivateKey, p);
            System.out.println("Shared Secret with Client: " + sharedSecretWithClient);

            attackerSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

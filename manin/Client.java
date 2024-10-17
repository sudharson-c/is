import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Client {
    private static final int BIT_LENGTH = 512;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6000)) { // Connect to MITM attacker on port 6000
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Receive public parameters p and g from the MITM (attacker proxy)
            BigInteger p = new BigInteger(input.readUTF());
            BigInteger g = new BigInteger(input.readUTF());
            BigInteger attackerPublicKey = new BigInteger(input.readUTF());
            System.out.println("Received Public Key:" + attackerPublicKey);
            // Generate the client's private key
            SecureRandom random = new SecureRandom();
            BigInteger clientPrivateKey = new BigInteger(BIT_LENGTH, random);
            System.out.println("Client's Private Key: " + clientPrivateKey);

            // Compute the client's public key
            BigInteger clientPublicKey = g.modPow(clientPrivateKey, p);
            System.out.println("Client's Public Key: " + clientPublicKey);

            // Send the client's public key to the attacker (thinking it's the server)
            output.writeUTF(clientPublicKey.toString());

            // Compute the shared secret with the attacker
            BigInteger sharedSecret = attackerPublicKey.modPow(clientPrivateKey, p);
            System.out.println("Shared Secret (with MITM): " + sharedSecret);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

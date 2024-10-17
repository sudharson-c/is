import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class Server {
    private static final int BIT_LENGTH = 512; // Bit length for the private key

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is listening on port 5000...");
            Socket socket = serverSocket.accept();

            Scanner scanner = new Scanner(System.in);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Get user input for p and g
            System.out.print("Enter a large prime number (p): ");
            BigInteger p = new BigInteger(scanner.nextLine());

            System.out.print("Enter a base (g): ");
            BigInteger g = new BigInteger(scanner.nextLine());

            // Generate the server's private key
            SecureRandom random = new SecureRandom();
            BigInteger serverPrivateKey = new BigInteger(BIT_LENGTH, random);
            System.out.println("Server's Private Key: " + serverPrivateKey);

            // Compute the server's public key
            BigInteger serverPublicKey = g.modPow(serverPrivateKey, p);
            System.out.println("Server's Public Key: " + serverPublicKey);

            // Send public parameters p and g, and the server's public key
            output.writeUTF(p.toString());
            output.writeUTF(g.toString());
            output.writeUTF(serverPublicKey.toString());

            // Receive the client's public key
            BigInteger clientPublicKey = new BigInteger(input.readUTF());

            // Compute the shared secret
            BigInteger sharedSecret = clientPublicKey.modPow(serverPrivateKey, p);
            System.out.println("Shared Secret: " + sharedSecret);

            socket.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

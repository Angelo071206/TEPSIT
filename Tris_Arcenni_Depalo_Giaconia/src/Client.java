import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    private static Socket socket;
    private static String address;
    private static int port = 12345;		//porta su cui il server è in ascolto
    private static Scanner scanner;
    private static BufferedReader in;
    private static PrintWriter out;

    public Client(String address, int port) throws IOException {
        socket = new Socket(address, port);		//creo la socket
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        try {
            String message;
            while ((message = in.readLine()) != null) {
            	
                System.out.println(message);

                if (message.startsWith("Attendi")) {
                    // Attendi che l'altro giocatore si connetta
                    System.out.println("Aspettando l'altro giocatore...");
                } else if (message.startsWith("Inserisci")) {
                	//inserisco la posizione del simbolo
                    String inp = scanner.nextLine();
                    out.println(inp);
                } else if (message.equals("TURNO")) {
                	//riferisco al giocatore che è il suo turno
                    System.out.println("Tocca a te!");
                } else if (message.equals("HAI PERSO")) {
                	//riferisco che ha perso
                    System.out.println("Hai perso la partita!");
                    break;
                } else if (message.equals("PAREGGIO")) {
                	//riferisco che ha pareggiato
                    System.out.println("La partita è finita in pareggio.");
                    break;
                }
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            
            String address = "127.0.0.1";	//indirizzo del server

            Client client = new Client(address, port);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

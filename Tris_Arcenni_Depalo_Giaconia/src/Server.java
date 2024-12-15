import java.io.*;
import java.net.*;
import java.util.*;



public class Server {
	private static int port = 12345;		//porta su cui il server è in ascolto
	private static LinkedList<Socket> attesa = new LinkedList<>();		//tiene conto del numero di connessioni effettuate al server 
	
	public static void main(String[] args) {
		//simboli su cui si basa il gioco
		String simbolo1 = "X";
		String simbolo2 = "0";
		
		try {
			
			ServerSocket server = new ServerSocket (port);	//creo la socket del server
			System.out.println ("Server avviato. In attesa di connessioni...");		
			
			while (true) {
				//aspetto la connessione da parte di un client
				Socket client = server.accept();
				System.out.println("Giocatore connesso");
				attesa.add(client);		//aggiungo alla lista delle connessioni il client che si è connesso al server
				
				PrintWriter out = new PrintWriter( client.getOutputStream() );
				out.println("Attendi, stiamo cercando un altro giocatore");		//avviso il client che aspetto un altro giocatore
				
				//Controllo se ci sono 2 giocatori, se ci sono il gioco comincia
				if ( attesa.size() >= 2 ) {
					
					//rimuovo due client dalla lista di attesa in quando iniziano a giocare
					Socket giocatore1 = attesa.poll();
					Socket giocatore2 = attesa.poll();
					
					System.out.println("Partita trovata");
					
					//creo gli Handler
					Handler p1 = new Handler (giocatore1, simbolo1, giocatore2);
					Handler p2 = new Handler (giocatore2, simbolo2, giocatore1);
					
					//avvio i thread per i giocatori
					p1.start();
					p2.start();
					
					}
				
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

}


import java.io.*;
import java.net.*;
import java.util.*;

public class Handler extends Thread {

	private Socket socket;
	private Socket avversario;
	private BufferedReader in;
	private PrintWriter out;
	private String simbolo;
	private static String[] POSIZIONI = new String[9];		//tabella delle poszioni
	private static boolean turnoX = true;		//per sapere di chi è il turno
	private static final Object lock = new Object();		//serve per sincronizzare i turni
	
	
	public Handler (Socket socket, String simbolo, Socket avversario) {
		this.socket = socket;
		this.avversario = avversario;
		this.simbolo = simbolo;
		Arrays.fill(POSIZIONI, " ");		//assegna ad ogni elemento di POSIZIONI uno spazio vuoto
	}
	
	public void run() {
		try {
			
			out = new PrintWriter ( socket.getOutputStream(), true );
			in = new BufferedReader ( new InputStreamReader ( socket.getInputStream() ) );
			//comunico ai giocatori che simbolo hanno e che la partita sta per iniziare
			out.println("Benvenuto!");
			out.println("Sei il giocatore: " + simbolo);
			out.println("Inizia la partita");
			
			//avviso i giocatori su chi comincia
			if ( simbolo.equals("X") ) {
				out.println("Inizi te");
			}else {
				out.println("Aspetta il tuo turno");
			}
			
			//visualizzo la griglia di gioco iniziale per far capire dove posizionare i simboli
			VisualizzaIniziale();
			
			//inizia il gioco
			while (true) {
				//sincronizzo i turni
				synchronized (lock) {
					
					if( Turno() ) {
						//all'inizio del turno visualizzo la griglia di gioco per aggiornare i giocatori sulle loro mosse e quelle
						//dell'avversario
						Visualizza();
						
						//faccio inserire all'utente la posizione 
						out.println("Inserisci il numero corrispondente alla posizione ( da 1 a 9 )");
						String mossa = in.readLine();
						int posizione = Integer.parseInt(mossa) - 1;	//sottraggo 1 perchè l'array va da 0 a 8
						
						//verifico se la mossa eseguita è valida
						if ( posizione >= 0 && posizione < 9 && POSIZIONI[posizione].equals(" ") ) {
							
							//aggiorno la griglia
							POSIZIONI[posizione] = simbolo;
							
							//controllo se un giocatore ha vinto o pareggiato
							if ( Vittoria() ) {
								out.println("HAI VINTO");
								InviaAvversario("HAI PERSO");
								break;
							}
							
							if ( Pareggio() ) {
								out.println("HAI PAREGGIATO");
								InviaAvversario("HAI PAREGGIATO");
								break;
							}
							
							//avviso l'altro giocatore che è il suo turno e cambio il turno
							InviaAvversario("TURNO");
							cambiaturno();
						} else {
							out.println("Posizione inserita non è valida o è occupata, ritenta");
						}
								
					} else {
						out.println("Aspetta il tuo turno");
						try {
							lock.wait();	//attende fino a quando arriva il suo turno
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			}
			
		} catch (IOException e ) {
			e.printStackTrace();
		}
		
	}

	//serve a cambiare turno
	private synchronized void cambiaturno() {
		turnoX = !turnoX;		//inverto il valore di turnoX
		lock.notifyAll();		//risveglio il client che aspetta il suo turno
	}

	//controlla se la partita è finita in pareggio
	private boolean Pareggio() {
		for ( int i = 0; i < POSIZIONI.length; i++) {
			if (POSIZIONI[i] == " ") {
				return false;		//se c'è una casella vuota non è ancora finita la partita e un giocatore puo ancora vincere
			}
		}
		return true;	//tutte le posizioni sono occupate e nessuno ha vinto
	}

	//serve a inviare messaggi all'avversario
	private void InviaAvversario(String message) {
		try {
			PrintWriter outa = new PrintWriter ( avversario.getOutputStream(), true );
			outa.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//controlla se il giocatore che ha eseguito la mossa ha vinto
	private boolean Vittoria() {
		//creo una matrice con le possibili conmbinazioni per la vittoria
		int[][] combinazioni = {
				{0,1,2}, {3,4,5}, {6,7,8},	//orizzontali
				{0,3,6}, {1,4,7}, {2,5,8},	//verticali
				{0,4,8}, {2,4,6}			//diagonali
		};
		
		//scorro tutte le possibili condizioni di vittoria
		for ( int[] combinazionivittoria : combinazioni) {
			if ( POSIZIONI[combinazionivittoria[0]].equals(simbolo) && 
				 POSIZIONI[combinazionivittoria[1]].equals(simbolo) && 
				 POSIZIONI[combinazionivittoria[2]].equals(simbolo)) {
				return true;
			}
		}
		return false;
	}

	//metodo per stampare la griglia aggiornata ogni turno
	private void Visualizza() {
		out.println("Griglia di gioco:");
		for ( int i = 0; i < 9; i++ ) {
			out.print(POSIZIONI[i] + " " );
			//ogni 3 caselle vado a capo
			if ( ( i + 1) % 3 == 0) {
				out.println();
			}
		}
		
	}

	//indica di chi è il turno
	private synchronized boolean Turno() {
		//restituisce vero se tocca al giocatore X ed  è il suo turno o viceversa
		return (simbolo.equals("X") && turnoX) || (simbolo.equals("0") && !turnoX );	
	}

	
	//metodo per visualizzare la griglia iniziale
	private void VisualizzaIniziale() {
		out.println("Griglia di gioco iniziale:");
		for ( int i = 0; i < 9; i++ ) {
			out.print(POSIZIONI[i] + ( i + 1 ) );
			//ogni 3 caselle vado a capo
			if ( ( i + 1) % 3 == 0) {
				out.println();
			}
		}
	}

}
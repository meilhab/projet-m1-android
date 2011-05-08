import java.io.*;
import java.net.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Gere la connexion avec le joueur (developper en C), en utilisant des sockets.
 * Gere egalement la connexion avec l'IA
 */
public class ConnexionJoueur {

	private ServerSocket ss;
	private Socket socketJoueur;
	private int portJoueur;
	private ConnexionProlog cp;
	private Coups coupsActuel;
	private Modele modele;
	private String messageRecu;
	private boolean commencePartie;
	
	public final static int TAILLE_MAX_BUFFER = 30;
	public final static int NB_PION_MAIN = 12;
	public final static String MOT_DE_PASSE = "chaussette";
	public final static String CHEMIN_IA = "ia/ia.pl";
	

	/**
	 * Constructeur qui initialise les variables
	 * @param portJoueur port qui va servir de connexion entre le joueur et le moteur Java
	 */
	public ConnexionJoueur(int portJoueur) {

		this.portJoueur = portJoueur;
		ss = null;
		socketJoueur = null;
		coupsActuel = null;
		modele = new Modele(NB_PION_MAIN);
		messageRecu = "";
		cp = new ConnexionProlog(CHEMIN_IA, modele);
		commencePartie = false;
		
		etablirConnexion();

	}
	
	/**
	 * Attend que le joueur se connect au moteur Java
	 */
	public void etablirConnexion() {
		
		try {
			ss = new ServerSocket(portJoueur);
			socketJoueur = ss.accept();
			System.out.println("JAVA : connexion avec le joueur etablie");
		} catch (IOException ioe) {
			System.out.println("JAVA : erreur (IOException) lors de la connexion avec le joueur : " + ioe);
		} catch (Exception e) {
			System.out.println("JAVA : erreur (autre exception) lors de la connexion avec le joueur : " + e);
		}
		
	}

	/**
	 * Demarre une partie avec le joueur
	 */
	public void demarrer() {

		if(ss != null && socketJoueur != null) {
			
			try {
				InputStream is = socketJoueur.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				OutputStream os = socketJoueur.getOutputStream();
				//BufferedWriter bw  = new BufferedWriter(new OutputStreamWriter(socketJoueur.getOutputStream()));
				//ou DataOutputStream dos = new DataOutputStream(socketJoueur.getOutputStream());
				
				int nbCaractereRecu = -1;
								
				char[] buffer = new char[TAILLE_MAX_BUFFER];
				nbCaractereRecu = br.read(buffer);
				messageRecu = new String(buffer, 0, nbCaractereRecu);
				
				if(messageRecu.equals(MOT_DE_PASSE)) {
					
					os.write(new String("OK").getBytes());
					System.out.println("JAVA : le client s'est correctement identifie");
					
					messageRecu = "start";
					
					while (!messageRecu.startsWith("fin")) {
						
						if(messageRecu.startsWith("restart") || messageRecu.startsWith("start")) {
							modele.restart();
							
							System.out.println("JAVA : Demarrage d'une nouvelle partie\n");
							
							messageRecu = br.readLine();
							
							if(messageRecu.startsWith("commence") || messageRecu.startsWith("attend")) {
								
								if(messageRecu.startsWith("commence"))
									commencePartie = true;
								else
									commencePartie = false;
								
								System.out.println("JAVA : commence : " + commencePartie + "\n\n");
								
								if(!commencePartie)
									recoiCoups(br);
								
								messageRecu = "";
							}
							else {
								buffer = new char[TAILLE_MAX_BUFFER];
								nbCaractereRecu = br.read(buffer);
								if(nbCaractereRecu > 0)
									messageRecu = new String(buffer, 0, nbCaractereRecu);
								else
									messageRecu = "restart";
							}
							
						}
						else if(!messageRecu.startsWith("fin")) {
							
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							
							demandeCoups(os);
							
							recoiCoups(br);
							
						}
						else
							messageRecu = "fin";
					}
				}
				else
					System.out.println("JAVA : Un client inconnu s'est connecte, le mot de passe donne etait : " + messageRecu);
				
				System.out.println("JAVA : Arret de l'envoi/reception de donnees avec le joueur");
				
				br.close();
				os.close();
				//bw.close();
				socketJoueur.close();
				ss.close();
			
			} catch (IOException e) {
				System.out.print("JAVA : erreur lors de l'envoi des donnees au joueur : " + e);
			}
			
		}
		else {
			System.out.print("JAVA : impossible de demarrer l'envoi de donnees ");
			System.out.println("avec le joueur car aucune connexion n'a ete etablie");
		}
	}
	
	
	/**
	 * Demande un coup a l'IA puis envoie ce dernier au joueur
	 * @param os
	 * @throws IOException
	 */
	public void demandeCoups(OutputStream os) throws IOException {
		
		System.out.println("JAVA : Attente du coups de l'IA ....\n\n");
		
		coupsActuel = cp.demanderCoups();
		
		if(modele.valider(1, coupsActuel))
			modele.jouer(1, coupsActuel);
		else
			System.out.println("JAVA : Coup genere par l'IA invalide : "+ coupsActuel.getReq());
		
		if(modele.aGagne(1))
			coupsActuel.setTypeCoups(Modele.GAGNE);
		
		System.out.println("JAVA : L'IA a choisi le coups : " + coupsActuel.getReq());
		
		System.out.println(modele.plateauToString());
		
		os.write(coupsActuel.getReq().getBytes());
		
	}
	
	/**
	 * Recoie un coup du joueur adverse, le convertit, puis va le jouer dans le modele du moteur Java
	 * @param br
	 * @throws IOException
	 */
	public void recoiCoups(BufferedReader br) throws IOException {

		char[] buffer = new char[TAILLE_MAX_BUFFER];
		int nbCaractereRecu = br.read(buffer);
		if(nbCaractereRecu > 0)
			messageRecu = new String(buffer, 0, nbCaractereRecu);
		else
			messageRecu = "fin";
		
		if(messageRecu.contains("[") && messageRecu.contains("]") && messageRecu.contains("-") && messageRecu.contains(",")) {
			
			System.out.println("JAVA : Reception du coups adverse : " + messageRecu);
			
			coupsActuel = new Coups(messageRecu);
			if(modele.valider(2, coupsActuel))
				modele.jouer(2, coupsActuel);
			else
				System.out.println("JAVA : Coup recu invalide : "+ coupsActuel.getReq());
			
			System.out.println(modele.plateauToString());
		}
		else if(!messageRecu.startsWith("fin") && !messageRecu.startsWith("restart"))
			System.out.println("JAVA : Le message recu n'est pas correctement forme : " + messageRecu);
	
	}

}

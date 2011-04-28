package projet.moia.scs;

import java.io.*;
import java.net.*;

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
	public final static String MOT_DE_PASSE = "chaussette";

	public ConnexionJoueur(int portJoueur) {

		this.portJoueur = portJoueur;
		ss = null;
		socketJoueur = null;
		coupsActuel = null;
		modele = new Modele(12);
		messageRecu = "";
		cp = new ConnexionProlog("ia.pl", modele);
		commencePartie = false;
		
		etablirConnexion();

	}
	
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
					System.out.println("le client s'est correctement identifie");
					
					messageRecu = "start";
					
					while (!messageRecu.equals("fin")) {
						
						if(messageRecu.equals("restart") || messageRecu.equals("start")) {
							modele.restart();
							
							System.out.println("Demarrage d'une nouvelle partie");
							
							//commence par recevoir ou par envoyer
							//buffer = new char[TAILLE_MAX_BUFFER];
							//nbCaractereRecu = br.read(buffer);
							//messageRecu = new String(buffer, 0, nbCaractereRecu);
							
							messageRecu = br.readLine();
							
							if(messageRecu.equals("commence"))
								commencePartie = true;
							else
								commencePartie = false;
							
							System.out.println("commence : " + commencePartie);
							
							if(!commencePartie)
								recoiCoups(br);
							
							messageRecu = "";
						}
						else if(!messageRecu.equals("fin")) {
							
							try {
								Thread.sleep(4000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							demandeCoups(os);
							
							recoiCoups(br);
							
						}
					}
				}
				else
					System.out.println("Un client inconnu s'est connecte, le mot de passe donne etait : " + messageRecu);
				
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
	
	
	public void demandeCoups(OutputStream os) throws IOException {
		
		coupsActuel = cp.demanderCoups();
		
		if(modele.valider(1, coupsActuel))
			modele.jouer(1, coupsActuel);
		else
			System.out.println("Coup genere invalide : "+ coupsActuel.getReq());
		
		System.out.println(modele.plateauToString());
		
		System.out.println("envoie au joueur du coups " + coupsActuel.getReq());
		
		os.write(coupsActuel.getReq().getBytes());
		
	}
	
	public void recoiCoups(BufferedReader br) throws IOException {

		char[] buffer = new char[TAILLE_MAX_BUFFER];
		int nbCaractereRecu = br.read(buffer);
		if(nbCaractereRecu > 0)
			messageRecu = new String(buffer, 0, nbCaractereRecu);
		else
			messageRecu = "fin";
		
		if(messageRecu.contains("[") && messageRecu.contains("]") && messageRecu.contains("-") && messageRecu.contains(",")) {
			
			System.out.println("Reception du coups " + messageRecu);
			
			coupsActuel = new Coups(messageRecu);
			if(modele.valider(2, coupsActuel))
				modele.jouer(2, coupsActuel);
			else
				System.out.println("Coup recu invalide : "+ coupsActuel.getReq());
			
			System.out.println(modele.plateauToString());
		}
		else if(!messageRecu.equals("fin"))
			System.out.println("Le message recu n'est pas correctement forme : " + messageRecu);
	
	}

}

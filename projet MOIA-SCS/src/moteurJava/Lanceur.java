/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Classe permettant de lancer le moteur Java ainsi que l'IA
 */
public class Lanceur {

	public static void main(String[] args) {
	
		if(args.length!=1) {
			System.out.println("JAVA : Erreur lors du lancement de la connexion avec le joueur, veuillez entrer en argument le port du joueur");
			System.out.println("JAVA : Usage : java Lanceur port_joueur");
			System.exit(0);
		}
		
		ConnexionJoueur cj= new ConnexionJoueur(Integer.parseInt(args[0]));
		
		cj.demarrer();
		
	}

}

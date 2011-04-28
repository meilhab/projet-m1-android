package projet.moia.scs;


public class Lanceur {

	public static void main(String[] args) {

    	if(args.length!=1) {
    		System.out.println("JAVA : Erreur lors du lancement de la connexion avec le joueur, veuillez entrer en arguemnt le port du joueur");
    		System.out.println("JAVA : Usage : java Lanceur port_joueur");
    		System.exit(0);
    	}
		
    	/*
    	Modele modele = new Modele(12);
    	modele.getPionsJoueur1().add(new Position(0, 0));
    	modele.getPionsJoueur1().add(new Position(1, 1));
    	modele.getPionsJoueur1().add(new Position(2, 1));
    	modele.getPionsJoueur1().add(new Position(1, 4));
    	modele.getPionsJoueur1().add(new Position(4, 2));
    	modele.getPionsJoueur1().add(new Position(4, 0));
    	
    	modele.getPionsJoueur2().add(new Position(2, 1));
    	modele.getPionsJoueur2().add(new Position(3, 2));
    	modele.getPionsJoueur2().add(new Position(3, 1));
    	modele.getPionsJoueur2().add(new Position(3, 0));
    	
    	System.out.println(modele.plateauToString());
    	Coups coups = new Coups(modele.PRISE,4,2,2,2,0,0);
    	System.out.println(modele.valider(1, coups));
    	modele.jouer(1, coups);
    	modele.prendre(1, 1, 1, 1, 3, 3, 0);
    	
    	System.out.println(modele.getPionsJoueurToString(1));
    	System.out.println(modele.getPionsJoueurToString(2));
    	
    	System.out.println(modele.plateauToString());
    	
    	Modele modele = new Modele(12);
    	modele.getPionsJoueur1().add(new Position(0, 0));
    	ConnexionProlog cp = new ConnexionProlog("ia.pl", modele);
    	cp.demanderCoups();
    	
    	*/
	    ConnexionJoueur cj= new ConnexionJoueur(Integer.parseInt(args[0]));

	    cj.demarrer();

	}

}

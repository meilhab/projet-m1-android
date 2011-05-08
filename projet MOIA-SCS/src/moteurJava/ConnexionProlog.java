import java.util.HashMap;
import se.sics.jasper.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Gestion de la connexion avec l'IA (faite en prolog) a l'aide de Jasper.
 * l'IA nous retournera un coup valide.
 */
public class ConnexionProlog {

	private SICStus sp;

	SPTerm typeCoups;
	SPTerm caseDepart;
	SPTerm caseArrivee;
	SPTerm prend2emePion;
	
	private String cheminFichier;
	private Modele modele;
	
	/**
	 * Constructeur
	 * @param cheminFichier chemin du fichier prolog qui contient l'IA du programme
	 * @param modele modele du projet
	 */
	public ConnexionProlog(String cheminFichier, Modele modele) {
		
		this.cheminFichier = cheminFichier;
		this.modele = modele;
		
		sp = null;
		
		try {

		    sp = new SICStus();
		    sp.load(this.cheminFichier);
		    
		}
		catch (SPException e) {
		    System.out.println("Exception SICStus Prolog : " + e);
		}
		catch (Exception e) {
		    System.out.println("Exception SICStus Prolog : " + e);
		}
		
	}
	
	/**
	 * Permet de demander a l'IA de choisir un coup valide
	 * @return coup choisi par l'IA
	 */
	public Coups demanderCoups() {
		
		Coups coups = null;
		
		if(sp != null) {
			
			try {
				
				String anciennePositionDernierPion;
				String dernierPionJoue;
				
				if(modele.getAnciennePositionDernierPion(1) != null)
					anciennePositionDernierPion = modele.getAnciennePositionDernierPion(1).toString();
				else
					anciennePositionDernierPion = "[5,6]";
				if(modele.getDernierPionJoue(1) != null)
					dernierPionJoue = modele.getDernierPionJoue(1).toString();
				else
					dernierPionJoue = "[5,6]";
				
				//System.out.println("JASPER : predicat envoye a prolog : " + "jouer("+modele.getPionsJoueurToString(1)+","+modele.getPionsJoueurToString(2)+", "+modele.getNbPionMain(1)+", TypeCoups, CaseDepart, CaseArrivee, Prend2emePion).");
				
				HashMap<String, SPTerm> results = new HashMap<String, SPTerm>();
				SPQuery query = sp.openQuery("jouer("+modele.getPionsJoueurToString(1)+","+modele.getPionsJoueurToString(2)+", "+modele.getNbPionMain(1)+", ["+anciennePositionDernierPion+","+dernierPionJoue+"], TypeCoups, CaseDepart, CaseArrivee, Prend2emePion).",results);
				
				query.nextSolution();
				
				typeCoups = results.get("TypeCoups");
				caseDepart = results.get("CaseDepart");
				caseArrivee = results.get("CaseArrivee");
				prend2emePion = results.get("Prend2emePion");
				
				coups = CoupsChoisi();
				
				query.close();
			    
			} catch (ConversionFailedException e) {
				System.out.println("JASPER : erreur : " + e);
			} catch (IllegalTermException e) {
				System.out.println("JASPER : erreur : " + e);
			} catch (SPException e) {
				System.out.println("JASPER : erreur : " + e);
			} catch (Exception e) {
				System.out.println("JASPER : erreur : " + e);
			}
			
		}
		
		return coups;
		
	}
	
	/**
	 * Permet d'obtenir un coups choisi par l'IA sous forme d'objet de type Coups
	 * @return coups choisi par l'IA sous forme d'objet de type Coups
	 * @throws ConversionFailedException
	 * @throws IllegalTermException
	 * @throws Exception
	 */
	public Coups CoupsChoisi() throws ConversionFailedException, IllegalTermException, Exception {
		
		int type = -1;
		
		if(typeCoups.toString().equals("pose"))
			type = Modele.POSE;
		else if(typeCoups.toString().equals("deplace"))
			type = Modele.DEPLACE;
		else if(typeCoups.toString().equals("prise"))
			type = Modele.PRISE;
		
		return new Coups(type, getIntToList(caseDepart, 0), getIntToList(caseDepart, 1), getIntToList(caseArrivee, 0), getIntToList(caseArrivee, 1), getIntToList(prend2emePion, 0), getIntToList(prend2emePion, 1));
	
	}
	
	/**
	 * Converti un element d'une liste de type SPTerm en un entier
	 * @param term liste de type SPTerm
	 * @param index index de l'element voulu dans la liste
	 * @return entier demande
	 * @throws ConversionFailedException
	 * @throws IllegalTermException
	 * @throws Exception
	 */
	public int getIntToList(SPTerm term, int index) throws ConversionFailedException, IllegalTermException, Exception {
		
		int position = -1;
		
		Term[] listPosition = term.toPrologTermArray();
		if(listPosition.length > index)
			position = (int) listPosition[index].getInteger();
		
		return position;		
	}
}

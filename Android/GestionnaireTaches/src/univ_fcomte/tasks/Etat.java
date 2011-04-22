package univ_fcomte.tasks;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Représente un l'état d'une tâche (annuler, en cours, ...), n'est pas utilisée dans notre modèle
 */
public class Etat {
	public static final int ANNULER = 0;
	public static final int A_PREVOIR = 1;
	public static final int EN_COURS = 2;
	public static final int TERMINER = 3;
	private int id;
	
	public Etat(){
		id = EN_COURS;
	}
	
	public Etat(int id){
		setID(id);
	}
	
	public void setID(int id){
		if(id < ANNULER && id > TERMINER) 
			this.id = EN_COURS;
		else 
			this.id = id;
	}
	
	public String getStringToID(){
		switch(id){
			case ANNULER:
				return "Sans";
			case A_PREVOIR:
				return "Faible";
			case EN_COURS:
				return "Urgent";
			case TERMINER:
				return "Ultra urgent";
		}
		return null;
	}
}

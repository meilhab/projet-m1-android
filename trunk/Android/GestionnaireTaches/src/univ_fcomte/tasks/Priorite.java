package univ_fcomte.tasks;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Représente la priorité d'une tâche (sans, faible, ...), n'est pas utilisée dans notre modèle
 */
public class Priorite {
	public static final int SANS = 0;
	public static final int FAIBLE = 1;
	public static final int URGENT = 2;
	public static final int ULTRA_URGENT = 3;
	private int id;
	
	public Priorite(){
		id = SANS;
	}
	
	public void setID(int id){
		if(id < SANS && id > ULTRA_URGENT) 
			this.id = SANS;
		else 
			this.id = id;
	}

	public String getStringToID(){
		switch(id){
			case SANS:
				return "Sans";
			case FAIBLE:
				return "Faible";
			case URGENT:
				return "Urgent";
			case ULTRA_URGENT:
				return "Ultra urgent";
		}
		return null;
	}

}

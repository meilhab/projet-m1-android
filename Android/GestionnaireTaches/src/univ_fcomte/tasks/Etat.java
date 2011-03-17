package univ_fcomte.tasks;

public class Etat {
	public static final int ANNULER = 0;
	public static final int A_PREVOIR = 1;
	public static final int EN_COURS = 2;
	public static final int TERMINER = 3;
	private int id;
	
	public Etat(int id){
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

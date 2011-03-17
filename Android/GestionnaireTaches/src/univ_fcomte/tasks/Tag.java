package univ_fcomte.tasks;

public class Tag {
	private int identifiant;
	private String nom;
	
	public Tag(int identifiant){
		this.identifiant = identifiant;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public int getIdentifiant(){
		return identifiant;
	}
}

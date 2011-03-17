package univ_fcomte.tasks;

public class Tag {
	private int identifiant;
	private String nom;
	
	public Tag(int identifiant){
		this.identifiant = identifiant;
	}
	
	public Tag(int identifiant, String nom){
		this.identifiant = identifiant;
		this.nom=nom;
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

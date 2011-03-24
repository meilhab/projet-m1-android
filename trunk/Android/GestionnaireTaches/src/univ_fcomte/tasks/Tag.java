package univ_fcomte.tasks;

public class Tag {
	private long identifiant;
	private String nom;
	
	public Tag(long identifiant){
		this.identifiant = identifiant;
	}
	
	public Tag(long identifiant, String nom){
		this.identifiant = identifiant;
		this.nom=nom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public long getIdentifiant(){
		return identifiant;
	}
	
	public void setIdentifiant(long identifiant){
		this.identifiant = identifiant;
	}
}

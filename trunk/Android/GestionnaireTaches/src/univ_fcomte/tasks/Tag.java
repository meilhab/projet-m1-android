package univ_fcomte.tasks;

public class Tag {
	private long identifiant;
	private String nom;
	private String idUtilisateur;
	
	public Tag(long identifiant){
		this.identifiant = identifiant;
	}
	
	public Tag(long identifiant, String nom, String idUtilisateur){
		this.identifiant = identifiant;
		this.nom=nom;
		this.idUtilisateur = idUtilisateur;
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
	
	public String getIdUtilisateur() {
		return idUtilisateur;
	}

	public void setIdUtilisateur(String idUtilisateur) {
		this.idUtilisateur = idUtilisateur;
	}
}

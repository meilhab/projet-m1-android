package univ_fcomte.tasks;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Représente un tag (attribut d'un tâche, ex : professionnel, personnel, ...), chaque tâche en possède une liste
 */
public class Tag {
	private long identifiant;
	private String nom;
	private int version;
	
	public Tag(long identifiant){
		this.identifiant = identifiant;
	}
	
	public Tag(long identifiant, String nom, int version){
		this.identifiant = identifiant;
		this.nom = nom;
		this.version = version;
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
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}

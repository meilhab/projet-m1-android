package univ_fcomte.tasks;

import java.util.ArrayList;

public class Tache {
	private long identifiant;
	private String nom;
	private int priorite;
	private int etat;
	private String description;
	private ArrayList<Long> listeTags;
	private ArrayList<Long> listeTachesFille;
	private String idUtilisateur;

	public Tache(long identifiant) {
		this.identifiant = identifiant;
		nom = "";
		priorite = 1;
		etat = 1;
		description = "";
		listeTags = new ArrayList<Long>();
		listeTachesFille = new ArrayList<Long>();
	}

	public Tache(long id, String nom, String description, int priorite,
			int etat, ArrayList<Long> listeTags,
			ArrayList<Long> listeTachesFille, String idUtilisateur) {
		this.identifiant = id;
		this.nom = nom;
		this.priorite = priorite;
		this.etat = etat;
		this.description = description;
		this.listeTags = listeTags;
		this.listeTachesFille = listeTachesFille;
		this.idUtilisateur = idUtilisateur;
	}

	public void ajoutTacheFille(long id) {
		listeTachesFille.add(id);
	}

	public void retirerTacheFille(long id) {
		listeTachesFille.remove(id);
	}

	public void ajoutTag(long id) {
		listeTags.add(id);
	}

	public void retirerTag(long id) {
		listeTags.remove(id);
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getPriorite() {
		return priorite;
	}

	public void setPriorite(int id) {
		priorite = id;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int id) {
		etat = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(long identifiant) {
		this.identifiant = identifiant;
	}

	public ArrayList<Long> getListeTags() {
		return listeTags;
	}

	public ArrayList<Long> getListeTachesFille() {
		return listeTachesFille;
	}

	public String getIdUtilisateur() {
		return idUtilisateur;
	}

	public void setIdUtilisateur(String idUtilisateur) {
		this.idUtilisateur = idUtilisateur;
	}

}

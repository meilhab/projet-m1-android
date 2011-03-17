package univ_fcomte.tasks;

import java.util.ArrayList;

public class Tache {
	private int identifiant;
	private String nom;
	private Priorite priorite;
	private Etat etat;
	private String description;
	private ArrayList<Tag> listeTags;
	private ListeTaches listeTachesFille;
	
	public Tache(int identifiant){
		this.identifiant = identifiant;
		listeTags = new ArrayList<Tag>();
		listeTachesFille = new ListeTaches();
		priorite = new Priorite();
		etat = new Etat();
	}

	public void ajoutTacheFille(Tache t){
		listeTachesFille.ajoutTache(t);
	}
	
	public void retirerTacheFille(Tache t){
		listeTachesFille.retirerTache(t);
	}
	
	public void ajoutTag(Tag t){
		listeTags.add(t);
	}
	
	public void retirerTag(Tag t){
		listeTags.remove(t);
	}
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Priorite getPriorite() {
		return priorite;
	}

	public void setPriorite(int id) {
		priorite.setID(id);
	}

	public Etat getEtat() {
		return etat;
	}

	public void setEtat(int id) {
		etat.setID(id);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getIdentifiant(){
		return identifiant;
	}
	
	
	
}

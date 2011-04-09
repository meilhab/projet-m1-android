package univ_fcomte.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.text.format.DateFormat;

public class Tache {
	private long identifiant;
	private String nom;
	private int priorite;
	private int etat;
	private Date dateLimite;
	private String description;
	private ArrayList<Long> listeTags;
	private ArrayList<Long> listeTachesFille;
	private int version;

	public Tache(long identifiant){
		this.identifiant = identifiant;
		nom = "";
		priorite = 1;
		etat = 1;
		description = "";
		listeTags = new ArrayList<Long>();
		listeTachesFille = new ArrayList<Long>();
	}
	
	public Tache(long id, String nom, String description, Date dateLimite, int priorite, int etat, int version, ArrayList<Long> listeTags,  ArrayList<Long> listeTachesFille){
		this.identifiant = id;
		this.nom = nom;
		this.priorite=priorite;
		this.etat = etat;
		this.description = description;
		this.version = version;
		this.listeTags = listeTags;
		this.listeTachesFille = listeTachesFille;
		this.dateLimite = dateLimite;
	}
	
	public Tache(long id, String nom, String description, String dateLimite, int priorite, int etat, int version, ArrayList<Long> listeTags,  ArrayList<Long> listeTachesFille){
		this.identifiant = id;
		this.nom = nom;
		this.priorite=priorite;
		this.etat = etat;
		this.description = description;
		this.version = version;
		this.listeTags = listeTags;
		this.listeTachesFille = listeTachesFille;
		this.dateLimite = null;
		try {
			this.dateLimite = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateLimite);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void ajoutTacheFille(long id){
		listeTachesFille.add(id);
	}
	
	public void retirerTacheFille(long id){
		listeTachesFille.remove(id);
	}
	
	public void ajoutTag(long id){
		listeTags.add(id);
	}
	
	public void retirerTag(long id){
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
	
	public long getIdentifiant(){
		return identifiant;
	}
	
	public void setIdentifiant(long identifiant){
		this.identifiant = identifiant;
	}
	
	public ArrayList<Long> getListeTags() {
		return listeTags;
	}
	
	public void setListeTags(ArrayList<Long> listeTags) {
		this.listeTags = listeTags;
	}

	public ArrayList<Long> getListeTachesFille() {
		return listeTachesFille;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public Date getDateLimite() {
		return dateLimite;
	}
	
	public String getDateLimiteToString() {
		
		if(dateLimite != null)
			return (String) DateFormat.format("yyyy-MM-dd hh:mm:ss", dateLimite);
		else
			return "0000-00-00 00:00:00";
	}

	public void setDateLimite(Date dateLimite) {
		this.dateLimite = dateLimite;
	}
	
	public void setDateLimiteToString(String dateLimite) {
		try {
			this.dateLimite = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(dateLimite);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}

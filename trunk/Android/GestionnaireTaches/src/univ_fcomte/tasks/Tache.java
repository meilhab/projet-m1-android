package univ_fcomte.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

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
		try {
			this.dateLimite = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("0000-00-00 00:00:00");
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
	
	public void setListeFils(ArrayList<Long> listeTachesFille) {
		this.listeTachesFille = listeTachesFille;
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
	
	public String getDateLimiteLettre() {
		
		if(dateLimite != null)
			return (String) DateFormat.format("dd/MM/yyyy", dateLimite);
		else
			return "00/00/0000";
		
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
	
	public int getJour() {
		Calendar c = GregorianCalendar.getInstance();
    	c.setTime(dateLimite);
    	return c.get(Calendar.DAY_OF_MONTH);
	}
	
	public int getMois() {
		Calendar c = GregorianCalendar.getInstance();
    	c.setTime(dateLimite);
    	return c.get(Calendar.MONTH) + 1;
	}
	
	public int getAnnee() {
		Calendar c = GregorianCalendar.getInstance();
    	c.setTime(dateLimite);
    	return c.get(Calendar.YEAR);
	}
	
}


class ComparateurTache implements Comparator<Tache> {
	public int compare(Tache t1, Tache t2){
		return t1.getNom().compareToIgnoreCase(t2.getNom());
	}      
}

class ComparateurTachePriorite implements Comparator<Tache> {
	public int compare(Tache t1, Tache t2){
		if(t1.getPriorite() > t2.getPriorite())
			return 1;
		else if(t1.getPriorite() < t2.getPriorite())
			return -1;
		else
			return 0;
	}      
}

class ComparateurTacheEtat implements Comparator<Tache> {
	public int compare(Tache t1, Tache t2){
		if(t1.getEtat() > t2.getEtat())
			return 1;
		else if(t1.getEtat() < t2.getEtat())
			return -1;
		else
			return 0;
	}      
}

class ComparateurTacheIdentifiant implements Comparator<Tache> {
	public int compare(Tache t1, Tache t2){
		if(t1.getIdentifiant() > t2.getIdentifiant())
			return 1;
		else if(t1.getIdentifiant() < t2.getIdentifiant())
			return -1;
		else
			return 0;
	}      
}

class ComparateurTacheDate implements Comparator<Tache> {
	public int compare(Tache t1, Tache t2){
		return t1.getDateLimiteToString().compareToIgnoreCase(t2.getDateLimiteToString());
	}      
}

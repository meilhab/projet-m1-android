package univ_fcomte.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import univ_fcomte.bdd.MaBaseSQLite;

public class Modele {

	static public enum Tri { NOM, DATE, PRIORITE, ETAT };
	
	private ArrayList<Tag> listeTags;
	private ArrayList<Tache> listeTaches;
	private Context context;
	private MaBaseSQLite bdd;
	private SQLiteDatabase db;
	private String rechercheCourante;
	private Tri tri;
	private boolean enCoursSynchro;
	private String serveur;

	public Modele(Context context) {
		listeTags = new ArrayList<Tag>();
		listeTaches = new ArrayList<Tache>();
		this.context = context;
		this.bdd = new MaBaseSQLite(context, "gestionnaire_taches.db", null, 1);
		this.db = this.bdd.getDb();
		rechercheCourante = "";
		tri = Tri.DATE;
		enCoursSynchro = false;
		//serveur = "http://10.0.2.2/gestionnaire_taches/requeteAndroid.php";
		serveur = "http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/requeteAndroid.php";
		
	}

	public void ajoutTag(Tag t){
		listeTags.add(t);
	}
	
	public void retirerTache(Tag t){
		listeTags.remove(t);
	}
	
	public ArrayList<Tag> getListeTags(){
		return listeTags;
	}
	
	public String toString(){
		String s = "";
		for(int i=0; i<listeTags.size(); i++)
			s += listeTags.get(i).getNom();
		return s;
	}

	public void ajoutTache(Tache t){
		listeTaches.add(t);
	}
	
	public void retirerTache(Tache t){
		listeTaches.remove(t);
	}
	
	public ArrayList<Tache> getListeTaches(){
		return listeTaches;
	}
	
	public MaBaseSQLite getBdd() {
		return bdd;
	}

	public SQLiteDatabase getDb() {
		return db;
	}
	
	public String getRechercheCourante() {
		return rechercheCourante;
	}

	public void setRechercheCourante(String rechercheCourante) {
		this.rechercheCourante = rechercheCourante;
	}
	
	public Tri getTri() {
		return tri;
	}

	public void setTri(Tri tri) {
		this.tri = tri;
	}

	public boolean isEnCoursSynchro() {
		return enCoursSynchro;
	}

	public void setEnCoursSynchro(boolean enCoursSynchro) {
		this.enCoursSynchro = enCoursSynchro;
	}

	public String getServeur() {
		return serveur;
	}

	public void setServeur(String serveur) {
		this.serveur = serveur;
	}
	
	public Tache getTacheById(long id) {
		
		Tache tache = null;
		for(int i=0;i<listeTaches.size();i++)
			if(listeTaches.get(i).getIdentifiant() == id)
				tache = listeTaches.get(i);
		
		return tache;
	}
	
	public Tag getTagById(long id) {
		
		Tag tag = null;
		for(int i=0;i<listeTags.size();i++)
			if(listeTags.get(i).getIdentifiant() == id)
				tag = listeTags.get(i);
		
		return tag;
	}

	public long getIdMaxTache() {
		
		long max = 0;
		for(Tache t : listeTaches)
			if(t.getIdentifiant()>max)
				max = t.getIdentifiant();
		
		return max;
		
	}
	
	public long getIdMaxTag(){
		long max = 0;
		for(Tag t : listeTags)
			if(t.getIdentifiant()>max)
				max = t.getIdentifiant();
		
		return max;
	}
	
	public void supprimerTache(Tache t) {
		
		ArrayList<Long> listeTachesSuppr = new ArrayList<Long>();
		
		supprimerTache2(t, listeTachesSuppr);
		
		for(Tache tache: listeTaches) {
			for(long l : listeTachesSuppr)
				tache.getListeTachesFille().remove(l);
		}

	}
	
	public void supprimerTache2(Tache t, ArrayList<Long> listeTachesSuppr) {
		
		Log.i("suppr2", t.getIdentifiant() + "");
		
		listeTachesSuppr.add(t.getIdentifiant());
		for(int i = 0; i<t.getListeTachesFille().size(); i++)
			supprimerTache2(getTacheById(t.getListeTachesFille().get(i)), listeTachesSuppr);
		listeTaches.remove(t);
		
	}
	
	public void reinitialiserModele() {
		listeTags = new ArrayList<Tag>();
		listeTaches = new ArrayList<Tache>();
	}
	
	public void initialiserModele() {
		listeTags = bdd.getListeTag();
		listeTaches = bdd.getListeTache();
	}
	
	public void trierTaches(boolean ascendant) {

		Comparator<Tache> compar;
		if(tri == Tri.PRIORITE)
			compar=new ComparateurTachePriorite();
		else if(tri == Tri.ETAT)
			compar=new ComparateurTacheEtat();
		else if(tri == Tri.NOM)
			compar=new ComparateurTache();
		else
			compar=new ComparateurTacheDate();
		
		if(ascendant)
			Collections.sort(listeTaches,compar);
		else
			Collections.sort(listeTaches,Collections.reverseOrder(compar));
		/*
		for(Tache t:listeTaches)
			if(t!=null) {
				if(ascendant)
					Collections.sort(t.getFils(),compar);
				else
					Collections.sort(t.getFils(),Collections.reverseOrder(compar));
				trierTachesFils(t, ascendant, compar);
			}
		*/
	}
}

package univ_fcomte.tasks;

import java.util.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import univ_fcomte.bdd.MaBaseSQLite;

public class Modele {

	static public enum Tri { NOM, DATE, PRIORITE, ETAT, ORDRE_CREATION };
	
	private ArrayList<Tag> listeTags;
	private ArrayList<Tache> listeTaches;
	private MaBaseSQLite bdd;
	private SQLiteDatabase db;
	private String rechercheCourante;
	private ArrayList<Long> tagsVisibles;
	private boolean afficherToutesTaches;
	private Tri tri;
	private boolean enCoursSynchro;
	private String serveur;
	private long parentCourant;
	private ArrayList<Long> tachesRacines;
	private ArrayList<Tache> arborescenceCourante;


	public Modele(Context context) {
		listeTags = new ArrayList<Tag>();
		listeTaches = new ArrayList<Tache>();
		this.bdd = new MaBaseSQLite(context, "gestionnaire_taches.db", null, 1);
		this.db = this.bdd.getDb();
		rechercheCourante = "";
		tagsVisibles = null;
		afficherToutesTaches = true;
		tri = Tri.DATE;
		enCoursSynchro = false;
		//serveur = "http://10.0.2.2/gestionnaire_taches/requeteAndroid.php";
		serveur = "http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/requeteAndroid.php";
		parentCourant = -1;
		tachesRacines = new ArrayList<Long>();
		arborescenceCourante = new ArrayList<Tache>();
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
	
	public long getParentCourant() {
		return parentCourant;
	}

	public void setParentCourant(long parentCourant) {
		this.parentCourant = parentCourant;
	}
	
	public ArrayList<Long> getTagsVisibles() {
		return tagsVisibles;
	}

	public void setTagsVisibles(ArrayList<Long> tagsVisibles) {
		this.tagsVisibles = tagsVisibles;
	}
	
	public boolean isAfficherToutesTaches() {
		return afficherToutesTaches;
	}

	public void setAfficherToutesTaches(boolean afficherToutesTaches) {
		this.afficherToutesTaches = afficherToutesTaches;
	}
	
	public ArrayList<Long> getTachesRacines() {
		return tachesRacines;
	}

	public void setTachesRacines(ArrayList<Long> tachesRacines) {
		this.tachesRacines = tachesRacines;
	}
	
	public Tache getTachePereCourant() {
		if(arborescenceCourante.size() > 0)
			return arborescenceCourante.get(arborescenceCourante.size()-1);
		else
			return null;
	}
	
	public ArrayList<Tache> getArborescenceCourante() {
		return arborescenceCourante;
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
		
		listeTachesSuppr.add(t.getIdentifiant());
		for(int i = 0; i<t.getListeTachesFille().size(); i++)
			supprimerTache2(getTacheById(t.getListeTachesFille().get(i)), listeTachesSuppr);
		listeTaches.remove(t);
		
	}
	
	public void reinitialiserModele() {
		listeTags = new ArrayList<Tag>();
		listeTaches = new ArrayList<Tache>();
		tachesRacines = new ArrayList<Long>();
		tagsVisibles = new ArrayList<Long>();
	}
	
	public void initialiserModele() {
		listeTags = bdd.getListeTag();
		listeTaches = bdd.getListeTache();
		tachesRacines = bdd.getListeTachesRacines();

		if(tagsVisibles == null) {
			tagsVisibles = new ArrayList<Long>();
			for(Tag t : listeTags)
				tagsVisibles.add(t.getIdentifiant());
		}
	}
	
	public void trierTaches(boolean ascendant) {

		Comparator<Tache> compar;
		if(tri == Tri.PRIORITE)
			compar=new ComparateurTachePriorite();
		else if(tri == Tri.ETAT)
			compar=new ComparateurTacheEtat();
		else if(tri == Tri.NOM)
			compar=new ComparateurTache();
		else if(tri == Tri.ORDRE_CREATION)
			compar=new ComparateurTacheIdentifiant();
		else
			compar=new ComparateurTacheDate();
		
		if(ascendant)
			Collections.sort(listeTaches,compar);
		else
			Collections.sort(listeTaches,Collections.reverseOrder(compar));
		
	}
}

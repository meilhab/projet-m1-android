package univ_fcomte.tasks;

import java.util.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import univ_fcomte.bdd.MaBaseSQLite;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Représente le modèle avec toutes les données nécessaires au bon fonctionnement de l'application : liste de tâches, adresse du serveur distant, ...
 */
public class Modele {

	static public enum Tri { NOM, DATE, PRIORITE, ETAT, ORDRE_CREATION };
	
	private ArrayList<Tag> listeTags; //liste de tous les tags de l'application
	private ArrayList<Tache> listeTaches; //liste de toutes les tâches de l'application
	private MaBaseSQLite bdd; //Permet de modifier la BdD
	private SQLiteDatabase db; //lien vers la base SQLite
	private String rechercheCourante; //recherche actuel, vide par défaut
	private ArrayList<Long> tagsVisibles; //tags que l'utilisateur a décidé d'afficher
	private boolean afficherToutesTaches;
	private Tri tri; //ordre dans lequel les tâches doivent être triées
	private boolean enCoursSynchro; //true si on est train de synchroniser
	private String serveur; //adresse du serveur distant pour la synchronisation
	private long parentCourant; //identifiant de la tâche racine
	private ArrayList<Long> tachesRacines; // liste de toutes les tâches qui n'ont pas de père
	private ArrayList<Tache> arborescenceCourante; //arborescence courante : liste des tâches qu'il faut traverser pour aller de la racine à la tâche courante


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
	
	/**
	 * Permet d'obtenir une tâche en connaissant son identifiant
	 * @param id identifiant de la tâche souhaitée
	 * @return tâche possèdant l'identifiant donné en paramètre
	 */
	public Tache getTacheById(long id) {
		
		Tache tache = null;
		for(int i=0;i<listeTaches.size();i++)
			if(listeTaches.get(i).getIdentifiant() == id)
				tache = listeTaches.get(i);
		
		return tache;
	}
	
	/**
	 * Permet d'obtenir un tag en connaissant son identifiant
	 * @param id identifiant du tag souhaité
	 * @return tag possèdant l'identifiant donné en paramètre
	 */
	public Tag getTagById(long id) {
		
		Tag tag = null;
		for(int i=0;i<listeTags.size();i++)
			if(listeTags.get(i).getIdentifiant() == id)
				tag = listeTags.get(i);
		
		return tag;
	}
	
	/**
	 * Permet d'obtenir l'identifiant maximum de toutes les tâches
	 * @return identifiant maximum de toutes les tâches
	 */
	public long getIdMaxTache() {
		
		long max = 0;
		for(Tache t : listeTaches)
			if(t.getIdentifiant()>max)
				max = t.getIdentifiant();
		
		return max;
		
	}
	
	/**
	 * Permet d'obtenir l'identifiant maximum de tous les tags
	 * @return identifiant maximum de tous les tags
	 */
	public long getIdMaxTag(){
		long max = 0;
		for(Tag t : listeTags)
			if(t.getIdentifiant()>max)
				max = t.getIdentifiant();
		
		return max;
	}
	
	/**
	 * Supprime une tâches dans le modèle ainsi que toutes ses sous-tâches
	 * @param t tâche à supprimer
	 */
	public void supprimerTache(Tache t) {
		
		ArrayList<Long> listeTachesSuppr = new ArrayList<Long>();
		
		supprimerTache2(t, listeTachesSuppr);
		
		for(Tache tache: listeTaches) {
			for(long l : listeTachesSuppr)
				tache.getListeTachesFille().remove(l);
		}

	}
	
	/**
	 * Supprime toutes les sous tâches d'une tâche
	 * @param t tâche racine
	 * @param listeTachesSuppr liste des tâches à supprimer
	 */
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
		//tachesRacines = bdd.getListeTachesRacines();
		refreshTachesRacines();
		
		if(tagsVisibles == null) {
			tagsVisibles = new ArrayList<Long>();
			for(Tag t : listeTags)
				tagsVisibles.add(t.getIdentifiant());
		}
	}
	
	/**
	 * Tri les tâches suivant le tri courant (par ordre alphabéthique, date, ...)
	 * @param ascendant ordre ascendant si true
	 */
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
	
	
	/**
	 * Recherche et enregistre toutes les tâches qui n'ont pas de père
	 */
	public void refreshTachesRacines() {
		
		tachesRacines.clear();
		ArrayList<Long> listeTachesFilles = new ArrayList<Long>();
		
		for(Tache t : listeTaches)
			for(long l : t.getListeTachesFille())
				if(!listeTachesFilles.contains(l))
					listeTachesFilles.add(l);
		
		for(Tache t : listeTaches)
			if(!listeTachesFilles.contains(t.getIdentifiant()))
				tachesRacines.add(t.getIdentifiant());
		
	}
}

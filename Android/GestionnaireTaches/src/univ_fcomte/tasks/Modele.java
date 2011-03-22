package univ_fcomte.tasks;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import univ_fcomte.bdd.MaBaseSQLite;

public class Modele {

	private ArrayList<Tag> listeTags;
	private ArrayList<Tache> listeTaches;
	private Context context;
	private MaBaseSQLite bdd;
	private SQLiteDatabase db;
	
	public Modele(Context context) {
		listeTags = new ArrayList<Tag>();
		listeTaches = new ArrayList<Tache>();
		this.context = context;
		this.bdd = new MaBaseSQLite(context, "gestionnaire_taches.db", null, 1);
		this.db = this.bdd.getDb();
		
		listeTags=new ArrayList<Tag>();
		listeTags.add(new Tag(1, "personnel"));
		listeTags.add(new Tag(2, "professionnel"));
		listeTags.add(new Tag(3, "Examen"));
		listeTags.add(new Tag(4, "université"));
		
		/*
		Tache t=new Tache(1, "tache 1", "description tache 1", new Etat(1), listeTags);
		ajoutTache(t);
		t=new Tache(2, "tache 2", "description tache 2", new Etat(2), listeTags);
		ajoutTache(t);
		t=new Tache(3, "tache 3", "description tache 3", new Etat(3), listeTags);
		ajoutTache(t);
		t=new Tache(4, "tache 4", "description tache 4", new Etat(3), listeTags);
		ajoutTache(t);
		t=new Tache(5, "tache 5", "description tache 5", new Etat(2), listeTags);
		ajoutTache(t);
		t=new Tache(6, "tache 6", "description tache 6", new Etat(1), listeTags);
		ajoutTache(t);
		t=new Tache(7, "tache 7", "description tache 7", new Etat(2), listeTags);
		ajoutTache(t);
		t=new Tache(8, "tache 8", "description tache 8", new Etat(3), listeTags);
		ajoutTache(t);
		*/
		
	}

	public void ajoutTache(Tag t){
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
	
}

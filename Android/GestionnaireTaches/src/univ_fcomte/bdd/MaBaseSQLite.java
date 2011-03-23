package univ_fcomte.bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import univ_fcomte.tasks.Tache;
import univ_fcomte.tasks.Tag;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MaBaseSQLite extends SQLiteOpenHelper {

	protected SQLiteDatabase db;

	public static String TABLE_TAG = "tag";
	public static String TABLE_PRIORITE = "priorite";
	public static String TABLE_ETAT = "etat";
	public static String TABLE_TACHE = "tache";
	public static String TABLE_APOURTAG = "apourtag";
	public static String TABLE_APOURFILS = "apourfils";
	
	
	private static String CREATE_TABLE_TAG = "create table " + TABLE_TAG + " ("
		+ "idTag INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "libelleTag varchar(64) not null"
		+ ")";
	
	private static String CREATE_TABLE_PRIORITE = "create table " + TABLE_PRIORITE + " ("
		+ "idPriorite INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "libellePriorite varchar(64) not null"
		+ ")";
	
	private static String CREATE_TABLE_ETAT = "create table " + TABLE_ETAT + " ("
		+ "idEtat INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "libelleEtat varchar(64) not null"
		+ ")";
	
	private static String CREATE_TABLE_TACHE = "create table " + TABLE_TACHE + " ("
		+ "idTache INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "nomTache varchar(255) not null,"
		+ "descriptionTache text,"
		+ "dateLimite datetime,"
		+ "idEtat INTEGER not null,"
		+ "idPriorite INTEGER not null,"
		+ "foreign key(idEtat) references etat(idEtat),"
		+ "foreign key(idPriorite) references priorite(idPriorite))";
	
	private static String CREATE_TABLE_APOURTAG = "create table " + TABLE_APOURTAG + " ("
		+ "idTache INTEGER not null,"
		+ "idTag INTEGER not null,"
		+ "foreign key(idTache) references tache(idTache),"
		+ "foreign key(idTag) references tag(idTag),"
		+ "primary key(idTache, idTag))";

	private static String CREATE_TABLE_APOURFILS = "create table " + TABLE_APOURFILS + " ("
		+ "idPere INTEGER not null,"
		+ "idFils INTEGER not null,"
		+ "foreign key(idPere) references tache(idPere),"
		+ "foreign key(idFils) references tache(idFils),"
		+ "primary key(idPere, idFils))";
	
	
	public MaBaseSQLite(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		open();
		db.execSQL(CREATE_TABLE_TAG);
		db.execSQL(CREATE_TABLE_PRIORITE);
		db.execSQL(CREATE_TABLE_ETAT);
		db.execSQL(CREATE_TABLE_TACHE);
		db.execSQL(CREATE_TABLE_APOURTAG);
		db.execSQL(CREATE_TABLE_APOURFILS);
		close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		open();
		db.execSQL("drop table if exists " + TABLE_APOURFILS);
		db.execSQL("drop table if exists " + TABLE_APOURTAG);
		db.execSQL("drop table if exists " + TABLE_TACHE);
		db.execSQL("drop table if exists " + TABLE_ETAT);
		db.execSQL("drop table if exists " + TABLE_PRIORITE);
		db.execSQL("drop table if exists " + TABLE_TAG);
		onCreate(db);
		close();
	}
	
	public MaBaseSQLite open(){
		db = getWritableDatabase();
		return this;
	}
	
	public void close(){
		db.close();
	}

	public SQLiteDatabase getDb() {
		return db;
	}
	
	public Tache getTache(long idTache) {
		
		open();
		
		Tache tache=null;
		
		ArrayList<Long> listeTag;
		ArrayList<Long> listeFils;

		String[] colonnesARecup = new String[] { "nomTache", "descriptionTache", "dateLimite", "idEtat", "idPriorite" };

		Cursor cursorResults = db.query(TABLE_TACHE, colonnesARecup, "idTache=" + idTache, null, null, null, "nomTache asc", null);
		Cursor cursorTag;
		Cursor cursorFils;
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					int columnNom= cursorResults.getColumnIndex("nomTache");
					int columnDescription= cursorResults.getColumnIndex("descriptionTache");
					int columnDate= cursorResults.getColumnIndex("dateLimite");
					int columnEtat= cursorResults.getColumnIndex("idEtat");
					int columnPriorite= cursorResults.getColumnIndex("idPriorite");
					
					listeTag = new ArrayList<Long>();
					listeFils = new ArrayList<Long>();
					
					cursorTag = db.query(TABLE_APOURTAG, new String[] { "idTag" }, "idTache=" + idTache, null, null, null, "idTag asc", null);
					if (null != cursorTag) {
						if (cursorTag.moveToFirst()) {
							do {
								int columnTag= cursorResults.getColumnIndex("idTag");
								listeTag.add(cursorResults.getLong(columnTag));
							} while (cursorTag.moveToNext());
						}
					}
					
					cursorFils = db.query(TABLE_APOURFILS, new String[] { "idFils" }, "idPere=" + idTache, null, null, null, "idPere asc", null);
					if (null != cursorFils) {
						if (cursorFils.moveToFirst()) {
							do {
								int columnFils= cursorResults.getColumnIndex("idFils");
								listeFils.add(cursorResults.getLong(columnFils));
							} while (cursorFils.moveToNext());
						}
					}
					
					tache=new Tache(idTache, cursorResults.getString(columnNom), cursorResults.getString(columnDescription), cursorResults.getInt(columnEtat), cursorResults.getInt(columnPriorite), listeTag, listeFils);
					
				} while (cursorResults.moveToNext());
			}
		}

		close();
		
		return tache;
		
	}
	
	public Tag getTag(long idTag) {
		
		open();
		
		Tag tag=null;

		String[] colonnesARecup = new String[] { "idTag", "libelleTag" };

		Cursor cursorResults = db.query(TABLE_TAG, colonnesARecup, "idTag="+idTag, null, null, null, "idTag asc", null);
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					int columnLibelle= cursorResults.getColumnIndex("libelleTag");
					tag=new Tag(idTag, cursorResults.getString(columnLibelle));
				} while (cursorResults.moveToNext());
			}
		}

		close();
		
		return tag;
		
	}
	
	public ArrayList<Tache> getListeTache() {
		
		open();
		
		ArrayList<Tache> listeTache = new ArrayList<Tache>();
		
		ArrayList<Long> listeTag;
		ArrayList<Long> listeFils;

		String[] colonnesARecup = new String[] { "idTache", "nomTache", "descriptionTache", "dateLimite", "idEtat", "idPriorite" };

		Cursor cursorResults = db.query(TABLE_TACHE, colonnesARecup, null, null, null, null, "nomTache asc", null);
		Cursor cursorTag;
		Cursor cursorFils;
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					int columnId= cursorResults.getColumnIndex("IdTache");
					int columnNom= cursorResults.getColumnIndex("nomTache");
					int columnDescription= cursorResults.getColumnIndex("descriptionTache");
					int columnDate= cursorResults.getColumnIndex("dateLimite");
					int columnEtat= cursorResults.getColumnIndex("idEtat");
					int columnPriorite= cursorResults.getColumnIndex("idPriorite");
					
					listeTag = new ArrayList<Long>();
					listeFils = new ArrayList<Long>();
					
					cursorTag = db.query(TABLE_APOURTAG, new String[] { "idTag" }, "idTache=" + columnId, null, null, null, "idTag asc", null);
					if (null != cursorTag) {
						if (cursorTag.moveToFirst()) {
							do {
								int columnTag= cursorResults.getColumnIndex("idTag");
								listeTag.add(cursorResults.getLong(columnTag));
							} while (cursorTag.moveToNext());
						}
					}
					
					cursorFils = db.query(TABLE_APOURFILS, new String[] { "idFils" }, "idPere=" + columnId, null, null, null, "idPere asc", null);
					if (null != cursorFils) {
						if (cursorFils.moveToFirst()) {
							do {
								int columnFils= cursorResults.getColumnIndex("idFils");
								listeFils.add(cursorResults.getLong(columnFils));
							} while (cursorFils.moveToNext());
						}
					}
					
					listeTache.add(new Tache(cursorResults.getInt(columnId), cursorResults.getString(columnNom), cursorResults.getString(columnDescription), cursorResults.getInt(columnEtat), cursorResults.getInt(columnPriorite), listeTag, listeFils));
					
				} while (cursorResults.moveToNext());
			}
		}

		close();
		
		return listeTache;
		
	}
	
	public ArrayList<Tag> getListeTag() {
		
		open();
		
		ArrayList<Tag> listeTag = new ArrayList<Tag>();

		String[] colonnesARecup = new String[] { "idTag", "libelleTag" };

		Cursor cursorResults = db.query(TABLE_TAG, colonnesARecup, null, null, null, null, "idTag asc", null);
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					int columnId = cursorResults.getColumnIndex("idTag");
					int columnLibelle= cursorResults.getColumnIndex("libelleTag");
					listeTag.add(new Tag(cursorResults.getInt(columnId), cursorResults.getString(columnLibelle)));
				} while (cursorResults.moveToNext());
			}
		}

		close();
		
		return listeTag;
		
	}
	
	
	
	public long ajouterTache(Tache tache, long pere, boolean plusieurs) {
		
		if(!plusieurs)
			open();
		
		ContentValues tacheToInsert = new ContentValues();
		tacheToInsert.put("nomTache", tache.getNom());
		tacheToInsert.put("descriptionTache", tache.getDescription());
		tacheToInsert.put("dateLimite", "");
		tacheToInsert.put("idEtat", tache.getEtat());
		tacheToInsert.put("idPriorite", tache.getPriorite());
		
		long nouveauId = db.insert(TABLE_TACHE, null, tacheToInsert);
		tache.setIdentifiant(nouveauId);
		
		if(nouveauId != -1 && pere > 0) {
			ContentValues filsToInsert = new ContentValues();
			tacheToInsert.put("idPere", pere);
			tacheToInsert.put("idPriorite", nouveauId);
			if(db.insert(TABLE_APOURFILS, null, filsToInsert) == -1)
				nouveauId = -1;
		}
		
		if(nouveauId != -1) {
			ContentValues TagsToInsert;
			for(Long l : tache.getListeTags()) {
				TagsToInsert = new ContentValues();
				TagsToInsert.put("idTache", nouveauId);
				TagsToInsert.put("idTag", l);
				if(db.insert(TABLE_APOURTAG, null, TagsToInsert) == -1)
					nouveauId = -1;
			}
				
		}
		
		if(!plusieurs)
			close();
		
		return nouveauId;
	}
	
	public long ajouterTag(Tag tag, boolean plusieurs) {
		
		if(!plusieurs)
			open();
		
		ContentValues tagToInsert = new ContentValues();
		tagToInsert.put("libelleTag", tag.getNom());
		
		long nouveauId = db.insert(TABLE_TAG, null, tagToInsert);
		tag.setIdentifiant(nouveauId);
		
		if(!plusieurs)
			close();
		
		return nouveauId;
	
	}	
	
	public boolean ajouterListeTag(ArrayList<Tag> listeTag) {
		
		open();
		
		boolean reussi=true;
		
		for(Tag t : listeTag)
			if(ajouterTag(t, true)<1)
				reussi = false;
		
		close();
		
		return reussi;
		
	}
	
	public boolean ajouterListeTache(ArrayList<Tache> listeTache) {
		
		open();
		
		boolean reussi=true;
		
		for(Tache t : listeTache)
			if(ajouterTache(t, -1, true)<1)
				reussi = false;
		
		close();
		
		return reussi;
		
	}
	
	public boolean ajouterlisteAPourFils(HashMap<Long, Long> listeAPourFils) {
		
		open();
		
		boolean reussi=true;

		Set cles = listeAPourFils.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()){
			Long cle = (Long) it.next();
			ContentValues apourfilsToInsert = new ContentValues();
			apourfilsToInsert.put("idPere", cle);
			apourfilsToInsert.put("idFils", listeAPourFils.get(cle));
			db.insert(TABLE_APOURFILS, null, apourfilsToInsert);
			Log.i("","ajout de "+ cle + ", "+listeAPourFils.get(cle));
		}
		
		close();
		
		return reussi;
		
	}
	
	public boolean ajouterlisteAPourTag(HashMap<Long, Long> listeAPourTag) {
		
		open();
		
		boolean reussi=true;

		Set cles = listeAPourTag.keySet();
		Iterator it = cles.iterator();
		while (it.hasNext()){
			Long cle = (Long) it.next();
			ContentValues apourtagToInsert = new ContentValues();
			apourtagToInsert.put("idTache", cle);
			apourtagToInsert.put("idTag", listeAPourTag.get(cle));
			db.insert(TABLE_APOURTAG, null, apourtagToInsert);
		}
		
		close();
		
		return reussi;
		
	}
	
	public boolean reinitialiserBDD(ArrayList<Tag> listeTag, ArrayList<Tache> listeTache, HashMap<Long, Long> listeAPourTag, HashMap<Long, Long> listeAPourFils) {
		
		open();
		
		db.execSQL("drop table if exists " + TABLE_APOURFILS);
		db.execSQL("drop table if exists " + TABLE_APOURTAG);
		db.execSQL("drop table if exists " + TABLE_TACHE);
		db.execSQL("drop table if exists " + TABLE_ETAT);
		db.execSQL("drop table if exists " + TABLE_PRIORITE);
		db.execSQL("drop table if exists " + TABLE_TAG);
		onCreate(db);
		
		close();
		
		return ajouterListeTag(listeTag) && ajouterListeTache(listeTache) && /*ajouterlisteAPourTag(listeAPourTag) && */ajouterlisteAPourFils(listeAPourFils);
		
		
	}
	
	public int modifTache(Tache tache) {
		
		open();
		
		ContentValues tacheToUpdate = new ContentValues();
		tacheToUpdate.put("nomTache", tache.getNom());
		tacheToUpdate.put("descriptionTache", tache.getDescription());
		tacheToUpdate.put("dateLimite", "");
		tacheToUpdate.put("idEtat", tache.getEtat());
		tacheToUpdate.put("idPriorite", tache.getPriorite());
		
		
		int retour = db.update(TABLE_TACHE, tacheToUpdate, "idTache=" + tache.getIdentifiant(), null);
		
		close();
		
		return retour;
		
	}
	
	public long modifTag(Tag tag) {
		
		open();
		
		ContentValues tagToUpdate = new ContentValues();
		tagToUpdate.put("libelleTag", tag.getNom());
		
		int retour = db.update(TABLE_TAG, tagToUpdate, "idTag=" + tag.getIdentifiant(), null);
		
		close();
		
		return retour;
		
	}
	
	public boolean supprimerTache(Tache tache) {
		
		open();
		
		boolean reussi=true;
		
		if(db.delete(TABLE_APOURFILS, "idPere=" + tache.getIdentifiant(), null)<1)
			reussi = false;
		if(db.delete(TABLE_APOURTAG, "idTache=" + tache.getIdentifiant(), null)<1)
			reussi = false;

		close();
		
		return reussi;
	}
	
	public void viderToutesTables() {
		
		onUpgrade(db, 1, 1);
		
	}
	
}

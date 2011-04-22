package univ_fcomte.bdd;

import java.util.*;

import univ_fcomte.tasks.*;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Gestion de la base de données SQLite sur le téléphone avec toutes les opérations possibles (ajout de tâches, modification, ...)
 */
public class MaBaseSQLite extends SQLiteOpenHelper {

	protected SQLiteDatabase db = null;
	
	private ArrayList<Long> listeTachesRacines;

	public static String TABLE_TAG = "tag";
	public static String TABLE_PRIORITE = "priorite";
	public static String TABLE_ETAT = "etat";
	public static String TABLE_TACHE = "tache";
	public static String TABLE_APOURTAG = "apourtag";
	public static String TABLE_APOURFILS = "apourfils";
	
	
	private static String CREATE_TABLE_TAG = "create table " + TABLE_TAG + " ("
		+ "idTag INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "libelleTag varchar(64) not null,"
		+ "versionTag INTEGER not null"
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
		+ "versionTache INTEGER not null,"
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
		listeTachesRacines = new ArrayList<Long>();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//open();
		db.execSQL(CREATE_TABLE_TAG);
		db.execSQL(CREATE_TABLE_PRIORITE);
		db.execSQL(CREATE_TABLE_ETAT);
		db.execSQL(CREATE_TABLE_TACHE);
		db.execSQL(CREATE_TABLE_APOURTAG);
		db.execSQL(CREATE_TABLE_APOURFILS);
		//close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//open();
		db.execSQL("drop table if exists " + TABLE_APOURFILS);
		db.execSQL("drop table if exists " + TABLE_APOURTAG);
		db.execSQL("drop table if exists " + TABLE_TACHE);
		db.execSQL("drop table if exists " + TABLE_ETAT);
		db.execSQL("drop table if exists " + TABLE_PRIORITE);
		db.execSQL("drop table if exists " + TABLE_TAG);
		onCreate(db);
		//close();
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

		String[] colonnesARecup = new String[] { "nomTache", "descriptionTache", "dateLimite", "idEtat", "idPriorite", "versionTache" };

		Cursor cursorResults = db.query(TABLE_TACHE, colonnesARecup, "idTache=" + idTache, null, null, null, "nomTache asc", null);
		int columnNom= cursorResults.getColumnIndex("nomTache");
		int columnDescription= cursorResults.getColumnIndex("descriptionTache");
		int columnDate= cursorResults.getColumnIndex("dateLimite");
		int columnEtat= cursorResults.getColumnIndex("idEtat");
		int columnPriorite= cursorResults.getColumnIndex("idPriorite");
		int columnVersion= cursorResults.getColumnIndex("versionTache");
		Cursor cursorTag;
		Cursor cursorFils;
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
				
					listeTag = new ArrayList<Long>();
					listeFils = new ArrayList<Long>();
					
					cursorTag = db.query(TABLE_APOURTAG, new String[] { "idTag" }, "idTache=" + idTache, null, null, null, "idTag asc", null);
					if (null != cursorTag) {
						if (cursorTag.moveToFirst()) {
							do {
								int columnTag= cursorTag.getColumnIndex("idTag");
								listeTag.add(cursorTag.getLong(columnTag));
							} while (cursorTag.moveToNext());
						}
					}
					
					cursorFils = db.query(TABLE_APOURFILS, new String[] { "idFils" }, "idPere=" + idTache, null, null, null, "idPere asc", null);
					if (null != cursorFils) {
						if (cursorFils.moveToFirst()) {
							do {
								int columnFils= cursorFils.getColumnIndex("idFils");
								listeFils.add(cursorFils.getLong(columnFils));
							} while (cursorFils.moveToNext());
						}
					}
					
					tache=new Tache(idTache, cursorResults.getString(columnNom), cursorResults.getString(columnDescription), cursorResults.getString(columnDate), cursorResults.getInt(columnPriorite), cursorResults.getInt(columnEtat), cursorResults.getInt(columnVersion), listeTag, listeFils);
					
				} while (cursorResults.moveToNext());
			}
		}

		close();
		
		return tache;
		
	}
	
	public Tag getTag(long idTag) {
		
		open();
		
		Tag tag=null;

		String[] colonnesARecup = new String[] { "idTag", "libelleTag", "versionTag" };

		Cursor cursorResults = db.query(TABLE_TAG, colonnesARecup, "idTag="+idTag, null, null, null, "idTag asc", null);
		int columnLibelle= cursorResults.getColumnIndex("libelleTag");
		int columnVersion= cursorResults.getColumnIndex("versionTag");
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					tag=new Tag(idTag, cursorResults.getString(columnLibelle), cursorResults.getInt(columnVersion));
				} while (cursorResults.moveToNext());
			}
		}

		close();
		
		return tag;
		
	}
	
	public ArrayList<Tache> getListeTache() {
		
		open();
		
		listeTachesRacines = new ArrayList<Long>();
		ArrayList<Long> listeTachesFilles = new ArrayList<Long>();
		
		ArrayList<Tache> listeTache = new ArrayList<Tache>();
		
		ArrayList<Long> listeTag;
		ArrayList<Long> listeFils;

		String[] colonnesARecup = new String[] { "idTache", "nomTache", "descriptionTache", "dateLimite", "idEtat", "idPriorite", "versionTache" };

		Cursor cursorResults = db.query(TABLE_TACHE, colonnesARecup, null, null, null, null, "nomTache asc", null);
		int columnId = cursorResults.getColumnIndex("idTache");
		int columnNom = cursorResults.getColumnIndex("nomTache");
		int columnDescription = cursorResults.getColumnIndex("descriptionTache");
		int columnDate = cursorResults.getColumnIndex("dateLimite");
		int columnEtat = cursorResults.getColumnIndex("idEtat");
		int columnPriorite = cursorResults.getColumnIndex("idPriorite");
		int columnVersion = cursorResults.getColumnIndex("versionTache");
		Cursor cursorTag;
		Cursor cursorFils;
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					
					listeTag = new ArrayList<Long>();
					listeFils = new ArrayList<Long>();
					
					cursorTag = db.query(TABLE_APOURTAG, new String[] { "idTag" }, "idTache=" + cursorResults.getInt(columnId), null, null, null, "idTag asc", null);
					if (null != cursorTag) {
						if (cursorTag.moveToFirst()) {
							do {
								int columnTag= cursorTag.getColumnIndex("idTag");
								listeTag.add(cursorTag.getLong(columnTag));
							} while (cursorTag.moveToNext());
						}
					}
					cursorTag.close();
					
					cursorFils = db.query(TABLE_APOURFILS, new String[] { "idFils" }, "idPere=" + cursorResults.getInt(columnId), null, null, null, "idPere asc", null);
					if (null != cursorFils) {
						if (cursorFils.moveToFirst()) {
							do {
								int columnFils= cursorFils.getColumnIndex("idFils");
								listeFils.add(cursorFils.getLong(columnFils));
								
								if(!listeTachesFilles.contains(cursorFils.getLong(columnFils)))
									listeTachesFilles.add(cursorFils.getLong(columnFils));
							
							} while (cursorFils.moveToNext());
						}
					}
					cursorFils.close();
										
					listeTache.add(new Tache(cursorResults.getLong(columnId), cursorResults.getString(columnNom), cursorResults.getString(columnDescription), cursorResults.getString(columnDate), cursorResults.getInt(columnPriorite), cursorResults.getInt(columnEtat), cursorResults.getInt(columnVersion), listeTag, listeFils));					
					
				} while (cursorResults.moveToNext());
			}
		}

		cursorResults.close();
		
		close();
		
		
		for(Tache t : listeTache)
			if(!listeTachesFilles.contains(t.getIdentifiant()))
				listeTachesRacines.add(t.getIdentifiant());
		
		return listeTache;
		
	}
	
	public ArrayList<Tag> getListeTag() {
		
		open();
		
		ArrayList<Tag> listeTag = new ArrayList<Tag>();

		String[] colonnesARecup = new String[] { "idTag", "libelleTag", "versionTag" };

		Cursor cursorResults = db.query(TABLE_TAG, colonnesARecup, null, null, null, null, "idTag asc", null);
		int columnId = cursorResults.getColumnIndex("idTag");
		int columnLibelle= cursorResults.getColumnIndex("libelleTag");
		int columnVersion= cursorResults.getColumnIndex("versionTag");
		if (null != cursorResults) {
			if (cursorResults.moveToFirst()) {
				do {
					listeTag.add(new Tag(cursorResults.getInt(columnId), cursorResults.getString(columnLibelle), cursorResults.getInt(columnVersion)));
				} while (cursorResults.moveToNext());
			}
		}
		
		cursorResults.close();

		close();
		
		return listeTag;
		
	}
	
	
	public long ajouterTache(Tache tache, long pere, boolean plusieurs) {
		
		if(!plusieurs)
			open();
		
		ContentValues tacheToInsert = new ContentValues();
		tacheToInsert.put("idTache", tache.getIdentifiant());
		tacheToInsert.put("nomTache", tache.getNom());
		tacheToInsert.put("descriptionTache", tache.getDescription());
		tacheToInsert.put("dateLimite", tache.getDateLimiteToString());
		tacheToInsert.put("idEtat", tache.getEtat());
		tacheToInsert.put("idPriorite", tache.getPriorite());
		tacheToInsert.put("versionTache", tache.getVersion());
		
		long nouveauId = db.insert(TABLE_TACHE, null, tacheToInsert);
		tache.setIdentifiant(nouveauId);
		
		if(nouveauId != -1 && pere > 0) {
			ContentValues filsToInsert = new ContentValues();
			filsToInsert.put("idPere", pere);
			filsToInsert.put("idFils", tache.getIdentifiant());
			if(db.insert(TABLE_APOURFILS, null, filsToInsert) == -1)
				nouveauId = -1;
		}
		
		if(nouveauId != -1) {
			ContentValues TagsToInsert;
			for(Long l : tache.getListeTags()) {
				TagsToInsert = new ContentValues();
				TagsToInsert.put("idTache", tache.getIdentifiant());
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
		tagToInsert.put("versionTag", tag.getVersion());
		
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
		
		boolean reussi=true;
		
		if(listeAPourFils != null) {
			open();
	
			Set<Long> cles = listeAPourFils.keySet();
			Iterator<Long> it = cles.iterator();
			while (it.hasNext()){
				Long cle = (Long) it.next();
				ContentValues apourfilsToInsert = new ContentValues();
				apourfilsToInsert.put("idPere", listeAPourFils.get(cle));
				apourfilsToInsert.put("idFils", cle);
				db.insert(TABLE_APOURFILS, null, apourfilsToInsert);
			}
			
			close();
		}
		
		return reussi;
		
	}
	
	public boolean ajouterlisteAPourTag(HashMap<Long, Long> listeAPourTag) {
		
		boolean reussi=true;
		
		if(listeAPourTag != null) {
			open();
			
			Set<Long> cles = listeAPourTag.keySet();
			Iterator<Long> it = cles.iterator();
			while (it.hasNext()){
				Long cle = (Long) it.next();
				ContentValues apourtagToInsert = new ContentValues();
				apourtagToInsert.put("idTache", cle);
				apourtagToInsert.put("idTag", listeAPourTag.get(cle));
				db.insert(TABLE_APOURTAG, null, apourtagToInsert);
			}
			
			close();
		}
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
		tacheToUpdate.put("dateLimite", tache.getDateLimiteToString());
		tacheToUpdate.put("idEtat", tache.getEtat());
		tacheToUpdate.put("idPriorite", tache.getPriorite());
		tacheToUpdate.put("versionTache", tache.getVersion());
		
		int retour = db.update(TABLE_TACHE, tacheToUpdate, "idTache=" + tache.getIdentifiant(), null);
		
		close();
		
		return retour;
		
	}
	
	public long modifTag(Tag tag) {
		
		open();
		
		ContentValues tagToUpdate = new ContentValues();
		tagToUpdate.put("libelleTag", tag.getNom());
		tagToUpdate.put("versionTag", tag.getVersion());
		
		int retour = db.update(TABLE_TAG, tagToUpdate, "idTag=" + tag.getIdentifiant(), null);
		
		close();
		
		return retour;
		
	}
	
	public boolean supprimerTache(long idTache, boolean ouvrirBase) {
		
		if(ouvrirBase)
			open();
		
		boolean reussi=true;
		
		Cursor cursorFils = db.query(TABLE_APOURFILS, new String[] { "idFils" }, "idPere=" + idTache, null, null, null, "idPere asc", null);
		if (null != cursorFils) {
			if (cursorFils.moveToFirst()) {
				do {
					int columnFils= cursorFils.getColumnIndex("idFils");
					supprimerTache(cursorFils.getLong(columnFils), false);
				} while (cursorFils.moveToNext());
			}
		}
		
		cursorFils.close();
		
		if(db.delete(TABLE_APOURTAG, "idTache=" + idTache, null)<1)
			reussi = false;
		if(db.delete(TABLE_APOURFILS, "idPere=" + idTache, null)<1)
			reussi = false;
		if(db.delete(TABLE_APOURFILS, "idFils=" + idTache, null)<1)
			reussi = false;
		if(db.delete(TABLE_TACHE, "idTache=" + idTache, null)<1)
			reussi = false;

		if(ouvrirBase)
			close();
		
		return reussi;
	}
	
	public boolean supprimerTag(long idTag) {
		
		boolean reussi=true;
		open();
		
		if(db.delete(TABLE_APOURTAG, "idTag=" + idTag, null)<1)
			reussi = false;
		if(db.delete(TABLE_TAG, "idTag=" + idTag, null)<1)
			reussi = false;
		
		close();
		return reussi;
		
	}
	
	public void viderToutesTables() {
		
		onUpgrade(db, 1, 1);
		
	}
	
	public ArrayList<Long> getListeTachesRacines() {
		return listeTachesRacines;
	}
}

package univ_fcomte.bdd;

import java.util.ArrayList;

import univ_fcomte.tasks.Tache;
import univ_fcomte.tasks.Tag;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ModifierBDD {

	private SQLiteDatabase db;
	
	public static String TABLE_TAG = "tag";
	public static String TABLE_PRIORITE = "priorite";
	public static String TABLE_ETAT = "etat";
	public static String TABLE_TACHE = "tache";
	public static String TABLE_APOURTAG = "apourtag";
	public static String TABLE_APOURFILS = "apourfils";
	
	public ModifierBDD(SQLiteDatabase db) {
		this.db = db;
	}
	
	public Tache getTache(long idTache) {
		
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

		return tache;
		
	}
	
	public Tag getTag(long idTag) {
		
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

		return tag;
		
	}
	
	public ArrayList<Tache> getListeTache() {
		
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

		return listeTache;
		
	}
	
	public ArrayList<Tag> getListeTag() {
		
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

		return listeTag;
		
	}
	
	
	
	public long ajouterTache(Tache tache, long pere) {
		
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
		
		return nouveauId;
	}
	
	public long ajouterTag(Tag tag) {
		
		ContentValues tagToInsert = new ContentValues();
		tagToInsert.put("libelleTag", tag.getNom());
		
		long nouveauId = db.insert(TABLE_TAG, null, tagToInsert);
		tag.setIdentifiant(nouveauId);
		
		return nouveauId;
	
	}	
	
	
	public int modifTache(Tache tache) {
		
		ContentValues tacheToUpdate = new ContentValues();
		tacheToUpdate.put("nomTache", tache.getNom());
		tacheToUpdate.put("descriptionTache", tache.getDescription());
		tacheToUpdate.put("dateLimite", "");
		tacheToUpdate.put("idEtat", tache.getEtat());
		tacheToUpdate.put("idPriorite", tache.getPriorite());
		
		return db.update(TABLE_TACHE, tacheToUpdate, "idTache=" + tache.getIdentifiant(), null);
		
	}
	
	public long modifTag(Tag tag) {
		
		ContentValues tagToUpdate = new ContentValues();
		tagToUpdate.put("libelleTag", tag.getNom());
		
		return db.update(TABLE_TAG, tagToUpdate, "idTag=" + tag.getIdentifiant(), null);
		
	}
	
	public boolean supprimerTache(Tache tache) {
		
		boolean reussi=true;
		
		if(db.delete(TABLE_APOURFILS, "idPere=" + tache.getIdentifiant(), null)<1)
			reussi = false;
		if(db.delete(TABLE_APOURTAG, "idTache=" + tache.getIdentifiant(), null)<1)
			reussi = false;

		return reussi;
	}
	
}

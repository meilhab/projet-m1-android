package univ_fcomte.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MaBaseSQLite extends SQLiteOpenHelper {

	private SQLiteDatabase db;

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
		Log.i("","constructeur");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("","onCreate");
		db.execSQL(CREATE_TABLE_TAG);
		db.execSQL(CREATE_TABLE_PRIORITE);
		db.execSQL(CREATE_TABLE_ETAT);
		db.execSQL(CREATE_TABLE_TACHE);
		db.execSQL(CREATE_TABLE_APOURTAG);
		db.execSQL(CREATE_TABLE_APOURFILS);
		Log.i("","finOncreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TABLE_APOURFILS);
		db.execSQL("drop table if exists " + TABLE_APOURTAG);
		db.execSQL("drop table if exists " + TABLE_TACHE);
		db.execSQL("drop table if exists " + TABLE_ETAT);
		db.execSQL("drop table if exists " + TABLE_PRIORITE);
		db.execSQL("drop table if exists " + TABLE_TAG);
		onCreate(db);
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
	
}

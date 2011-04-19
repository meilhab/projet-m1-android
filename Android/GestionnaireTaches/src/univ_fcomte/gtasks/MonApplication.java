package univ_fcomte.gtasks;

import univ_fcomte.tasks.Modele;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

public class MonApplication extends Application{

	private Modele modele;
	private GestionnaireTaches gt;
	private String rechercheCourante;
	
	public MonApplication() {
		modele=new Modele(this);
		rechercheCourante = "";
	}

	public String getRechercheCourante() {
		return rechercheCourante;
	}

	public void setRechercheCourante(String rechercheCourante) {
		this.rechercheCourante = rechercheCourante;
	}

	public Modele getModele() {
		return modele;
	}
	
	public void setModele(Modele modele) {
		this.modele = modele;
	}

	public GestionnaireTaches getGt() {
		return gt;
	}

	public void setGt(GestionnaireTaches gt) {
		this.gt = gt;
	}
	
	public int getVersionAppli() {
		
		int v = 0;
		try {
			v = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionCode;
		} catch (NameNotFoundException e) {}
		
		Log.i("version appli", "" +v);
		
		return v;
	}
	
	public int getVersionPref() {

		return PreferenceManager.getDefaultSharedPreferences(gt).getInt("version_appli", 0);
		
	}
	
	public boolean testNouvelleVersion() {
		
		if(getVersionAppli() > getVersionPref())
			return true;
		else
			return false;
				
	}
	
	public void afficherMessageBienvenu() {
		
		if(testNouvelleVersion()) {
			enregistrerVersionAppliToPref();
			
			String[] listeNews = gt.getResources().getStringArray(R.array.version_news);
			String message = "";
			if(getVersionAppli() < listeNews.length)
				message = listeNews[getVersionAppli()];
			else
				message = listeNews[0];
			
			AlertDialog.Builder builder = new AlertDialog.Builder(gt);
			builder.setTitle(R.string.app_name);
			builder.setMessage(message);
			builder.setCancelable(false);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {}
			});
			AlertDialog alert = builder.create();
			alert.show();
		
		}
	}
	
	public void enregistrerVersionAppliToPref() {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gt);
		Editor editor = preferences.edit();
		editor.putInt("version_appli", getVersionAppli());
		editor.commit();
		
	}
	
}

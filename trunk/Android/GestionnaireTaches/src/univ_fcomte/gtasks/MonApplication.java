package univ_fcomte.gtasks;

import univ_fcomte.tasks.Modele;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Application qui contient les variables globales. elle est accessible dans toutes les activity par (MonApplication)getApplication().
 * Ce qui permet d'accéder dans n'importe quelle activity au modèle ((MonApplication)getApplication().getModele()) et à l'activity principale
 */
/**
 * @author guillaume
 *
 */
public class MonApplication extends Application{

	private Modele modele; //modèle
	private GestionnaireTaches gt; //activity principale
	private boolean premierLancement; //true si on vient de lancer l'application,
	//si on change l'orientation de l'application, l'activity redémarrera et elle sera à false
	
	public MonApplication() {
		modele=new Modele(this);
		premierLancement = true;
	}

	/**
	 * Permet d'obtenir le modèle
	 * @return modèle
	 */
	public Modele getModele() {
		return modele;
	}
	
	public void setModele(Modele modele) {
		this.modele = modele;
	}

	/**
	 * Permet d'obtenir l'activity principale
	 * @return activity principale
	 */
	public GestionnaireTaches getGt() {
		return gt;
	}

	public void setGt(GestionnaireTaches gt) {
		this.gt = gt;
	}
	
	/**
	 * Permet d'obtenir la version de l'application qui se trouve dans le manifest
	 * @return version de l'application qui se trouve dans le manifest
	 */
	public int getVersionAppli() {
		
		int v = 0;
		try {
			v = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionCode;
		} catch (NameNotFoundException e) {}
				
		return v;
	}
	
	/**
	 * Permet de connaître la dernière version lancée par l'utilisateur avant celle-ci
	 * @return dernière version lancée par l'utilisateur avant celle-ci
	 */
	public int getVersionPref() {

		return PreferenceManager.getDefaultSharedPreferences(gt).getInt("version_appli", 0);
		
	}
	
	/**
	 * Permet de savoir si on lance une nouvelle version de l'application
	 * @return true si on lance une nouvelle version de l'application
	 */
	public boolean testNouvelleVersion() {
		
		if(getVersionAppli() > getVersionPref())
			return true;
		else
			return false;
				
	}
	
	/**
	 * Affiche un message de bienvenu ou des modification qui ont été apporté dans la nouvelle MAJ
	 */
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
	
	/**
	 * Enregistre la version de l'application lancé comme étant la dernière lancée par l'utilisateur
	 */
	public void enregistrerVersionAppliToPref() {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(gt);
		Editor editor = preferences.edit();
		editor.putInt("version_appli", getVersionAppli());
		editor.commit();
		
	}
	
	/**
	 * Permet de savoir si on vient de lancer l'application
	 * @return true si on vient de lancer l'application
	 */
	public boolean isPremierLancement() {
		return premierLancement;
	}

	public void setPremierLancement(boolean premierLancement) {
		this.premierLancement = premierLancement;
	}
	
}

package univ_fcomte.synchronisation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.util.Log;
import univ_fcomte.gtasks.GestionnaireTaches;
import univ_fcomte.synchronisation.Synchronisation.ApiException;
import univ_fcomte.tasks.Modele;

public class ThreadSynchronisation extends Thread {
	private String serveur;
	private Synchronisation sw;
	private Modele modele;
	private GestionnaireTaches gt;
	private int modeSynchronisation;
	
	public static final int ECRASEMENT_SERVEUR = 0;
	public static final int ECRASEMENT_MOBILE = 1;
	public static final int COMBINER_SERVEUR_MOBILE = 2;
	
	public ThreadSynchronisation(Modele modele, GestionnaireTaches gt){
		this.gt = gt;
		this.modele = modele;
		this.modeSynchronisation = -1;
		
		serveur = "http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/requeteAndroid.php";
		boolean useProxy = true;
		
		sw = new Synchronisation(this.gt, useProxy);
	}
	
	public void run(){
		Log.i("Commence", "");
		switch (modeSynchronisation) {
			case ECRASEMENT_SERVEUR:
				ecraserServeur();
				break;
			case ECRASEMENT_MOBILE:
				ecraserMobile();
				break;
			case COMBINER_SERVEUR_MOBILE:
				combinerServeurMobile();
				break;
			default:
				return;	
		}
		Log.i("Termine", "");
		gt.runOnUiThread(new Runnable() {	
			@Override
			public void run() {
				gt.setProgressBarIndeterminateVisibility(false);
			}
		});
	}

	public void selectionModeSynchronisation(int mode){
		modeSynchronisation = mode;
	}
	
	public void ecraserMobile(){
		String codeJson = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("identifiant", "guillaume"));  
		nameValuePairs.add(new BasicNameValuePair("mdPasse", sw.md5("android")));
		nameValuePairs.add(new BasicNameValuePair("objet", "importer"));
		
        try {
        	codeJson = sw.GetHTML(serveur, nameValuePairs);
		} catch (ApiException e1) {
			e1.printStackTrace();
		}



		modele.reinitialiserModele();
		JsonParser json = new JsonParser(modele);
		try {
			json.parse(codeJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		modele.getBdd().reinitialiserBDD(modele.getListeTags(), modele.getListeTaches(), json.getListeAPourTag(), json.getListeAPourFils());
		gt.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				gt.updateList();
			}
		});
	}
	
	public void ecraserServeur(){
		List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);  
		nvp.add(new BasicNameValuePair("identifiant", "guillaume"));  
		nvp.add(new BasicNameValuePair("mdPasse", sw.md5("android")));
		nvp.add(new BasicNameValuePair("objet", "exporter"));
		nvp.add(new BasicNameValuePair("json", new EnvoyerJson(modele).genererJson().toString()));

		try {
			String reponse = sw.GetHTML(serveur, nvp);
			Log.i("reponse",reponse);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
	public void combinerServeurMobile(){
		
	}
	
}

package univ_fcomte.synchronisation;

import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import univ_fcomte.gtasks.*;
import univ_fcomte.synchronisation.Synchronisation.ApiException;
import univ_fcomte.tasks.Modele;

public class ThreadSynchronisation extends Thread {

	private Synchronisation sw;
	private Modele modele;
	private GestionnaireTaches gt;
	private int modeSynchronisation;
	
	public static final int ECRASEMENT_SERVEUR = 0;
	public static final int ECRASEMENT_MOBILE = 1;
	public static final int COMBINER_SERVEUR_MOBILE = 2;
	public static final int SUPPRESSION_TACHES = 3;
	public static final int SUPPRESSION_TAGS = 4;
	public static final int AJOUT_UTILISATEUR = 5;

	private String reponseServeur;
	private String identifiant;
	private String mdPasse;
	private ArrayList<Long> listeTachesSuppr;
	private ArrayList<Long> listeTagsSuppr;
	private HashMap<String,String> profilUtilisateur;
	private AjoutUtilisateur au;

	public ThreadSynchronisation(Modele modele, GestionnaireTaches gt, Synchronisation sw){
		this.gt = gt;
		this.sw = sw;
		this.modele = modele;
		this.modeSynchronisation = -1;
		this.reponseServeur = "";
		this.listeTachesSuppr = new ArrayList<Long>();
		this.listeTagsSuppr = new ArrayList<Long>();
		this.profilUtilisateur = new HashMap<String, String>();
		identifiant = PreferenceManager.getDefaultSharedPreferences(gt).getString("login", "");
		mdPasse = PreferenceManager.getDefaultSharedPreferences(gt).getString("password", "");
	}
	
	public void run(){

		gt.runOnUiThread(new Runnable() {	
			@Override
			public void run() {
				gt.setProgressBarIndeterminateVisibility(true);
				modele.setEnCoursSynchro(true);
			}
		});
		
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
			case SUPPRESSION_TACHES:
				supprimerTaches();
				break;
			case SUPPRESSION_TAGS:
				supprimerTags();
				break;
			case AJOUT_UTILISATEUR:
				ajoutUtilisateur();
				break;
			default:
				return;	
		}
		
		gt.runOnUiThread(new Runnable() {	
			@Override
			public void run() {
				if(reponseServeur.equals(""))
					Toast.makeText(gt.getApplicationContext(), gt.getResources().getString(R.string.erreur_pas_connexion), 4000).show();
				else if(reponseServeur.startsWith("Erreur de connexion"))
					Toast.makeText(gt.getApplicationContext(), gt.getResources().getString(R.string.erreur_login), 4000).show();
				gt.setProgressBarIndeterminateVisibility(false);
				modele.setEnCoursSynchro(false);
			}
		});
	}

	public void selectionModeSynchronisation(int mode){
		modeSynchronisation = mode;
	}
	
	public void ecraserMobile(){
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
		nameValuePairs.add(new BasicNameValuePair("identifiant", identifiant));  
		nameValuePairs.add(new BasicNameValuePair("mdPasse", sw.md5(mdPasse)));
		nameValuePairs.add(new BasicNameValuePair("objet", "importer"));
		
        try {
        	reponseServeur = sw.GetHTML(nameValuePairs);
		} catch (ApiException e1) {
			e1.printStackTrace();
		}
		
		Log.i("reponse", "code : " + reponseServeur);

		if(!reponseServeur.equals("") && !reponseServeur.startsWith("Erreur de connexion")) {

			modele.reinitialiserModele();
			JsonParser json = new JsonParser(modele);
			try {
				json.parse(reponseServeur);
				Log.i("pas erreur", "code");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			Log.i("pas erreur", "nb taches ds modele" + modele.getListeTaches().size());
			
			modele.getBdd().reinitialiserBDD(modele.getListeTags(), modele.getListeTaches(), json.getListeAPourTag(), json.getListeAPourFils());
			
			modele.setTachesRacines(modele.getBdd().getListeTachesRacines());
			
			gt.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.i("update", "mise a jour liste, nb tache : " + modele.getListeTaches().size());
					gt.updateList(true, -1);
				}
			});
			
		}

	}
	
	public void ecraserServeur(){
		
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();  
		nvp.add(new BasicNameValuePair("identifiant", identifiant));  
		nvp.add(new BasicNameValuePair("mdPasse", sw.md5(mdPasse)));
		nvp.add(new BasicNameValuePair("objet", "exporter"));
		nvp.add(new BasicNameValuePair("json", new EnvoyerJson(modele).genererJson().toString()));

		try {
			reponseServeur = sw.GetHTML(nvp);
			Log.i("reponse",reponseServeur);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		
	}
	
	public void combinerServeurMobile(){
		
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();  
		nvp.add(new BasicNameValuePair("identifiant", identifiant));  
		nvp.add(new BasicNameValuePair("mdPasse", sw.md5(mdPasse)));
		nvp.add(new BasicNameValuePair("objet", "exporter_puis_importer"));
		nvp.add(new BasicNameValuePair("json", new EnvoyerJson(modele).genererJson().toString()));
		Log.i("json envoye",new EnvoyerJson(modele).genererJson().toString());
		try {
			reponseServeur = sw.GetHTML(nvp);
			Log.i("reponse",reponseServeur);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		
		if(!reponseServeur.equals("") && !reponseServeur.startsWith("Erreur de connexion")) {
			
			JsonParser json = new JsonParser(modele);
			try {
				json.parseAvecVersion(reponseServeur);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			modele.getBdd().reinitialiserBDD(modele.getListeTags(), modele.getListeTaches(), json.getListeAPourTag(), json.getListeAPourFils());
			
			modele.setTachesRacines(modele.getBdd().getListeTachesRacines());
			
			gt.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.i("update", "mise a jour liste, nb tache : " + modele.getListeTaches().size());
					gt.updateList(false, -1);
				}
			});
			
		}
		
		
	}
	
	public void supprimerTags() {
		
		for(long l : listeTagsSuppr) {
			
			List<NameValuePair> nvp = new ArrayList<NameValuePair>();  
			nvp.add(new BasicNameValuePair("identifiant", identifiant));  
			nvp.add(new BasicNameValuePair("mdPasse", sw.md5(mdPasse)));
			nvp.add(new BasicNameValuePair("objet", "supprimer_tag"));
			nvp.add(new BasicNameValuePair("idTag", String.valueOf(l)));

			try {
				reponseServeur = sw.GetHTML(nvp);
				Log.i("reponse",reponseServeur);
			} catch (ApiException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	public void supprimerTaches() {
		
		for(long l : listeTachesSuppr) {
			
			List<NameValuePair> nvp = new ArrayList<NameValuePair>();  
			nvp.add(new BasicNameValuePair("identifiant", identifiant));  
			nvp.add(new BasicNameValuePair("mdPasse", sw.md5(mdPasse)));
			nvp.add(new BasicNameValuePair("objet", "supprimer_tache"));
			nvp.add(new BasicNameValuePair("idTache", String.valueOf(l)));

			try {
				reponseServeur = sw.GetHTML(nvp);
				Log.i("reponse",reponseServeur);
			} catch (ApiException e) {
				e.printStackTrace();
			}
			
		}
		
		gt.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gt.updateList(false, -1);
			}
		});
		
	}
	
	public void setListeTachesSuppr(ArrayList<Long> listeTachesSuppr) {
		
		this.listeTachesSuppr = listeTachesSuppr;
		
	}
	
	public void setListeTagsSuppr(ArrayList<Long> listeTagsSuppr) {
		
		this.listeTagsSuppr = listeTagsSuppr;
		
	}
	
	public void setProfilUtilisateur(AjoutUtilisateur au, HashMap<String,String> profilUtilisateur) {
		
		this.au = au;
		this.profilUtilisateur = profilUtilisateur;
		
	}
	
	private void ajoutUtilisateur() {
		
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();  
		nvp.add(new BasicNameValuePair("identifiant", identifiant));  
		nvp.add(new BasicNameValuePair("mdPasse", sw.md5(mdPasse)));
		nvp.add(new BasicNameValuePair("objet", "ajouter_user"));
		
		Set<String> cles = profilUtilisateur.keySet();
		Iterator<String> it = cles.iterator();
		while (it.hasNext()){
			String cle = (String) it.next();
			nvp.add(new BasicNameValuePair(cle, profilUtilisateur.get(cle)));
		}
		
		try {
			reponseServeur = sw.GetHTML(nvp);
			Log.i("reponse",reponseServeur);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		
		if(reponseServeur.startsWith("reussi"))
			au.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					au.ajoutDansPrefNouveauUser();
				}
			});
		
	}
	
}

package univ_fcomte.synchronisation;

import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.preference.PreferenceManager;
import android.util.Log;
import univ_fcomte.gtasks.*;
import univ_fcomte.synchronisation.Synchronisation.ApiException;
import univ_fcomte.tasks.Modele;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Permet de synchroniser les données du téléphones avec les données du serveur distant (dans un nouveau thread)
 */
public class ThreadSynchronisation extends Thread {

	private Synchronisation sw;
	private Modele modele; //modèle de l'application
	private GestionnaireTaches gt; //activity principale
	private int modeSynchronisation; //mode de synchronisation choisit (voir ci-dessous)
	
	//modes de synchronisation
	public static final int ECRASEMENT_SERVEUR = 0;
	public static final int ECRASEMENT_MOBILE = 1;
	public static final int COMBINER_SERVEUR_MOBILE = 2;
	public static final int SUPPRESSION_TACHES = 3;
	public static final int SUPPRESSION_TAGS = 4;
	public static final int AJOUT_UTILISATEUR = 5;

	private String reponseServeur; //réponse que le serveur a renvoyé
	private String identifiant;
	private String mdPasse;
	private ArrayList<Long> listeTachesSuppr;
	private ArrayList<Long> listeTagsSuppr;
	private HashMap<String,String> profilUtilisateur;
	private AjoutUtilisateur au; //activity d'ajout de compte utilisateur

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
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
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
					new ErreurDialog(R.string.erreur, R.string.erreur_pas_connexion, gt);
				else if(reponseServeur.startsWith("Erreur de connexion"))
					new ErreurDialog(R.string.erreur, R.string.erreur_login, gt);
				gt.setProgressBarIndeterminateVisibility(false);
				modele.setEnCoursSynchro(false);
			}
		});
	}

	/**
	 * Choisir le mode de synchronisation
	 * @param mode mode de synchronisation choisi
	 */
	public void selectionModeSynchronisation(int mode){
		modeSynchronisation = mode;
	}
	
	/**
	 * Télécharge les données du seveur distant, vide le téléphone et importe les données dans le téléphone
	 */
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
						
			//modele.setTachesRacines(modele.getBdd().getListeTachesRacines());
			modele.refreshTachesRacines();
			
			gt.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.i("update", "mise a jour liste, nb tache : " + modele.getListeTaches().size());
					gt.updateList(true, -1);
				}
			});
			
		}

	}
	
	/**
	 * Envoie les données du téléphone au serveur distant, le vide puis importe les données reçues
	 */
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
	
	/**
	 * Synchronise les données en prenant en compte la version de chaque tâche
	 * Le téléphone envoie ses données, le serveur les analyses et importe les tâches modifiées
	 * Puis il renvoie les tâches que le téléphone ne possède pas ou qui sont trop anciennes
	 * Le téléphone reçoi ensuite ces tâches puis les importe
	 */
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
			
			boolean vide = true;
			JsonParser json = new JsonParser(modele);
			try {
				vide = json.parseAvecVersion(reponseServeur);
			} catch (JSONException e) {
				e.printStackTrace();
			}
						
			if(!vide) {
			
				modele.getBdd().reinitialiserBDD(modele.getListeTags(), modele.getListeTaches(), json.getListeAPourTag(), json.getListeAPourFils());
							
				//modele.setTachesRacines(modele.getBdd().getListeTachesRacines());
				modele.refreshTachesRacines();
				
				gt.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gt.updateList(false, -1);
					}
				});
			
			}
			
		}
		
	}
	
	/**
	 * Supprime des tags dans le serveur distant
	 */
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

	/**
	 * Supprime des taches dans le serveur distant
	 */
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
	
	/**
	 * Ajoute un compte utilisateur dans le serveur distant
	 */
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
		else if(reponseServeur.startsWith("echec"))
			au.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new ErreurDialog(R.string.erreur, R.string.erreur_user_existe, au);
				}
			});			
		
	}
	
}

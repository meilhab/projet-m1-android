package univ_fcomte.gtasks;

import java.util.*;

import univ_fcomte.synchronisation.ThreadSynchronisation;
import univ_fcomte.tasks.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Activity permettant d'ajouter un compte utilisateur
 */
public class AjoutUtilisateur extends Activity {

	private EditText identifiant;
	private EditText nom;
	private EditText prenom;
	private EditText mail;
	private EditText mdPasse;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ajout_utilisateur);
		
		identifiant = (EditText)findViewById(R.id.edit_user_identifiant);
		nom = (EditText)findViewById(R.id.edit_user_nom);
		prenom = (EditText)findViewById(R.id.edit_user_prenom);
		mail = (EditText)findViewById(R.id.edit_user_mail);
		mdPasse = (EditText)findViewById(R.id.edit_user_md_passe);
		
		Button enregistrer = (Button)findViewById(R.id.bouton_valider_ajout_user);
		Button annuler = (Button)findViewById(R.id.bouton_annuler_ajout_user);
		
		annuler.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		enregistrer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				enregistrerUtilisateur();
			}
		});
		
	}
	
	/**
	 * Permet d'envoyer une requete afin d'ajouter un compte utilisateur sur le serveur
	 */
	public void enregistrerUtilisateur() {
		
		if(nom.getText().toString().length() > 0 && prenom.getText().toString().length() > 0 && identifiant.getText().toString().length() > 0 && mail.getText().toString().length() > 0 && mdPasse.getText().toString().length() >= 6) {
		
			HashMap<String, String> profilUtilisateur = new HashMap<String, String>();
			profilUtilisateur.put("nom", nom.getText().toString());
			profilUtilisateur.put("prenom", prenom.getText().toString());
			profilUtilisateur.put("mail", mail.getText().toString());
			profilUtilisateur.put("mdPasse", mdPasse.getText().toString());
			profilUtilisateur.put("identifiant", identifiant.getText().toString());
			
			ThreadSynchronisation tsAjoutUser = new ThreadSynchronisation(((MonApplication)getApplication()).getModele(), ((MonApplication)getApplication()).getGt(), ((MonApplication)getApplication()).getGt().getSw());
			tsAjoutUser.selectionModeSynchronisation(ThreadSynchronisation.AJOUT_UTILISATEUR);
			tsAjoutUser.setProfilUtilisateur(this, profilUtilisateur);
			tsAjoutUser.start();
			
		}
		else
			afficherMessageErreur();
		
	}
	
	public void afficherMessageErreur() {
		new ErreurDialog(R.string.erreur, R.string.erreur_ajout_user, this);
	}
	
	/**
	 * Ajoute l'identifiant et le mot de passe dans les règlages de l'application
	 * pour pouvoir synchroniser directement ses tâches sans être obligé d'ajouter son identifiant dans les préférences
	 */
	public void ajoutDansPrefNouveauUser() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.nouveau_user_cree);
		builder.setMessage(R.string.connecter_avec_nouveau_user);
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(((MonApplication)getApplication()).getGt());
				Editor editor = preferences.edit();
				editor.putString("login", identifiant.getText().toString());
				editor.putString("password", mdPasse.getText().toString());
				editor.putBoolean("utilise_compte", true);
				editor.commit();
		        
				((MonApplication)getApplication()).getModele().reinitialiserModele();
				((MonApplication)getApplication()).getModele().getBdd().reinitialiserBDD(new ArrayList<Tag>(), new ArrayList<Tache>(), new HashMap<Long, Long>(), new HashMap<Long, Long>());
				
				((MonApplication)getApplication()).getGt().updateList(true, -1);
				
				finish();
			}
		});
		builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
	    		finish();
	    	}
	    });
		builder.setCancelable(false);
		AlertDialog alert = builder.create();
		alert.show();
		
	}

}

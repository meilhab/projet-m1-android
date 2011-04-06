package univ_fcomte.gtasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import univ_fcomte.synchronisation.EnvoyerJson;
import univ_fcomte.synchronisation.JsonParser;
import univ_fcomte.synchronisation.Synchronisation;
import univ_fcomte.synchronisation.ThreadSynchronisation;
import univ_fcomte.synchronisation.Synchronisation.ApiException;
import univ_fcomte.tasks.Modele;
import univ_fcomte.tasks.Tache;
import univ_fcomte.tasks.Tag;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;

public class GestionnaireTaches extends Activity {
	/** Called when the activity is first created. */

	private int positionX;
	private Modele modele;
	private ListView maListViewPerso;

	private String serveur;
	private Synchronisation sw;
	private final int CODE_DE_MON_ACTIVITE = 1;
	private final int CODE_ACTIVITE_PREFERENCES = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.main);

		modele = ((MonApplication)getApplication()).getModele();
		modele.initialiserModele();
		positionX=0;
		//serveur = "http://10.0.2.2/gestionnaire_taches/requeteAndroid.php";
		serveur = "http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/requeteAndroid.php";
		boolean useProxy = false;

		sw=new Synchronisation(this, serveur, useProxy);

		

		//Récupération de la listview crée dans le fichier main.xml
		maListViewPerso = (ListView) findViewById(R.id.listviewperso);

		updateList();

		//Enfin on met un écouteur d'événement sur notre listView
		//On met un écouteur d'événement sur notre listView
		maListViewPerso.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.i("","position : "+event.getX());
				positionX=(int) event.getX();
				return false;
			}
		});

		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
				Log.i("","item");

				//if(positionX<=getResources().getDrawable(R.drawable.icon).getMinimumWidth())
				Log.i("","on clic sur l'image");

				Toast.makeText(maListViewPerso.getContext(), "Nouvelle tache", Toast.LENGTH_SHORT).show();
				Bundle objetbunble = new Bundle();
				Intent intent = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
				//objetbunble.putAll(new Bundle(b))
				//objetbunble.putString("titre", "Nouvelle tache");

				HashMap map = (HashMap) maListViewPerso.getItemAtPosition(position);

				objetbunble.putInt("id", Integer.valueOf((String)map.get("id")));
				intent.putExtras(objetbunble);

				startActivityForResult(intent, CODE_DE_MON_ACTIVITE);

			}
		});

		maListViewPerso.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView a, View v, int position, long id) {
				Log.i("","appui long sur "+ position);

				/*Builder builder = new Builder(v.getContext());
				builder.setTitle("Options");
			    String[] myItemClickDialog = new String[4];
				myItemClickDialog[0] = "Add";
				myItemClickDialog[1] = "Edit";
				myItemClickDialog[2] = "Open";
				myItemClickDialog[3] = "Delete";
				String[] items = myItemClickDialog;
				builder.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(final DialogInterface dialog, final int which) {
						switch (which) {
							case 0:
							//Do stuff
							break;
							case 1:
							//Do stuff
							break;
							case 2:
							//Do stuff
							break;
							case 3:
							//Do stuff
							break;
						}
					}
				});*/

				return true;
			}
		}); 
		//((MonApplication)getApplication()).test=false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){


		//on regarde quelle Activity a répondu
		switch(requestCode){
		case CODE_DE_MON_ACTIVITE:
			Log.i("update", "test mise a jour");
			updateList();
			/*
		   		//On regarde qu'elle est la réponse envoyée et en fonction de la réponse on affiche un message différent.
	    		switch(resultCode){
			    	case 1:
			    		adb.setMessage("Vous utilisez Word.");
			    		adb.show();
			    		return;
	    		}*/
			break;
		default : break;
		}

	}


	public void updateList() {
		//Création de la ArrayList qui nous permettra de remplire la listView
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		//On déclare la HashMap qui contiendra les informations pour un item
		HashMap<String, String> map;
		for(Tache t:modele.getListeTaches()) {
			map = new HashMap<String, String>();
			map.put("titre", t.getNom());
			map.put("description", t.getDescription());
			map.put("img", String.valueOf(R.drawable.icon));
			map.put("id", String.valueOf(t.getIdentifiant()));
			listItem.add(map);
		}

		//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
		SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.affichageitem,
				new String[] {"img", "titre", "description", "id"}, new int[] {R.id.img, R.id.titre, R.id.description, R.id.id});

		//On attribut à notre listView l'adapter que l'on vient de créer
		maListViewPerso.setAdapter(mSchedule);
	}

	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		//menu.getItem(3).getSubMenu().setHeaderIcon(R.drawable.icon);
		//menu.getItem(0).getSubMenu().setHeaderIcon(R.drawable.);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_ajout_tache:
			Toast.makeText(this.getApplicationContext(), "Nouvelle tache", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this.getApplicationContext(), DetailsTaches.class);
			startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
			return true;
		case R.id.menu_ajout_tag:
			AlertDialog.Builder builderAjoutTag = new AlertDialog.Builder(this);
			builderAjoutTag.setTitle(getResources().getText(R.string.label_ajout_tag));
			builderAjoutTag.setMessage(getResources().getText(R.string.label_nom_tag));
			final EditText etNomTag = new EditText(this);
			builderAjoutTag.setView(etNomTag);
			builderAjoutTag.setNegativeButton("Annuler", new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {

				}

			});
			builderAjoutTag.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!etNomTag.getText().toString().equals("")){
						Tag t = new Tag(modele.getIdMaxTag()+1, etNomTag.getText().toString());
						modele.ajoutTag(t);
						Toast.makeText(GestionnaireTaches.this, "Nouveau tag ajouté", Toast.LENGTH_SHORT).show();
					}
					else{
						Toast.makeText(GestionnaireTaches.this, "Champ vide", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builderAjoutTag.show();
			return true;
		case R.id.menu_supprimer_tag:
			CharSequence[] itemsTags = new CharSequence[modele.getListeTags().size()];
			final boolean[] tagsAffiches = new boolean[modele.getListeTags().size()];
			final boolean[] tagsChoisis = new boolean[tagsAffiches.length];
			for(int i=0;i<modele.getListeTags().size();i++) {
				itemsTags[i]=modele.getListeTags().get(i).getNom();
				tagsAffiches[i]=false;
				tagsChoisis[i]=false;
			}

			AlertDialog.Builder builderSuppressionTag = new AlertDialog.Builder(this);
			builderSuppressionTag.setTitle(getResources().getText(R.string.label_menu_supprimer_tag));
			builderSuppressionTag.setNegativeButton("Annuler", new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					for(int i=0; i<tagsAffiches.length; i++)
						tagsAffiches[i] = tagsChoisis[i];
				}

			});
			builderSuppressionTag.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for(int i=0; i<tagsChoisis.length; i++){
						tagsChoisis[i] = tagsAffiches[i];
			    		if(tagsChoisis[i]){
			    			Long indice = modele.getListeTags().get(i).getIdentifiant();
			    			modele.getListeTags().remove(i);
			    			for(int j=0; j<modele.getListeTaches().size(); j++){
			    				if(modele.getListeTaches().get(j).getListeTags().contains(indice)){
			    					modele.getListeTaches().get(j).getListeTags().remove(indice);
			    				}
			    					
			    			}
			    		}
					}
					
				}
			});
			builderSuppressionTag.setMultiChoiceItems(itemsTags, tagsAffiches, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					
				}
			}); 
			AlertDialog alertTags;
			alertTags = builderSuppressionTag.create();
			alertTags.show();
			return true;
		case R.id.menu_reglage:
			Intent intentPrefs = new Intent(this.getApplicationContext(), Preferences.class);
			startActivityForResult(intentPrefs, CODE_ACTIVITE_PREFERENCES);
			return true;
		case R.id.menu_plus:
			return true;
		case R.id.menu_synchronisation:
			return true;
		case R.id.sous_menu_synchronisation_ecrasement_serveur:
	    	ThreadSynchronisation ts_ecrasement_serveur = new ThreadSynchronisation(modele, GestionnaireTaches.this, sw);
	    	ts_ecrasement_serveur.selectionModeSynchronisation(ThreadSynchronisation.ECRASEMENT_SERVEUR);
	    	ts_ecrasement_serveur.start();	    	
			return true;
		case R.id.sous_menu_synchronisation_ecrasement_mobile:
	    	ThreadSynchronisation ts_ecrasement_mobile = new ThreadSynchronisation(modele, GestionnaireTaches.this, sw);
	    	ts_ecrasement_mobile.selectionModeSynchronisation(ThreadSynchronisation.ECRASEMENT_MOBILE);
	    	ts_ecrasement_mobile.start();	 
			return true;
		case R.id.sous_menu_synchronisation_combiner_serveur_mobile:
	    	ThreadSynchronisation ts_combiner_serveur_mobile = new ThreadSynchronisation(modele, GestionnaireTaches.this, sw);
	    	ts_combiner_serveur_mobile.selectionModeSynchronisation(ThreadSynchronisation.COMBINER_SERVEUR_MOBILE);
	    	ts_combiner_serveur_mobile.start();	    	
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
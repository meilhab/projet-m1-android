package univ_fcomte.gtasks;

import java.util.ArrayList;
import java.util.HashMap;
import univ_fcomte.synchronisation.Synchronisation;
import univ_fcomte.synchronisation.ThreadSynchronisation;
import univ_fcomte.tasks.Modele;
import univ_fcomte.tasks.Modele.Tri;
import univ_fcomte.tasks.Tache;
import univ_fcomte.tasks.Tag;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class GestionnaireTaches extends Activity {
	/** Called when the activity is first created. */

	private int positionX;
	private Modele modele;
	private ListView maListViewPerso;

	private String serveur;
	private Synchronisation sw;
	private String recherche;
	private Tri tri;
	private boolean enCoursSynchro;

	private final int CODE_DE_MON_ACTIVITE = 1;
	private final int CODE_ACTIVITE_PREFERENCES = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.main);

		modele = ((MonApplication)getApplication()).getModele();
		((MonApplication)getApplication()).setGt(this);
		
		modele.initialiserModele();
		positionX=0;
		//serveur = "http://10.0.2.2/gestionnaire_taches/requeteAndroid.php";
		serveur = "http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/requeteAndroid.php";

		sw=new Synchronisation(this, serveur);
		
		recherche = "";
		tri = Tri.DATE;
		enCoursSynchro = false;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		//Récupération de la listview crée dans le fichier main.xml
		maListViewPerso = (ListView) findViewById(R.id.listviewperso);
		//Log.i("test", maListViewPerso.toString());
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

				if(!enCoursSynchro) {
					if(positionX<=getResources().getDrawable(R.drawable.btn_check_buttonless_on).getMinimumWidth()+20) {
						Log.i("","on clic sur l'image");
						Tache t = modele.getTacheById(Integer.valueOf((String)((HashMap)maListViewPerso.getItemAtPosition(position)).get("id")));
						if(t.getEtat() != 4)
							t.setEtat(4);
						else
							t.setEtat(3);
						t.setVersion(t.getVersion() + 1);
						modele.getBdd().modifTache(t);
						updateList();
							
						//((ImageView)v.findViewById(R.id.img).);
						//maListViewPerso.getItemAtPosition(position).;
						//((HashMap) maListViewPerso.getItemAtPosition(position)). put("img", String.valueOf(R.drawable.btn_check_buttonless_off));
					}
					else {
						//Toast.makeText(maListViewPerso.getContext(), "Nouvelle tache", Toast.LENGTH_SHORT).show();
						Bundle objetbunble = new Bundle();
						Intent intent = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
						//objetbunble.putAll(new Bundle(b))
						//objetbunble.putString("titre", "Nouvelle tache");
						
						HashMap map = (HashMap) maListViewPerso.getItemAtPosition(position);
						
						objetbunble.putInt("id", Integer.valueOf((String)map.get("id")));
						intent.putExtras(objetbunble);
						startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
						transitionActivity();
					}
				}
				else
					attenteSynchronisation();
			}
				
		});

		maListViewPerso.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView a, View v, int position, long id) {
				//registerForContextMenu(v);
				
				if(!enCoursSynchro) {
				
					Builder builder = new Builder(v.getContext());
					final HashMap map = (HashMap) maListViewPerso.getItemAtPosition(position);
					//map.get("id");
					builder.setTitle(map.get("titre")+"");
				    String[] myItemClickDialog = new String[3];
					myItemClickDialog[0] = "Ajouter";
					myItemClickDialog[1] = "Modifier";
					myItemClickDialog[2] = "Supprimer";
					String[] items = myItemClickDialog;
					builder.setItems(items, new DialogInterface.OnClickListener() {
	
						public void onClick(final DialogInterface dialog, final int which) {
							switch (which) {
								case 0:
									//Do stuff
									break;
								case 1:
									Bundle objetbunble = new Bundle();
									objetbunble.putInt("id", Integer.valueOf((String)map.get("id")));
									Intent intent = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
									intent.putExtras(objetbunble);
									startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
									transitionActivity();
									break;
								case 2:
									long identifiant = Long.valueOf((String)map.get("id"));
									modele.supprimerTache(modele.getTacheById(identifiant));
									modele.getBdd().supprimerTache(identifiant, true);
									ArrayList<Long> listeTachesSuppr = new ArrayList<Long>();
									listeTachesSuppr.add(/*new Long(*/identifiant/*)*/);
									ThreadSynchronisation tsSupprTache = new ThreadSynchronisation(modele, GestionnaireTaches.this, sw);
									tsSupprTache.selectionModeSynchronisation(ThreadSynchronisation.SUPPRESSION_TACHES);
									tsSupprTache.setListeTachesSuppr(listeTachesSuppr);
									tsSupprTache.start();
									break;
								default:
									break;
							}
						}
					});
	
					builder.show();
			}
			else
				attenteSynchronisation();
			return true;
			}
		}); 
		//modele.getBdd().supprimerTache(4, true);
	}

	@Override
	public void onBackPressed() {
		if(!recherche.equals("")) {
			recherche = "";
			updateList();
		}
		else
			super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

		Log.i("request","" + requestCode);
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
			case CODE_ACTIVITE_PREFERENCES:
				updateList();
				break;
			default :
				break;
		}

	}


	public void updateList() {
		//Création de la ArrayList qui nous permettra de remplire la listView
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		//On déclare la HashMap qui contiendra les informations pour un item
		HashMap<String, String> map;
		
		modele.trierTaches(true, tri);
		
		for(Tache t:modele.getListeTaches()) {
			if(t.getEtat() != 1 || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("afficher_taches_annulees", false)) {
				if(t.getNom().indexOf(recherche) != -1 || t.getDescription().indexOf(recherche) != -1) {
					map = new HashMap<String, String>();
					map.put("titre", t.getNom());
					map.put("description", t.getDescription());
					if(t.getEtat() == 4)
						map.put("img", String.valueOf(R.drawable.btn_check_buttonless_on));
					else
						map.put("img", String.valueOf(R.drawable.btn_check_buttonless_off));
					map.put("id", String.valueOf(t.getIdentifiant()));
					listItem.add(map);
				}
			}
		}

		//Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
		AdapterListView mSchedule = new AdapterListView (this.getBaseContext(), listItem, R.layout.affichageitem,
				new String[] {"img", "titre", "description", "id"}, new int[] {R.id.img, R.id.titre, R.id.description, R.id.id});

		//On attribut à notre listView l'adapter que l'on vient de créer
		maListViewPerso.setAdapter(mSchedule);
		//convertView.setBackgroundColor((position & 1) == 1 ? Color.WHITE : Color.LTGRAY);
		//((View)maListViewPerso.getSelectedItem()).setBackgroundColor(Color.BLUE);
	}

	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		//menu.getItem(3).getSubMenu().setHeaderIcon(R.drawable.icon);
		//menu.getItem(0).getSubMenu().setHeaderIcon(R.drawable.);
		
		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("utilise_compte", false))
			menu.findItem(R.id.menu_synchronisation).setVisible(true);
		else
			menu.findItem(R.id.menu_synchronisation).setVisible(false);
		return super.onMenuOpened(featureId, menu);
		
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_ajout_tache:
			if(!enCoursSynchro) {
				Toast.makeText(this.getApplicationContext(), "Nouvelle tache", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this.getApplicationContext(), DetailsTaches.class);
				startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
				transitionActivity();
			}
			else
				attenteSynchronisation();
			return true;
		case R.id.menu_ajout_tag:
			if(!enCoursSynchro) {
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
							Tag t = new Tag(modele.getIdMaxTag()+1, etNomTag.getText().toString(), 1);
							modele.ajoutTag(t);
							modele.getBdd().ajouterTag(t, false);
							Toast.makeText(GestionnaireTaches.this, "Nouveau tag ajouté", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(GestionnaireTaches.this, "Champ vide", Toast.LENGTH_SHORT).show();
						}
					}
				});
				builderAjoutTag.show();
			}
			else
				attenteSynchronisation();
			return true;
		case R.id.menu_supprimer_tag:
			
			if(!enCoursSynchro) {
			
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
						
						ArrayList<Long> listeTagsSuppr = new ArrayList<Long>();
						
						for(int i=0; i<tagsChoisis.length; i++){
							tagsChoisis[i] = tagsAffiches[i];
				    		if(tagsChoisis[i]){
				    			Long indice = modele.getListeTags().get(i).getIdentifiant();
				    			listeTagsSuppr.add(indice);
				    			modele.getListeTags().remove(i);
				    			for(int j=0; j<modele.getListeTaches().size(); j++){
				    				if(modele.getListeTaches().get(j).getListeTags().contains(indice))
				    					modele.getListeTaches().get(j).getListeTags().remove(indice);
				    				
				    			}
				    			
				    			modele.getBdd().supprimerTag(indice);
				    		}
				    		
						}
						
						ThreadSynchronisation tsSupprTag = new ThreadSynchronisation(modele, GestionnaireTaches.this, sw);
						tsSupprTag.selectionModeSynchronisation(ThreadSynchronisation.SUPPRESSION_TAGS);
						tsSupprTag.setListeTagsSuppr(listeTagsSuppr);
						tsSupprTag.start();
						
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
			
			}
			else
				attenteSynchronisation();
			return true;
		case R.id.menu_reglage:
			Intent intentPrefs = new Intent(this.getApplicationContext(), Preferences.class);
			startActivityForResult(intentPrefs, CODE_ACTIVITE_PREFERENCES);
			transitionActivity();
			return true;
		case R.id.menu_plus:
			return true;
		case R.id.menu_synchronisation:
			if(!enCoursSynchro)
				return true;
			else
				return false;
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
		case R.id.sous_menu_tri_date:
			item.setChecked(true);
			tri = Tri.DATE;
			updateList();
			return true;
		case R.id.sous_menu_tri_nom:
			item.setChecked(true);
			tri = Tri.NOM;
			updateList();
			return true;
		case R.id.sous_menu_tri_etat:
			item.setChecked(true);
			tri = Tri.ETAT;
			updateList();
			return true;
		case R.id.sous_menu_tri_priorite:
			item.setChecked(true);
			tri = Tri.PRIORITE;
			updateList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public Synchronisation getSw() {
		return sw;
	}

	public void setSw(Synchronisation sw) {
		this.sw = sw;
	}
	
	@Override
	public boolean onSearchRequested() {

        //final String queryAppDataString = mQueryAppData.getText().toString();
        //if (queryAppDataString != null) {
        //    appDataBundle = new Bundle();
            //appDataBundle.putString("demo_key", queryAppDataString);
       // }
        
        // Now call the Activity member function that invokes the Search Manager UI.
		//Bundle appDataBundle = new Bundle();
		//startSearch("", false, null, false); 
		
		return super.onSearchRequested();
	}
	
	@Override
	public void onNewIntent(final Intent newIntent) {
		super.onNewIntent(newIntent);
		// get and process search query here
		if (Intent.ACTION_SEARCH.equals(newIntent.getAction())) {
			recherche = newIntent.getStringExtra(SearchManager.QUERY);
			updateList();
		}
	}
	
	public void transitionActivity() {
		overridePendingTransition(R.anim.push_left_in,R.anim.push_up_out);  
	}
	
	public void attenteSynchronisation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.attente_synchronisation).setCancelable(false)
		       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public boolean isEnCoursSynchro() {
		return enCoursSynchro;
	}

	public void setEnCoursSynchro(boolean enCoursSynchro) {
		this.enCoursSynchro = enCoursSynchro;
	}

}
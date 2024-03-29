package univ_fcomte.gtasks;

import java.util.*;
import univ_fcomte.synchronisation.*;
import univ_fcomte.tasks.*;
import univ_fcomte.tasks.Modele.Tri;
import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.*;
import android.widget.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Activity principale qui se lance au démarrage de l'application, elle contient la liste de tâches
 */
public class GestionnaireTaches extends Activity implements View.OnClickListener {

	private Modele modele; //modèle de l'application
	private ListView maListViewPerso; //liste qui contient les tâches
	private TextView arborescence; //arborescence des tâches pères
	private HorizontalScrollView arborecence_scrollbar;
	private Button back;

	private Synchronisation sw;
	private MonApplication application; //application

	private final int CODE_DE_MON_ACTIVITE = 1;
	private final int CODE_ACTIVITE_PREFERENCES = 2;
	private final int CODE_ACTIVITE_AJOUT_UTILISATEUR = 3;
	
	private View ancienItemSelectionne;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		application = (MonApplication)getApplication();
		modele = application.getModele();
		application.setGt(this);
		modele.initialiserModele();
		sw=new Synchronisation(this, modele.getServeur());
        
		application.afficherMessageBienvenu();
		
		maListViewPerso = (ListView) findViewById(R.id.listviewperso);
		arborescence = (TextView) findViewById(R.id.arborecence);
		arborecence_scrollbar = (HorizontalScrollView) findViewById(R.id.arborecence_scrollbar);
		
		//si la liste a le focus on sélectionne le premier item
		maListViewPerso.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					if(maListViewPerso.getSelectedView() != null) {
						maListViewPerso.getSelectedView().setBackgroundResource(R.drawable.background_item_in);
						ancienItemSelectionne = maListViewPerso.getSelectedView();
					}
				}
				else if(ancienItemSelectionne != null) {
					ancienItemSelectionne.setBackgroundResource(R.drawable.background_item_out);
				}
			}
		});
		
		//on modifie le fond de l'item sélectionné
		maListViewPerso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int arg2, long arg3) {
				if(ancienItemSelectionne != null)
					ancienItemSelectionne.setBackgroundResource(R.drawable.background_item_out);
				v.setBackgroundResource(R.drawable.background_item_in);
				ancienItemSelectionne = v;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		
		back = (Button) findViewById(R.id.bouton_precedent);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		Button accueil = (Button) findViewById(R.id.bouton_accueil);
		accueil.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				modele.getArborescenceCourante().clear();
				updateList(true, -1);
			}
		});
		
		Button nouvelleTache = (Button) findViewById(R.id.bouton_ajouter);
		nouvelleTache.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!modele.isEnCoursSynchro()) {
					Intent intent = new Intent(application.getGt(), DetailsTaches.class);
					startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
					transitionActivity();
				}
				else
					attenteSynchronisation();
			}
		});		
		
		//lorsqu'on clique sur un item on le modifie
		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
				
				if(ancienItemSelectionne != null && ancienItemSelectionne != v)
					ancienItemSelectionne.setBackgroundResource(R.drawable.background_item_out);
				
				long identifiantTache = Long.valueOf((String)((HashMap)maListViewPerso.getItemAtPosition(position)).get("id"));
				
				if(!modele.isEnCoursSynchro()) {
						Bundle objetbunble = new Bundle();
						Intent intent = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
						
						objetbunble.putLong("id", identifiantTache);
						intent.putExtras(objetbunble);
						startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
						transitionActivity();
				}
				else
					attenteSynchronisation();

			}
				
		});

		//lorsqu'on appui longtemps sur un item on peut choisir une action a effectuer
		maListViewPerso.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView a, View v, int position, long id) {
					
				if(ancienItemSelectionne != null && ancienItemSelectionne != v)
					ancienItemSelectionne.setBackgroundResource(R.drawable.background_item_out);
				
				if(!modele.isEnCoursSynchro()) {
				
					Builder builder = new Builder(v.getContext());
					final HashMap map = (HashMap) maListViewPerso.getItemAtPosition(position);
					final long identifiant = Long.valueOf((String)map.get("id"));
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
									modele.getArborescenceCourante().add(modele.getTacheById(identifiant));
									Intent intentNewTache = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
									startActivityForResult(intentNewTache, CODE_DE_MON_ACTIVITE);
									transitionActivity();
									break;
								case 1:
									Bundle objetbunble = new Bundle();
									objetbunble.putLong("id", identifiant);
									Intent intent = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
									intent.putExtras(objetbunble);
									startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
									transitionActivity();
									break;
								case 2:
									modele.supprimerTache(modele.getTacheById(identifiant));
									modele.getBdd().supprimerTache(identifiant, true);
									if(PreferenceManager.getDefaultSharedPreferences(application.getGt()).getBoolean("utilise_compte", false) && PreferenceManager.getDefaultSharedPreferences(application.getGt()).getBoolean("synchro_auto", false)) {
										ArrayList<Long> listeTachesSuppr = new ArrayList<Long>();
										listeTachesSuppr.add(identifiant);
										ThreadSynchronisation tsSupprTache = new ThreadSynchronisation(modele, GestionnaireTaches.this, sw);
										tsSupprTache.selectionModeSynchronisation(ThreadSynchronisation.SUPPRESSION_TACHES);
										tsSupprTache.setListeTachesSuppr(listeTachesSuppr);
										tsSupprTache.start();
									}
									else
										updateList(false, -1);
									
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
		
		//on affiche la liste
		if(application.isPremierLancement()) {
			updateList(true, -1);
			application.setPremierLancement(false);
		}
		else
			updateList(false, -1);
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		
		if(!modele.getRechercheCourante().equals("")) {
			modele.setRechercheCourante("");
			updateList(false, -1);
		}
		else if(modele.getTachePereCourant() != null) {
			//on retourne à la tâche père
			long id = modele.getArborescenceCourante().get(modele.getArborescenceCourante().size() - 1).getIdentifiant();
			modele.getArborescenceCourante().remove(modele.getArborescenceCourante().size() - 1);
			updateList(false, id);
		}
		else {
			super.onBackPressed();
			application.setPremierLancement(true);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

		switch(requestCode){
			case CODE_DE_MON_ACTIVITE:
				updateList(false, resultCode);
				break;
			case CODE_ACTIVITE_PREFERENCES:
				updateList(false, -1);
				break;
			case CODE_ACTIVITE_AJOUT_UTILISATEUR:
				//TODO
				break;
			default :
				break;
		}

	}

	
	/**
	 * Mise à jour de la liste de tâche
	 * @param animation true si on veut une animation
	 * @param idTacheSelectionnee identifiant de la tâche qui sera automatiquement sélectionnée, -1 si aucune tâche sélectionnée
	 */
	public void updateList(boolean animation, long idTacheSelectionnee) {

		//on modifie le titre de l'activity avec le nombre de tâches
		String title = getResources().getString(R.string.app_name) + " ( "+ modele.getListeTaches().size() + " " + getResources().getString(R.string.tache);
		if(modele.getListeTaches().size()>1)
			title += "s";
		title +=  ")";
		this.setTitle(title);
		
		//on met à jour l'arborescence
		updateArborecence();
		
		//Création de la ArrayList qui nous permettra de remplire la listView
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
		//On déclare la HashMap qui contiendra les informations pour un item
		HashMap<String, String> map;
		
		//on trie les tâches
		modele.trierTaches(true);
		
		int positionTacheSelectionnee = -1;
		
		for(Tache t:modele.getListeTaches()) {
			if(t.getEtat() != 1 || PreferenceManager.getDefaultSharedPreferences(this).getBoolean("afficher_taches_annulees", false)) {
				if(t.getNom().indexOf(modele.getRechercheCourante()) != -1 || t.getDescription().indexOf(modele.getRechercheCourante()) != -1) {
					
					boolean possedeTag = false;
					if(!modele.isAfficherToutesTaches()) {
						for(Long l : modele.getTagsVisibles())
							if(t.getListeTags().contains(l)) {
								possedeTag = true;
								break;
							}
					}
					
					if(modele.isAfficherToutesTaches() || possedeTag) {
						
						if((modele.getTachePereCourant() == null && modele.getTachesRacines().contains(t.getIdentifiant())) || (modele.getTachePereCourant() != null && modele.getTachePereCourant().getListeTachesFille().contains(t.getIdentifiant()))) {
							map = new HashMap<String, String>();
							
							map.put("titre", t.getNom());
							map.put("description", t.getDescription());
							if(t.getEtat() == 4)
								map.put("img", String.valueOf(R.drawable.btn_check_buttonless_on));
							else
								map.put("img", String.valueOf(R.drawable.btn_check_buttonless_off));
							map.put("id", String.valueOf(t.getIdentifiant()));

							switch (t.getEtat() -1) {
								case Etat.ANNULER:
									map.put("etat_couleur", Color.RED + "");
									break;
								case Etat.A_PREVOIR:
									map.put("etat_couleur", Color.rgb(237, 127, 16) + ""); //orange
									break;
								case Etat.EN_COURS:
									map.put("etat_couleur", Color.BLUE + "");
									break;
								case Etat.TERMINER:
									map.put("etat_couleur", Color.GREEN + "");
									break;
								default:
									map.put("etat_couleur", Color.WHITE + "");
									break;
							}
							
							map.put("nb_fils", t.getListeTachesFille().size() + " " + getResources().getString(R.string.nb_fils));
							long jour = (long) Math.ceil((t.getDateLimite().getTime() - Calendar.getInstance().getTime().getTime()) / (24*60*60*1000.0));
							
							if(jour>=0)
								map.put("jour_restant", jour + " " + getResources().getString(R.string.jours_restants));
							else
								map.put("jour_restant", (0-jour) + " " + getResources().getString(R.string.jours_passes));
							
							listItem.add(map);
							
							if(t.getIdentifiant() == idTacheSelectionnee)
								positionTacheSelectionnee = listItem.size()-1;
						}
					}
				}	
			}
		}

		AdapterListView mSchedule = new AdapterListView (this.getBaseContext(), listItem, R.layout.affichageitem,
				new String[] { "img", "titre", "description", "id", "nb_fils", "jour_restant" }, new int[] {R.id.img, R.id.titre, R.id.description, R.id.id, R.id.nb_fils, R.id.jour_restant });

		maListViewPerso.setAdapter(mSchedule);

		//on anime ou pas l'affichage de la liste
		maListViewPerso.setLayoutAnimation(null);
		if(animation) {
			maListViewPerso.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.list_layout_controller));
			maListViewPerso.startLayoutAnimation();
		}
		
		if(idTacheSelectionnee > 0 && positionTacheSelectionnee >=0)
			maListViewPerso.setSelection(positionTacheSelectionnee);
	}

	/**
	 * Met à jour l'arborescence des tâches pères en haut de la liste de tâches
	 */
	public void updateArborecence() {
		
		String titre = "/";
		for(Tache t : modele.getArborescenceCourante())
			titre += t.getNom() + "/";
		
		arborescence.setText(titre);
		arborescence.requestLayout();
		
		arborecence_scrollbar.postDelayed(new Runnable() {
			public void run() {
				arborecence_scrollbar.refreshDrawableState();
				arborecence_scrollbar.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 50);
		
		if(modele.getArborescenceCourante().size() == 0)
			back.setVisibility(View.INVISIBLE);
		else
			back.setVisibility(View.VISIBLE);
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		if(modele.getTri() == Tri.NOM)
			menu.findItem(R.id.sous_menu_tri_nom).setChecked(true);
		else if(modele.getTri() == Tri.ORDRE_CREATION)
			menu.findItem(R.id.sous_menu_tri_ordre_creation).setChecked(true);
		else if(modele.getTri() == Tri.PRIORITE)
			menu.findItem(R.id.sous_menu_tri_priorite).setChecked(true);
		else if(modele.getTri() == Tri.ETAT)
			menu.findItem(R.id.sous_menu_tri_etat).setChecked(true);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuOpened(int, android.view.Menu)
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("utilise_compte", false))
			menu.findItem(R.id.menu_synchronisation).setVisible(true);
		else
			menu.findItem(R.id.menu_synchronisation).setVisible(false);
		
		return super.onMenuOpened(featureId, menu);
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		//on ajoute un tâche
		case R.id.menu_ajout_tache:
			if(!modele.isEnCoursSynchro()) {
				Intent intent = new Intent(this.getApplicationContext(), DetailsTaches.class);
				startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
				transitionActivity();
			}
			else
				attenteSynchronisation();
			return true;
		//on ajoute des tags
		case R.id.menu_ajout_tag:
			if(!modele.isEnCoursSynchro()) {
				AlertDialog.Builder builderAjoutTag = new AlertDialog.Builder(this);
				builderAjoutTag.setTitle(getResources().getText(R.string.label_ajout_tag));
				builderAjoutTag.setMessage(getResources().getText(R.string.label_nom_tag));
				final EditText etNomTag = new EditText(this);
				builderAjoutTag.setView(etNomTag);
				builderAjoutTag.setNegativeButton("Annuler", new OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {}
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
		//on choisit d'afficher certains tags
		case R.id.menu_afficher_tags:
			
			CharSequence[] itemsTagsAAfficher = new CharSequence[modele.getListeTags().size()+1];
			final boolean[] tagsVisiblesAAfficher = new boolean[modele.getListeTags().size()+1];
			final boolean[] tagsChoisisAAfficher = new boolean[tagsVisiblesAAfficher.length];
			
			itemsTagsAAfficher[0]=getResources().getString(R.string.afficher_toutes_taches);
			tagsVisiblesAAfficher[0]=modele.isAfficherToutesTaches();
			tagsChoisisAAfficher[0]=modele.isAfficherToutesTaches();
			
			for(int i=0;i<modele.getListeTags().size();i++) {
				itemsTagsAAfficher[i+1]=modele.getListeTags().get(i).getNom();
				if(modele.getTagsVisibles().contains(modele.getListeTags().get(i).getIdentifiant())) {
					tagsVisiblesAAfficher[i+1]=true;
					tagsChoisisAAfficher[i+1]=true;
				}
				else {
					tagsVisiblesAAfficher[i+1]=false;
					tagsChoisisAAfficher[i+1]=false;
				}
			}

			AlertDialog.Builder builderAfficheTag = new AlertDialog.Builder(this);
			builderAfficheTag.setTitle(getResources().getText(R.string.label_menu_afficher_tag));
			builderAfficheTag.setNegativeButton("Annuler", new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					for(int i=0; i<tagsVisiblesAAfficher.length; i++)
						tagsVisiblesAAfficher[i] = tagsChoisisAAfficher[i];
				}
			});
			builderAfficheTag.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					ArrayList<Long> listeTagsAffiches = new ArrayList<Long>();
					
					tagsChoisisAAfficher[0] = tagsVisiblesAAfficher[0];
					modele.setAfficherToutesTaches(tagsChoisisAAfficher[0]);
					
					for(int i=1; i<tagsChoisisAAfficher.length; i++){
						tagsChoisisAAfficher[i] = tagsVisiblesAAfficher[i];
			    		if(tagsChoisisAAfficher[i]){
			    			Long indice = modele.getListeTags().get(i-1).getIdentifiant();
			    			listeTagsAffiches.add(indice);			    			
			    		}
					}
					
					modele.setTagsVisibles(listeTagsAffiches);
					
					updateList(true, -1);
				}
			});
			builderAfficheTag.setMultiChoiceItems(itemsTagsAAfficher, tagsVisiblesAAfficher, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					if(which == 0) {
						if(isChecked) {
							for(int i = 1;i< tagsVisiblesAAfficher.length;i++) {
								tagsChoisisAAfficher[i] = true;
								tagsVisiblesAAfficher[i] = true;
							}
						}
					}
				}
			});
			
			AlertDialog afficheTags;
			afficheTags = builderAfficheTag.create();
			afficheTags.show();
			
			return true;
		//on choisit de supprimer des tags
		case R.id.menu_supprimer_tag:
			
			if(!modele.isEnCoursSynchro()) {
			
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
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
				}); 
				AlertDialog alertTags;
				alertTags = builderSuppressionTag.create();
				alertTags.show();
			
			}
			else
				attenteSynchronisation();
			return true;
		case R.id.menu_ajout_utilisateur:
			if(!modele.isEnCoursSynchro()) {
				Intent intentNewUser = new Intent(this.getApplicationContext(), AjoutUtilisateur.class);
				startActivityForResult(intentNewUser, CODE_ACTIVITE_AJOUT_UTILISATEUR);
				transitionActivity();
			}
			else
				attenteSynchronisation();
			return true;
		case R.id.menu_reglage:
			Intent intentPrefs = new Intent(this.getApplicationContext(), Preferences.class);
			startActivityForResult(intentPrefs, CODE_ACTIVITE_PREFERENCES);
			transitionActivity();
			return true;
		case R.id.menu_a_propos:
			Dialog about = new Dialog(this);
			about.setContentView(R.layout.about_dialog);
			about.setTitle(R.string.menu_a_propos);
			about.show();
			return true;
			
		//on choisit de synchroniser
		case R.id.menu_synchronisation:
			item.getSubMenu().setGroupVisible(R.id.groupe_synchronisation, true);
			if(modele.isEnCoursSynchro()) {
				item.getSubMenu().setGroupVisible(R.id.groupe_synchronisation, false);
				attenteSynchronisation();
				return false;
			}
			else
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
			
		//on choisit de trier
		case R.id.sous_menu_tri_date:
			item.setChecked(true);
			modele.setTri(Tri.DATE);
			updateList(true, -1);
			return true;
		case R.id.sous_menu_tri_nom:
			item.setChecked(true);
			modele.setTri(Tri.NOM);
			updateList(true, -1);
			return true;
		case R.id.sous_menu_tri_ordre_creation:
			item.setChecked(true);
			modele.setTri(Tri.ORDRE_CREATION);
			updateList(true, -1);
			return true;
		case R.id.sous_menu_tri_etat:
			item.setChecked(true);
			modele.setTri(Tri.ETAT);
			updateList(true, -1);
			return true;
		case R.id.sous_menu_tri_priorite:
			item.setChecked(true);
			modele.setTri(Tri.PRIORITE);
			updateList(true, -1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public Synchronisation getSw() {
		return sw;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onSearchRequested()
	 */
	@Override
	public boolean onSearchRequested() {
		
		/*
        final String queryAppDataString = mQueryAppData.getText().toString();
        if (queryAppDataString != null) {
        	appDataBundle = new Bundle();
        	appDataBundle.putString("demo_key", queryAppDataString);
       	}
		Bundle appDataBundle = new Bundle();
		startSearch("", false, null, false); 
		*/
		return super.onSearchRequested();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	public void onNewIntent(final Intent newIntent) {
		super.onNewIntent(newIntent);

		if (Intent.ACTION_SEARCH.equals(newIntent.getAction())) {
			modele.setRechercheCourante(newIntent.getStringExtra(SearchManager.QUERY));
			updateList(true, -1);
		}
	}
	
	/**
	 * Permet de générer une transition lorsqu'on lance une nouvelle activity
	 */
	public void transitionActivity() {
		overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);  
	}
	
	/**
	 * Affiche une boite de dialogue pour dire qu'il faut attendre la fin de la synchronisation
	 */
	public void attenteSynchronisation() {
		new ErreurDialog(R.string.menu_synchronisation, R.string.attente_synchronisation, this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContentChanged()
	 */
	@Override
	public void onContentChanged() {
		//changement orientation
		super.onContentChanged();
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
    public void onClick(View v) {
    	
    	long identifiantTache = -1;
    	
    	//si on clique sur le bouton pour afficher les tâches filles
    	if(v.getId() == R.id.img_fils) {
    		
    		identifiantTache = (Long) ((ImageView) v).getTag();
    		modele.getArborescenceCourante().add(modele.getTacheById(identifiantTache));
			updateList(true, -1);
    		
    	}
    	else if(v.getId() == R.id.img) {
    		//si on clique sur le bouton pour modifier l'état de la tâche
    		if(!modele.isEnCoursSynchro()) {
    		
    			identifiantTache = (Long) ((ImageView) v).getTag();
    			
				Tache t = modele.getTacheById(identifiantTache);
				if(t.getEtat() != 4) {
					t.setEtat(4);
					((ImageView)v).setImageResource(R.drawable.btn_check_buttonless_on);
				}
				else {
					t.setEtat(3);
					((ImageView)v).setImageResource(R.drawable.btn_check_buttonless_off);
				}
				t.setVersion(t.getVersion() + 1);
				modele.getBdd().modifTache(t);
								
    		}
    		else
    			attenteSynchronisation();
    			
    	}

    }

}
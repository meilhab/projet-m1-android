package univ_fcomte.gtasks;

import java.util.*;

import univ_fcomte.synchronisation.ThreadSynchronisation;
import univ_fcomte.tasks.*;
import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Activity qui gère la modification ou l'ajout d'un tâche
 */
public class DetailsTaches extends Activity {
    
	private Modele modele; //modèle de l'application
	private GestionnaireTaches gt; //activity principale
	private long identifiant; //identifiant de la tâche à modifier si modification
	private EditText description;
	private EditText nom;
	private Spinner spinnerEtat;
	private Spinner spinnerPriorite;
	private boolean[] tagsAffiches;
	private boolean[] tagsChoisis;
	private AlertDialog.Builder builderTags;
	private final int CODE_ACTIVITE_PREFERENCES = 2;
	private int annneeDateLimite;
	private int moisDateLimite;
	private int jourDateLimite;
	private Button boutonDateLimite;
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_taches);
        
        modele=((MonApplication)getApplication()).getModele();
        Bundle objetbunble  = this.getIntent().getExtras();
        
        if(objetbunble != null && objetbunble.containsKey("id"))
        	identifiant=this.getIntent().getLongExtra("id",-1);
        else
        	identifiant = -1;
        
        gt = ((MonApplication)getApplication()).getGt();
        nom = ((EditText)findViewById(R.id.edit_nom));
        description = ((EditText)findViewById(R.id.edit_description));
        
        spinnerPriorite = (Spinner) findViewById(R.id.edit_priorite);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.priorite, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriorite.setAdapter(adapter);
        
        spinnerEtat = (Spinner) findViewById(R.id.edit_etat);
        adapter = ArrayAdapter.createFromResource(this, R.array.etat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEtat.setAdapter(adapter);
        spinnerEtat.setSelection(2);

        boutonDateLimite = (Button) findViewById(R.id.bouton_date_limite);
		final Calendar c = GregorianCalendar.getInstance();
		annneeDateLimite = c.get(Calendar.YEAR);
		moisDateLimite = c.get(Calendar.MONTH) + 1;
		jourDateLimite = c.get(Calendar.DAY_OF_MONTH);
		boutonDateLimite.setText(ajouterZeroDate(jourDateLimite) + "/" + ajouterZeroDate(moisDateLimite) + "/" + String.valueOf(annneeDateLimite));
		
        boutonDateLimite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			    	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			    		annneeDateLimite = year;
			    		moisDateLimite = monthOfYear + 1;
			    		jourDateLimite = dayOfMonth;
			    		boutonDateLimite.setText(ajouterZeroDate(jourDateLimite) + "/" + ajouterZeroDate(moisDateLimite) + "/" + String.valueOf(annneeDateLimite));
					}
			    };
				new DatePickerDialog(DetailsTaches.this, mDateSetListener, annneeDateLimite, moisDateLimite - 1, jourDateLimite).show();
			}
		});
        
        
        creerBoiteDialogTags();
        
        Button boutonTag = (Button) findViewById(R.id.bouton_ajout_tag);
        boutonTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alert;
				alert = builderTags.create();
				alert.show();
			}
		});
        
    }
   
    /**
     * Remplir les champs de l'activity avec le propriétés de la tâche si modification
     */
    public void remplirChamps() {
    	
    	if(identifiant!=-1) {
        	Tache t=modele.getTacheById(identifiant);
        	//this.setTitle(t.getNom());
        	nom.setText(t.getNom());
        	description.setText(t.getDescription());
        	for(int i=0; i<modele.getListeTags().size(); i++) {
        		if(t.getListeTags().contains(modele.getListeTags().get(i).getIdentifiant())) {
        			tagsChoisis[i] = true;
        			tagsAffiches[i] = true;
        		}
        	}
        	spinnerPriorite.setSelection(t.getPriorite()-1);
        	spinnerEtat.setSelection(t.getEtat()-1);
        	
        	annneeDateLimite = t.getAnnee();
        	moisDateLimite = t.getMois();
        	jourDateLimite = t.getJour();
        	boutonDateLimite.setText(t.getDateLimiteLettre());
        	
        }
    	
    }
    
    /**
     * Permet d'afficher la boite de dialogue pour modifier les tags
     */
    public void creerBoiteDialogTags() {
    	
        CharSequence[] items = new CharSequence[modele.getListeTags().size()];
        tagsAffiches = new boolean[modele.getListeTags().size()];
        tagsChoisis = new boolean[modele.getListeTags().size()];
        for(int i=0;i<modele.getListeTags().size();i++) {
        	items[i]=modele.getListeTags().get(i).getNom();
        	tagsAffiches[i]=false;
        	tagsChoisis[i]=false;
        }
    	
        remplirChamps();
        
        builderTags = new AlertDialog.Builder(this);
        builderTags.setTitle(getResources().getText(R.string.label_tag));
        builderTags.setNegativeButton("Annuler", new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				for(int i=0; i<tagsAffiches.length; i++)
					tagsAffiches[i] = tagsChoisis[i];
			}
        });
        builderTags.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for(int i=0; i<tagsChoisis.length; i++)
					tagsChoisis[i] = tagsAffiches[i];
			}
		});
        
        builderTags.setMultiChoiceItems(items, tagsAffiches, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
		});
    	
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
    	enregistrerTache();
    	super.onBackPressed();
    }
    
    /**
     * Enregistre la tâche modifiée ou la nouvelle tâche dans le modèle et la BdD
     */
    public void enregistrerTache() {
    	
    	ArrayList<Long> listeTag = new ArrayList<Long>();
    	for(int i=0; i<tagsChoisis.length; i++)
    		if(tagsChoisis[i] == true)
    			listeTag.add(modele.getListeTags().get(i).getIdentifiant());
    	String date = ajouterZeroDate(annneeDateLimite) + "-" + ajouterZeroDate(moisDateLimite) + "-" + String.valueOf(jourDateLimite) + " 00:00:00";
    	    	
    	Tache tache = null;
    	if(identifiant == -1) {
    		tache = new Tache(modele.getIdMaxTache() + 1, nom.getText().toString(), description.getText().toString(), date, spinnerPriorite.getSelectedItemPosition()+1, spinnerEtat.getSelectedItemPosition()+1, 1, listeTag, new ArrayList<Long>());
    		modele.ajoutTache(tache);
    		
    		if(modele.getTachePereCourant() == null)
    			modele.getTachesRacines().add(tache.getIdentifiant());
    		
    		long pere = -1;
    		if(modele.getTachePereCourant() != null) {
    			modele.getTachePereCourant().getListeTachesFille().add(tache.getIdentifiant());
    			pere = modele.getTachePereCourant().getIdentifiant();
    		}
    		modele.getBdd().ajouterTache(tache, pere, false);
    	}
    	else {
    		tache = modele.getTacheById(identifiant);
    		tache.setNom(nom.getText().toString());
    		tache.setDescription(description.getText().toString());
    		tache.setEtat(spinnerEtat.getSelectedItemPosition()+1);
    		tache.setPriorite(spinnerPriorite.getSelectedItemPosition()+1);
    		tache.setListeTags(listeTag);
    		tache.setVersion(tache.getVersion() + 1);
    		tache.setDateLimiteToString(date);
    		
    		modele.getBdd().modifTache(tache);
    	}
    	
    	setResult((int) tache.getIdentifiant());
    	
    }
    
    /**
     * Ajoute un zéro à un mois si il est inférieur à 10
     * @param date mois
     * @return mois avec un zéro en plus
     */
    public String ajouterZeroDate(int date) {
    	String dateTemp = String.valueOf(date);
    	if(dateTemp.length() == 1)
    		dateTemp = "0" + dateTemp;
    	return dateTemp;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){

		switch(requestCode){
			case CODE_ACTIVITE_PREFERENCES:
				break;
			default :
				break;
		}

	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_details, menu);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_enregistrer_tache:
			enregistrerTache();
			finish();
			return true;
		case R.id.menu_supprimer_tache:
			modele.supprimerTache(modele.getTacheById(identifiant));
			modele.getBdd().supprimerTache(identifiant, true);

			if (PreferenceManager.getDefaultSharedPreferences(gt).getBoolean("utilise_compte", false)
					&& PreferenceManager.getDefaultSharedPreferences(gt).getBoolean("synchro_auto", false)) {
				ArrayList<Long> listeTachesSuppr = new ArrayList<Long>();
				listeTachesSuppr.add(identifiant);
				ThreadSynchronisation tsSupprTache = new ThreadSynchronisation(modele, gt, gt.getSw());
				tsSupprTache.selectionModeSynchronisation(ThreadSynchronisation.SUPPRESSION_TACHES);
				tsSupprTache.setListeTachesSuppr(listeTachesSuppr);
				tsSupprTache.start();
			}

			finish();
			return true;
		case R.id.menu_ajout_tag:
			AlertDialog.Builder builderAjoutTag = new AlertDialog.Builder(this);
			builderAjoutTag.setTitle(getResources().getText(R.string.label_ajout_tag));
			builderAjoutTag.setMessage(getResources().getText(R.string.label_nom_tag));
			final EditText etNomTag = new EditText(this);
			builderAjoutTag.setView(etNomTag);
			builderAjoutTag.setNegativeButton("Annuler", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {}
			});
			builderAjoutTag.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!etNomTag.getText().toString().equals("")) {
						Tag t = new Tag(modele.getIdMaxTag() + 1, etNomTag
								.getText().toString(), 1);
						modele.ajoutTag(t);
						modele.getBdd().ajouterTag(t, false);
						creerBoiteDialogTags();
						remplirChamps();
						Toast.makeText(DetailsTaches.this, "Nouveau tag ajouté", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(DetailsTaches.this, "Champ vide", Toast.LENGTH_SHORT).show();
					}
				}
			});
			builderAjoutTag.show();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
}

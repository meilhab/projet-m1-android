package univ_fcomte.gtasks;

import java.util.*;

import univ_fcomte.tasks.Modele;
import univ_fcomte.tasks.Tache;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DetailsTaches extends Activity {
    
	private Modele modele;
	private int identifiant;
	private EditText description;
	private EditText nom;
	private Spinner spinnerEtat;
	private Spinner spinnerPriorite;
	private boolean[] tagsAffiches;
	private boolean[] tagsChoisis;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_taches);
        modele=((MonApplication)getApplication()).getModele();
        
        Bundle objetbunble  = this.getIntent().getExtras();
        
        if(objetbunble != null && objetbunble.containsKey("id"))
        	identifiant=this.getIntent().getIntExtra("id",-1);
        else
        	identifiant =- 1;
        
        //Log.i("","id : "+identifiant);
        nom = ((EditText)findViewById(R.id.edit_nom));
        description = ((EditText)findViewById(R.id.edit_description));
        
        spinnerPriorite = (Spinner) findViewById(R.id.edit_priorite);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.priorite, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriorite.setAdapter(adapter);
        
        spinnerEtat = (Spinner) findViewById(R.id.edit_etat);
        adapter = ArrayAdapter.createFromResource(this, R.array.etat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEtat.setAdapter(adapter);
        
        
        
        
        
        
        
        
        CharSequence[] items = new CharSequence[modele.getListeTags().size()];
        tagsAffiches = new boolean[modele.getListeTags().size()];
        tagsChoisis = new boolean[tagsAffiches.length];
        for(int i=0;i<modele.getListeTags().size();i++) {
        	items[i]=modele.getListeTags().get(i).getNom();
        	tagsAffiches[i]=false;
        	tagsChoisis[i]=false;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.label_tag));
        builder.setNegativeButton("Annuler", new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				for(int i=0; i<tagsAffiches.length; i++)
					tagsAffiches[i] = tagsChoisis[i];
			}
        });
        builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for(int i=0; i<tagsChoisis.length; i++)
					tagsChoisis[i] = tagsAffiches[i];
			}
		});
        
        builder.setMultiChoiceItems(items, tagsAffiches, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				//tagsChoisis[which] = isChecked;
			}
		});       
        
        
        
        
        
        
        
        Button b = (Button) findViewById(R.id.bouton_ajout_tag);
        b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog alert;
				alert = builder.create();
				alert.show();
			}
		});
        
        if(identifiant!=-1) {
        	Tache t=modele.getTacheById(identifiant);
        	this.setTitle(t.getNom());
        	nom.setText(t.getNom());
        	description.setText(t.getDescription());
        }
        	
        
    }
    
    public void onBackPressed() {
    	
    	ArrayList<Long> listeTag = new ArrayList<Long>();
    	for(int i=0; i<tagsChoisis.length; i++)
    		if(tagsChoisis[i] == true)
    			listeTag.add(modele.getListeTags().get(i).getIdentifiant());
    	
    	Tache tache = new Tache(modele.getIdMax(), nom.getText().toString(), description.getText().toString(), 3, 1, listeTag, new ArrayList<Long>());
    	
    	modele.ajoutTache(tache);
    	modele.getBdd().ajouterTache(tache, -1, false);
    	
    	super.onBackPressed();
    }
    
}

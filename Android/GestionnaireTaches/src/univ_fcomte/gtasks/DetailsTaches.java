package univ_fcomte.gtasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import univ_fcomte.synchronisation.EnvoyerJson;
import univ_fcomte.synchronisation.Synchronisation.ApiException;
import univ_fcomte.tasks.Modele;
import univ_fcomte.tasks.Tache;
import univ_fcomte.tasks.Tag;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_taches);
        modele=((MonApplication)getApplication()).getModele();
        
        Bundle objetbunble  = this.getIntent().getExtras();
        //String titre;
        //String description;
        //On récupère les données du Bundle
        /*if (objetbunble != null && objetbunble.containsKey("titre") && objetbunble.containsKey("description")) {
        	titre = this.getIntent().getStringExtra("titre");
            description = this.getIntent().getStringExtra("description");
        }else {
        	//Erreur
        	titre = "Error";
        	description = "Error";
        }
        this.setTitle(titre);
        */
        if(objetbunble != null && objetbunble.containsKey("id"))
        	identifiant=this.getIntent().getIntExtra("id",-1);
        else
        	identifiant=-1;
        
        //Log.i("","id : "+identifiant);
        nom = ((EditText)findViewById(R.id.edit_nom));
        description = ((EditText)findViewById(R.id.edit_description));
        
        Spinner s = (Spinner) findViewById(R.id.edit_priorite);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.priorite, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        
        s = (Spinner) findViewById(R.id.edit_etat);
        adapter = ArrayAdapter.createFromResource(this, R.array.etat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        
        final AlertDialog alert;
        
        
        
        CharSequence[] items=new CharSequence[modele.getListeTags().size()];
        boolean[] booleen=new boolean[modele.getListeTags().size()];
        for(int i=0;i<modele.getListeTags().size();i++) {
        	items[i]=modele.getListeTags().get(i).getNom();
        	booleen[i]=false;
        }
        //items = {"Université", "Perso", "Réunion"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.label_tag));
        builder.setMultiChoiceItems(items, booleen, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				Toast.makeText(getApplicationContext(), ""+which+" a ete selectionné", Toast.LENGTH_SHORT).show();
			}
		});
        builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//enregistrer
			}
		});
        alert = builder.create();
        
        Button b = (Button) findViewById(R.id.bouton_ajout_tag);
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
    	
    	Tache tache = new Tache(modele.getIdMax(), nom.getText().toString(), description.getText().toString(), 3, 1, new ArrayList<Long>(), new ArrayList<Long>());
    	
    	modele.ajoutTache(tache);
    	modele.getBdd().ajouterTache(tache, -1, false);
    	
    	super.onBackPressed();
    }
    
}

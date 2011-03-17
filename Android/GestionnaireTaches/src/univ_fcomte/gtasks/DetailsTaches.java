package univ_fcomte.gtasks;

import univ_fcomte.tasks.Modele;
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
import android.widget.Spinner;
import android.widget.Toast;

public class DetailsTaches extends Activity {
    
	private Modele modele;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_taches);
        modele=((MonApplication)getApplication()).getModele();
        
        Bundle objetbunble  = this.getIntent().getExtras();
        String titre;
        String description;
        //On récupère les données du Bundle
        if (objetbunble != null && objetbunble.containsKey("titre") && objetbunble.containsKey("description")) {
        	titre = this.getIntent().getStringExtra("titre");
            description = this.getIntent().getStringExtra("description");
        }else {
        	//Erreur
        	titre = "Error";
        	description = "Error";
        }
        this.setTitle(titre);
        
        
        Spinner s = (Spinner) findViewById(R.id.edit_priorite);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.priorite, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        
        s = (Spinner) findViewById(R.id.edit_etat);
        adapter = ArrayAdapter.createFromResource(this, R.array.etat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        
        final AlertDialog alert;
        final CharSequence[] items = {"Université", "Perso", "Réunion"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getText(R.string.label_tag));
        builder.setMultiChoiceItems(items, new boolean[]{false,false,false}, new DialogInterface.OnMultiChoiceClickListener() {
			
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
        
    }
}

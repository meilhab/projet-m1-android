package univ_fcomte.gtasks;

import univ_fcomte.tasks.Taches;
import univ_fcomte.tasks.Priorite;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

public class GestionnaireTaches extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		
        //Priorite p = new Priorite(2);
        //Log.i("", p.getStringToID());
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	int CODE_DE_MON_ACTIVITE = 1;
    	Toast.makeText(this, "Nouvelle tache", Toast.LENGTH_SHORT).show();
        Bundle objetbunble = new Bundle();
        Intent intent = new Intent(this, DetailsTaches.class);
		objetbunble.putString("titre", "Nouvelle tache");
		objetbunble.putString("description", "Nouvelle tache");
    	intent.putExtras(objetbunble);
    	
		startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
		
    	return super.onTouchEvent(event);
    }
}
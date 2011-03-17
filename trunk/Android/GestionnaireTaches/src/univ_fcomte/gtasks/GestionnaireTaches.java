package univ_fcomte.gtasks;

import java.util.ArrayList;
import java.util.HashMap;

import univ_fcomte.tasks.Modele;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;

public class GestionnaireTaches extends Activity {
    /** Called when the activity is first created. */
	
	private int positionX;
	private Modele modele;
	private ListView maListViewPerso;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        modele=((MonApplication)getApplication()).getModele();
        positionX=0;
        
        
        
      //Récupération de la listview créée dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);

        //Création de la ArrayList qui nous permettra de remplire la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
 
        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
 
        //Création d'une HashMap pour insérer les informations du premier item de notre listView
        map = new HashMap<String, String>();
        //on insère un élément titre que l'on récupérera dans le textView titre créé dans le fichier affichageitem.xml
        map.put("titre", "Word");
        //on insère un élément description que l'on récupérera dans le textView description créé dans le fichier affichageitem.xml
        map.put("description", "Editeur de texte");
        //on insère la référence à l'image (convertit en String car normalement c'est un int) que l'on récupérera dans l'imageView créé dans le fichier affichageitem.xml
        map.put("img", String.valueOf(R.drawable.icon));
        //enfin on ajoute cette hashMap dans la arrayList
        listItem.add(map);
 
        //On refait la manip plusieurs fois avec des données différentes pour former les items de notre ListView
 
        map = new HashMap<String, String>();
        map.put("titre", "Tache 1");
        map.put("description", "Tableur");
        map.put("img", String.valueOf(R.drawable.icon));
        listItem.add(map);
 
        map = new HashMap<String, String>();
        map.put("titre", "Power Point");
        map.put("description", "Logiciel de présentation");
        map.put("img", String.valueOf(R.drawable.icon));
        listItem.add(map);
 
        map = new HashMap<String, String>();
        map.put("titre", "Outlook");
        map.put("description", "Client de courrier électronique");
        map.put("img", String.valueOf(R.drawable.icon));
        listItem.add(map);
 
        //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.affichageitem,
               new String[] {"img", "titre", "description"}, new int[] {R.id.img, R.id.titre, R.id.description});
 
        //On attribut à notre listView l'adapter que l'on vient de créer
        maListViewPerso.setAdapter(mSchedule);
        
        //Enfin on met un écouteur d'évènement sur notre listView
      //On met un écouteur d'évènement sur notre listView
        maListViewPerso.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("","touch");
				//Log.i("","position : "+event.getX());
				positionX=(int) event.getX();
				return false;
			}
		});
        
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
		@SuppressWarnings("unchecked")
		@Override
			public void onItemClick(AdapterView a, View v, int position, long id) {
				Log.i("","item");
				
				if(positionX<=getResources().getDrawable(R.drawable.icon).getMinimumWidth())
					Log.i("","on clic sur l'image");
				
				int CODE_DE_MON_ACTIVITE = 1;
		    	Toast.makeText(maListViewPerso.getContext(), "Nouvelle tache", Toast.LENGTH_SHORT).show();
		        Bundle objetbunble = new Bundle();
		        Intent intent = new Intent(maListViewPerso.getContext(), DetailsTaches.class);
		        //objetbunble.putAll(new Bundle(b))
				objetbunble.putString("titre", "Nouvelle tache");
				objetbunble.putString("description", "Nouvelle tache");
		    	intent.putExtras(objetbunble);
		    	
				startActivityForResult(intent, CODE_DE_MON_ACTIVITE);
			//on récupère la HashMap contenant les infos de notre item (titre, description, img)
			/*HashMap map = (HashMap) maListViewPerso.getItemAtPosition(position);
 
			//On créé un objet Bundle, c'est ce qui va nous permetre d'envoyer des données à l'autre Activity
			Bundle objetbunble = new Bundle();
 
			//Cela fonctionne plus ou moins comme une HashMap, on entre une clef et sa valeur en face
			objetbunble.putString("titre", (String) map.get("titre"));
			objetbunble.putString("description", (String) map.get("description"));
 
			//On créé l'Intent qui va nous permettre d'afficher l'autre Activity
			//Attention il va surement falloir que vous modifier le premier paramètre (Tutoriel9_Android.this)
			//Mettez le nom de l'Activity dans la quelle vous êtes actuellement
			Intent intent = new Intent(fr_no7.this, QuestionActivity.class);
 
			//On affecte à l'Intent le Bundle que l'on a créé
			intent.putExtras(objetbunble);
 
			//On démarre l'autre Activity
			startActivityForResult(intent, CODE_DE_MON_ACTIVITE);*/
        	}
         });
 
        
        
        
        //((MonApplication)getApplication()).test=false;
        //Priorite p = new Priorite(2);
        //Log.i("", p.getStringToID());
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub

		
    	return super.onTouchEvent(event);
    }
}
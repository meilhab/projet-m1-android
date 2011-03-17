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
        
        
        
      //R�cup�ration de la listview cr��e dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);

        //Cr�ation de la ArrayList qui nous permettra de remplire la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
 
        //On d�clare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
 
        //Cr�ation d'une HashMap pour ins�rer les informations du premier item de notre listView
        map = new HashMap<String, String>();
        //on ins�re un �l�ment titre que l'on r�cup�rera dans le textView titre cr�� dans le fichier affichageitem.xml
        map.put("titre", "Word");
        //on ins�re un �l�ment description que l'on r�cup�rera dans le textView description cr�� dans le fichier affichageitem.xml
        map.put("description", "Editeur de texte");
        //on ins�re la r�f�rence � l'image (convertit en String car normalement c'est un int) que l'on r�cup�rera dans l'imageView cr�� dans le fichier affichageitem.xml
        map.put("img", String.valueOf(R.drawable.icon));
        //enfin on ajoute cette hashMap dans la arrayList
        listItem.add(map);
 
        //On refait la manip plusieurs fois avec des donn�es diff�rentes pour former les items de notre ListView
 
        map = new HashMap<String, String>();
        map.put("titre", "Tache 1");
        map.put("description", "Tableur");
        map.put("img", String.valueOf(R.drawable.icon));
        listItem.add(map);
 
        map = new HashMap<String, String>();
        map.put("titre", "Power Point");
        map.put("description", "Logiciel de pr�sentation");
        map.put("img", String.valueOf(R.drawable.icon));
        listItem.add(map);
 
        map = new HashMap<String, String>();
        map.put("titre", "Outlook");
        map.put("description", "Client de courrier �lectronique");
        map.put("img", String.valueOf(R.drawable.icon));
        listItem.add(map);
 
        //Cr�ation d'un SimpleAdapter qui se chargera de mettre les items pr�sent dans notre list (listItem) dans la vue affichageitem
        SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.affichageitem,
               new String[] {"img", "titre", "description"}, new int[] {R.id.img, R.id.titre, R.id.description});
 
        //On attribut � notre listView l'adapter que l'on vient de cr�er
        maListViewPerso.setAdapter(mSchedule);
        
        //Enfin on met un �couteur d'�v�nement sur notre listView
      //On met un �couteur d'�v�nement sur notre listView
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
			//on r�cup�re la HashMap contenant les infos de notre item (titre, description, img)
			/*HashMap map = (HashMap) maListViewPerso.getItemAtPosition(position);
 
			//On cr�� un objet Bundle, c'est ce qui va nous permetre d'envoyer des donn�es � l'autre Activity
			Bundle objetbunble = new Bundle();
 
			//Cela fonctionne plus ou moins comme une HashMap, on entre une clef et sa valeur en face
			objetbunble.putString("titre", (String) map.get("titre"));
			objetbunble.putString("description", (String) map.get("description"));
 
			//On cr�� l'Intent qui va nous permettre d'afficher l'autre Activity
			//Attention il va surement falloir que vous modifier le premier param�tre (Tutoriel9_Android.this)
			//Mettez le nom de l'Activity dans la quelle vous �tes actuellement
			Intent intent = new Intent(fr_no7.this, QuestionActivity.class);
 
			//On affecte � l'Intent le Bundle que l'on a cr��
			intent.putExtras(objetbunble);
 
			//On d�marre l'autre Activity
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
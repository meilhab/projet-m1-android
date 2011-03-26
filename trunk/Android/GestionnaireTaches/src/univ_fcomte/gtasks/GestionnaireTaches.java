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
import univ_fcomte.synchronisation.Synchronisation.ApiException;
import univ_fcomte.tasks.Modele;
import univ_fcomte.tasks.Tache;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //((MonApplication)getApplication()).setModele(new Modele(this));
        modele=((MonApplication)getApplication()).getModele();
        positionX=0;        
        
        //Toast.makeText(this, new Synchronisation().md5("marseille"), 2000).show();
        String om = null;
        Synchronisation sw=new Synchronisation(this);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
        nameValuePairs.add(new BasicNameValuePair("identifiant", "guillaume"));  
        nameValuePairs.add(new BasicNameValuePair("mdPasse", sw.md5("android")));
        
        try {
			om = sw.GetHTML("http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/index.php"/*"http://10.0.2.2/gestionnaire_taches/index.php"*/, nameValuePairs);
		} catch (ApiException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        //Toast.makeText(this, om, 2000).show();

		JsonParser json = new JsonParser(modele);
		try {
			json.parse(om);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		modele.getBdd().reinitialiserBDD(modele.getListeTags(), modele.getListeTaches(), json.getListeAPourTag(), json.getListeAPourFils());
		
        List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);  
        nvp.add(new BasicNameValuePair("identifiant", "guillaume"));  
        nvp.add(new BasicNameValuePair("mdPasse", sw.md5("android")));
        nvp.add(new BasicNameValuePair("json", new EnvoyerJson(modele).genererJson().toString()));
		
        try {
			String reponse = sw.GetHTML("http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/reception.php"/*"http://10.0.2.2/gestionnaire_taches/reception.php"*/, nvp);
			Log.i("",reponse);
        } catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		//sw.envoyerJson(/*"http://marseillaisdu90.javabien.fr/android/index.php"*/"http://10.0.2.2/gestionnaire_taches/reception.php", new EnvoyerJson(modele).genererJson());
		
        //Récupération de la listview créée dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);
        //Création de la ArrayList qui nous permettra de remplire la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        //On déclare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
        for(Tache t:modele.getListeTaches()) {
        	map = new HashMap<String, String>();
            map.put("titre", t.getNom());
            map.put("description", t.getDescription());
            map.put("img", String.valueOf(R.drawable.icon));
            listItem.add(map);
        }
 
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
				//Log.i("","position : "+event.getX());
				positionX=(int) event.getX();
				return false;
			}
		});
        
        maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
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
				//objetbunble.putString("titre", "Nouvelle tache");
		        objetbunble.putInt("id", position);
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

}
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
	
	private String serveur;
	Synchronisation sw;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //((MonApplication)getApplication()).setModele(new Modele(this));
        modele=((MonApplication)getApplication()).getModele();
        positionX=0;
        //serveur = "http://10.0.2.2/gestionnaire_taches/";
        serveur = "http://projetandroid.hosting.olikeopen.com/gestionnaire_taches/";
        
        //Toast.makeText(this, new Synchronisation().md5("marseille"), 2000).show();
        String om = null;
        sw=new Synchronisation(this);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
        nameValuePairs.add(new BasicNameValuePair("identifiant", "guillaume"));  
        nameValuePairs.add(new BasicNameValuePair("mdPasse", sw.md5("android")));
        
        try {
			om = sw.GetHTML(serveur + "index.php", nameValuePairs);
		} catch (ApiException e1) {
			e1.printStackTrace();
		}
		
		Log.i("",om);
		
        //Toast.makeText(this, om, 2000).show();

		JsonParser json = new JsonParser(modele);
		try {
			json.parse(om);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		modele.getBdd().reinitialiserBDD(modele.getListeTags(), modele.getListeTaches(), json.getListeAPourTag(), json.getListeAPourFils());
		
        
        //R�cup�ration de la listview cr��e dans le fichier main.xml
        maListViewPerso = (ListView) findViewById(R.id.listviewperso);
        //Cr�ation de la ArrayList qui nous permettra de remplire la listView
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        //On d�clare la HashMap qui contiendra les informations pour un item
        HashMap<String, String> map;
        for(Tache t:modele.getListeTaches()) {
        	map = new HashMap<String, String>();
            map.put("titre", t.getNom());
            map.put("description", t.getDescription());
            map.put("img", String.valueOf(R.drawable.icon));
            listItem.add(map);
        }
 
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

    @Override
    public void onBackPressed() {
    	
    	Log.i("","appuie sur back");
        List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);  
        nvp.add(new BasicNameValuePair("identifiant", "guillaume"));  
        nvp.add(new BasicNameValuePair("mdPasse", sw.md5("android")));
        nvp.add(new BasicNameValuePair("json", new EnvoyerJson(modele).genererJson().toString()));
		
        try {
			String reponse = sw.GetHTML(serveur + "reception.php", nvp);
			Log.i("reponse",reponse);
        } catch (ApiException e) {
			e.printStackTrace();
		}
        
    	super.onBackPressed();
    }
    
    
}
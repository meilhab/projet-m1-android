package univ_fcomte.synchronisation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import univ_fcomte.tasks.Modele;
import univ_fcomte.tasks.Tache;
import univ_fcomte.tasks.Tag;

public class JsonParser {

	private JSONObject ob;
	private Modele modele;
	private HashMap<Long, Long> listeAPourTag;
	private HashMap<Long, Long> listeAPourFils;
	
	public JsonParser(Modele modele) {
		// TODO Auto-generated constructor stub
		this.modele = modele;
	}
	
	public void parseStream(InputStream is) throws JSONException {
		//is = context.getResources().openRawResource(fichier)
		parse(stream2String(is));
	}
	
	public void parse(String contenu) throws JSONException
	{
		
		if (!contenu.equals("")) {
			
			ob = new JSONObject(contenu);
			
			int nbTaches=ob.getInt("nbTaches");
			int nbTags=ob.getInt("nbTags");			

			// On charge le tableau de personnes qui 
			// se trouve dans le fichier json
			JSONArray tags = ob.getJSONArray("tags");
			JSONArray taches = ob.getJSONArray("taches");
			JSONArray apourtags = ob.getJSONArray("apourtag");
			JSONArray apourfils = ob.getJSONArray("apourfils");
			
			JSONObject tag;
			JSONObject tache;
			JSONObject apourtag;
			JSONObject apourfillle;
			
			for (int i = 0;i < tags.length();i++) {
				tag = tags.getJSONObject(i);
				modele.ajoutTag(new Tag(tag.getLong("idTag"),tag.getString("libelleTag")));
			}
			
			for (int i = 0;i < taches.length();i++) {
				tache = taches.getJSONObject(i);
				
				JSONArray listeTags = tache.getJSONArray("apourtag");
				ArrayList<Long> listeTag= new ArrayList<Long>();
				for (int j = 0; j< listeTags.length();j++)
					listeTag.add(listeTags.getLong(j));
				
				JSONArray listeFils = tache.getJSONArray("apourfils");
				ArrayList<Long> listeTachesFilles= new ArrayList<Long>();
				for (int j = 0; j< listeFils.length();j++)
					listeTachesFilles.add(listeFils.getLong(j));
				
				modele.ajoutTache(new Tache(tache.getLong("idTache"),tache.getString("nomTache"),tache.getString("descriptionTache"),tache.getInt("idPriorite"), tache.getInt("idEtat"),listeTag,listeTachesFilles));
			}

			
			listeAPourTag=new HashMap<Long, Long>();
			for (int i = 0;i < apourtags.length();i++) {
				apourtag = apourtags.getJSONObject(i);
				listeAPourTag.put(apourtag.getLong("idTache"), apourtag.getLong("idTag"));
			}
			
			listeAPourFils=new HashMap<Long, Long>();
			for (int i = 0;i < apourfils.length();i++) {
				apourfillle = apourfils.getJSONObject(i);
				listeAPourFils.put(apourfillle.getLong("idPere"), apourfillle.getLong("idFils"));
			}			
		} 

	}
	
	private String stream2String(InputStream stream) {
		
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		try {
			String cur;
			while ((cur = buffer.readLine()) != null) {
				sb.append(cur);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
		
	}
	
	
	public HashMap<Long, Long> getListeAPourTag() {
		return listeAPourTag;
	}

	public HashMap<Long, Long> getListeAPourFils() {
		return listeAPourFils;
	}
	
}

package univ_fcomte.synchronisation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.*;

import univ_fcomte.tasks.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Permet d'importer des données (tâches, tags) encodées dans un format JSON dans le modèle de l'application
 */
public class JsonParser {

	private JSONObject ob;
	private Modele modele; //modèle de l'application
	private HashMap<Long, Long> listeAPourTag;
	private HashMap<Long, Long> listeAPourFils;
	
	public JsonParser(Modele modele) {
		this.modele = modele;
	}
	
	public void parseStream(InputStream is) throws JSONException {
		parse(stream2String(is));
	}
	
	/**
	 * importer les données encodées dans un format JSON, dans le modèle de l'application
	 * @param contenu données encodées en JSON
	 * @throws JSONException
	 */
	public void parse(String contenu) throws JSONException	{
		
		if (!contenu.equals("")) {
			
			ob = new JSONObject(contenu);
			
			//int nbTaches=ob.getInt("nbTaches");
			//int nbTags=ob.getInt("nbTags");

			JSONArray tags = ob.getJSONArray("tags");
			JSONArray taches = ob.getJSONArray("taches");
			JSONArray apourtags = ob.getJSONArray("apourtag");
			JSONArray apourfils = ob.getJSONArray("apourfils");
			
			JSONObject tag;
			JSONObject tache;
			JSONObject apourtag;
			JSONObject apourfille;
			
			for (int i = 0; i < tags.length(); i++) {
				tag = tags.getJSONObject(i);
				modele.ajoutTag(new Tag(tag.getLong("idTag"), tag.getString("libelleTag"), tag.getInt("versionTag")));
			}
			
			for (int i = 0;i < taches.length();i++) {
				tache = taches.getJSONObject(i);
				
				
				ArrayList<Long> listeTag= new ArrayList<Long>();
				try {
					JSONArray listeTags = tache.getJSONArray("apourtag");
					for (int j = 0; j< listeTags.length();j++)
						listeTag.add(listeTags.getLong(j));
				}catch (Exception e) {}
				
				ArrayList<Long> listeTachesFilles= new ArrayList<Long>();
				try {
					JSONArray listeFils = tache.getJSONArray("apourfils");
					for (int j = 0; j< listeFils.length();j++)
						listeTachesFilles.add(listeFils.getLong(j));
				}catch (Exception e) {}
				
				modele.ajoutTache(new Tache(tache.getLong("idTache"),tache.getString("nomTache"),tache.getString("descriptionTache"),tache.getString("dateLimite"),tache.getInt("idPriorite"), tache.getInt("idEtat"), tache.getInt("versionTache"), listeTag, listeTachesFilles));
			}

			
			listeAPourTag=new HashMap<Long, Long>();
			for (int i = 0;i < apourtags.length();i++) {
				apourtag = apourtags.getJSONObject(i);
				listeAPourTag.put(apourtag.getLong("idTache"), apourtag.getLong("idTag"));
			}
			
			listeAPourFils=new HashMap<Long, Long>();
			for (int i = 0;i < apourfils.length();i++) {
				apourfille = apourfils.getJSONObject(i);
				listeAPourFils.put(apourfille.getLong("idFils"), apourfille.getLong("idPere"));
			}			
		} 

	}
	
	/**
	 * importer les données encodées dans un format JSON, dans le modèle de l'application avec la gestion des version des tâches et tags
	 * @param contenu données encodées en JSON
	 * @return true si le JSON ne contient aucune données
	 * @throws JSONException
	 */
	public boolean parseAvecVersion(String contenu) throws JSONException	{
		
		boolean vide = true;
		
		if (!contenu.equals("")) {
		
			ob = new JSONObject(contenu);
			
			//int nbTaches=ob.getInt("nbTaches");
			//int nbTags=ob.getInt("nbTags");

			JSONArray tags = ob.getJSONArray("tags");
			JSONArray taches = ob.getJSONArray("taches");
			JSONArray apourtags = ob.getJSONArray("apourtag");
			JSONArray apourfils = ob.getJSONArray("apourfils");
			
			if(tags.length() != 0 || taches.length() != 0 || apourtags.length() != 0 || apourfils.length() != 0)
				vide = false;
				
			JSONObject tag;
			JSONObject tache;
			JSONObject apourtag;
			JSONObject apourfille;
			
			for (int i = 0; i < tags.length(); i++) {
				tag = tags.getJSONObject(i);
				
				Tag t = modele.getTagById(tag.getLong("idTag"));
				if(t != null && tag.getInt("versionTag") > t.getVersion()) {
					t.setNom(tag.getString("libelleTag"));
					t.setVersion(tag.getInt("versionTag"));
				}
				else if(t == null)
					modele.ajoutTag(new Tag(tag.getLong("idTag"), tag.getString("libelleTag"), tag.getInt("versionTag")));
			}
			
			for (int i = 0;i < taches.length();i++) {
				tache = taches.getJSONObject(i);
				
				
				ArrayList<Long> listeTag= new ArrayList<Long>();
				try {
					JSONArray listeTags = tache.getJSONArray("apourtag");
					for (int j = 0; j< listeTags.length();j++)
						listeTag.add(listeTags.getLong(j));
				}catch (Exception e) {}
				
				ArrayList<Long> listeTachesFilles= new ArrayList<Long>();
				try {
					JSONArray listeFils = tache.getJSONArray("apourfils");
					for (int j = 0; j< listeFils.length();j++)
						listeTachesFilles.add(listeFils.getLong(j));
				}catch (Exception e) {}
				
				Tache t = modele.getTacheById(tache.getLong("idTache"));
				if(t != null && tache.getInt("versionTache") > t.getVersion()) {
					t.setNom(tache.getString("nomTache"));
					t.setDescription(tache.getString("descriptionTache"));
					t.setDateLimiteToString(tache.getString("dateLimite"));
					t.setEtat(tache.getInt("idEtat"));
					t.setPriorite(tache.getInt("idPriorite"));
					t.setVersion(tache.getInt("versionTache"));
					t.setListeTags(listeTag);
					t.setListeFils(listeTachesFilles);
				}
				else if(t == null)
					modele.ajoutTache(new Tache(tache.getLong("idTache"),tache.getString("nomTache"),tache.getString("descriptionTache"), tache.getString("dateLimite"),tache.getInt("idPriorite"), tache.getInt("idEtat"), tache.getInt("versionTache"), listeTag, listeTachesFilles));
			}

			
			listeAPourTag=new HashMap<Long, Long>();
			for (int i = 0;i < apourtags.length();i++) {
				apourtag = apourtags.getJSONObject(i);
				listeAPourTag.put(apourtag.getLong("idTache"), apourtag.getLong("idTag"));
			}
			
			listeAPourFils=new HashMap<Long, Long>();
			for (int i = 0;i < apourfils.length();i++) {
				apourfille = apourfils.getJSONObject(i);
				listeAPourFils.put(apourfille.getLong("idFils"), apourfille.getLong("idPere"));
			}	
		}
		
		return vide;
	}
	
	/**
	 * Génère une chaîne de caractères à partir d'un InputStream
	 * @param stream InputStream à convertir en String
	 * @return chaîne de caractères générée
	 */
	private String stream2String(InputStream stream) {
		
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder sb = new StringBuilder();
		try {
			String cur;
			while ((cur = buffer.readLine()) != null) {
				sb.append(cur).append("\n"); ;
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
	
	
	/**
	 * Permet d'obtenir la liste des couples père, fils contenues dans le JSON
	 * @return liste des couples père, fils contenues dans le JSON
	 */
	public HashMap<Long, Long> getListeAPourTag() {
		return listeAPourTag;
	}

	public HashMap<Long, Long> getListeAPourFils() {
		return listeAPourFils;
	}
	
}

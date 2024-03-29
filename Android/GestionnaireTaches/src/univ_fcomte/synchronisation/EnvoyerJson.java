package univ_fcomte.synchronisation;

import org.json.*;
import univ_fcomte.tasks.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Permet de générer un objet JSON qui contient toutes les données du modèle (liste de tâches et tags), qui pourra facilement être convertit en String
 */
public class EnvoyerJson {

	private Modele modele;
	private JSONObject ob;
	
	public EnvoyerJson(Modele modele) {
		this.modele = modele;
		ob = new JSONObject();
	}
	
	/**
	 * Génère automatiquement un objet JSON qui contient toutes les tâches et tags
	 * @return objet JSON qui contient toutes les tâches et tags
	 */
	public JSONObject genererJson() {
		
		JSONArray arrayTags = new JSONArray();
		JSONArray arrayTaches = new JSONArray();
		JSONArray arrayApourtags = new JSONArray();
		JSONArray arrayApourfils = new JSONArray();

		JSONObject obTemp;
		JSONObject obTemp2;
		JSONArray obTempTags;
		JSONArray obTempFils;

		try {
			
			int i=0;
			for(Tag t : modele.getListeTags()) {
				
				obTemp = new JSONObject();
				obTemp.put("idTag", t.getIdentifiant());
				obTemp.put("libelleTag", t.getNom());		
				obTemp.put("versionTag", t.getVersion());
				
				arrayTags.put(i, obTemp);
				i++;
			}
			
			i=0;
			int j=0;
			int k=0;
			for(Tache t : modele.getListeTaches()) {
				
				obTemp = new JSONObject();
				obTemp.put("idTache", t.getIdentifiant());
				obTemp.put("nomTache", t.getNom());		
				obTemp.put("descriptionTache", t.getDescription());	
				obTemp.put("dateLimite", t.getDateLimiteToString());	
				obTemp.put("idEtat", t.getEtat());
				obTemp.put("idPriorite", t.getPriorite());
				obTemp.put("versionTache", t.getVersion());
				
				int nb = 0;
				obTempTags = new JSONArray();
				obTempFils = new JSONArray();

				for(long l : t.getListeTags()) {

					obTempTags.put(nb, l);
					obTemp2 = new JSONObject();
					obTemp2.put("idTache", t.getIdentifiant());
					obTemp2.put("idTag", l);
					
					arrayApourtags.put(j, obTemp2);
					
					nb++;
					j++;
				}
				
				nb = 0;
				for(long l : t.getListeTachesFille()) {
					
					obTempFils.put(nb, l);
					obTemp2 = new JSONObject();
					obTemp2.put("idPere", t.getIdentifiant());
					obTemp2.put("idFils", l);
					
					arrayApourfils.put(k, obTemp2);
					
					nb++;
					k++;
				}
				
				obTemp.put("apourtag", obTempTags);
				obTemp.put("apourfils", obTempFils);
				
				arrayTaches.put(i, obTemp);
				
				i++;
				
			}
			
			ob.put("tags", arrayTags);
			ob.put("nbTags", modele.getListeTags().size());
			ob.put("taches", arrayTaches);
			ob.put("nbTaches", modele.getListeTaches().size());
			ob.put("apourtag", arrayApourtags);
			ob.put("apourfils", arrayApourfils);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return ob;
		
	}
	
}

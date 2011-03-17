package univ_fcomte.tasks;

import java.util.ArrayList;

public class Modele {

	private ArrayList<Tag> listeTags;
	private ArrayList<Tache> listeTaches;
	
	public Modele() {
		listeTags = new ArrayList<Tag>();
		listeTaches = new ArrayList<Tache>();
	}

	public void ajoutTache(Tag t){
		listeTags.add(t);
	}
	
	public void retirerTache(Tag t){
		listeTags.remove(t);
	}
	
	public ArrayList<Tag> getListeTags(){
		return listeTags;
	}
	
	public String toString(){
		String s = "";
		for(int i=0; i<listeTags.size(); i++)
			s += listeTags.get(i).getNom();
		return s;
	}

	public void ajoutTache(Tache t){
		listeTaches.add(t);
	}
	
	public void retirerTache(Tache t){
		listeTaches.remove(t);
	}
	
	public ArrayList<Tache> getListeTaches(){
		return listeTaches;
	}
	
}

package univ_fcomte.tasks;

import java.util.ArrayList;

public class ListeTaches {
	private ArrayList<Tache> listeTaches;
	
	public ListeTaches(){
		listeTaches = new ArrayList<Tache>();
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

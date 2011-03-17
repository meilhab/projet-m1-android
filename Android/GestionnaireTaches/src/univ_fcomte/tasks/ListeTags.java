package univ_fcomte.tasks;

import java.util.ArrayList;

public class ListeTags {
	private ArrayList<Tag> listeTags;
	
	public ListeTags(){
		listeTags = new ArrayList<Tag>();
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
}

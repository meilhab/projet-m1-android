package univ_fcomte.tasks;

import android.content.res.Resources;
import univ_fcomte.gtasks.*;

public class Priorite {
	public static final int SANS = 0;
	public static final int FAIBLE = 1;
	public static final int URGENT = 2;
	public static final int ULTRA_URGENT = 3;
	private int id;
	
	public Priorite(int id){
		if(id < SANS && id > ULTRA_URGENT) 
			this.id = SANS;
		else 
			this.id = id;
	}
	/*
	public String getStringToID(){
		String[] s = Resources.getSystem().getStringArray(R.array.priorite);
		return s[id];
//		switch(id){
//			case SANS:
//				return "Sans";
//			case FAIBLE:
//				return "Faible";
//			case URGENT:
//				return "Urgent";
//			case ULTRA_URGENT:
//				return "Ultra urgent";
//		}
//		return null;
	}
	*/
}

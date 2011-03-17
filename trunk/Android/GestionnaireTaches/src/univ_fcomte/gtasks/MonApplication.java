package univ_fcomte.gtasks;

import univ_fcomte.tasks.Modele;
import android.app.Application;

public class MonApplication extends Application{

	private Modele modele;
	
	public MonApplication() {
		modele=new Modele();
	}

	public Modele getModele() {
		return modele;
	}
	
}

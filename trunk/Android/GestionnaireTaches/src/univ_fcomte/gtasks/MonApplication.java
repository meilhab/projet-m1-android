package univ_fcomte.gtasks;

import univ_fcomte.tasks.Modele;
import android.app.Application;

public class MonApplication extends Application{

	private Modele modele;
	
	public MonApplication() {
		modele=new Modele(this);
	}

	public Modele getModele() {
		return modele;
	}
	
	public void setModele(Modele modele) {
		this.modele = modele;
	}
	
}

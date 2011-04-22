package univ_fcomte.gtasks;

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Construit automatiquement les items de la listeView de tâches
 */
public class AdapterListView extends SimpleAdapter {

    //private int[] colors = new int[] { Color.parseColor("#40FF0000"), Color.parseColor("#40FFFF00") };
    private long identifiant;
    private String jourRestant;
    private List<HashMap<String, String>> listItem;
    
    public AdapterListView(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
    	super(context, items, resource, from, to);
    	this.listItem = items;
    	jourRestant = context.getResources().getString(R.string.jours_passes);
    }

    /* (non-Javadoc)
     * @see android.widget.SimpleAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View view = super.getView(position, convertView, parent);
    	/*
    	int colorPos = position % colors.length;
    	view.setBackgroundColor(colors[colorPos]);
    	*/
    	
    	identifiant = Long.valueOf(((TextView) view.findViewById(R.id.id)).getText().toString());
    	((ImageView) view.findViewById(R.id.img)).setTag(identifiant);
    	((ImageView) view.findViewById(R.id.img_fils)).setTag(identifiant);

    	//colorie en rouge les items si la date est dépassé
    	if(((TextView) view.findViewById(R.id.jour_restant)).getText().toString().indexOf(jourRestant) != -1)
    		((TextView) view.findViewById(R.id.jour_restant)).setTextColor(Color.RED);
    	else
    		((TextView) view.findViewById(R.id.jour_restant)).setTextColor(((TextView) view.findViewById(R.id.nb_fils)).getTextColors());
    	
    	//ajoute une couleurs à gauche de l'item selon l'état de la tâche
    	((ImageView) view.findViewById(R.id.etat_couleur)).setBackgroundColor(Integer.valueOf(listItem.get(position).get("etat_couleur")));
    	
    	return view;

    }

}

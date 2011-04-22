package univ_fcomte.gtasks;

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class AdapterListView extends SimpleAdapter {

    private int[] colors = new int[] { Color.parseColor("#40FF0000"), Color.parseColor("#40FFFF00") };
    private long identifiant;
    private String jourRestant;
    private List<HashMap<String, String>> listItem;
    
    public AdapterListView(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
    	super(context, items, resource, from, to);
    	this.listItem = items;
    	jourRestant = context.getResources().getString(R.string.jours_passes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View view = super.getView(position, convertView, parent);
    	/*
    	int colorPos = position % colors.length;
    	view.setBackgroundColor(colors[colorPos]);
    	*/
    	//view.setBackgroundResource(R.drawable.background_item);
    	
    	identifiant = Long.valueOf(((TextView) view.findViewById(R.id.id)).getText().toString());
    	((ImageView) view.findViewById(R.id.img)).setTag(identifiant);
    	((ImageView) view.findViewById(R.id.img_fils)).setTag(identifiant);

    	if(((TextView) view.findViewById(R.id.jour_restant)).getText().toString().indexOf(jourRestant) != -1)
    		((TextView) view.findViewById(R.id.jour_restant)).setTextColor(Color.RED);
    	else
    		((TextView) view.findViewById(R.id.jour_restant)).setTextColor(((TextView) view.findViewById(R.id.nb_fils)).getTextColors());

    	((ImageView) view.findViewById(R.id.etat_couleur)).setBackgroundColor(Integer.valueOf(listItem.get(position).get("etat_couleur")));
    	
    	return view;

    }

}

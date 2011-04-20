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
    
    public AdapterListView(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
    	super(context, items, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	View view = super.getView(position, convertView, parent);
    	/*int colorPos = position % colors.length;
    	view.setBackgroundColor(colors[colorPos]);*/
    	
    	view.setBackgroundResource(R.drawable.background_item);
    	
    	identifiant = Long.valueOf(((TextView) view.findViewById(R.id.id)).getText().toString());
    	((ImageView) view.findViewById(R.id.img)).setTag(identifiant);
    	((ImageView) view.findViewById(R.id.img_fils)).setTag(identifiant);
    	
    	((ImageView) view.findViewById(R.id.img_fils)).setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				v.setBackgroundResource(R.drawable.bouton_fils_in);
				return false;
			}
		});
				

    	
    	return view;

    }

}

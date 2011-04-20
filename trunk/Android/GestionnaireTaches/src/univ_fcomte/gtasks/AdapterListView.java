package univ_fcomte.gtasks;

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.SimpleAdapter;

public class AdapterListView extends SimpleAdapter {

    private int[] colors = new int[] { Color.parseColor("#40FF0000"), Color.parseColor("#40FFFF00") };
    
    public AdapterListView(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
    	super(context, items, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	View view = super.getView(position, convertView, parent);
    	int colorPos = position % colors.length;
    	view.setBackgroundColor(colors[colorPos]);
    	return view;

    }

}

package univ_fcomte.gtasks;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceClickListener;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {
    /** Called when the activity is first created. */
	
	SharedPreferences pref;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
                        
        pref = getPreferenceManager().getSharedPreferences();
        pref.registerOnSharedPreferenceChangeListener(this);

    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		//Toast.makeText(this,key + ":" + sharedPreferences.getString(key, ""), Toast.LENGTH_SHORT).show();		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		/*
		if(preference.getKey().equals("read")){
				        
	        SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(this);
	        String value = mgr.getString("edit", "default");
	        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
			
		}else if(preference.getKey().equals("write")){
			
	        SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(this);
	        Editor editor = mgr.edit();
	        editor.putString("edit", ""+SystemClock.currentThreadTimeMillis());
	        editor.commit();
			
		}else{		
			Toast.makeText(this, preference.getKey()+preference.getTitle(), Toast.LENGTH_LONG).show();
		}
		*/
		return true;
	}
}
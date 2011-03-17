package univ_fcomte.gtasks;

import univ_fcomte.tasks.Priorite;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GestionnaireTaches extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Priorite p = new Priorite(2);
        Log.i("", p.getStringToID());
    }
}
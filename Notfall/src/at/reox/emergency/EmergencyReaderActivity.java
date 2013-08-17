package at.reox.emergency;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class EmergencyReaderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_emergency_reader);

	Context context = getActionBar().getThemedContext();

	SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(context,
	    R.array.readerOptions, android.R.layout.simple_spinner_dropdown_item);

	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

	getActionBar().setListNavigationCallbacks(mSpinnerAdapter,
	    new ActionBar.OnNavigationListener() {

		@Override
		public boolean onNavigationItemSelected(int arg0, long arg1) {
		    // TODO Auto-generated method stub
		    return false;
		}

	    });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.emergency_reader, menu);
	return true;
    }

}

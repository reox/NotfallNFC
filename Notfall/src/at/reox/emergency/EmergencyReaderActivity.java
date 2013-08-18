package at.reox.emergency;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import at.reox.emergency.tools.EmergencyData;
import at.reox.emergency.tools.NfcUtils;

public class EmergencyReaderActivity extends Activity {

    PendingIntent pendingIntent;
    NfcAdapter mAdapter;
    String[][] techListsArray = new String[][] { new String[] { NfcV.class.getName() } };
    IntentFilter[] intentFilter = new IntentFilter[] { new IntentFilter(
	NfcAdapter.ACTION_TECH_DISCOVERED), };

    private final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	mAdapter = NfcAdapter.getDefaultAdapter(this);

	// handle foreground intent for nfc here
	pendingIntent = PendingIntent.getActivity(this, 0,
	    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

	setContentView(R.layout.activity_emergency_reader);

	Context context = getActionBar().getThemedContext();
	SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(context,
	    R.array.readerOptions, android.R.layout.simple_spinner_dropdown_item);
	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	getActionBar().setListNavigationCallbacks(mSpinnerAdapter,
	    new ActionBar.OnNavigationListener() {
		String[] strings = getResources().getStringArray(R.array.readerOptions);

		@Override
		public boolean onNavigationItemSelected(int arg0, long arg1) {
		    // Create new fragment from our own Fragment class
		    ListContentFragment newFragment = new ListContentFragment();
		    FragmentTransaction ft = getFragmentManager().beginTransaction();
		    // Replace whatever is in the fragment container with this fragment
		    // and give the fragment a tag name equal to the string at the position selected
		    currentTag = strings[arg0];
		    currentFragment = newFragment;
		    ft.replace(R.id.fragment_container, newFragment, currentTag);
		    // Apply changes
		    ft.commit();
		    return true;
		}
	    });
    }

    private String currentTag = null;
    private Fragment currentFragment = null;

    @Override
    public void onPause() {
	super.onPause();
	mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onResume() {
	super.onResume();
	mAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, techListsArray);
    }

    @Override
    public void onNewIntent(Intent intent) {
	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	// do something with tagFromIntent
	Log.d("Nfcreader", "Found a Tag!" + tag);
	Toast.makeText(this, "NFC Tag gefunden, bitte warten...", Toast.LENGTH_SHORT).show();
	// TODO Fake data here because tags dont work right now
	try {
	    byte[] realData = NfcUtils.read(tag);
	    Log.d(TAG, "Found a tag and read: " + NfcUtils.printHexString(realData));
	    ((EmergencyApplication) getApplication()).loadTag(new EmergencyData()
		.readBinaryData(NfcUtils.fakeData));
	    Toast.makeText(this, "Tag wurde gelesen!", Toast.LENGTH_SHORT).show();
	    ListContentFragment newFragment = new ListContentFragment();
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    // Replace whatever is in the fragment container with this fragment
	    // and give the fragment a tag name equal to the string at the position selected
	    ft.add(R.id.fragment_container, newFragment, currentTag);
	    ft.remove(currentFragment);
	    currentFragment = newFragment;
	    // Apply changes
	    ft.commit();

	} catch (Exception e) {
	    Toast.makeText(this, "Tag kann nicht gelesen werden: " + e.getMessage(),
		Toast.LENGTH_LONG).show();
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.emergency_reader, menu);
	return true;
    }

}

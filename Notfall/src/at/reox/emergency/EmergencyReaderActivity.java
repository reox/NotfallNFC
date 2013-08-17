package at.reox.emergency;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
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
import at.reox.emergency.tools.EmergencyDataParseException;
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
		    ft.replace(R.id.fragment_container, newFragment, strings[arg0]);
		    // Apply changes
		    ft.commit();
		    return true;
		}

	    });

    }

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
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (FormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	byte[] fakData = new byte[] { (byte) 0x02, (byte) 0x00, (byte) 0x08, (byte) 0x42,
	    (byte) 0x61, (byte) 0x63, (byte) 0x68, (byte) 0x6D, (byte) 0x61, (byte) 0x6E,
	    (byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x53, (byte) 0x65,
	    (byte) 0x62, (byte) 0x61, (byte) 0x73, (byte) 0x74, (byte) 0x69, (byte) 0x61,
	    (byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x53, (byte) 0x65,
	    (byte) 0x6E, (byte) 0x65, (byte) 0x6B, (byte) 0x6F, (byte) 0x77, (byte) 0x69,
	    (byte) 0x74, (byte) 0x73, (byte) 0x63, (byte) 0x68, (byte) 0x67, (byte) 0x61,
	    (byte) 0x73, (byte) 0x73, (byte) 0x65, (byte) 0x20, (byte) 0x33, (byte) 0x2F,
	    (byte) 0x33, (byte) 0x2F, (byte) 0x31, (byte) 0x30, (byte) 0x00, (byte) 0x00,
	    (byte) 0x49, (byte) 0xA5, (byte) 0x45, (byte) 0x12, (byte) 0x26, (byte) 0x00,
	    (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x4A,
	    (byte) 0xE7, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x06, (byte) 0x04,
	    (byte) 0x0A, (byte) 0x5A, (byte) 0x09, (byte) 0x29, (byte) 0x00, (byte) 0x01,
	    (byte) 0x00, (byte) 0x09, (byte) 0x66, (byte) 0x6F, (byte) 0x6F, (byte) 0x62,
	    (byte) 0x61, (byte) 0x72, (byte) 0x66, (byte) 0x6F, (byte) 0x6F, (byte) 0x00,
	    (byte) 0x00, (byte) 0x01, (byte) 0x40, (byte) 0x8D, (byte) 0x68, (byte) 0x33,
	    (byte) 0xE9, (byte) 0x34, (byte) 0x94, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
	    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
	    0x00, 0x00, 0x00 };

	try {
	    ((EmergencyApplication) getApplication()).loadTag(new EmergencyData()
		.readBinaryData(fakData));
	} catch (EmergencyDataParseException e) {
	    Toast.makeText(this, "Tag kann nicht geladen werden: " + e.getMessage(),
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

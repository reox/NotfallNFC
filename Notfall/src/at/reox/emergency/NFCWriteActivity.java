package at.reox.emergency;

import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import at.reox.emergency.tools.NfcUtils;

public class NFCWriteActivity extends Activity {

    PendingIntent pendingIntent;
    NfcAdapter mAdapter;
    String[][] techListsArray = new String[][] { new String[] { NfcV.class.getName() } };
    IntentFilter[] intentFilter = new IntentFilter[] { new IntentFilter(
	NfcAdapter.ACTION_TECH_DISCOVERED), };

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_nfcwrite);

	mAdapter = NfcAdapter.getDefaultAdapter(this);

	// handle foreground intent for nfc here
	pendingIntent = PendingIntent.getActivity(this, 0,
	    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    @Override
    public void onPause() {
	super.onPause();
	mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	if (tag != null) {
	    Log.d(TAG, "Got a tag that supports these Tech: " + Arrays.toString(tag.getTechList()));

	    try {
		byte[] data = ((EmergencyApplication) getApplication()).getData();

		NfcUtils.write(tag, data);
		Toast.makeText(this, "Wrote to tag!", Toast.LENGTH_LONG).show();
	    } catch (Exception e) {
		Toast.makeText(this, "Error...: " + e.getMessage() + " --- " + e.toString(),
		    Toast.LENGTH_LONG).show();
		Log.e(TAG, "Exception", e);
	    }
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
	mAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, techListsArray);
	Intent intent = getIntent();

	byte[] data = intent.getByteArrayExtra("binarydata");
	if (data != null) {
	    ((EmergencyApplication) getApplication()).setData(data);
	    Log.d(TAG, "found data...");
	    TextView t = (TextView) findViewById(R.id.sizeInformation);
	    t.setText("Minimum Tag Size required: " + data.length + "Bytes");
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.nfcwrite, menu);
	return true;
    }

}

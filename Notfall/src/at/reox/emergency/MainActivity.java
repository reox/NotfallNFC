package at.reox.emergency;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.reox.emergency.tools.EmergencyData;

public class MainActivity extends Activity {

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
    }

    public void onSaveClick(View view) {
	EmergencyData d = new EmergencyData();
	d.setName(((EditText) findViewById(R.id.name)).getText().toString());
	d.setSurname(((EditText) findViewById(R.id.surname)).getText().toString());
	d.setAddress(((EditText) findViewById(R.id.address)).getText().toString());
	d.setExtra(((EditText) findViewById(R.id.extraText)).getText().toString());
	d.setSvnr(((EditText) findViewById(R.id.svnr)).getText().toString());
	// TODO set PZN and ICD things here...

	Log.d(TAG, EmergencyData.getHex(d.getBinaryData()));

	Intent i = new Intent(this, NFCWriteActivity.class);
	i.putExtra("binarydata", d.getBinaryData());
	startActivity(i);
    }

    public void openMedication(View view) {
	Intent data = new Intent(this, MedicationActivity.class);
	startActivityForResult(data, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	if ((requestCode == 100) && (resultCode == Activity.RESULT_OK)) {
	    String[] pzn = intent.getStringArrayExtra("PZNList");
	    Log.d(TAG, "Got some PZN: " + Arrays.toString(pzn));
	    Button b = (Button) findViewById(R.id.button1);
	    b.setText(getString(R.string.medication) + " (" + pzn.length + ")");
	}
    }

    public void openDiseases(View view) {
	// TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

}

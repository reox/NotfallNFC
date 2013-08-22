package at.reox.emergency;

import java.util.Arrays;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import at.reox.emergency.tools.EmergencyData;

public class MainActivity extends Activity {

    private final String TAG = this.getClass().getName();
    EmergencyData d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	d = new EmergencyData();

	// Try to regain old values
	SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
	d.setName(sharedPref.getString("name", ""));
	((EditText) findViewById(R.id.name)).setText(d.getName());
	d.setSurname(sharedPref.getString("surname", ""));
	((EditText) findViewById(R.id.surname)).setText(d.getSurname());
	d.setAddress(sharedPref.getString("address", ""));
	((EditText) findViewById(R.id.address)).setText(d.getAddress());
	d.setSvnr(sharedPref.getString("svnr", ""));
	((EditText) findViewById(R.id.svnr)).setText(d.getSvnr());
	((Spinner) findViewById(R.id.blutgruppe)).setSelection(sharedPref.getInt("bloodgroup", 0));
	((Spinner) findViewById(R.id.rhesusfaktor)).setSelection(sharedPref.getInt("rhesus", 0));
	((Spinner) findViewById(R.id.kellfaktor)).setSelection(sharedPref.getInt("kell", 0));
	d.setExtra(sharedPref.getString("extra", ""));
	((EditText) findViewById(R.id.extraText)).setText(d.getExtra());
	((CheckBox) findViewById(R.id.savelocal)).setChecked(sharedPref.getBoolean("savelocal",
	    false));
	((CheckBox) findViewById(R.id.checkorgan)).setChecked(sharedPref.getBoolean("organdonor",
	    false));

	d.addPZN(sharedPref.getStringSet("pzn", new HashSet<String>()));
	d.addICD(sharedPref.getStringSet("icd", new HashSet<String>()));

	d.setSex(sharedPref.getInt("sex", 0));
	if (d.getSex() == EmergencyData.MALE) {
	    ((RadioButton) findViewById(R.id.sexmale)).setChecked(true);
	} else {
	    ((RadioButton) findViewById(R.id.sexfemale)).setChecked(true);
	}

	Button b = (Button) findViewById(R.id.button1);
	b.setText(getString(R.string.medication) + " (" + d.getPZN().size() + ")");

	Button b2 = (Button) findViewById(R.id.button2);
	b2.setText(getString(R.string.diseases) + " (" + d.getICD().size() + ")");

	for (int id : new int[] { R.id.name, R.id.surname, R.id.address }) {
	    findViewById(id).setOnFocusChangeListener(new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean hasFocus) {
		    if (!hasFocus) {
			EditText t = (EditText) arg0;

			t.setError(null);
			if (t.getText().toString().length() == 0) {
			    t.setError(getString(R.string.obligatoryfield));
			}
		    }
		}

	    });
	}

	Spinner bg = (Spinner) findViewById(R.id.blutgruppe);
	bg.setOnItemSelectedListener(new OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String group = (String) arg0.getItemAtPosition(arg2);
		if (group.equals("A")) {
		    d.setBloodgroup(EmergencyData.BLOOD_A);
		} else if (group.equals("B")) {
		    d.setBloodgroup(EmergencyData.BLOOD_B);
		} else if (group.equals("AB")) {
		    d.setBloodgroup(EmergencyData.BLOOD_AB);
		} else if (group.equals("0")) {
		    d.setBloodgroup(EmergencyData.BLOOD_0);
		} else {
		    d.setBloodgroup(EmergencyData.BLOOD_UNKNOWN);
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }

	});

	Spinner rh = (Spinner) findViewById(R.id.rhesusfaktor);
	rh.setOnItemSelectedListener(new OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String rhesus = (String) arg0.getItemAtPosition(arg2);
		if (rhesus.contains("pos")) {
		    d.setRhesus(EmergencyData.KELL_POS);
		} else if (rhesus.contains("neg")) {
		    d.setRhesus(EmergencyData.KELL_NEG);
		} else {
		    d.setRhesus(EmergencyData.KELL_UNKNOWN);
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }

	});

	Spinner kf = (Spinner) findViewById(R.id.kellfaktor);
	kf.setOnItemSelectedListener(new OnItemSelectedListener() {

	    @Override
	    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String kell = (String) arg0.getItemAtPosition(arg2);
		if (kell.contains("pos")) {
		    d.setKell(EmergencyData.KELL_POS);
		} else if (kell.contains("neg")) {
		    d.setKell(EmergencyData.KELL_NEG);
		} else {
		    d.setKell(EmergencyData.KELL_UNKNOWN);
		}
	    }

	    @Override
	    public void onNothingSelected(AdapterView<?> arg0) {
	    }

	});

	final EditText edittext = (EditText) findViewById(R.id.svnr);

	edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		EditText editText;

		if (!hasFocus) {
		    editText = (EditText) v;
		    editText.setError(null);
		    // check if svnr is entered correctly...
		    String rawNumber = editText.getText().toString();
		    if (rawNumber.length() != 10) {
			editText.setError(getString(R.string.novalidsvnr));
			Log.d(TAG, "SVNR: Length check denied");
			return;
		    }
		    int[] n = new int[10];
		    int nc = 0;
		    for (char c : rawNumber.toCharArray()) {
			n[nc++] = Integer.parseInt(new String(c + ""));
		    }
		    int[] m = { 3, 7, 9, 0, 5, 8, 4, 2, 1, 6 };
		    int checksum;
		    do {
			checksum = 0;
			for (int i = 0; i < 10; i++) {
			    checksum += n[i] * m[i];
			}
			checksum %= 11;
			Log.d(TAG,
			    "Calculated Checksum: " + checksum + " from " + Arrays.toString(n));
			if (checksum == 10) {
			    n[2]++;
			}
		    } while (checksum == 10);

		    if (checksum != n[3]) {
			editText.setError(getString(R.string.novalidsvnr) + " (Checksum)");
		    }

		}
	    }
	});
    }

    public void onRadioButtonClicked(View view) {
	// Is the button now checked?
	boolean checked = ((RadioButton) view).isChecked();

	// Check which radio button was clicked
	switch (view.getId()) {
	case R.id.sexmale:
	    if (checked) {
		d.setSex(EmergencyData.MALE);
	    }
	    break;
	case R.id.sexfemale:
	    if (checked) {
		d.setSex(EmergencyData.FEMALE);
	    }
	    break;
	}
    }

    public void onSaveClick(View view) {
	boolean error = false;
	for (int id : new int[] { R.id.name, R.id.surname, R.id.address, R.id.svnr }) {
	    if (((EditText) findViewById(id)).getText().toString().length() == 0) {
		((EditText) findViewById(id)).setError(getString(R.string.obligatoryfield));
		error = true;
	    }
	}
	if (!error) {
	    // check if we should save our values to the local store:

	    d.setName(((EditText) findViewById(R.id.name)).getText().toString());
	    d.setSurname(((EditText) findViewById(R.id.surname)).getText().toString());
	    d.setAddress(((EditText) findViewById(R.id.address)).getText().toString());
	    d.setExtra(((EditText) findViewById(R.id.extraText)).getText().toString());
	    d.setSvnr(((EditText) findViewById(R.id.svnr)).getText().toString());

	    d.setOrganDonor(((CheckBox) findViewById(R.id.checkorgan)).isChecked());

	    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPref.edit();

	    if (((CheckBox) findViewById(R.id.savelocal)).isChecked()) {

		editor.putBoolean("savelocal", true);
		editor.putString("name", d.getName());
		editor.putString("surname", d.getSurname());
		editor.putString("address", d.getAddress());
		editor.putString("extra", d.getExtra());
		editor.putString("svnr", d.getSvnr());
		editor.putInt("bloodgroup",
		    ((Spinner) findViewById(R.id.blutgruppe)).getSelectedItemPosition());
		editor.putInt("rhesus",
		    ((Spinner) findViewById(R.id.rhesusfaktor)).getSelectedItemPosition());
		editor.putInt("kell",
		    ((Spinner) findViewById(R.id.kellfaktor)).getSelectedItemPosition());
		editor.putStringSet("pzn", d.getPZN());
		editor.putStringSet("icd", d.getICD());
		editor.putInt("sex",
		    ((RadioButton) findViewById(R.id.sexmale)).isChecked() ? EmergencyData.MALE
			: EmergencyData.FEMALE);
		editor.putBoolean("organdonor",
		    ((CheckBox) findViewById(R.id.checkorgan)).isChecked());

		editor.commit();
	    } else {
		editor.clear();
		editor.commit();
	    }

	    Intent i = new Intent(this, NFCWriteActivity.class);
	    i.putExtra("binarydata", d.getBinaryData());
	    startActivity(i);
	} else {
	    Toast.makeText(this, getString(R.string.fillallfields), Toast.LENGTH_SHORT).show();
	}
    }

    public void openMedication(View view) {
	Intent data = new Intent(this, MedicationActivity.class);
	data.putExtra("pzndata", d.getPZN().toArray(new String[d.getPZN().size()]));
	startActivityForResult(data, 100);
    }

    public void openDiseases(View view) {
	Intent data = new Intent(this, DiseaseActivity.class);
	data.putExtra("icddata", d.getICD().toArray(new String[d.getICD().size()]));
	startActivityForResult(data, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	if ((requestCode == 100) && (resultCode == Activity.RESULT_OK)) {
	    // PZN Code
	    String[] pzn = intent.getStringArrayExtra("PZNList");
	    if (pzn != null) {
		d.clearPZN();
		Log.d(TAG, "Got some PZN: " + Arrays.toString(pzn));
		d.addPZN(pzn);
		Button b = (Button) findViewById(R.id.button1);
		b.setText(getString(R.string.medication) + " (" + pzn.length + ")");
	    }

	}
	if ((requestCode == 200) && (resultCode == Activity.RESULT_OK)) {
	    String[] icd = intent.getStringArrayExtra("ICDList");
	    if (icd != null) {
		d.clearICD();
		Log.d(TAG, "Got some ICD: " + Arrays.toString(icd));
		d.addICD(icd);
		Button b = (Button) findViewById(R.id.button2);
		b.setText(getString(R.string.diseases) + " (" + icd.length + ")");
	    }
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

}

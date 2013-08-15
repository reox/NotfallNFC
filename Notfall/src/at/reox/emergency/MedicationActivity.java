package at.reox.emergency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MedicationActivity extends Activity {

    private SimpleAdapter simpleAdpt;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    public final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_medication);

	ListView lv = (ListView) findViewById(R.id.listView1);
	Intent intent = getIntent();

	for (String s : intent.getStringArrayExtra("pzndata")) {
	    // TODO get from list
	    data.add(createItem(s, "Generic Item GEN"));
	}

	simpleAdpt = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
	    new String[] { "name", "pzn" }, new int[] { android.R.id.text1, android.R.id.text2 });
	lv.setAdapter(simpleAdpt);
	registerForContextMenu(lv);

	// Show the Up button in the action bar.
	setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

	getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	if ((item.getItemId() == 2) && (item.getGroupId() == 1)) {
	    // delete
	    data.remove(simpleAdpt.getItem(currentPos));
	    simpleAdpt.notifyDataSetChanged();

	    Toast.makeText(this, "Medikament wurde entfernt", Toast.LENGTH_LONG).show();
	    return true;
	}
	return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.medication, menu);
	return true;
    }

    public void onNewMedication(View view) {
	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	intent.putExtra("SCAN_MODE", "BAR_CODE_MODE");
	startActivityForResult(intent, 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    saveData();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
	super.onBackPressed();
	saveData();
    }

    private void saveData() {
	Intent data = new Intent();

	String[] pzn = new String[this.data.size()];
	int c = 0;
	for (Map<String, String> m : this.data) {
	    pzn[c++] = m.get("pzn");
	}

	data.putExtra("PZNList", pzn);

	if (getParent() == null) {
	    setResult(Activity.RESULT_OK, data);
	} else {
	    getParent().setResult(Activity.RESULT_OK, data);
	}
	finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	Log.d(TAG, "got something...");

	// D/at.reox.emergency.MedicationActivity( 8862): contents: -2425979 ... format: CODE_39

	if (requestCode == 0) {
	    if (resultCode == RESULT_OK) {

		String contents = intent.getStringExtra("SCAN_RESULT");
		String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

		if (format.equals("CODE_39") && contents.startsWith("-")
		    && (contents.length() == 8)) {
		    // should have a proper PZN Code here...
		    data.add(createItem(contents.substring(1), "Unknown Medication"));
		    simpleAdpt.notifyDataSetChanged();
		}

		Log.d(TAG, "contents: " + contents + " ... format: " + format);
		// Handle successful scan
	    } else if (resultCode == RESULT_CANCELED) {
		// Handle cancel
		// do nothing?
	    }
	}
    }

    private int currentPos = -1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

	super.onCreateContextMenu(menu, v, menuInfo);
	AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

	// We know that each row in the adapter is a Map
	HashMap<String, String> map = (HashMap<String, String>) simpleAdpt.getItem(aInfo.position);

	menu.setHeaderTitle(map.get("name"));
	menu.add(1, 2, 2, "Delete");
	currentPos = aInfo.position;

    }

    private HashMap<String, String> createItem(String pzn, String name) {
	HashMap<String, String> item = new HashMap<String, String>();
	item.put("pzn", pzn);
	item.put("name", name);
	return item;
    }

}

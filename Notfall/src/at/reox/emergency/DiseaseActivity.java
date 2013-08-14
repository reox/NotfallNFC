package at.reox.emergency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import at.reox.emergency.tools.IcdObject;

public class DiseaseActivity extends Activity {

    private final String TAG = getClass().getName();
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    SimpleAdapter simpleAdpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_disease);
	// Show the Up button in the action bar.
	setupActionBar();

	// Read ICD10 Codes
	List<IcdObject> codes = new ArrayList<IcdObject>();
	HashMap<String, String> lookup = new HashMap<String, String>();
	try {
	    InputStream input = getAssets().open("icd10.txt");
	    BufferedReader br = new BufferedReader(new InputStreamReader(input));

	    String line;
	    while ((line = br.readLine()) != null) {
		String[] icd = line.split("\\|");
		codes.add(new IcdObject(icd[0], icd[1]));
		lookup.put(icd[0], icd[1]);
	    }

	    input.close();
	} catch (IOException e) {
	    // TODO
	    e.printStackTrace();
	}

	ListView lv = (ListView) findViewById(R.id.diseaseList);
	Intent intent = getIntent();

	for (String s : intent.getStringArrayExtra("icddata")) {
	    data.add(createItem(s, lookup.get(s)));
	}

	simpleAdpt = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
	    new String[] { "name", "icd" }, new int[] { android.R.id.text1, android.R.id.text2 });
	lv.setAdapter(simpleAdpt);

	Log.d(TAG, "Read " + codes.size() + " Items");

	final ArrayAdapter<IcdObject> adapter = new ArrayAdapter<IcdObject>(this,
	    android.R.layout.select_dialog_item, codes);
	AutoCompleteTextView at = (AutoCompleteTextView) findViewById(R.id.diseaseAutoComplete);
	at.setThreshold(3);
	at.setAdapter(adapter);
	at.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		currentObject = adapter.getItem(arg2);
	    }

	});
    }

    private IcdObject currentObject;

    public void onAdd(View v) {
	if (currentObject != null) {
	    data.add(createItem(currentObject.getCode(), currentObject.getName()));
	    simpleAdpt.notifyDataSetChanged();
	    currentObject = null;
	    ((AutoCompleteTextView) findViewById(R.id.diseaseAutoComplete)).setText("");
	}
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

	getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.disease, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    // This ID represents the Home or Up button. In the case of this
	    // activity, the Up button is shown. Use NavUtils to allow users
	    // to navigate up one level in the application structure. For
	    // more details, see the Navigation pattern on Android Design:
	    //
	    // http://developer.android.com/design/patterns/navigation.html#up-vs-back
	    //
	    NavUtils.navigateUpFromSameTask(this);
	    finish();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    private HashMap<String, String> createItem(String pzn, String name) {
	HashMap<String, String> item = new HashMap<String, String>();
	item.put("icd", pzn);
	item.put("name", name);
	return item;
    }

}

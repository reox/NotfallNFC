package at.reox.emergency;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import at.reox.emergency.tools.EmergencyData;

public class ListContentFragment extends Fragment {
    private String mText;

    @Override
    public void onAttach(Activity activity) {
	// This is the first callback received; here we can set the text for
	// the fragment as defined by the tag specified during the fragment transaction
	super.onAttach(activity);
	if ((getTag() != null)) {
	    mText = getTag();
	}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// This is called to define the layout for the fragment;
	// we just create a TextView and set its text to be the fragment tag
	Log.d("foobar", mText + "  ");

	// Fill up this LinearLayout
	LinearLayout l = new LinearLayout(getActivity());
	l.setOrientation(LinearLayout.VERTICAL);
	l.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
	    LayoutParams.MATCH_PARENT));

	if (mText.equals("wait")) {
	    // return waiting logo...
	    l.addView(inflater.inflate(R.layout.fragment_wait, container, false));
	    return l;
	}
	if (mText.equals("place")) {
	    l.addView(inflater.inflate(R.layout.fragment_notag, container, false));
	    return l;
	}
	if (mText.equals("done")) {
	    l.addView(inflater.inflate(R.layout.fragment_done, container, false));
	    return l;
	}

	if (((EmergencyApplication) getActivity().getApplication()).isTagLoaded()) {
	    EmergencyData d = ((EmergencyApplication) getActivity().getApplication()).getTag();
	    // load the ui according to our list of planned features
	    // We need these containers:
	    // * Patient Data (Name, Surname, Age) --> All Views
	    // * patient Extra text, if set --> All Views
	    // * Advanced Patient Data (SVNR, Address, Bloodgroup, Organ Donor) --> Identify, Medic
	    // * Medication --> Medic
	    // * Diseases --> Medic, Paramedic

	    // Inflate Data that everyone needs
	    int icounter = 0;
	    LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    LinearLayout lb = (LinearLayout) inflater.inflate(R.layout.fragment_patientdata, null);
	    l.addView(lb, icounter++, p);
	    ((TextView) l.findViewById(R.id.patient_pname)).setText(d.getName());
	    ((TextView) l.findViewById(R.id.patient_surname)).setText(d.getSurname());
	    ((TextView) l.findViewById(R.id.patient_age)).setText(d.getAge() + " Jahre");
	    ((TextView) l.findViewById(R.id.patient_extra)).setText(d.getExtra());

	    // Load Specific views
	    if (mText.equals("Sanitäter") || mText.equals("Notarzt")) {
		// Load Diseases
		l.addView(inflater.inflate(R.layout.fragment_dislist, null), icounter++, p);
		ListView lv = (ListView) l.findViewById(R.id.list_dislist);
		SimpleAdapter simpleAdpt = new SimpleAdapter(l.getContext(), d.getDiseases(),
		    android.R.layout.simple_list_item_2, new String[] { "value", "key" },
		    new int[] { android.R.id.text1, android.R.id.text2 });
		lv.setAdapter(simpleAdpt);

	    }
	    if (mText.equals("Notarzt")) {
		// Load Medication
		l.addView(inflater.inflate(R.layout.fragment_medlist, null), icounter++, p);
		ListView lv = (ListView) l.findViewById(R.id.list_medlist);
		SimpleAdapter simpleAdpt = new SimpleAdapter(l.getContext(), d.getMedication(),
		    android.R.layout.simple_list_item_2, new String[] { "value", "key" },
		    new int[] { android.R.id.text1, android.R.id.text2 });
		lv.setAdapter(simpleAdpt);

	    }
	    if (mText.equals("Identifizierung") || mText.equals("Notarzt")) {
		// Load Advanced Patient Data
		LinearLayout li = (LinearLayout) inflater.inflate(R.layout.fragment_identify, null);
		l.addView(li, icounter++, p);
		((TextView) l.findViewById(R.id.ident_address)).setText(d.getAddress());
		((TextView) l.findViewById(R.id.ident_svnr)).setText(d.getSvnr());
		((TextView) l.findViewById(R.id.ident_bloodgroup)).setText(d.getBloodgroupString());
		((TextView) l.findViewById(R.id.ident_organdonor)).setText((d.isOrganDonor() ? "JA"
		    : "NEIN"));
	    }

	    TextView update = new TextView(l.getContext());
	    update.setText("Tag vom: "
		+ new SimpleDateFormat("dd.MM.yyyy (H:mm)").format(d.getUpdate()));
	    l.addView(update);

	} else {
	    // Show a default menu for waiting until a tag is scanned...
	    l.addView(inflater.inflate(R.layout.fragment_notag, container, false));
	}

	return l;
    }
}

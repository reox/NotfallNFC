package at.reox.emergency;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

	// We need these containers:
	// * Patient Data (Name, Surname, Age) --> All Views
	// * patient Extra text, if set --> All Views
	// * Advanced Patient Data (SVNR, Address, Bloodgroup, Organ Donor) --> Identify, Medic
	// * Medication --> Medic
	// * Diseases --> Medic, Paramedic

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
	    Log.d("foobar", "Tag is loaded...");
	    EmergencyData d = ((EmergencyApplication) getActivity().getApplication()).getTag();
	    // load the ui according to our list of planned features
	    Log.d("foobar", "The Tag: " + d);

	    l.addView(inflater.inflate(R.layout.fragment_patientdata, container, false));
	    Log.d("foobar", d.getName() + " " + d.getSurname() + " " + d.getAge());

	    ((TextView) l.findViewById(R.id.patient_name)).setText(d.getName());
	    ((TextView) l.findViewById(R.id.patient_surname)).setText(d.getSurname());
	    ((TextView) l.findViewById(R.id.patient_age)).setText(d.getAge() + " Jahre");

	} else {
	    // Show a default menu for waiting until a tag is scanned...
	    l.addView(inflater.inflate(R.layout.fragment_notag, container, false));
	}

	return l;
    }
}

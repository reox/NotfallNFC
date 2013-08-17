package at.reox.emergency;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListContentFragment extends Fragment {
    private String mText;

    @Override
    public void onAttach(Activity activity) {
	// This is the first callback received; here we can set the text for
	// the fragment as defined by the tag specified during the fragment transaction
	super.onAttach(activity);
	mText = getTag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// This is called to define the layout for the fragment;
	// we just create a TextView and set its text to be the fragment tag
	Log.d("foobar", mText + "  ");
	View rootView;
	if ((mText == null) || (mText.equals("Sanitäter"))) {
	    rootView = inflater.inflate(R.layout.fragment_paramedic, container, false);
	} else if (mText.equals("Arzt")) {
	    rootView = inflater.inflate(R.layout.fragment_medic, container, false);
	} else {
	    rootView = inflater.inflate(R.layout.fragment_identify, container, false);
	}

	return rootView;
    }

}

package at.reox.emergency;

import android.app.Application;
import at.reox.emergency.tools.EmergencyData;

public class EmergencyApplication extends Application {

    private byte[] data;
    private boolean isTagLoaded = false;
    private EmergencyData d;

    public boolean isTagLoaded() {
	return isTagLoaded;
    }

    public EmergencyData getTag() {
	return d;
    }

    public void loadTag(EmergencyData d) {
	isTagLoaded = true;
	this.d = d;
    }

    public void unloadTag() {
	isTagLoaded = false;
	d = null;
    }

    public void setData(byte[] data) {
	this.data = data;
    }

    public byte[] getData() {
	return data;
    }

}

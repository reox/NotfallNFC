package at.reox.emergency;

import android.app.Application;

public class EmergencyApplication extends Application {

    private byte[] data;

    public void setData(byte[] data) {
	this.data = data;
    }

    public byte[] getData() {
	return data;
    }
}

package at.reox.emergency.tools;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class EmergencyData {

    private final String TAG = this.getClass().getName();

    private String surname;
    private String name;
    private String address;
    private String svnr;
    private Date update;
    private String extra;

    private final List<String> PZN;
    private final List<String> ICD;

    public final byte TYPE_MEDICATION = 0x01;
    public final byte TYPE_DISEASE = 0x02;

    public final int HAS_EXTRA_DATA = 0x01;
    public final int HAS_MEDICATION_DATA = 0x01 << 1;
    public final int HAS_DISEASE_DATA = 0x01 << 2;

    public EmergencyData() {
	surname = "";
	name = "";
	address = "";
	svnr = "";
	extra = "";

	PZN = new ArrayList<String>();
	ICD = new ArrayList<String>();
    }

    public void addPZN(String pzn) {
	PZN.add(pzn);
    }

    public void addICD(String icd) {
	ICD.add(icd);
    }

    public void removePZN(String pzn) {
	PZN.remove(pzn);
    }

    public void removeICD(String icd) {
	ICD.remove(icd);
    }

    public String getSurname() {
	return surname;
    }

    public void setSurname(String surname) {
	this.surname = surname;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public String getSvnr() {
	return svnr;
    }

    public void setSvnr(String svnr) {
	this.svnr = svnr;
    }

    public String getExtra() {
	return extra;
    }

    public void setExtra(String extra) {
	this.extra = extra;
    }

    public Date getUpdate() {
	return update;
    }

    public byte[] getBinaryData() {
	ByteBuffer b = ByteBuffer.allocate(256);

	int header = 0x0000;

	if (extra.length() > 0) {
	    header |= HAS_EXTRA_DATA;
	}
	if (PZN.size() > 0) {
	    header |= HAS_MEDICATION_DATA;
	}
	if (ICD.size() > 0) {
	    header |= HAS_DISEASE_DATA;
	}

	b.putInt(header);

	// add the name
	b.putInt(name.length());
	b.put(name.getBytes());
	b.putShort((short) 0x00);

	// add the surname
	b.putInt(surname.length());
	b.put(surname.getBytes());
	b.putShort((short) 0x00);

	// Add the address
	b.putInt(address.length());
	b.put(address.getBytes());
	b.putShort((short) 0x00);

	// TODO proper length check
	b.put(svnr.getBytes());

	// TODO add the medication/disease data here

	if (extra.length() > 0) {
	    b.putInt(extra.length());
	    b.put(extra.getBytes());
	    b.putShort((short) 0x00);
	}

	// TODO add the update Date here

	// adding a CRC of the whole Array
	int crc = CRC.crc16(Arrays.copyOf(b.array(), b.position()));
	Log.d(TAG, "Calculating CRC: " + crc);
	b.putInt(crc);

	byte[] raw = new byte[b.position()];
	raw = Arrays.copyOf(b.array(), b.position());

	Log.d(TAG, "buffer length " + raw.length);

	return raw;
    }

    final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	'A', 'B', 'C', 'D', 'E', 'F' };

    public static String getHex(byte[] bytes) {
	char[] hexChars = new char[bytes.length * 2];
	int v;
	for (int j = 0; j < bytes.length; j++) {
	    v = bytes[j] & 0xFF;
	    hexChars[j * 2] = hexArray[v >>> 4];
	    hexChars[(j * 2) + 1] = hexArray[v & 0x0F];
	}
	return new String(hexChars);
    }

}

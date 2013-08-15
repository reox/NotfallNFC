package at.reox.emergency.tools;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

public class EmergencyData {

    private final String TAG = this.getClass().getName();

    private final char[] chars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
	'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    private String surname;
    private String name;
    private String address;
    private String svnr;
    private Date update;
    private String extra;
    private int sex;
    private boolean organdonor = false;

    private int bloodgroup = BLOOD_UNKNOWN;
    private int rhesus = RHESUS_UNKNOWN;
    private int kell = KELL_UNKNOWN;

    public static final int BLOOD_UNKNOWN = 0;
    public static final int BLOOD_A = 1;
    public static final int BLOOD_B = 2;
    public static final int BLOOD_AB = 3;
    public static final int BLOOD_0 = 4;

    public static final int RHESUS_UNKNOWN = 0;
    public static final int RHESUS_POS = 1;
    public static final int RHESUS_NEG = 2;

    public static final int KELL_UNKNOWN = 0;
    public static final int KELL_POS = 1;
    public static final int KELL_NEG = 2;

    public static final int MALE = 0;
    public static final int FEMALE = 1;

    private final Set<String> PZN;
    private final Set<String> ICD;

    public final byte TYPE_EXTRA = 0x01;
    public final byte TYPE_MEDICATION = 0x01 << 1;
    public final byte TYPE_DISEASE = 0x01 << 2;

    public final int HAS_EXTRA_DATA = 0x01;
    public final int HAS_MEDICATION_DATA = 0x01 << 1;
    public final int HAS_DISEASE_DATA = 0x01 << 2;

    public EmergencyData() {

	surname = "";
	name = "";
	address = "";
	svnr = "";
	extra = "";
	sex = MALE;

	PZN = new HashSet<String>();
	ICD = new HashSet<String>();

	setUpdate();
    }

    public boolean isOrganDonor() {
	return organdonor;
    }

    public void setOrganDonor(boolean organdonor) {
	this.organdonor = organdonor;
    }

    public void setSex(int sex) {
	this.sex = sex;
    }

    public int getSex() {
	return sex;
    }

    public int getBloodgroup() {
	return bloodgroup;
    }

    public void setBloodgroup(int bloodgroup) {
	this.bloodgroup = bloodgroup;
    }

    public int getRhesus() {
	return rhesus;
    }

    public void setRhesus(int rhesus) {
	this.rhesus = rhesus;
    }

    public int getKell() {
	return kell;
    }

    public void setKell(int kell) {
	this.kell = kell;
    }

    public void setUpdate() {
	update = Calendar.getInstance().getTime();
    }

    public void addPZN(String pzn) {
	PZN.add(pzn);
    }

    public void addPZN(String[] pzn) {
	for (String s : pzn) {
	    PZN.add(s);
	}
    }

    public void addPZN(Set<String> pzn) {
	PZN.addAll(pzn);
    }

    public void addICD(String icd) {
	ICD.add(icd);
    }

    public void addICD(Set<String> icd) {
	ICD.addAll(icd);
    }

    public void removePZN(String pzn) {
	PZN.remove(pzn);
    }

    public void removeICD(String icd) {
	ICD.remove(icd);
    }

    public void clearPZN() {
	PZN.clear();
    }

    public void clearICD() {
	ICD.clear();
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

    public Set<String> getPZN() {
	return PZN;
    }

    public Set<String> getICD() {
	return ICD;
    }

    public Date getUpdate() {
	return update;
    }

    public byte[] getBinaryData() {
	ByteBuffer b = ByteBuffer.allocate(256);

	// Create header
	byte header = 0x00;

	if (extra.length() > 0) {
	    header |= HAS_EXTRA_DATA;
	}
	if (PZN.size() > 0) {
	    header |= HAS_MEDICATION_DATA;
	}
	if (ICD.size() > 0) {
	    header |= HAS_DISEASE_DATA;
	}

	b.put(header);

	// Flags
	byte flags = 0x00;
	flags |= (sex & 0x1);
	flags |= (organdonor ? 1 << 1 : 0 << 1);
	b.put(flags);

	// add the name
	b.putShort((short) name.length());
	b.put(name.getBytes());
	b.put((byte) 0x00);

	// add the surname
	b.putShort((short) surname.length());
	b.put(surname.getBytes());
	b.put((byte) 0x00);

	// Add the address
	b.putShort((short) address.length());
	b.put(address.getBytes());
	b.put((byte) 0x00);

	// svnr is always nnnX DDMMYY.
	// so we can use an 33 bit = 5 Byte value here!
	if (svnr.equals("")) {
	    b.put((byte) 0x00);
	    b.putInt(0x00);
	} else {
	    long number = Long.parseLong(svnr);
	    b.put((byte) ((number >> 32) & 0xFF)); // strip off first byte
	    b.putInt((int) (number & 0xFFFFFFFF)); // get 32bit
	}

	// Bloodgroup
	// we have one byte for that...
	// 4 bit for the bloodgroup, 2 bit each for rhesus and kell.
	// if one of these is 0, its unknown.
	Log.d(TAG, "Calculating Bloodgroup: "
	    + getHex(new byte[] { (byte) ((bloodgroup << 4) | (rhesus << 2) | kell) }));
	b.put((byte) ((bloodgroup << 4) | (rhesus << 2) | kell));

	if (PZN.size() > 0) {
	    b.put(TYPE_MEDICATION);
	    b.putShort((short) PZN.size());
	    // PZN has 8 integers, old ones have 7, add padding 0 then...
	    // that means we need 27 bits for the number...
	    // so for easy access we store it as 4byte integer
	    for (String s : PZN) {
		b.putInt(Integer.parseInt(s));
	    }
	}
	// TODO add the disease data here
	if (ICD.size() > 0) {
	    b.put(TYPE_DISEASE);
	    b.putShort((short) ICD.size());
	    for (String s : ICD) {
		// first byte is for character
		b.put((byte) Arrays.asList(chars).indexOf(s.charAt(0)));

		String[] numbers = s.substring(1).split("\\.");
		int[] n = new int[2];
		Arrays.fill(n, 0x7F); // Default Value if not set
		for (int i = 0; i < numbers.length; i++) {
		    n[i] = Integer.parseInt(numbers[i]);
		}
		for (int i : n) {
		    b.put((byte) i);
		}

	    }
	}

	if (extra.length() > 0) {
	    b.put(TYPE_EXTRA);
	    b.putInt(extra.length());
	    b.put(extra.getBytes());
	    b.put((byte) 0x00);
	}

	b.putLong(update.getTime());

	// adding a CRC of the whole Array
	int crc = CRC.crc16(Arrays.copyOf(b.array(), b.position()));
	Log.d(TAG, "Calculating CRC: " + crc);
	b.putShort((short) (crc & 0xFFFF));

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

    public void addICD(String[] icd2) {
	for (String s : icd2) {
	    ICD.add(s);
	}
    }

}

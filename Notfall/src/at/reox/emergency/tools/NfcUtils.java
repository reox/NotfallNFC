package at.reox.emergency.tools;

import java.io.IOException;
import java.util.Arrays;

import android.nfc.FormatException;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

public class NfcUtils {

    private static final String TAG = "NfcUtils";

    public static byte[] read(Tag tag) throws IOException, FormatException {
	byte[] data = new byte[64 * 4]; // FIXME should be generic for other kinds of tags
	Arrays.fill(data, (byte) 0x0);
	if (tag == null) {
	    return data;
	}
	NfcV nfc = NfcV.get(tag);
	byte[] ID = tag.getId();

	nfc.connect();

	byte[] arrByte = new byte[11];
	// Flags
	arrByte[0] = 0x22; // 0x20 = Addressed Mode, 0x02 = Fast Mode
	// Command
	arrByte[1] = 0x20; // read single block
	// ID
	System.arraycopy(ID, 0, arrByte, 2, 8);

	for (int i = 0; i < 64; i++) {
	    // block number
	    arrByte[10] = (byte) (i);
	    byte[] result = nfc.transceive(arrByte);
	    System.arraycopy(result, 1, data, i * 4, 4);
	}

	nfc.close();
	return data;
    }

    public static boolean verify(NfcV nfc, int block, byte[] data) {

	try {
	    byte[] arrByte = new byte[3];
	    // Flags
	    arrByte[0] = 0x02; // 0x20 = Addressed Mode, 0x02 = Fast Mode
	    // Command
	    arrByte[1] = 0x20; // read single block

	    arrByte[2] = (byte) (block);
	    byte[] result = nfc.transceive(arrByte);

	    for (int i = 0; i < 4; i++) {
		Log.d(
		    TAG,
		    "Verify read " + Integer.toHexString(result[i]) + "should "
			+ Integer.toHexString(data[i]));
		if (result[i] != data[i]) {
		    return false;
		}
	    }

	    return true;
	} catch (IOException e) {
	    return false;
	}

    }

    public static void write(Tag tag, byte[] data) throws IOException, FormatException,
	InterruptedException {
	if (tag == null) {
	    return;
	}
	NfcV nfc = NfcV.get(tag);

	nfc.connect();

	Log.d(TAG, "Max Transceive Bytes: " + nfc.getMaxTransceiveLength());

	// NfcV Tag has 64 Blocks with 4 Byte
	if ((data.length / 4) > 64) {
	    // ERROR HERE!
	    Log.d(TAG, "too much data...");
	}

	if ((data.length % 4) != 0) {
	    byte[] ndata = new byte[(data.length) + (4 - (data.length % 4))];
	    Arrays.fill(ndata, (byte) 0x00);
	    System.arraycopy(data, 0, ndata, 0, data.length);
	    data = ndata;
	}

	byte[] arrByte = new byte[7];
	// Flags
	arrByte[0] = 0x42;
	// Command
	arrByte[1] = 0x21;

	for (int i = 0; i < (data.length / 4); i++) {

	    // block number
	    arrByte[2] = (byte) (i);

	    // data, DONT SEND LSB FIRST!
	    arrByte[3] = data[(i * 4)];
	    arrByte[4] = data[(i * 4) + 1];
	    arrByte[5] = data[(i * 4) + 2];
	    arrByte[6] = data[(i * 4) + 3];

	    Log.d(TAG, "Writing Data to block " + i + " [" + printHexString(arrByte) + "]");
	    try {
		nfc.transceive(arrByte);
	    } catch (IOException e) {
		if (e.getMessage().equals("Tag was lost.")) {
		    // continue, because of Tag bug
		} else {
		    throw e;
		}
	    }
	}

	nfc.close();
    }

    public static byte[] getInfoRmation(Tag t) throws IOException {
	NfcV mNfcV = NfcV.get(t);
	byte[] ID = t.getId();

	mNfcV.connect();

	byte[] cmd = new byte[10];
	cmd[0] = (byte) 0x22; // flag
	cmd[1] = (byte) 0x2B; // command
	System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
	byte[] infoRmation = mNfcV.transceive(cmd);
	Log.d(TAG, "RMATION: " + printHexString(infoRmation));
	byte blockNumber = infoRmation[12];
	byte oneBlockSize = infoRmation[13];

	Log.d(TAG, "infoRmation: " + Arrays.toString(infoRmation));
	Log.d(TAG, "blockNumber: " + blockNumber + " -- oneBlockSize: " + oneBlockSize);
	mNfcV.close();
	return infoRmation;
    }

    public static String printHexString(byte[] data) {
	StringBuffer s = new StringBuffer();
	;
	for (int i = 0; i < data.length; i++) {
	    String hex = Integer.toHexString(data[i] & 0xFF);
	    if (hex.length() == 1) {
		hex = '0' + hex;
	    }
	    s.append("0x" + hex + ", ");
	}
	return s.toString().substring(0, s.length() - 2);
    }

}

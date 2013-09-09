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
	if (tag == null) {
	    return null;
	}
	NfcV nfc = NfcV.get(tag);

	nfc.connect();

	byte[] cmd = new byte[2];
	cmd[0] = (byte) 0x02; // flag
	cmd[1] = (byte) 0x2B; // command
	byte[] infoRmation = nfc.transceive(cmd);
	int blockNumber = infoRmation[12] + 1;
	int oneBlockSize = infoRmation[13] + 1;
	byte[] data = new byte[blockNumber * oneBlockSize];

	Arrays.fill(data, (byte) 0x0);

	byte[] arrByte = new byte[3];
	// Flags
	arrByte[0] = 0x02; // 0x20 = Addressed Mode, 0x02 = Fast Mode
	// Command
	arrByte[1] = 0x20; // read single block
	// ID
	for (int i = 0; i < blockNumber; i++) {
	    // block number
	    arrByte[2] = (byte) (i);
	    byte[] result = nfc.transceive(arrByte);
	    if (result[0] != 0) {
		throw new IOException("Data Read was unsuccessfull");
	    }
	    System.arraycopy(result, 1, data, i * oneBlockSize, oneBlockSize);
	}

	nfc.close();
	return data;
    }

    public static void write(Tag tag, byte[] data) throws IOException, FormatException {
	if (tag == null) {
	    return;
	}
	NfcV nfc = NfcV.get(tag);

	nfc.connect();

	Log.d(TAG, "Max Transceive Bytes: " + nfc.getMaxTransceiveLength());

	byte[] cmd = new byte[2];
	cmd[0] = (byte) 0x02; // flag
	cmd[1] = (byte) 0x2B; // command
	byte[] infoRmation = nfc.transceive(cmd);
	int blockNumber = infoRmation[12] + 1;
	int oneBlockSize = infoRmation[13] + 1;

	// NfcV Tag has 64 Blocks with 4 Byte
	if ((data.length / oneBlockSize) > blockNumber) {
	    throw new IOException("Data is too big");
	}

	if ((data.length % oneBlockSize) != 0) {
	    byte[] ndata = new byte[(data.length) + (oneBlockSize - (data.length % oneBlockSize))];
	    Arrays.fill(ndata, (byte) 0x00);
	    System.arraycopy(data, 0, ndata, 0, data.length);
	    data = ndata;
	}

	byte[] arrByte = new byte[3 + oneBlockSize];
	// Flags

	// The texas instruments (E00780...) needs special flag here, need to add 0x40
	byte[] id = tag.getId();

	if (((id[7] & 0xFF) == 0xe0) && ((id[6] & 0xFF) == 0x07) && ((id[5] & 0xFF) == 0x80)) {
	    arrByte[0] = 0x42;
	} else {
	    arrByte[0] = 0x02;
	}
	Log.d(TAG, "Set command to " + Integer.toHexString(arrByte[0]));
	// Command
	arrByte[1] = 0x21;

	for (int i = 0; i < (data.length / oneBlockSize); i++) {
	    // block numberAction Processed.Document notification sent successfully!

	    arrByte[2] = (byte) (i);
	    // data
	    System.arraycopy(data, i * oneBlockSize, arrByte, 3, oneBlockSize);

	    Log.d(TAG, "Writing Data to block " + i + " [" + printHexString(arrByte) + "]");
	    try {
		// Result will probably never get anywhere ...
		byte[] result = nfc.transceive(arrByte);

		Log.d(TAG, Arrays.toString(result) + "Wrote Data to Tag");
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

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
	    System.arraycopy(result, 0, data, i * 4, 4);
	}

	nfc.close();
	return data;
    }

    public static void write(Tag tag, byte[] data) throws IOException, FormatException {
	if (tag == null) {
	    return;
	}
	NfcV nfc = NfcV.get(tag);
	byte[] ID = tag.getId();

	nfc.connect();

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

	for (int i = 0; i < (data.length / 4); i++) {
	    byte[] arrByte = new byte[17];

	    // Flags
	    arrByte[0] = (byte) 0x22; // Tag only supports flags = 0
	    // Command
	    arrByte[1] = 0x21;
	    // ID
	    System.arraycopy(ID, 0, arrByte, 2, 8);
	    // block number
	    arrByte[10] = (byte) (i);

	    // data
	    arrByte[11] = data[(i * 4) + 3];
	    arrByte[12] = data[(i * 4) + 2];
	    arrByte[13] = data[(i * 4) + 1];
	    arrByte[14] = data[(i * 4)];

	    // CRC 16 of all command
	    byte[] check = new byte[15];
	    System.arraycopy(arrByte, 0, check, 0, 15);
	    int crc = CRC.crc16(check);
	    arrByte[15] = (byte) (crc >>> 8);
	    arrByte[16] = (byte) (crc & 0xFF);

	    Log.d(TAG, "Writing Data to block " + i + " [" + printHexString(arrByte) + "]");

	    // byte[] result = nfc.transceive(arrByte);
	    // Log.d(TAG, "got result: " + Arrays.toString(result));
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

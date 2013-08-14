package at.reox.emergency;

import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class NFCWriteActivity extends Activity {

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_nfcwrite);

    }

    @Override
    protected void onResume() {
	super.onResume();
	Intent intent = getIntent();

	byte[] data = intent.getByteArrayExtra("binarydata");
	if (data != null) {
	    ((EmergencyApplication) getApplication()).setData(data);
	    Log.d(TAG, "found data...");
	    TextView t = (TextView) findViewById(R.id.sizeInformation);
	    t.setText("Minimum Tag Size required: " + data.length + "Bytes");
	}

	Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	if (tag != null) {
	    Log.d(TAG, "Got a tag that supports these Tech: " + Arrays.toString(tag.getTechList()));
	    try {
		Log.d(TAG, "Read a whole Tag: " + Arrays.toString(read(tag)));
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    } catch (FormatException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }

	    Log.d(TAG, "Found a NFC Tag: " + tag);
	    try {
		write(tag);
		Toast.makeText(this, "Wrote to tag!", Toast.LENGTH_LONG).show();
	    } catch (Exception e) {
		Toast.makeText(this, "Error...: " + e.getMessage() + " --- " + e.toString(),
		    Toast.LENGTH_LONG).show();
		Log.e(TAG, "Exception", e);
	    }
	}
    }

    private byte[] read(Tag tag) throws IOException, FormatException {
	byte[] data = new byte[64 * 4];
	Arrays.fill(data, (byte) 0x0);
	if (tag == null) {
	    return data;
	}
	NfcV nfc = NfcV.get(tag);
	byte[] ID = tag.getId();

	nfc.connect();

	for (int i = 0; i < 64; i++) {
	    byte[] arrByte = new byte[11];

	    // Flags
	    arrByte[0] = 0x22; // 0x20 = Addressed Mode, 0x02 = Fast Mode
	    // Command
	    arrByte[1] = 0x20; // read single block
	    // ID
	    System.arraycopy(ID, 0, arrByte, 2, 8);
	    // block number
	    arrByte[10] = (byte) (i);
	    byte[] result = nfc.transceive(arrByte);
	    System.arraycopy(result, 0, data, i * 4, 4);
	}

	nfc.close();
	return data;
    }

    private void write(Tag tag) throws IOException, FormatException {
	if (tag == null) {
	    return;
	}
	getInfoRmation(tag);
	NfcV nfc = NfcV.get(tag);
	byte[] ID = tag.getId();

	nfc.connect();

	Log.d(TAG, "Data: " + new String(((EmergencyApplication) getApplication()).getData()));

	byte[] data = ((EmergencyApplication) getApplication()).getData();
	// NfcV Tag has 64 Blocks with 4 Byte
	if ((data.length / 4) > 64) {
	    // ERROR HERE!
	    Log.d(TAG, "too much data...");
	}

	for (int i = 0; i < data.length; i++) {
	    byte[] arrByte = new byte[15];

	    // Flags
	    arrByte[0] = (byte) 0x22; // Tag only supports flags = 0
	    // Command
	    arrByte[1] = 0x21;
	    // ID
	    Log.d(TAG, "Found ID length: " + ID.length + "... ID: " + Arrays.toString(ID));
	    System.arraycopy(ID, 0, arrByte, 2, 8);
	    // block number
	    arrByte[10] = (byte) (i);

	    // data
	    // TODO send LSB first...
	    System.arraycopy(data, i * 4, arrByte, 11, 4);

	    // CRC 16 of all command
	    byte[] check = new byte[15];
	    System.arraycopy(arrByte, 0, check, 0, 15);
	    // int crc = CRC.crc16(check);
	    // arrByte[16] = (byte) (crc >> 8);
	    // arrByte[15] = (byte) (crc & 0xFF);

	    Log.d(TAG, "Writing Data: " + Arrays.toString(arrByte));

	    byte[] result = nfc.transceive(arrByte);
	    Log.d(TAG, "got result: " + Arrays.toString(result));
	}

	nfc.close();
	Toast.makeText(this, "wrote to tag", Toast.LENGTH_LONG).show();

    }

    private byte[] getInfoRmation(Tag t) throws IOException {
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

    private String printHexString(byte[] data) {
	StringBuffer s = new StringBuffer();
	;
	for (int i = 0; i < data.length; i++) {
	    String hex = Integer.toHexString(data[i] & 0xFF);
	    if (hex.length() == 1) {
		hex = '0' + hex;
	    }
	    s.append(hex);
	}
	return s.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.nfcwrite, menu);
	return true;
    }

}

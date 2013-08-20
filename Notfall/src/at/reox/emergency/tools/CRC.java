package at.reox.emergency.tools;

public class CRC {

    private static final char POLYNOMIAL = 0x8408;
    private static final char PRESET_VALUE = 0xFFFF;

    public static int crc16(byte[] data) {
	int current_crc_value = PRESET_VALUE;
	for (int i = 0; i < data.length; i++) {
	    int nxor = data[i];
	    current_crc_value ^= (nxor & 0xFF);
	    for (int j = 0; j < 8; j++) {
		if ((current_crc_value & 1) != 0) {
		    current_crc_value = (current_crc_value >>> 1) ^ POLYNOMIAL;
		} else {
		    current_crc_value = current_crc_value >>> 1;
		}
	    }
	}
	current_crc_value = (~current_crc_value) & 0xFFFF;

	return current_crc_value;
    }
}

package com.gen.mube.utils;

import java.io.IOException;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;

public class NfcUtils {
	
	private NfcUtils () {}
	
	public static final String[][] TECH_LIST = new String[][]{
		{
			android.nfc.tech.NfcA.class.getName(),
			android.nfc.tech.NfcB.class.getName(),
			android.nfc.tech.IsoDep.class.getName(),
			android.nfc.tech.NfcV.class.getName(),
			android.nfc.tech.NfcF.class.getName(),
		}
	};
	
	public static String getNfcId (Intent intent) {
		byte[] nfcTagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		return bytesToTagId(nfcTagId);
	}
	
	public static String getNfcText (Intent intent) {
		
		Parcelable[] nfcMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (nfcMessages == null) return "";
		
		NdefMessage[] messages = new NdefMessage[nfcMessages.length];
		String nfcText = "";
		
		for (int i=0; i<nfcMessages.length; i++) {
			messages[i] = (NdefMessage) nfcMessages[i];
			for (NdefRecord record : messages[i].getRecords()) {
				byte[] payload = record.getPayload();
				nfcText += bytesToNfcText(payload);
			}
		}
		
		return nfcText;
	}
	
	public static void writeNfcText (Intent intent, String text) {
		NdefRecord record   = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, "text/plain".getBytes(), new byte[]{}, text.getBytes());
		NdefMessage message = new NdefMessage(new NdefRecord[]{record});
		
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(tag);
		try {
			ndef.connect();
			ndef.writeNdefMessage(message);
			ndef.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean enableNfc (NfcAdapter adapter) {
		if (adapter == null) 	  return false;
		if (!adapter.isEnabled()) return false;
		return true;
	}
	
	private static String bytesToTagId (byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		
		for (byte b : bytes) {
			if (isFirst) isFirst = false;
			else 		 builder.append("-");
			
			// 負の場合があるので0xffを加える。
			builder.append(Integer.toString(b & 0xff));
		}
		return builder.toString();
	} 
	
	private static String bytesToNfcText (byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			// 負の場合があるので0xffを加える。
			builder.append(String.format("%c", b & 0xff));
		}
		return builder.toString();
	}

}

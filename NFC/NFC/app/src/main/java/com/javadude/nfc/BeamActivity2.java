package com.javadude.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class BeamActivity2 extends AppCompatActivity {
	private EditText inputView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		inputView = (EditText) findViewById(R.id.input);
	}

	@Override
	protected void onResume() {
		super.onResume();
		NfcAdapter.getDefaultAdapter(this).setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
			@Override
			public NdefMessage createNdefMessage(NfcEvent event) {
				return new NdefMessage(new NdefRecord[]{createText(inputView.getText().toString())});
			}
		}, this);
	}

	private NdefRecord createText(String textString) {
		try {
			byte[] language = Locale.getDefault().getLanguage().getBytes("UTF-8");
			byte[] text = textString.getBytes();
			byte[] payload = new byte[text.length + language.length + 1];

			payload[0] = 0x02; // UTF-8
			System.arraycopy(language, 0, payload, 1, language.length);
			System.arraycopy(text, 0, payload, 1 + language.length, text.length);

			NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
			return record;

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}

package com.javadude.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

public class BeamActivity1 extends AppCompatActivity {
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
		NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{createText("Foo")});
		NfcAdapter.getDefaultAdapter(this).setNdefPushMessage(ndefMessage, this);
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

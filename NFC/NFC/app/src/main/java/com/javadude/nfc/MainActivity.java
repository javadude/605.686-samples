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
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	private TextView textView;
	private PendingIntent pendingIntent;
	private IntentFilter[] readFilters;
	private EditText inputView;
	private NdefMessage messageToWrite;
	private IntentFilter[] writeFilters;
	private String[][] writeTechList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.text);
		inputView = (EditText) findViewById(R.id.input);


		try {
			Intent intent = new Intent(this, getClass());
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			IntentFilter javadudeFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
			javadudeFilter.addDataScheme("http");
			javadudeFilter.addDataAuthority("javadude.com", null);
			IntentFilter textFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, "text/plain");

			readFilters = new IntentFilter[]{javadudeFilter, textFilter};

			writeFilters = new IntentFilter[]{};

			writeTechList = new String[][]{
					{Ndef.class.getName()},
					{NdefFormatable.class.getName()}
			};


		} catch (IntentFilter.MalformedMimeTypeException e) {
			e.printStackTrace();
		}

		processNFC(getIntent());
	}

	private void enableRead() {
		NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, readFilters, null);
	}
	private void enableWrite() {
		NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, writeFilters, writeTechList);
	}
	private void disableRead() {
		NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		enableRead();
	}

	@Override
	protected void onPause() {
		super.onPause();
		disableRead();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processNFC(intent);
	}

	private void processNFC(Intent intent) {
		if (messageToWrite != null) {
			writeTag(intent);
		} else {
			readTag(intent);
		}
	}

	private void writeTag(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			try {
				Ndef ndef = Ndef.get(tag);
				if (ndef == null) {
					NdefFormatable ndefFormatable = NdefFormatable.get(tag);
					if (ndefFormatable != null) {
						ndefFormatable.connect();
						ndefFormatable.format(messageToWrite);
						ndefFormatable.close();
						Toast.makeText(this, "Tag formatted and written", Toast.LENGTH_LONG).show();
					} else {
						// report the tag cannot be formatted
					}
				} else {
					ndef.connect();
					ndef.writeNdefMessage(messageToWrite);
					ndef.close();
					Toast.makeText(this, "Tag written", Toast.LENGTH_LONG).show();
				}
			} catch (FormatException | IOException e) {
				throw new RuntimeException(e);
			} finally {
				messageToWrite = null;
			}
		}
	}

	private void readTag(Intent intent) {
		Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		textView.setText("");
		if(messages != null) {
			for(Parcelable message : messages) {
				NdefMessage ndefMessage = (NdefMessage) message;
				for(NdefRecord record : ndefMessage.getRecords()) {
					switch(record.getTnf()) {
						case NdefRecord.TNF_WELL_KNOWN:
							textView.append("WELL KNOWN: ");
							if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
								textView.append("TEXT: ");
								textView.append(new String(record.getPayload()));
								textView.append("\n");
							} else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
								textView.append("URI: ");
								textView.append(new String(record.getPayload()));
								textView.append("\n");
							}
					}
				}
			}
		}
	}

	public void onWriteText(View view) {
		try {
			byte[] language = Locale.getDefault().getLanguage().getBytes("UTF-8");
			byte[] text = inputView.getText().toString().getBytes();
			byte[] payload = new byte[text.length + language.length + 1];

			payload[0] = 0x02; // UTF-8
			System.arraycopy(language, 0, payload, 1, language.length);
			System.arraycopy(text, 0, payload, 1 + language.length, text.length);

			NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
			messageToWrite = new NdefMessage(new NdefRecord[]{record});
			textView.setText("Please tap a tag to write the text");

			enableWrite();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void onWriteUri(View view) {
		NdefRecord record = NdefRecord.createUri(inputView.getText().toString());
		messageToWrite = new NdefMessage(new NdefRecord[]{record});
		textView.setText("Please tap a tag to write the uri");

		enableWrite();
	}
}

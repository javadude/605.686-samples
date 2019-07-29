package com.javadude.nfc2

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var pendingIntent: PendingIntent? = null
    private var readFilters: Array<IntentFilter>? = null
    private var messageToWrite: NdefMessage? = null
    private var writeFilters: Array<IntentFilter>? = null
    private var writeTechList: Array<Array<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, javaClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val javadudeFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        javadudeFilter.addDataScheme("http")
        javadudeFilter.addDataAuthority("javadude.com", null)
        val textFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED, "text/plain")

        readFilters = arrayOf(javadudeFilter, textFilter)

        writeFilters = emptyArray()

        writeTechList = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))

        write_text_button.setOnClickListener { onWriteText() }
        write_uri_button.setOnClickListener { onWriteUri() }

        processNFC(intent)
    }

    private fun enableRead() {
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, readFilters, null)
    }

    private fun enableWrite() {
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, pendingIntent, writeFilters, writeTechList)
    }

    private fun disableRead() {
        NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this)
    }

    override fun onResume() {
        super.onResume()
        enableRead()
    }

    override fun onPause() {
        super.onPause()
        disableRead()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processNFC(intent)
    }

    private fun processNFC(intent: Intent) {
        if (messageToWrite != null) {
            writeTag(intent)
        } else {
            readTag(intent)
        }
    }

    private fun writeTag(intent: Intent) {
        intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let { tag ->
            try {
                val ndef = Ndef.get(tag)
                if (ndef == null) {
                    NdefFormatable.get(tag)?.let { ndefFormatable ->
                        ndefFormatable.use {
                            it.connect()
                            it.format(messageToWrite)
                            Toast.makeText(this, "Tag formatted and written", Toast.LENGTH_LONG).show()
                        }
                    } ?: throw IllegalStateException("tag cannot be formatted") // report more nicely in a real app...
                } else {
                    ndef.use {
                        it.connect()
                        it.writeNdefMessage(messageToWrite)
                        Toast.makeText(this, "Tag written", Toast.LENGTH_LONG).show()
                    }
                }
            } finally {
                messageToWrite = null
            }
        }
    }

    private fun readTag(intent: Intent) {
        text.text = ""
        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.let { messages ->
            messages.forEach { message ->
                message as NdefMessage
                text.text = message.records.mapNotNull { record ->
                    when (record.tnf) {
                        NdefRecord.TNF_WELL_KNOWN -> {
                            when {
                                Arrays.equals(record.type, NdefRecord.RTD_TEXT) -> "WELL-KNOWN TEXT: ${String(record.payload)}"
                                Arrays.equals(record.type, NdefRecord.RTD_URI) -> "WELL-KNOWN URI: ${String(record.payload)}"
                                else -> null
                            }
                        }
                        else -> null
                    }
                }.joinToString("\n")
            }
        }
    }

    private fun onWriteText() {
        val record = NdefRecord.createTextRecord(Locale.getDefault().language, input.text.toString())
        messageToWrite = NdefMessage(arrayOf(record))
        text.text = getString(R.string.tap_to_write_uri)

        enableWrite()


        // NOTE - if min API < 21, you'll need to create the record explicitly, as createTextRecord did not
        //        exist until 21...
        //
        //        val language = Locale.getDefault().language.toByteArray(charset("UTF-8"))
        //        val textArray = input.text.toString().toByteArray()
        //        val payload = ByteArray(textArray.size + language.size + 1)
        //
        //        payload[0] = 0x02 // UTF-8
        //        System.arraycopy(language, 0, payload, 1, language.size)
        //        System.arraycopy(textArray, 0, payload, 1 + language.size, textArray.size)
        //
        //        val record = NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
        //        messageToWrite = NdefMessage(arrayOf(record))
        //        text.text = getString(R.string.tap_to_write_text)
        //
        //        enableWrite()
    }

    private fun onWriteUri() {
        val record = NdefRecord.createUri(input.text.toString())
        messageToWrite = NdefMessage(arrayOf(record))
        text.text = getString(R.string.tap_to_write_uri)

        enableWrite()
    }
}

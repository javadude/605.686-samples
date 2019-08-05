package com.javadude.speech2

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.UiThread
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_main4.*

import org.antlr.v4.runtime.ANTLRErrorListener
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.BitSet
import java.util.HashMap
import java.util.Locale

class GameActivity4 : AppCompatActivity() {
    private var game: Game? = null
    private var textToSpeech: TextToSpeech? = null
    private val utteranceInfo = HashMap<String, String>()

    private fun startListening() = runWithPermissions(Manifest.permission.RECORD_AUDIO) {
        runOnUiThread {
            stopListening()

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@GameActivity4)
            speechRecognizer!!.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle) {
                    runOnUiThread { statusView.text = "Please speak a command" }
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    // stop and restart listening on error 7 and error 8
                    if (error == 8 || error == 7) {
                        // stop listening
                        // restart listening
                    }
                }

                override fun onResults(results: Bundle) {
                    val resultArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val command = resultArray!![0].toLowerCase()
                    parseCommand(command)
                }

                override fun onPartialResults(partialResults: Bundle) {}
                override fun onEvent(eventType: Int, params: Bundle) {}
            })
            speechRecognizer!!.startListening(recognizerIntent)
        }
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null

    private val listener = object : ANTLRErrorListener {
        override fun syntaxError(
            arg0: Recognizer<*, *>, arg1: Any, arg2: Int,
            arg3: Int, arg4: String, arg5: RecognitionException
        ) {
            throw RuntimeException("I don't know what $arg4 means")
        }

        override fun reportContextSensitivity(
            arg0: Parser, arg1: DFA, arg2: Int,
            arg3: Int, arg4: Int, arg5: ATNConfigSet
        ) {
            throw RuntimeException()
        }

        override fun reportAttemptingFullContext(
            arg0: Parser, arg1: DFA, arg2: Int,
            arg3: Int, arg4: BitSet, arg5: ATNConfigSet
        ) {
            throw RuntimeException()
        }

        override fun reportAmbiguity(
            arg0: Parser, arg1: DFA, arg2: Int, arg3: Int,
            arg4: Boolean, arg5: BitSet, arg6: ATNConfigSet
        ) {
            throw RuntimeException()
        }
    }

    @UiThread
    private fun stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer!!.stopListening()
            speechRecognizer!!.cancel()
            speechRecognizer!!.destroy()
            speechRecognizer = null
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            textToSpeech!!.language = Locale.US
            utteranceInfo[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "spoken"
            textToSpeech!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {}
                override fun onDone(utteranceId: String) {
                    startListening()
                }

                override fun onError(utteranceId: String) {}
            })
            startTheGame()
        })
    }

    override fun onPause() {
        super.onPause()
        stopListening()
        if (textToSpeech != null)
            textToSpeech!!.shutdown()
    }

    private fun parseCommand(command: String) {
        Log.d("RECOGNIZED", command)
        val inputStream = ANTLRInputStream(command)
        val lexer = CommandsLexer(inputStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = CommandsParser(tokenStream)
        try {
            lexer.addErrorListener(listener)
            parser.addErrorListener(listener)
            parser.command(game)
        } catch (e: RecognitionException) {
            game!!.report(command, "Command not recognized; try again")
        } catch (e: RuntimeException) {
            game!!.report(command, "Command $command not recognized; try again")
        }

    }

    private fun startTheGame() {
        val json = InputStreamReader(resources.openRawResource(R.raw.data)).readText()
        game = Game(json, object : Game.Reporter {
            override fun report(message: String, text: String) {
                textView!!.text = text
                statusView!!.text = "Shhhhhhhh"
                textToSpeech!!.speak(message, TextToSpeech.QUEUE_ADD, utteranceInfo)
            }
        })
    }

    fun onSubmitCommand(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me what you want to do")
        startActivityForResult(intent, REQUEST_GET_SPEECH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GET_SPEECH -> {
                val results = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val command = results!![0].toLowerCase(Locale.getDefault())
                parseCommand(command)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val REQUEST_GET_SPEECH = 42
    }
}
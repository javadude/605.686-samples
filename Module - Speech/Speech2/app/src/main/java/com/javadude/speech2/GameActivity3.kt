package com.javadude.speech2

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.io.InputStreamReader
import java.util.*

class GameActivity3 : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var game: Game
    private lateinit var textToSpeech: TextToSpeech

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

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        textView = findViewById(R.id.textView)

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            textToSpeech.language = Locale.US
            startTheGame()
        })
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
            game.report(command, "Command not recognized; try again")
        } catch (e: RuntimeException) {
            game.report(command, "Command $command not recognized; try again")
        }

    }

    private fun startTheGame() {
        val json = InputStreamReader(resources.openRawResource(R.raw.data)).readText()
        game = Game(json, object : Game.Reporter {
            override fun report(message: String, text: String) {
                textView.text = text
                textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null)
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
package com.javadude.speech;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Locale;

public class GameActivity3 extends AppCompatActivity {
	private TextView textView;
	private Game game;
	private TextToSpeech textToSpeech;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main3);
		textView = (TextView) findViewById(R.id.textView);

		textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				textToSpeech.setLanguage(Locale.US);
				startTheGame();
			}
		});
	}

	private ANTLRErrorListener listener = new ANTLRErrorListener() {
		@Override public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2,
		                                  int arg3, String arg4, RecognitionException arg5) {
			throw new RuntimeException("I don't know what " + arg4 + " means");
		}

		@Override
		public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2,
		                                     int arg3, int arg4, ATNConfigSet arg5) {
			throw new RuntimeException();
		}

		@Override
		public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2,
		                                        int arg3, BitSet arg4, ATNConfigSet arg5) {
			throw new RuntimeException();
		}

		@Override
		public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3,
		                            boolean arg4, BitSet arg5, ATNConfigSet arg6) {
			throw new RuntimeException();
		}
	};

	private void parseCommand(String command) {
		Log.d("RECOGNIZED", command);
		ANTLRInputStream inputStream = new ANTLRInputStream(command);
		CommandsLexer lexer = new CommandsLexer(inputStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		CommandsParser parser = new CommandsParser(tokenStream);
		try {
			lexer.addErrorListener(listener);
			parser.addErrorListener(listener);
			parser.command(game);
		} catch (RecognitionException e) {
			game.report(command, "Command not recognized; try again");
		} catch (RuntimeException e) {
			game.report(command, "Command " + command + " not recognized; try again");
		}

	}

	private void startTheGame() {
		final StringBuilder json = new StringBuilder();
		new AutoFileCloser() {
			@Override protected void doWork() throws Throwable {
				BufferedReader br = watch(new BufferedReader(watch(new InputStreamReader(watch(getResources().openRawResource(R.raw.data))))));
				String line;
				while ((line = br.readLine()) != null) {
					json.append(line);
					json.append('\n');
				}
			}};
		game = new Game(json.toString(), new Game.Reporter() {
			@Override public void report(String message, String text) {
				textView.setText(text);
				textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null);
			}
		});
	}

	private static final int REQUEST_GET_SPEECH = 42;
	public void onSubmitCommand(View view) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell me what you want to do");
		startActivityForResult(intent, REQUEST_GET_SPEECH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case REQUEST_GET_SPEECH:
				ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String command = results.get(0).toLowerCase(Locale.getDefault());
				parseCommand(command);
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
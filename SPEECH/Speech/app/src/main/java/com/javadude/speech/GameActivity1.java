package com.javadude.speech;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.BitSet;

public class GameActivity1 extends AppCompatActivity {
	private EditText inputView;
	private TextView textView;
	private Game game;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.textView);
		inputView = (EditText) findViewById(R.id.input);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startTheGame();
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
			}
		});
	}

	public void onSubmitCommand(View view) {
		String command = inputView.getText().toString();
		parseCommand(command.trim());
		inputView.setText("");
	}
}
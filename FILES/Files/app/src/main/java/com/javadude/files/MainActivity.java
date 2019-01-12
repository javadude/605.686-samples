package com.javadude.files;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private PermissionManager permissionManager = new PermissionManager();

    private PermissionAction writeToSDCard = new PermissionAction(this, null, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        @Override
        protected void onPermissionGranted() {
            File music = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            File file = new File(music, "sample3.txt");
            try {
                PrintWriter pw = new PrintWriter(file);
                pw.println("This is line 1b");
                pw.println("This is line 2b");
                pw.println("This is line 3b");
                pw.close(); // BAD PLACEMENT!!!
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPermissionDenied() {
            Log.d("FILES", "Permission denied for writing to SDCARD");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.text);
        assert textView != null;

        try {
            FileInputStream fis = openFileInput("sample.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null) {
                textView.append("\n");
                textView.append(line);
            }

            br.close(); // BAD PLACEMENT!!!

        } catch (IOException e) {
            Log.e("FILES", "Error reading file", e);
            e.printStackTrace();
        }


        // write data to application dir - BETTER CLOSING - works in Java 1.6 and before, as well as
        //   in Android 18 and before
        new AutoFileCloser() {
            @Override protected void doWork() throws Throwable {
                FileOutputStream fos = watch(openFileOutput("sample.txt", Context.MODE_PRIVATE));
                OutputStreamWriter osw = watch(new OutputStreamWriter(fos, "UTF-8"));
                PrintWriter pw = watch(new PrintWriter(osw));

                pw.println("This is line 1");
                pw.println("This is line 2");
                pw.println("This is line 3");
            }};

        // write data to application dir - GOOD CLOSING
        {
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;
            PrintWriter pw = null;
            Throwable pending = null;
            try {
                fos = openFileOutput("sample.txt", Context.MODE_PRIVATE);
                osw = new OutputStreamWriter(fos, "UTF-8");
                pw = new PrintWriter(osw);

                pw.println("This is line 1");
                pw.println("This is line 2");
                pw.println("This is line 3");


            } catch (Throwable t) {
                pending = t;
            } finally {
                if (pw != null) {
                    try {
                        pw.close();
                    } catch (Throwable t) {
                        if (pending == null)
                            pending = t;
                    }
                }
                if (osw != null) {
                    try {
                        osw.close();
                    } catch (Throwable t) {
                        if (pending == null)
                            pending = t;
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Throwable t) {
                        if (pending == null)
                            pending = t;
                    }
                }
                if (pending != null) {
                    if (pending instanceof Error)
                        throw (Error) pending;
                    if (pending instanceof RuntimeException)
                        throw (RuntimeException) pending;
                    throw new RuntimeException(pending);
                }
            }
        }

        // write data to application dir - GOOD CLOSING
        try (
            FileOutputStream fos = openFileOutput("sample.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            PrintWriter pw = new PrintWriter(osw);
        ) {
            pw.println("This is line 1");
            pw.println("This is line 2");
            pw.println("This is line 3");

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }



        // BAD CLOSING: write data to application dir
        try {
            FileOutputStream fos = openFileOutput("sample.txt", Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            PrintWriter pw = new PrintWriter(osw);

            pw.println("This is line 1");
            pw.println("This is line 2");
            pw.println("This is line 3");

            pw.close(); // BAD PLACEMENT!!!

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            Log.e("FILES", "Error writing file", e);
        }

        String externalStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            File externalFilesDir = getExternalFilesDir(null);
            File file = new File(externalFilesDir, "sample2.txt");
            try {
                PrintWriter pw = new PrintWriter(file);
                pw.println("This is line 1a");
                pw.println("This is line 2a");
                pw.println("This is line 3a");
                pw.close(); // BAD PLACEMENT!!!
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("FILES", "No writeable external storage mounted");
        }

        permissionManager.run(writeToSDCard);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

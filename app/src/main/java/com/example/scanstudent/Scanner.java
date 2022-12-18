package com.example.scanstudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Scanner extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData,chargems;
    MediaPlayer mediaPlayer;
    Button btninfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_scanner);
        btninfo=findViewById(R.id.btninfos);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bipsound);

        scannView=findViewById(R.id.scannerView);
        codeScanner=new CodeScanner(this,scannView);
        resultData=findViewById(R.id.resultsOfQr);
        chargems=findViewById(R.id.chargem);

        //btninfo.setEnabled(false);
     //   chargems.setEnabled(false);

        resultData.setVisibility(View.GONE);
        chargems.setVisibility(View.GONE);
        btninfo.setVisibility(View.GONE);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.start();
                        resultData.setVisibility(View.VISIBLE);
                        resultData.setText(result.getText());

                        btninfo.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
                btninfo.setVisibility(View.GONE);
                chargems.setVisibility(View.GONE);
                resultData.setVisibility(View.GONE);
                btninfo.setEnabled(true);
            }
        });

        btninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetOn()==true){
                    chargems.setVisibility(View.VISIBLE);
                    btninfo.setEnabled(false);
                    new Async().execute();
                }else{
                    Toast.makeText(Scanner.this, "Pas de connexion internet", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(Scanner.this, "Camera Permission is requered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    class Async extends AsyncTask<Void, Void, Void> {
        @Override

        protected Void doInBackground(Void... voids) {
            String matricule = resultData.getText().toString();

            try

            {
                Class.forName("com.mysql.jdbc.Driver");

                Connection connection = DriverManager.getConnection("jdbc:mysql://196.1.137.136:3306/etudiantdb", "unimapon", "N@%l@m@p0n");
                if(connection!=null){
                    Statement statement = connection.createStatement();

                    ResultSet rs = statement.executeQuery("SELECT * FROM etudiant WHERE Matricule='"+ matricule.toString() +"'");

                    if(rs.next()) {
                        Intent intent=new Intent(Scanner.this,StudentActivity.class);
                        intent.putExtra("mat",rs.getString(11));
                        intent.putExtra("nom",rs.getString(2));
                        intent.putExtra("sexe",rs.getString(3));
                        intent.putExtra("fac",rs.getString(4));
                        intent.putExtra("prom",rs.getString(5));
                        intent.putExtra("fil",rs.getString(6));
                        intent.putExtra("frais",rs.getString(7));
                        intent.putExtra("obs",rs.getString(8));
                        intent.putExtra("annee",rs.getString(12));
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        chargems.setText("Mauvais QR Code");
                        btninfo.setEnabled(true);
                    }
                }else{
                    chargems.setText("Désolé, erreur de connexion à la base de données");
                    btninfo.setEnabled(true);
                }
            }

            catch(Exception e)

            {
                chargems.setText(e.getMessage());
                btninfo.setEnabled(true);

            }

            return null;

        }



        @Override

        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

        }



    }
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            // Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}
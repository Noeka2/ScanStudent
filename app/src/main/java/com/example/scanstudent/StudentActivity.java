package com.example.scanstudent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class StudentActivity extends AppCompatActivity {
    public  static TextView matricule,genre,frais,nom,faculte,promotion,filiere,annee,obs;
    Button btnscans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_student);

        matricule=findViewById(R.id.mat);
        String mat=getIntent().getStringExtra("mat");
        matricule.setText(mat);

        nom=findViewById(R.id.nom_complet);
        String noms=getIntent().getStringExtra("nom");
        nom.setText(noms);

        genre=findViewById(R.id.genre);
        String sexe=getIntent().getStringExtra("sexe");
        genre.setText(sexe);

        faculte=findViewById(R.id.fac);
        String facul=getIntent().getStringExtra("fac");
        faculte.setText(facul);

        promotion=findViewById(R.id.prom);
        String promot=getIntent().getStringExtra("prom");
        promotion.setText(promot);


        filiere=findViewById(R.id.fil);
        String filier=getIntent().getStringExtra("fil");
        filiere.setText(filier);

        frais=findViewById(R.id.montant_frais);
        String frais_ac=getIntent().getStringExtra("frais");
        frais.setText(frais_ac);

        obs=findViewById(R.id.obs_frais);
        String obsfrais=getIntent().getStringExtra("obs");
        obs.setText(obsfrais);

        annee=findViewById(R.id.annee_ac);
        String anneeac=getIntent().getStringExtra("annee");
        annee.setText(anneeac);

        btnscans=findViewById(R.id.btnscans);
        btnscans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent=new Intent(StudentActivity.this,Scanner.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
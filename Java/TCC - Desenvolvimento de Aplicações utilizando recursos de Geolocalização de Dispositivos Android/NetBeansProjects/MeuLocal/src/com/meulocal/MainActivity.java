package com.meulocal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Context;
import android.widget.Button;
import android.location.Location;
import android.location.LocationManager;
import Entidade.Localizacao;
import android.location.LocationListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class MainActivity extends Activity {

    Button btnGps;
    Button btnFinalizar;
    EditText txtMatricula;
    TextView txtMatriculaDigitada;
    TextView txtLatitude;
    TextView txtLongitude;
    TextView txtData;
    TextView txtHora;
    TextView txtResposta;
    
    Localizacao objLocalizacao = new Localizacao();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtMatricula = (EditText) findViewById(R.id.txtMatricula);
        txtMatriculaDigitada = (TextView) findViewById(R.id.txtMatriculaDigitada);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtData = (TextView) findViewById(R.id.txtData);
        txtHora = (TextView) findViewById(R.id.txtHora);
        btnGps = (Button) findViewById(R.id.btnGps);
        //btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        txtResposta = (TextView) findViewById(R.id.txtResposta);

        btnGps.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                txtResposta.setText("");
                txtMatriculaDigitada.setText(MostrarMatricula());
                ManterMatricula();
                txtData.setText(RetornaData());
                txtHora.setText(RetornaHora());
                GPS();
                tarefa1();
            }
        });
    }
    public static final long TEMPO = (200 * 60); // atualiza a cada 12 segundos

    public void tarefa1() {

        Timer timer = null;
        
        if (timer == null) {
            timer = new Timer();
            
            TimerTask tarefa = new TimerTask() {
                
                public void run() {
                    try {
                        
                        executeHttptPostData();
                    
                    } catch (Exception e) {
                        e.printStackTrace();
                        txtResposta.setText(e.getMessage());
                    }
                }
            };
            timer.scheduleAtFixedRate(tarefa, TEMPO, TEMPO);

        }
    }

    //MATRICULA****************************************************************************************************************  
    public String MostrarMatricula() {

        String matriculaDigitada = txtMatricula.getText().toString();
        return matriculaDigitada;

    }

    public void ManterMatricula() {

        txtMatricula.setFocusable(false);
    }

    //LATITUDE E LONGITUDE****************************************************************************************************************
    public void GPS() {
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                if (locat != null) {
                    Atualiza(locat);
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }

    public Double Atualiza(Location location) {

        Double lat = location.getLatitude();
        Double lon = location.getLongitude();
        txtLatitude.setText(lat.toString());
        txtLongitude.setText(lon.toString());

        return null;
    }

    //DATA****************************************************************************************************************
    public String RetornaData() {

        String Tempo = "";

        GregorianCalendar calendar2 = new GregorianCalendar();
        int fano = calendar2.get(Calendar.YEAR);
        int fmes = calendar2.get(Calendar.MONTH) + 1;
        int fdia = calendar2.get(Calendar.DAY_OF_MONTH);

        String ct = "" + fano;
        if (ct.length() == 1) {
            ct = "0" + ct;
        }
        Tempo = Tempo + ct + "-";

        ct = "" + fmes;
        if (ct.length() == 1) {
            ct = "0" + ct;
        }
        Tempo = Tempo + ct + "-";

        ct = "" + fdia;
        if (ct.length() == 1) {
            ct = "0" + ct;
        }
        Tempo = Tempo + ct;

        //txtData.setText(Tempo);
       return Tempo;

    }
    //HORA****************************************************************************************************************

    public String RetornaHora() {

        String Tempo = "";

        GregorianCalendar calendar2 = new GregorianCalendar();
        int fhora = calendar2.get(Calendar.HOUR_OF_DAY);
        int fminuto = calendar2.get(Calendar.MINUTE);
        int fsegundo = calendar2.get(Calendar.SECOND);

        String ct = "" + fhora;
        if (ct.length() == 1) {
            ct = "0" + ct;
        }
        Tempo = Tempo + ct + ":";


        ct = "" + fminuto;
        if (ct.length() == 1) {
            ct = "0" + ct;
        }
        Tempo = Tempo + ct + ":";


        ct = "" + fsegundo;
        if (ct.length() == 1) {
            ct = "0" + ct;
        }
        Tempo = Tempo + ct;

        //txtHora.setText(Tempo);
        return Tempo;
    }
    public final String URL = "http://localizador.no-ip.biz:8080/Localiza/RecebeDados";
    public InputStream executeHttptPostData() {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);

        objLocalizacao.setMatricula(MostrarMatricula());
        objLocalizacao.setLatitude(txtLatitude.getText().toString());
        objLocalizacao.setLongitude(txtLongitude.getText().toString());
        objLocalizacao.setData(RetornaData());
        objLocalizacao.setHora(RetornaHora());
      
        String matricula = objLocalizacao.getMatricula();
        String latitude = objLocalizacao.getLatitude();
        String longitude = objLocalizacao.getLongitude();
        String data = objLocalizacao.getData().toString();
        String hora = objLocalizacao.getHora().toString();
               
        try {
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("matricula", matricula.toString()));
            nameValuePairs.add(new BasicNameValuePair("latitude", latitude.toString()));
            nameValuePairs.add(new BasicNameValuePair("longitude", longitude.toString()));
            nameValuePairs.add(new BasicNameValuePair("data", String.valueOf(data)));
            nameValuePairs.add(new BasicNameValuePair("hora", String.valueOf(hora)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            return response.getEntity().getContent();

        } catch (ClientProtocolException e) {
            txtResposta.setText(e.getMessage());
        } catch (IOException e) {
            txtResposta.setText(e.getMessage());
        }
        return null;
    }
}

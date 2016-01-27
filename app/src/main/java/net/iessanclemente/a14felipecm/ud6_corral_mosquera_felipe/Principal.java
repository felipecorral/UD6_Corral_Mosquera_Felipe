package net.iessanclemente.a14felipecm.ud6_corral_mosquera_felipe;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Principal extends FragmentActivity implements OnMapReadyCallback,LocationListener {


    private ArrayList<String> localizacions;
    private ArrayAdapter<String> adaptador;

    // Google Maps variables
    private GoogleMap mMap;
    // Geolocation variables
    private LocationManager locManager;
    private String provedor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //setSupportActionBar(toolbar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        localizacions = new ArrayList<String>();
        //adaptador = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, localizacions);
        //ListView listaGPS = (ListView)findViewById(R.id.UD6_01_lstListaCoordGPS);
        //listaGPS.setAdapter(adaptador);

        obterprovedores();


        locManager.requestLocationUpdates(provedor, 0, 500, Principal.this);
        Toast.makeText(getApplicationContext(), "Comenzado a rexistrar...", Toast.LENGTH_SHORT).show();

        Location last = locManager.getLastKnownLocation(provedor);
        if (last != null)
            localizacions.add("ULTIMA COÑECIDA: LAT:" + String.valueOf(last.getLatitude()) + " - LONX:" + String.valueOf(last.getLongitude()));




    }

   /* private void xestionarEventos(){

        Button btnRexistrar = (Button)findViewById(R.id.UD6_01_btnRexistrarGPS);
        btnRexistrar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                locManager.requestLocationUpdates(provedor, 0, 100, Principal.this);
                Toast.makeText(getApplicationContext(), "Comenzado a rexistrar...", Toast.LENGTH_SHORT).show();

                Location last = locManager.getLastKnownLocation(provedor);
                if (last != null)
                    localizacions.add("ULTIMA COÑECIDA: LAT:" + String.valueOf(last.getLatitude()) + " - LONX:" + String.valueOf(last.getLongitude()));

            }
        });

        Button btnPararRexistrar = (Button)findViewById(R.id.UD6_01_btnPararRexistro);
        btnPararRexistrar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                locManager.removeUpdates(Principal.this);
                Toast.makeText(getApplicationContext(), "Parando de rexistrar...", Toast.LENGTH_SHORT).show();
            }
        });

    }*/


    // Method for the map once ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(mMap.getCameraPosition().target)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
    }


    public void pararRegistrar(View v) throws Exception{
        locManager.removeUpdates(Principal.this);
        Toast.makeText(getApplicationContext(), "Parando de rexistrar...", Toast.LENGTH_SHORT).show();
    }

    public void registrar(View v) throws Exception{

        locManager.requestLocationUpdates(provedor, 0, 500, Principal.this);
        Toast.makeText(getApplicationContext(), "Comenzado a rexistrar...", Toast.LENGTH_SHORT).show();
        try {
            Location last = locManager.getLastKnownLocation(provedor);

        if (last != null)
            localizacions.add("ULTIMA COÑECIDA: LAT:" + String.valueOf(last.getLatitude()) + " - LONX:" + String.valueOf(last.getLongitude()));
        }catch(Exception e){

        }
    }

    private void obterprovedores(){
        Criteria filtro = new Criteria();
        filtro.setAccuracy(Criteria.ACCURACY_FINE);

        locManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        provedor = locManager.getBestProvider(filtro, false);   // Se non está activo o avisamos e chamamos a activity para activalo
//              provedor = LocationManager.NETWORK_PROVIDER;    => Exemplo concreto sen filtro

        if (provedor==null){
            Toast.makeText(getApplicationContext(), "Non existen provedores dispoñibles.", Toast.LENGTH_LONG).show();
            finish();
        }
        if (!locManager.isProviderEnabled(provedor)){
            Toast.makeText(getApplicationContext(), "O " + provedor + " non está activo", Toast.LENGTH_LONG).show();
            dialogoAlertaNonGPS();
        }
        else{
            Toast.makeText(getApplicationContext(), "provedor atopado:" + provedor, Toast.LENGTH_LONG).show();
        }
    }

    private void dialogoAlertaNonGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "O GPS parece desactivado, queres activalo ?")
                .setCancelable(false)
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // TODO Auto-generated method stub
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        Principal.this.finish();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        localizacions.add("LATITUDE:" + String.valueOf(location.getLatitude()) + " - LONXITUDE:" + String.valueOf(location.getLongitude()));
//        adaptador.notifyDataSetChanged();
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());  // Posición de Santiago de Compostela
        //mMap = googleMap;
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "O provedor " + provider + " xa non está activo", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "O provedor " + provider + " está activo", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

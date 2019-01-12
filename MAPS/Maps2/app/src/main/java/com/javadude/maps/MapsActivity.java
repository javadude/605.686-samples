package com.javadude.maps;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.renderscript.Double2;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private PermissionManager permissionManager = new PermissionManager();
	private SupportMapFragment mapFragment;
	private BitmapDescriptor blueCar;
	private LatLng savedPosition;
	private Marker marker;
	private Polyline route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		blueCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_blue);
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		SharedPreferences preferences = getSharedPreferences("position", MODE_PRIVATE);
		String latitudeString = preferences.getString("latitude", null);
		String longitudeString = preferences.getString("longitude", null);
		if (latitudeString != null && longitudeString != null) {
			savedPosition = new LatLng(
					Double.parseDouble(latitudeString),
					Double.parseDouble(longitudeString)
			);
			if (marker != null)
				marker.remove();
			marker = mMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 0.5f)
					.icon(blueCar)
					.title("Saved Location")
					.snippet("Lat/Lng: " + savedPosition.latitude + ", " + savedPosition.longitude)
					.position(savedPosition)
			);
		}
		permissionManager.run(enableMyLocationAction);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMap.isMyLocationEnabled())
			//noinspection MissingPermission
			mMap.setMyLocationEnabled(false);
		if (savedPosition != null) {
			getSharedPreferences("position", MODE_PRIVATE)
					.edit()
					.putString("latitude", savedPosition.latitude + "")
					.putString("longitude", savedPosition.longitude + "")
					.apply();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapFragment.getMapAsync(this);
	}

	private PermissionAction enableMyLocationAction = new PermissionAction(this, null, Manifest.permission.ACCESS_FINE_LOCATION) {
		@Override
		protected void onPermissionGranted() {
			//noinspection MissingPermission
			mMap.setMyLocationEnabled(true);
		}

		@Override
		protected void onPermissionDenied() {
		}
	};


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		permissionManager.handleRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_navigate: {
				Uri uri = Uri.parse("google.navigation:q=" + savedPosition.latitude + "," + savedPosition.longitude);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			}
			case R.id.action_show_route: {
				Location myLocation = mMap.getMyLocation();
				new RouteFetcher().execute(myLocation.getLatitude(), myLocation.getLongitude(), savedPosition.latitude, savedPosition.longitude);
				return true;
			}
			case R.id.action_remember_location: {
				Location myLocation = mMap.getMyLocation();
				savedPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				if (marker != null)
					marker.remove();
				marker = mMap.addMarker(new MarkerOptions()
						.anchor(0.5f, 0.5f)
						.icon(blueCar)
						.title("Saved Location")
						.position(savedPosition)
				);
				return true;
			}
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class RouteFetcher extends AsyncTask<Double, Void, List<LatLng>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (route != null)
				route.remove();
		}

		@Override
		protected void onPostExecute(List<LatLng> latLngs) {
			super.onPostExecute(latLngs);
			route = mMap.addPolyline(new PolylineOptions()
					.addAll(latLngs)
					.color(Color.RED)
					.width(8)
			);
		}

		@Override
		protected List<LatLng> doInBackground(Double... params) {
			double myLat = params[0];
			double myLon = params[1];
			double savedLat = params[2];
			double savedLon = params[3];
			try {
				String uriString = "http://maps.googleapis.com/maps/api/directions/json?mode=walking&origin=" + myLat + ',' + myLon + "&destination=" + savedLat + ',' + savedLon + "&sensor=true";

				URL url = new URL(uriString);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				connection.setRequestMethod("GET");
				connection.connect();
				int responseCode = connection.getResponseCode();
				String content = "";
				if (responseCode < 300) {
					InputStream in = connection.getInputStream();
					InputStreamReader isr = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(isr);
					String line;
					while((line = br.readLine()) != null) {
						content += line + "\n";
					}
					// SHOW THE ROUTE ON THE MAP
					JSONObject object = new JSONObject(content);
					JSONArray steps = object.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
					List<LatLng> allLatLngs = new ArrayList<>();
					for(int i = 0; i < steps.length(); i++) {
						JSONObject step = steps.getJSONObject(i);
						String points = step.getJSONObject("polyline").getString("points");
						List<LatLng> latLngs = PolyUtil.decode(points);
						allLatLngs.addAll(latLngs);
					}
					return allLatLngs;
				}

				throw new RuntimeException("Could not access routing information. Response from server: " + responseCode);

			} catch (IOException| JSONException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

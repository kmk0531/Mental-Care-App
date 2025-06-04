package com.example.tp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue requestQueue;
    private final String apiKey = "AIzaSyCl5EbxtatiDu8EeGZ_7z8P6n_n1Wa2aEo";

    private List<Hospital> hospitalList = new ArrayList<>();
    private RecyclerView hospitalRecyclerView;
    private HospitalAdapter hospitalAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestQueue = Volley.newRequestQueue(requireContext());
        EditText editTextLocation = view.findViewById(R.id.editText_location);
        Button buttonSearch = view.findViewById(R.id.button_search);
        hospitalRecyclerView = view.findViewById(R.id.hospital_list);

        hospitalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hospitalAdapter = new HospitalAdapter(hospitalList, hospital -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hospital.latLng, 16));
        });
        hospitalRecyclerView.setAdapter(hospitalAdapter);

        buttonSearch.setOnClickListener(v -> {
            String locationText = editTextLocation.getText().toString();
            if (!locationText.isEmpty()) {
                searchLocation(locationText);
            } else {
                Toast.makeText(getContext(), "지역명을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }

    private void searchLocation(String locationText) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                Uri.encode(locationText) + "&key=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject location = results.getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");

                            LatLng latLng = new LatLng(lat, lng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                            hospitalList.clear();
                            searchNearbyPsychiatryHospitals(lat, lng);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("GeocodingError", "API Request failed: " + error.getMessage())
        );

        requestQueue.add(request);
    }

    private void searchNearbyPsychiatryHospitals(double lat, double lng) {
        String location = lat + "," + lng;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + location +
                "&radius=3000" +
                "&keyword=정신과" +
                "&type=hospital" +
                "&key=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        mMap.clear();

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject hospital = results.getJSONObject(i);
                            String placeId = hospital.getString("place_id");
                            String name = hospital.getString("name");
                            double hospLat = hospital.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double hospLng = hospital.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            double rating = hospital.optDouble("rating", 0.0);
                            String vicinity = hospital.optString("vicinity", "No address");

                            fetchPlaceDetails(placeId, name, hospLat, hospLng, rating, vicinity);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("PlacesError", "Hospital scan failed: " + error.getMessage())
        );

        requestQueue.add(request);
    }

    private void fetchPlaceDetails(String placeId, String name, double lat, double lng, double rating, String vicinity) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json" +
                "?place_id=" + placeId +
                "&fields=name,formatted_phone_number,formatted_address" +
                "&key=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject result = response.getJSONObject("result");
                        String phone = result.optString("formatted_phone_number", "No Phone Number");
                        String address = result.optString("formatted_address", vicinity);

                        LatLng latLng = new LatLng(lat, lng);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .snippet("⭐ 평점: " + rating +
                                        "\n📞 전화: " + phone +
                                        "\n📍 주소: " + address));

                        hospitalList.add(new Hospital(name, phone, address, rating, latLng, marker));

                        Collections.sort(hospitalList, Comparator.comparingDouble(h -> -h.rating));
                        List<Hospital> top2 = hospitalList.size() > 2 ? hospitalList.subList(0, 2) : hospitalList;
                        hospitalAdapter = new HospitalAdapter(top2, hospital -> {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hospital.latLng, 16));
                            if (hospital.marker != null) {
                                hospital.marker.showInfoWindow();
                            }
                        });
                        hospitalRecyclerView.setAdapter(hospitalAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("PlaceDetailsError", "Failed to get details: " + error.getMessage())
        );

        requestQueue.add(request);
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = LayoutInflater.from(requireContext()).inflate(R.layout.custom_info_window, null);
        }

        private void renderWindowText(Marker marker, View view) {
            String title = marker.getTitle();
            String snippet = marker.getSnippet();

            TextView titleView = view.findViewById(R.id.info_title);
            TextView ratingView = view.findViewById(R.id.info_rating);
            TextView phoneView = view.findViewById(R.id.info_phone);
            TextView addressView = view.findViewById(R.id.info_address);

            titleView.setText(title);

            if (snippet != null) {
                String[] lines = snippet.split("\n");
                ratingView.setText(lines.length > 0 ? lines[0] : "");
                phoneView.setText(lines.length > 1 ? lines[1] : "");
                addressView.setText(lines.length > 2 ? lines[2] : "");
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            renderWindowText(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}

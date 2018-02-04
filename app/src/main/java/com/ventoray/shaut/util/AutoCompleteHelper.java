package com.ventoray.shaut.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ventoray.shaut.R;

import static com.firebase.ui.auth.ui.email.CheckEmailFragment.TAG;

/**
 * Created by Nick on 2/4/2018.
 */

public class AutoCompleteHelper {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001;
    public static final String AVAILABLE_COUNTRY_US = "US";

    public interface CurrentPlaceListener {
        void onPlaceDetected(String likelyCity);
    }

    /**
     * This method prepares the AutoCompleteFragment, limiting search results to cities in the
     * US and sets the placeSelectionListener which will relay the result.
     * @param appCompatActivity
     * @param placeSelectionListener
     */
    public static void initializePlaceAutoComplete(AppCompatActivity appCompatActivity,
                                             PlaceSelectionListener placeSelectionListener) {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                appCompatActivity
                        .getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry(AVAILABLE_COUNTRY_US)
                .build();
        autocompleteFragment.setFilter(autocompleteFilter);
        autocompleteFragment.setOnPlaceSelectedListener(placeSelectionListener);
    }


    public static void getPlacePhoto(final Context context,
                                     String placeId, final ImageView imageView) {
        final GeoDataClient geoDataClient = Places.getGeoDataClient(context, null);

        Task<PlacePhotoMetadataResponse> photoMetadataResponseTask = geoDataClient
                .getPlacePhotos(placeId);
        photoMetadataResponseTask.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);

                // Get the attribution text.
                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.
                Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
                photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                        PlacePhotoResponse photo = task.getResult();
                        Bitmap bitmap = photo.getBitmap();

                        Glide.with(context)
                                .asBitmap()
                                .load(bitmap)
                                .into(imageView);
                    }
                });
            }
        });
    }


    public static void getCurrentPlace(Context context, final CurrentPlaceListener placeListener) throws SecurityException {
        PlaceDetectionClient placeDetectionClient = Places.getPlaceDetectionClient(context, null);
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                PlaceLikelihood mostLikely = null;
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                    if (mostLikely == null) {
                        mostLikely = placeLikelihood;
                    } else if (mostLikely.getLikelihood() <= placeLikelihood.getLikelihood()) {
                        mostLikely = placeLikelihood;
                    }

                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));


                }

                String likelyCity = getLikelyCityName(mostLikely.getPlace().getAddress().toString());
//                placeListener.onPlaceDetected(likelyCity);
                likelyPlaces.release();
            }
        });
    }





    /**
     * Requests permission for location detection explicitly
     * @param appCompatActivity
     * @return
     */
    public static void getLocationPermission(AppCompatActivity appCompatActivity) {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
//        boolean locationPermissionGranted = false;

        if (ContextCompat.checkSelfPermission(appCompatActivity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(appCompatActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

//        return locationPermissionGranted;
    }


    private static String getLikelyCityName(String address) {
        String cityName = null;

        int index = address.indexOf(",");
        cityName = address.substring(index + 1);

        Log.d("AutoCompleteHelper", "City name: " + cityName);


        return cityName;
    }


}

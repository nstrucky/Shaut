package com.ventoray.shaut.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
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

/**
 * Created by Nick on 2/4/2018.
 */

public class AutoCompleteHelper {



    public static void initializePlaceAutoComplete(AppCompatActivity appCompatActivity,
                                             PlaceSelectionListener placeSelectionListener) {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                appCompatActivity
                        .getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
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

}

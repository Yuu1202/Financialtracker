package com.example.financialtracker;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class ApiClient {
    private static Retrofit retrofit;
    private static final String TAG = "ApiClient";

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Creating new Retrofit instance");

            // Logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message ->
                    Log.d("OkHttp", message)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://senopati.its.ac.id/senopati-lokal-dev/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            Log.d(TAG, "Retrofit instance created");
        }
        return retrofit;
    }
}
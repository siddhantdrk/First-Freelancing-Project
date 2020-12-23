package com.example.order_eckn2015;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String getContactsUrl = "http://192.168.43.24/phoneDir/api/read.php";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private RecyclerView profileContactRecycler;
    private List<profileContactItem> profileContactItemList;
    private String jsonResponse;
    private profileContactsRecyclerAdapter profileContactsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileContactItemList = new ArrayList<>();
        profileContactRecycler = findViewById(R.id.contacts_recycler);
        Thread urlRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonResponse = requestProfileContactsFromUrl();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestContactPermission();
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        urlRequestThread.start();
    }

    private void updateResultOnUIThread(String jsonResponse) {
        if (jsonResponse.isEmpty()) {
            for (int i = 0; i < 10; i++) {
                profileContactItemList.add(new profileContactItem("", ""));
            }
            Toast.makeText(this, "Sorry can't load the contacts, network Error !!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject contactsJsonObject = new JSONObject(jsonResponse);
                boolean success = contactsJsonObject.getBoolean("success");
                String msg = contactsJsonObject.getString("msg");
                if (success) {
                    int contactsCount = contactsJsonObject.getInt("count");
                    JSONArray contactsJsonArray = contactsJsonObject.getJSONArray("data");
                    for (int i = 0; i < 10; i++) {
                        if (i < contactsCount) {
                            String contactNum = contactsJsonArray.getString(i);
                            String contactName = getContactName(contactNum, this);
                            profileContactItemList.add(new profileContactItem(contactName, contactNum));
                        } else {
                            profileContactItemList.add(new profileContactItem("", ""));
                        }
                    }
                }
                Log.d("requestMessage: ", msg);
            } catch (JSONException e) {
                Log.e("JsonError: ", e.getMessage());
            }
        }
        profileContactsRecyclerAdapter = new profileContactsRecyclerAdapter(this, profileContactItemList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        profileContactRecycler.setLayoutManager(gridLayoutManager);
        profileContactRecycler.setAdapter(profileContactsRecyclerAdapter);
        profileContactRecycler.setVisibility(View.VISIBLE);
    }

    private String requestProfileContactsFromUrl() throws IOException, JSONException {
        String jsonResponse = "";
        URL url = new URL(getContactsUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.connect();
        String jsonInput = "{\"email\":\"entwickler@proximo-wsd.de\"}";
        try (OutputStream os = httpURLConnection.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (Exception exception) {
            Log.d("OutputStream Code :", exception.getMessage());
        }
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                jsonResponse = response.toString();
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        } else {
            Log.e("Network Error: ", httpURLConnection.getResponseMessage());
        }
        return jsonResponse;
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                updateResultOnUIThread(jsonResponse);
            }
        } else {
            updateResultOnUIThread(jsonResponse);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateResultOnUIThread(jsonResponse);
            } else {
                Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
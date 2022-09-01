package com.example.geeksretrofit;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private EditText descriptionEdt, date_publishedEdt;
    private Button postDataBtn, selectVideoBtn;
    private TextView responseTV;
    private ProgressBar loadingPB;
    public String path;
    public Uri uri;

    private static final int BUFFER_SIZE = 1024 * 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestMultiplePermissions();

        initialize();

        selectVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent,1);

                /*Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);*/

            }
        });


        //******************************* post request after click the button ********************************************************//
        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionEdt.getText().toString().isEmpty() && date_publishedEdt.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling a method to post the data and passing our name and job.
                File file = null;
                try {
                    file = new File(path);
                } catch (Exception e) {
                    Log.d("FILE SELECTED?",e.getMessage());
                }
                //DataModal creation
                //DataModal dataModal = new DataModal(descriptionEdt.getText().toString(), date_publishedEdt.getText().toString(), file);
                postData(descriptionEdt.getText().toString(), date_publishedEdt.getText().toString(), file);
                //postData(dataModal);
            }
        });
        //***********************************************************************************************************************//
    }



    //***********************************************************************//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Get the Uri of the selected file
            uri = data.getData();
            path = getFilePathFromURI(this, uri);
            Log.d("PATH OF SELECTED FILE:",path);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            //the image URI
            Uri selectedImageUri = data.getData();
            path = getFilePathFromURI(this, selectedImageUri);
            Log.d("FILE PATH IS?",path);


        }
    }*/
    //************************path selects successfully**********************//










    private void postData(String description, String date_published, File file) {

        // below line is for displaying our progress bar.
        loadingPB.setVisibility(View.VISIBLE);

        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody dateBody = RequestBody.create(MediaType.parse("text/plain"), date_published);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(/*getContentResolver().getType(uri)*/"video"),
                        file
                );
        MultipartBody.Part fileBody =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        /*Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();*/
        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // below line is to create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // passing data from our text fields to our modal class.
//        DataModal modal = new DataModal(description, date_published, file);

        // calling a method to create a post and passing our modal class.
        Call<ResponseBody> call = retrofitAPI.createPost(descBody, dateBody, fileBody);
        // on below line we are executing our method.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();

                    loadingPB.setVisibility(View.GONE);

                    date_publishedEdt.setText("");
                    descriptionEdt.setText("");

                    ResponseBody responseFromAPI = response.body();

                    String responseString = "Response Code : " + response.code() + "\ndescription : " + response.message() + "\n";

                    responseTV.setText(responseString);
                }

                else {
                    Log.d("RESPONSE ERROOOOOOR: ", response.errorBody().toString() + "   RESPONSE CODE:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // setting text to our text view when
                // we get error response from API.
                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }






    private void initialize(){
        descriptionEdt = findViewById(R.id.idEdtDescription);
        date_publishedEdt = findViewById(R.id.idEdtDate_published);
        postDataBtn = findViewById(R.id.idBtnPost);
        responseTV = findViewById(R.id.idTVResponse);
        loadingPB = findViewById(R.id.idLoadingPB);
        selectVideoBtn = findViewById(R.id.idBtnSelectVideo);
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }



    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path

        File wallpaperDirectory = new File(
                String.valueOf(Environment.getExternalStorageDirectory()));
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        File copyFile = new File(wallpaperDirectory + File.separator + Calendar.getInstance()
                .getTimeInMillis()+".mp4");
        // create folder if not exists

        copy(context, contentUri, copyFile);
        Log.d("vPath--->",copyFile.getAbsolutePath());

        return copyFile.getAbsolutePath();

    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = ((Cursor) cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}

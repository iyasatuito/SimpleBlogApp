package com.myblogapp.blogapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.myblogapp.blogapp.Models.BlogEntry;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.MyLogger;
import com.myblogapp.blogapp.Utils.PathUtils;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.Utils.UserUtil;
import com.myblogapp.blogapp.permissions.RxPermissionsExt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by piasatuito on 3/30/17.
 */

public class AddBlogActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.name) EditText title;
    @Bind(R.id.fileName) TextView fileName;
    @Bind(R.id.content) EditText content;
    @Bind(R.id.uploadImageBtn) Button uploadImageBtn;
    @Bind(R.id.createBtn) Button createBtn;

    public static final int SELECT_PICTURE_REQ_CODE = 100;
    public static final int CAMERA_REQUEST_CODE = 101;
    private Uri selectedPhotoUri;
    private String profileImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_blog_activity);
        ButterKnife.bind(this);

        uploadImageBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.uploadImageBtn:
                checkCameraPermission(view);
                break;
            case R.id.createBtn:
                createBlog();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = PathUtils.getPath(this, getSelectedPhotoUri());
                    profileImagePath = filePath;
                    updateFileName(profileImagePath);
                }
                break;
            case SELECT_PICTURE_REQ_CODE:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    String filePath = PathUtils.getPath(this, uri);
                    profileImagePath = filePath;
                    updateFileName(profileImagePath);
                }
                break;
            default:
        }
    }

    private void updateFileName(String profileImagePath) {
        File f = new File(profileImagePath);
        String fame = f.getName();
        fileName.setText(fame);
    }

    private void createBlog() {
        String uniqueId = UUID.randomUUID().toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = df.format(c.getTime());

        BlogEntry newBlogEntry = new BlogEntry();
        newBlogEntry.setID(uniqueId);
        newBlogEntry.setDate(" • " + formattedDate);
        newBlogEntry.setTitle(title.getText().toString());
        newBlogEntry.setContent(content.getText().toString());
        newBlogEntry.setActive(true);

        //temp only
        if (profileImagePath != null) {
            newBlogEntry.setImagePath(profileImagePath);
        }
        newBlogEntry.setAuthor(UserUtil.getInstance().getLoggedUser());
        RealmController.getInstance().save(newBlogEntry);

        MyLogger.showLog("author : " + UserUtil.getInstance().getLoggedUser());

        setResult(RESULT_OK);
        finish();

    }

    public void checkCameraPermission(View v) {
        RxPermissionsExt.requestOrDirectToSettings(this,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .takeUntil(RxView.detaches(v))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (!granted) {
                            return;
                        } else {
                            selectImage();
                        }
                    }
                });
    }

    private void selectImage() {
        final CharSequence[] csItems = getResources().
                getStringArray(R.array.choose_options);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Profile photo")
                .setItems(csItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openCamera();
                                break;
                            case 1:
                                openImageChooser();
                                break;
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();

    }

    private void openCamera() {
        Uri generatedPhotoFile = generatePhotoUri();
        selectedPhotoUri = generatedPhotoFile;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, generatedPhotoFile);

        if (canHandleExternalIntent(this, intent)) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "NO CAMERA DETECTED", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getSelectedPhotoUri() {
        return selectedPhotoUri;
    }

    public Uri generatePhotoUri() {
        File newfile = getAppRootDir();
        try {
            newfile.createNewFile();
        } catch (Exception e) {

        }
        return Uri.fromFile(newfile);
    }

    private boolean canHandleExternalIntent(Context context, Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    public static File getAppRootDir() {
        File rootDir = Environment.getExternalStorageDirectory();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(rootDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    private void openImageChooser() {
        try {
            Intent intent = getChooseImageIntent();
            if (Build.VERSION.SDK_INT < 19) {
                startActivityForResult(Intent.createChooser(intent, "TITLE"),
                        SELECT_PICTURE_REQ_CODE);
            } else {
                startActivityForResult(intent, SELECT_PICTURE_REQ_CODE);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "SORRY", Toast.LENGTH_SHORT).show();
        }
    }

    private static Intent getChooseImageIntent() {
        String mimeType = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mimeType = Intent.normalizeMimeType("image/*");
        }

        if (Build.VERSION.SDK_INT < 19) {
            // Launch intent to select video or photo
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg"); // Make sure only pictures are shown in gallery
            return intent;
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(mimeType);
            return intent;
        }
    }

}

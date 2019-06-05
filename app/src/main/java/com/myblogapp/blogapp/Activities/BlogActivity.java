package com.myblogapp.blogapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myblogapp.blogapp.Models.BlogEntry;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.MyLogger;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.Utils.UserUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.myblogapp.blogapp.Activities.BlogListActivity.EDIT_ENTRY;

/**
 * Created by piasatuito on 30/3/17.
 */

public class BlogActivity extends AppCompatActivity {

    @Bind(R.id.imageCover) ImageView imageCover;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.content) TextView content;

    private BlogEntry blogEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(R.color.colorPrimaryDark);

        Intent intent = getIntent();
        String blogId = intent.getStringExtra("id");
        blogEntry = RealmController.getInstance().getRealm().where(BlogEntry.class).
                equalTo("id", blogId).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(blogEntry.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(resizeImage(R.drawable.menu_white, 200,200));
        }

        Glide.with(this).load(blogEntry.getImagePath()).centerCrop().into(imageCover);
        title.setText(blogEntry.getTitle());
        date.setText(blogEntry.getDate());
        author.setText(UserUtil.getInstance().getLoggedUser().getName());
        content.setText(blogEntry.getContent());

    }

    private Drawable resizeImage(int resId, int w, int h)
    {
        // load the origial Bitmap
        Bitmap BitmapOrg = BitmapFactory.decodeResource(getResources(), resId);
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,width, height, matrix, true);
        return new BitmapDrawable(resizedBitmap);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions, menu);
        menu.findItem(R.id.action_favorite).setIcon(resizeImage(R.drawable.edit_white,200,200));
        menu.findItem(R.id.action_settings).setIcon(resizeImage(R.drawable.vertical_menu_white,200,200));
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                MyLogger.showLog("settings");
                return true;

            case R.id.action_favorite:
                MyLogger.showLog("edit");
                Intent intent = new Intent(this, EditBlogEntryActivity.class);
                intent.putExtra("id", blogEntry.getID());
                startActivityForResult(intent, EDIT_ENTRY);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Intent intent = getIntent();
            finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);


        }
    }

}

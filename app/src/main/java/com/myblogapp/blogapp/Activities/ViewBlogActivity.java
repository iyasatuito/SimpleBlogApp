package com.myblogapp.blogapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.myblogapp.blogapp.Models.BlogEntry;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.Utils.UserUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by piasatuito on 30/3/17.
 */

public class ViewBlogActivity extends AppCompatActivity {

    @Bind(R.id.imageCover) ImageView imageCover;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.author) TextView author;
    @Bind(R.id.content) TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(R.color.colorPrimaryDark);

        Intent intent = getIntent();
        String blogId = intent.getStringExtra("id");
        BlogEntry blogEntry = RealmController.getInstance().getRealm().where(BlogEntry.class).
                equalTo("id", blogId).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(blogEntry.getTitle());
            Drawable drawable = getResources().getDrawable(R.drawable.back_white);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, 80, 80, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);
        }

        Glide.with(this).load(blogEntry.getImagePath()).centerCrop().into(imageCover);
        title.setText(blogEntry.getTitle());
        date.setText(blogEntry.getDate());
        author.setText(UserUtil.getInstance().getLoggedUser().getName());
        content.setText(blogEntry.getContent());

    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        supportFinishAfterTransition();
    }
}

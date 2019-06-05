package com.myblogapp.blogapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myblogapp.blogapp.Models.BlogEntry;
import com.myblogapp.blogapp.Models.User;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.adapter.OtherUserBlogAdapter;
import com.myblogapp.blogapp.custom.SpacesItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * Created by piasatuito on 3/30/17.
 */

public class ViewBlogListActivity extends AppCompatActivity {

    @Bind(R.id.listview) RecyclerView listview;
    @Bind(R.id.emptyView) TextView emptyView;

    private RealmList<BlogEntry> usersBlogs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_blog_list_activity);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(R.color.colorPrimaryDark);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("user");

        User user = RealmController.getInstance().getRealm().where(User.class).
                equalTo("name", name).findFirst();

        usersBlogs = user.getBlogEntries();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name+"'s "+"blog articles");
            Drawable drawable = getResources().getDrawable(R.drawable.back_white);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, 80, 80, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        }

        showOriginalList();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(mLayoutManager);
        listview.addItemDecoration(new SpacesItemDecoration(25));

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

    public void viewBlogEntry(String id, ImageView imageCover) {
        Intent intent = new Intent(this, ViewBlogActivity.class);
        intent.putExtra("id", id);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageCover, "profile");
        startActivity(intent, options.toBundle());
    }

    private void showOriginalList() {
        listview.setAdapter(null);
        if (usersBlogs.size() > 0) {
            emptyView.setVisibility(View.GONE);
            OtherUserBlogAdapter blogAdapter = new OtherUserBlogAdapter(this, usersBlogs);
            listview.setAdapter(blogAdapter);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}

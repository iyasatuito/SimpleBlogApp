package com.myblogapp.blogapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myblogapp.blogapp.Models.BlogEntry;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.MyLogger;
import com.myblogapp.blogapp.Utils.UserUtil;
import com.myblogapp.blogapp.adapter.BlogAdapter;
import com.myblogapp.blogapp.custom.SpacesItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * Created by piasatuito on 3/30/17.
 */

public class BlogListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @Bind(R.id.listview) RecyclerView listview;
    @Bind(R.id.emptyView) TextView emptyView;
    @Bind(R.id.userName) TextView userName;
    @Bind(R.id.searchView) SearchView searchView;
    @Bind(R.id.nvView) NavigationView nvDrawer;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.navList) ListView mDrawerList;

    private BlogAdapter blogAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayAdapter<String> mAdapter;
    private int position;

    public static int EDIT_ENTRY = 1;
    public static int ADD_ENTRY = 2;
    public static int VIEW_ENTRY = 3;

    public SharedPreferences shared;
    public SharedPreferences.Editor edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_list_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(R.color.colorPrimaryDark);

        shared = getSharedPreferences("BlogPrefs", Context.MODE_PRIVATE);
        edit = shared.edit();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Blog articles");
            Drawable drawable= getResources().getDrawable(R.drawable.menu_white);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, 90, 90, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        }

        int width = (getResources().getDisplayMetrics().widthPixels / 3) * 2;
        assert nvDrawer != null;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams)
                nvDrawer.getLayoutParams();
        params.width = width;
        nvDrawer.setLayoutParams(params);
        userName.setText(shared.getString("user",""));

        mLayoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(mLayoutManager);

        showOriginalList();
        listview.addItemDecoration(new SpacesItemDecoration(25));

        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Find your blog");

        addDrawerItems();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               switch (position){
                   case 0:
                       mDrawer.closeDrawers();
                       startActivity(new Intent(BlogListActivity.this, UserListActivity.class));
                       break;
                   case 1:
                       MyLogger.showLog("Sign out");
                       UserUtil.getInstance().removeLoggedUser();
                       edit.putString("user","");
                       edit.apply();
                       mDrawer.closeDrawers();
                       Intent intent = new Intent(BlogListActivity.this, MainActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                       break;
               }
            }
        });

    }

    private void addDrawerItems() {
        String[] osArray = {"View Users", "Sign out"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }



    public void viewBlogEntry(String id, int adapterPosition, ImageView imageCover){
        position = adapterPosition;
        Intent intent = new Intent(this, BlogActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("position", position);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageCover, "profile");
        startActivityForResult(intent, BlogListActivity.VIEW_ENTRY, options.toBundle());
    }

    public void editBlogEntry(String id){
        Intent intent2 = new Intent(this, EditBlogEntryActivity.class);
        intent2.putExtra("id", id);
        startActivityForResult(intent2, BlogListActivity.EDIT_ENTRY);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivityForResult(new Intent(BlogListActivity.this,
                        AddBlogActivity.class), ADD_ENTRY);
                return true;
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_ENTRY && resultCode == RESULT_OK) {
                blogAdapter.notifyDataSetChanged();
        }

        if(requestCode == ADD_ENTRY && resultCode == RESULT_OK){
            Intent intent = getIntent();
            finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        if (requestCode == VIEW_ENTRY) {
            blogAdapter.notifyItemChanged(position);
            ((SimpleItemAnimator) listview.getItemAnimator()).setSupportsChangeAnimations(false);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        RealmList<BlogEntry> blogEntries = new RealmList<>();
        for (BlogEntry blog : UserUtil.getInstance().getLoggedUser().getBlogEntries()) {
            if (blog.getTitle().toLowerCase().contains(query)) {
                blogEntries.add(blog);
            }
        }
        if (blogEntries.size() > 0) {
            emptyView.setVisibility(View.GONE);
            blogAdapter.setList(blogEntries);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals("") || newText.isEmpty()){
            blogAdapter.setList(UserUtil.getInstance().getLoggedUser().getBlogEntries());
        }
        return false;
    }

    private void showOriginalList(){
        listview.setAdapter(null);
        if(UserUtil.getInstance().getLoggedUser().getBlogEntries().size() > 0) {
            emptyView.setVisibility(View.GONE);
            blogAdapter = new BlogAdapter(this, UserUtil.getInstance().getLoggedUser().getBlogEntries());
            listview.setAdapter(blogAdapter);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}

package com.myblogapp.blogapp.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.myblogapp.blogapp.Models.User;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.Utils.UserUtil;
import com.myblogapp.blogapp.adapter.UsersAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmList;

/**
 * Created by piasatuito on 30/3/17.
 */

public class UserListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener  {

    @Bind(R.id.userSearchview) SearchView userSearchview;
    @Bind(R.id.userRecycler) RecyclerView userRecycler;
    @Bind(R.id.emptyView) TextView emptyView;

    private UsersAdapter usersAdapter;
    private ArrayList<User> usersList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list_activity);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(R.color.colorPrimaryDark);

        usersList = new ArrayList<>(RealmController.getInstance().getRealm().
                where(User.class).notEqualTo("name", UserUtil.getInstance().getLoggedUser().getName()).findAll());
        showOriginalList();
        userRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        userSearchview.setOnQueryTextListener(this);
        userSearchview.setSubmitButtonEnabled(true);
        userSearchview.setQueryHint("Search for user");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        RealmList<User> users = new RealmList<>();
        for (User user : RealmController.getInstance().getRealm().where(User.class).notEqualTo("name",
                UserUtil.getInstance().getLoggedUser().getName()).findAll()) {
            if (user.getName().toLowerCase().contains(query)) {
                users.add(user);
            }
        }
        if(users.size() > 0 ) {
            ArrayList<User> usersList = new ArrayList<>(users);
            usersAdapter.setList(usersList);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.equals("") || newText.isEmpty()){
            usersAdapter.setList(usersList);
        }
        return false;
    }

    private void showOriginalList(){
        userRecycler.setAdapter(null);
        if(usersList.size() > 0) {
            emptyView.setVisibility(View.GONE);
            usersAdapter = new UsersAdapter(this, usersList);
            userRecycler.setAdapter(usersAdapter);

        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}

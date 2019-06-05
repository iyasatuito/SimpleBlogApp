package com.myblogapp.blogapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.myblogapp.blogapp.Activities.BlogListActivity;
import com.myblogapp.blogapp.Models.User;
import com.myblogapp.blogapp.R;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.Utils.UserUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.registerBtn) Button registerBtn;
    @Bind(R.id.loginBtn) Button loginBtn;

    @Bind(R.id.userName) EditText userName;
    @Bind(R.id.userPassword) EditText userPassword;

    public SharedPreferences shared;
    public SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(R.color.colorPrimaryDark);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shared = getSharedPreferences("BlogPrefs", Context.MODE_PRIVATE);
        edit = shared.edit();

        if(!shared.getString("user","").equals("")){
            UserUtil.getInstance().setLoggedUser(shared.getString("user",""));
            startActivity(new Intent(this, BlogListActivity.class));
            finish();
        }

        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.registerBtn:
                registerUser();
                break;
            case R.id.loginBtn:
                loginUser();
                break;

        }
    }

    private void registerUser() {

        User newUser = new User(userName.getText().toString(), userPassword.getText().toString());
        RealmController.getInstance().save(newUser);

        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show();
        userName.setText("");
        userPassword.setText("");

    }

    private void loginUser() {

        String name = userName.getText().toString();
        String password = userPassword.getText().toString();

        User loggedUser = RealmController.getInstance().getRealm().where(User.class).equalTo("name", name).equalTo("password", password).findFirst();
        User userWithWrongPassword =  RealmController.getInstance().getRealm().where(User.class).equalTo("name", name).notEqualTo("password", password).findFirst();
        User userNameNotExist =  RealmController.getInstance().getRealm().where(User.class).equalTo("name", name).findFirst();

        if (name.equals("") || password.equals("")) {

            Toast.makeText(this, "Please complete the login credentials", Toast.LENGTH_SHORT).show();

        } else if (userNameNotExist == null) {

            Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show();

        } else if (userWithWrongPassword != null) {

            Toast.makeText(this, "Username and password do not match", Toast.LENGTH_SHORT).show();

        } else if (loggedUser != null) {

            edit.putString("user", loggedUser.getName());
            edit.apply();

            UserUtil.getInstance().setLoggedUser(loggedUser.getName());
            startActivity(new Intent(this, BlogListActivity.class));
            finish();
        }

    }
}

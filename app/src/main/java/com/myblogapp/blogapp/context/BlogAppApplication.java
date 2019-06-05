package com.myblogapp.blogapp.context;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.myblogapp.blogapp.Utils.RealmController;
import com.myblogapp.blogapp.permissions.RxPermissionsExt;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by piasatuito on 3/29/17.
 */

public class BlogAppApplication extends Application {


  @Override public void onCreate() {
    super.onCreate();
    RxPermissionsExt.init(this);
    Stetho.initialize(
        Stetho.newInitializerBuilder(this)
            .enableDumpapp(
                Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
            .build());

    //Realm.init(this);
    RealmConfiguration config = new RealmConfiguration.Builder(this).name("blogApp.realm")
        .deleteRealmIfMigrationNeeded()
        .schemaVersion(1)
        .build();
    Realm.setDefaultConfiguration(config);

    RealmController.with(this);
  }
}

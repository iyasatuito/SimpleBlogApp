package com.myblogapp.blogapp.Utils;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by piasatuito on 1/27/17.
 */

public class RealmController<T extends RealmObject> {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        //realm.refresh();
    }

    public T save(T item) {
        realm.beginTransaction();
        T itemRealm = realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        return itemRealm;
    }

    public List<T> save(List<T> itemList) {
        MyLogger.showLog("save!");
        realm.beginTransaction();
        List<T> itemListRealm = realm.copyToRealmOrUpdate(itemList);
        realm.commitTransaction();
        return itemListRealm;
    }

}

package com.myblogapp.blogapp.permissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.myblogapp.blogapp.permissions.internal.PermissionsDirectionDialogFragment;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by pia on 31/3/17.
 */

public class RxPermissionsExt {

    static RxPermissions base;

    @MainThread
    public static void init(Context context) {
        base = RxPermissions.getInstance(context);
    }

    @MainThread
    public static void setLogging(boolean logging) {
        try {
            base.setLogging(logging);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Map emitted items from the source observable into {@code true} if permissions in parameters
     * are granted, or {@code false} if not.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public static Observable.Transformer<Object, Boolean> ensure(String... permissions) {
        try {
            return base.ensure(permissions);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Map emitted items from the source observable into {@link Permission} objects for each
     * permissions in parameters.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public static Observable.Transformer<Object, Permission> ensureEach(String... permissions) {
        try {
            return base.ensureEach(permissions);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    public static Observable<Boolean> request(String... permissions) {
        try {
            return base.request(permissions);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    public static Observable<Permission> requestEach(String... permissions) {
        try {
            return base.requestEach(permissions);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method is all permissions haven been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    public static Observable<Boolean> shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        try {
            return base.shouldShowRequestPermissionRationale(activity, permissions);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    public static boolean isGranted(String permission) {
        try {
            return base.isGranted(permission);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    public static boolean isRevoked(String permission) {
        try {
            return base.isRevoked(permission);
        } catch (NullPointerException e) {
            throw maybeThrowNotInit(e);
        }
    }

    /**
     * Returns true if all permissions are already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    public static boolean isGranted(@NonNull @Size(min = 1) String... permissions) {
        //noinspection ConstantConditions
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("RxPermissionsExt.isGranted requires at least one input permission");
        }
        boolean everythingGranted = true;
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                everythingGranted = false;
                break;
            }
        }
        return everythingGranted;
    }

    static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Like {@linkplain #request(String...) request()} but if user flagged NEVER ASK AGAIN,
     * requests permissions via a dialog that directs a user to the app settings.
     */
    public static Observable<Boolean> requestOrDirectToSettings(FragmentActivity activity, String... permissions) {
        return Observable.just(null).compose(ensureWithDirectToSettings(activity, permissions));
    }

    /**
     * Like {@linkplain #requestEach(String...) requestEach()} but if user flagged NEVER ASK AGAIN,
     * requests permissions via a dialog that directs a user to the app settings.
     */
    public static Observable<Permission> requestEachOrDirectToSettings(FragmentActivity activity, String... permissions) {
        return Observable.just(null).compose(ensureEachWithDirectToSettings(activity, permissions));
    }

    /**
     * Like {@linkplain #ensure(String...) ensure()} but if user flagged NEVER ASK AGAIN, requests
     * permissions via a dialog that directs a user to the app settings.
     */
    public static Observable.Transformer<Object, Boolean> ensureWithDirectToSettings(FragmentActivity activity, String... permissions) {
        return withDirectToSettings(activity, ensure(permissions), permissions);
    }

    /**
     * Like {@linkplain #ensureEach(String...) ensureEach()} but if user flagged NEVER ASK AGAIN,
     * requests permissions via a dialog that directs a user to the app settings.
     */
    public static Observable.Transformer<Object, Permission> ensureEachWithDirectToSettings(FragmentActivity activity, String... permissions) {
        return withDirectToSettings(activity, ensureEach(permissions), permissions);
    }

    static <T, R> Observable.Transformer<T, R> withDirectToSettings(final FragmentActivity activity, final Observable.Transformer<T, R> permissionsEnsurer, final String[] permissions) {
        if (!isMarshmallow()) {
            // Not marshmallow, let `permissionsEnsurer` do its thing to mark all permissions as granted
            // ...skipping complex calculations below.
            return permissionsEnsurer;
        }

        return new Observable.Transformer<T, R>() {
            @Override
            public Observable<R> call(Observable<T> o) {
                return o.flatMap(new Func1<T, Observable<? extends R>>() {
                    @Override
                    public Observable<? extends R> call(T trigger) {
                        // We do the checking in the beginning before the user has a chance to interact with each permission dialogs.
                        // To collect all those that were flagged NEVER ASK AGAIN in the past
                        // ...because no request dialog would be displayed for such permissions.

                        // This trick ensures that a custom request dialog would be displayed for such flagged permissions.

                        final ArrayList<String> hasNoRationale = new ArrayList<>();

                        for (String permission : permissions) {
                            if (!isGranted(permission) && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                // User either flagged NEVER ASK AGAIN before for this permission or this is the first run...
                                hasNoRationale.add(permission); // See also, see http://stackoverflow.com/a/31925748
                            }
                        }

                        Observable<R> request = Observable.just(trigger).compose(permissionsEnsurer);

                        // Then we display the appropriate message to direct the user to the app settings, that is, if any.

                        return hasNoRationale.isEmpty() ? request : request.doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                ArrayList<String> flaggedWithNeverAskAgain = new ArrayList<>(hasNoRationale.size());

                                for (String permission : hasNoRationale) {
                                    if (!isGranted(permission) && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                        // User flagged NEVER ASK AGAIN for this permission, for sure this time...
                                        flaggedWithNeverAskAgain.add(permission);
                                    }
                                }

                                // NOTE: If you're wondering why we performed 2 checks (i.e. one at the beginning,
                                // and then another one at the end), this is to ensure that if a user has just
                                // recently denied a permission with NEVER ASK AGAIN, we don't display our custom
                                // dialog right after that.
                                //
                                // That is, the 2 checks ensure that only the permissions in the PAST that the user
                                // already interacted with more than twice (i.e. first interaction without the
                                // NEVER ASK AGAIN checkbox and second interaction with it ticked) are the only
                                // ones that we display our custom dialog. The main key "word" here is "PAST".

                                if (!flaggedWithNeverAskAgain.isEmpty()) {
                                    PermissionsDirectionDialogFragment
                                            .newInstance(true, flaggedWithNeverAskAgain)
                                            .removePreviousAndShow(activity.getSupportFragmentManager());
                                }
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * Used to lazily evaluate whether {@link #base} was properly initialized. Use this inside a
     * catch clause that catches a {@link NullPointerException}. This method will evaluate whether
     * the NPE is caused by a {@code null} {@link #base} and throw the appropriate exception with
     * the proper message. The correct usage is as follows:
     * <pre>
     * try {
     *     *** protected code that uses `base` ***
     * } catch (NullPointerException e) {
     *     throw maybeThrowNotInit(e);
     * }
     * </pre>
     * <p>
     * The style that this utility should be used is merely an optimization so that null-checks are
     * deferred (i.e. to prevent unnecessary null-checks when exceptions are sure to be not thrown)
     * <p>
     * This method does not return.
     */
    @CheckResult
    private static NullPointerException maybeThrowNotInit(NullPointerException source) {
        if (base == null) {
            throw new NullPointerException("`RxPermissionsExt` not yet initialized! You must call `RxPermissionsExt.init(ctx)` before everything else.");
        }
        // Nope. `base` was initialized. So propagate the original exception.
        throw source;
    }

}

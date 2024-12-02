// ReactNativeAndroidXxpermissionModule.java

package com.reactlibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReactNativeAndroidXxpermissionModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public ReactNativeAndroidXxpermissionModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "XPERMISSION";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }


    @ReactMethod
    public void checkCameraPermission(final Callback successCallback, final Callback errorCallback) {
        // Permission.READ_EXTERNAL_STORAGE 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
        // 如果 targetSdk >= 33，则添加 Permission.READ_MEDIA_IMAGES 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
        // 如果 targetSdk < 33，则添加 Permission.READ_EXTERNAL_STORAGE 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
        // TIRAMISU == 33
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            XXPermissions.with(reactContext.getCurrentActivity())
                    .permission(Permission.CAMERA)
                    .permission(Permission.READ_EXTERNAL_STORAGE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                errorCallback.invoke("err:暂无相机权限");
                                return;
                            }else {
                                successCallback.invoke("success");
                            }
                        }
                    });
        } else {
            XXPermissions.with(reactContext.getCurrentActivity())
                    .permission(Permission.CAMERA)
                    .permission(Permission.READ_MEDIA_IMAGES)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                errorCallback.invoke("err:暂无相机权限");
                                return;
                            }else {
                                successCallback.invoke("success");
                            }
                        }
                    });
        }

    }

    @ReactMethod
    public void checkLocationPermission(final Callback successCallback, final Callback errorCallback) {
        XXPermissions.with(reactContext.getCurrentActivity())
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .interceptor(new PermissionInterceptor())
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            errorCallback.invoke("err:暂无定位权限");
                            return;
                        }else {
                            successCallback.invoke("success");
                        }
                    }
                });
    }

    @ReactMethod
    public void checkStoragePermission(final Callback successCallback, final Callback errorCallback) {
        // Permission.READ_EXTERNAL_STORAGE 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
        // 如果 targetSdk >= 33，则添加 Permission.READ_MEDIA_IMAGES 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
        // 如果 targetSdk < 33，则添加 Permission.READ_EXTERNAL_STORAGE 和 Permission.MANAGE_EXTERNAL_STORAGE 二选一
        // TIRAMISU == 33
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            XXPermissions.with(reactContext.getCurrentActivity())
                    .permission(Permission.READ_EXTERNAL_STORAGE)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                errorCallback.invoke("err:暂无存储权限");
                                return;
                            }else {
                                successCallback.invoke("success");
                            }
                        }
                    });
        } else {
            XXPermissions.with(reactContext.getCurrentActivity())
                    .permission(Permission.READ_MEDIA_IMAGES)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (!allGranted) {
                                errorCallback.invoke("err:暂无存储权限");
                                return;
                            }else {
                                successCallback.invoke("success");
                            }
                        }
                    });
        }

    }

    @ReactMethod
    public void canInstallApk(String permission, final Callback successCallback, final Callback errorCallback) {
        XXPermissions.with(reactContext.getCurrentActivity())
                .permission(Permission.REQUEST_INSTALL_PACKAGES)
                .interceptor(new PermissionInterceptor())
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (!allGranted) {
                            errorCallback.invoke("err:暂无安装权限");
                            return;
                        }else {
                            successCallback.invoke("success");
                        }

                    }
                });
    }

    @ReactMethod
    public void installApk(String apkPath, final Callback successCallback, final Callback errorCallback) {
        try {
            File file = new File(apkPath);
            // 这里有文件流的读写，需要处理一下异常
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24
                String packageName = reactContext.getApplicationContext().getPackageName();
                String authority = packageName + ".fileprovider";
                Uri uri = FileProvider.getUriForFile(reactContext, authority, file);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }
            reactContext.startActivity(intent);
            successCallback.invoke("success");
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.invoke(e.toString());
        }
    }

    @ReactMethod
    public void isGrantedPermission(String permission, final Callback successCallback, final Callback errorCallback) {
        try {
            // XXPermissions.isGranted(reactContext, permission) === true 权限已授予
            successCallback.invoke(XXPermissions.isGranted(reactContext, permission));
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.invoke(e.toString());
        }
    }

    @ReactMethod
    public void isGrantedPermissions(ReadableArray permissions, final Callback successCallback, final Callback errorCallback) {
        try {
            List<String> permission = new ArrayList<>();
            for (int i = 0; i < permissions.size(); i++)
                permission.add(permissions.getString(i));
            successCallback.invoke(XXPermissions.isGranted(reactContext, permission));
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.invoke(e.toString());
        }
    }
}

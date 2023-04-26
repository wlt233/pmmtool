package moe.tqlwsl.pmmtool;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class xp implements IXposedHookLoadPackage {
    ClassLoader mclassloader = null;
    Context mcontext = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedBridge.log(lpparam.packageName);

        if (lpparam.packageName.equals("com.android.nfc")) {

            XposedBridge.log("Inside " + lpparam.packageName);

            XposedHelpers.findAndHookMethod("com.android.nfc.NfcApplication",
                lpparam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    XposedBridge.log("Inside com.android.nfc.NfcApplication#onCreate");
                    super.beforeHookedMethod(param);
                    Application application = (Application) param.thisObject;
                    mcontext = application.getApplicationContext();
                    XposedBridge.log("Got context");
                }
            });


            XposedHelpers.findAndHookMethod("android.nfc.cardemulation.NfcFCardEmulation",
                lpparam.classLoader, "isValidSystemCode", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    XposedBridge.log("Inside android.nfc.cardemulation.NfcFCardEmulation#isValidSystemCode");
                    Class activitythreadclass = lpparam.classLoader.loadClass("android.app.ActivityThread");
                    Object activityobj = XposedHelpers.callStaticMethod(activitythreadclass, "currentActivityThread");
                    Object mInitialApplication = XposedHelpers.getObjectField(activityobj, "mInitialApplication");
                    Object mLoadedApk = XposedHelpers.getObjectField(mInitialApplication, "mLoadedApk");
                    mclassloader = (ClassLoader) XposedHelpers.getObjectField(mLoadedApk, "mClassLoader");
                    //mclassloader = mcontext.getClassLoader();
                    XposedBridge.log("Got classloader");
                    String path = getSoPath();
                    XposedBridge.log("So path = " + path);
                    int version = android.os.Build.VERSION.SDK_INT;
                    try {
                        if (!path.equals("")) {
                            XposedBridge.log("Start inject libpmm.so");
                            if (version >= 28) {
                                XposedHelpers.callMethod(Runtime.getRuntime(), "nativeLoad", path, mclassloader);
                            } else {
                                XposedHelpers.callMethod(Runtime.getRuntime(), "doLoad", path, mclassloader);
                            }
                            XposedBridge.log("Injected libpmm.so");
                        }
                    } catch (Exception e) {
                        XposedBridge.log(e);
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private String getSoPath() {
        try {
            String text = "";
            PackageManager pm = mcontext.getPackageManager();
            List<PackageInfo> pkgList = pm.getInstalledPackages(0);
            if (pkgList.size() > 0) {
                for (PackageInfo pi: pkgList) {
                    if (pi.applicationInfo.publicSourceDir.contains("moe.tqlwsl.pmmtool")) {
                        text = pi.applicationInfo.publicSourceDir.replace("base.apk", "lib/arm64/libpmm.so");
                        return text;
                    }
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
            e.printStackTrace();
        }
        return "";
    }

}

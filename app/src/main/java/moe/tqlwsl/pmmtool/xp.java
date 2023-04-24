package moe.tqlwsl.pmmtool;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class xp implements IXposedHookLoadPackage {
    ClassLoader mclassloader = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log(lpparam.packageName);
        if (lpparam.packageName.equals("com.android.nfc")) {
            XposedHelpers.findAndHookMethod("android.nfc.cardemulation.NfcFCardEmulation",
                    lpparam.classLoader, "isValidSystemCode", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    // 获取classloader
                    XposedBridge.log("Xposed in afterHookedMethod");
                    Class activitythreadclass = lpparam.classLoader.loadClass("android.app.ActivityThread");
                    Object activityobj = XposedHelpers.callStaticMethod(activitythreadclass, "currentActivityThread");
                    Object mInitialApplication = XposedHelpers.getObjectField(activityobj, "mInitialApplication");
                    Object mLoadedApk = XposedHelpers.getObjectField(mInitialApplication, "mLoadedApk");
                    mclassloader = (ClassLoader) XposedHelpers.getObjectField(mLoadedApk, "mClassLoader");
                    XposedBridge.log("classloader change success");
                    String path = getSoPath();
                    XposedBridge.log(path);
                    int version = android.os.Build.VERSION.SDK_INT;
                    if (!path.equals("")){
                        if (version >= 28) {
                            XposedHelpers.callMethod(Runtime.getRuntime(), "nativeLoad", path, mclassloader);
                        } else {
                            XposedHelpers.callMethod(Runtime.getRuntime(), "doLoad", path, mclassloader);
                        }
                        XposedBridge.log("start inject libpmm.so");
                    }
                }
            });
        }
    }

    private String getSoPath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                InputStream inputStream = null;
                Reader reader = null;
                BufferedReader bufferedReader = null;
                try {
                    File file = new File("/sdcard/pmmtool", "soPath.txt");
                    inputStream = new FileInputStream(file);
                    reader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(reader);
                    StringBuilder result = new StringBuilder();
                    String temp;
                    while ((temp = bufferedReader.readLine()) != null) {
                        result.append(temp);
                    }
                    XposedBridge.log("read so path is " + result.toString());
                    return result.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        return "";
    }
}

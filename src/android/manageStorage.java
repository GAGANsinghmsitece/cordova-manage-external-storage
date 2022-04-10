package cordova.manage.external.storage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.PluginResult;


import android.os.Environment;


public class manageStorage extends CordovaPlugin {

  private static final String CHECK_PERMISSION="checkPermission";
  private static final String REQUEST_PERMISSION="requestPermission";

  private static final String KEY_ERROR="error";
  private static final String KEY_MESSAGE="message";
  private static final String KEY_RESULT_PERMISSION="hasPermission";

  private CallbackContext permissionCallback=null;
  public final int MY_OP = 11;
  private ActivityResultLauncher<Intent> activityResultLauncher;
  private static String TAG = "Permissions";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      Log.i(TAG, action);
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }else if(action.equals(CHECK_PERMISSION)){
          cordova.getThreadPool().execute(new Runnable(){
            public void run(){
              checkPermission(callbackContext);
            }
          });
          return true;
        }else if(action.equals(REQUEST_PERMISSION)){
          cordova.getThreadPool().execute(new Runnable(){
            public void run(){
              try{
                requestPermission(callbackContext);
              }catch(Exception e){
                e.printStackTrace();
                JSONObject result=new JSONObject();
                addProperty(result,KEY_ERROR,REQUEST_PERMISSION);
                addProperty(result,KEY_MESSAGE,"Request permission has been denied");
                callbackContext.error(result);
                permissionCallback=null;
              }
            }
          });
            return true;
        }
        return false;
    }

    private void success(CallbackContext callbackContext,boolean current_status){
      JSONObject result=new JSONObject();
      try{
          result.put("status",current_status);
      }catch(JSONException e){
        callbackContext.error("An error has occurred");
      }
      callbackContext.success(result);
    }

    private boolean hasPermission(){
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
        return Environment.isExternalStorageManager();
      }else{
        Context context=this.cordova.getActivity().getApplicationContext();
        int readcheck= ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int writecheck=ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (readcheck== PackageManager.PERMISSION_GRANTED && writecheck==PackageManager.PERMISSION_GRANTED);
      }
    }

    private void checkPermission(CallbackContext callbackContext) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
          if(Environment.isExternalStorageManager()){
            JSONObject response=new JSONObject();
            addProperty(response,"response_code",1);
            addProperty(response,"message","we have the permission");
            callbackContext.success(response);
          }else{
            JSONObject response=new JSONObject();
            addProperty(response,"response_code",2);
            addProperty(response,"message","we do not have the permission");
            callbackContext.success(response);
          }
        }else{
          JSONObject response=new JSONObject();
          addProperty(response,"response_code",3);
          addProperty(response,"message","Device API is below 30");
          callbackContext.success(response);
        }
        callbackContext.error(" An error has occurred");
    }



    private void requestPermission(CallbackContext callbackContext) throws Exception{
      if(hasPermission()){
        JSONObject result=new JSONObject();
        addProperty(result,KEY_RESULT_PERMISSION,true);
        callbackContext.success(result);
      }else{
        permissionCallback=callbackContext;

        Activity activity =this.cordova.getActivity();
        Context context=this.cordova.getActivity().getApplicationContext();

        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cordova.setActivityResultCallback (this);
        cordova.startActivityForResult(this,intent,MY_OP);

      }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      if( requestCode == MY_OP ){
        JSONObject response=new JSONObject();
        addProperty(response,"request Code",requestCode);
        addProperty(response,"result code",resultCode);
        addProperty(response,"data",data);

        PluginResult result = new PluginResult(PluginResult.Status.OK,response);
        result.setKeepCallback(true);
        permissionCallback.sendPluginResult(result);
      }else{
        JSONObject response=new JSONObject();
        addProperty(response,"request Code",requestCode);
        addProperty(response,"result code",resultCode);
        PluginResult result = new PluginResult(PluginResult.Status.OK,response);
        result.setKeepCallback(true);
        permissionCallback.sendPluginResult(result);
      }
    }



    private void addProperty(JSONObject obj, String key, Object value) {
      try {
        if (value == null) {
          obj.put(key, JSONObject.NULL);
        } else {
          obj.put(key, value);
        }
      } catch (JSONException ignored) {

      }
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}

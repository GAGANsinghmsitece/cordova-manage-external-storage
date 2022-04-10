# Cordova Mange External Storage
This plugin request `MANAGE_EXTERNAL_STORAGE` on android devices with API 30+. The request gives access of all files to the app. You can read more about it here:-\
[Android Developers:- Manage all files](https://developer.android.com/training/data-storage/manage-all-files)

## Installation
The plugin can be installed by following command:-

`cordova plugin add https://github.com/GAGANsinghmsitece/cordova-manage-external-storage`

After installing the files you need to declare the permissions in `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## Methods
The plugin provides following methods:-
1. `checkPermission`:- This method checks whether we have the permission or not.
2. `requestPermission`:- This method requests the permission.

## Examples
1. We can check whether we have the permission or not using the following example:-
```javascript
cordova.plugins.manageStorage.checkPermission(function(result){
    if(result.response_code===1){
        //we have the permission
        return true;
    }else if(result.response_code===2){
        //we do not have the permission
        return false;
    }else{
        //API of device is below 30. Here, you can request WRITE_EXTERNAL_STORAGE permission here using cordova-plugin-android-permissions
    }
});
```
2. For requesting the permission, you can use `requestPermission` method as follows:-
```javascript
cordova.plugins.manageStorage.requestPermission(function(result){
    //here you can check the permission
});
```
It will launch settings app where user can grant permission to your app. Sadly, the intent does not return any result, hence, it is impossible to know if the user has provided the permission or simply closed the intent. To check the permission, you can use onResume lifecycle hook of cordova application.

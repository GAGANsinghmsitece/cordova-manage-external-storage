var exec = require('cordova/exec');

/*exports.coolMethod = function (arg0, success, error) {
    exec(success, error, 'manageStorage', 'coolMethod', [arg0]);
    /*
        success:- the callback which is called in case of success
        error:- the callback which is called in case of failure
        'manageS':- the class(in android) which we are calling to implement native method
        'coolMethod':- It is the string identifier which will be used to find the function inside java class
        [arg0]:- the arguments array.

};*/
exports.checkPermission=function(success,error){
    exec(success,error,'manageStorage','checkPermission',[]);
};
exports.requestPermission=function(packagename,success,error){
    exec(success,error,'manageStorage','requestPermission',[]);
};

// main index.js

import { NativeModules, Platform } from 'react-native';

const isAndroid = Platform.OS === 'android';

const { XPERMISSION } = NativeModules;

//相机权限
function checkCameraPermission() {
    return new Promise((resolve, reject) => {
        if (!isAndroid) {
            reject("not support");
            return;
        }
        XPERMISSION.checkCameraPermission(
            (result) => {
                resolve(result);
            }, () => {
                reject("cancel");
            });
    });
}

//相机最小权限
function checkCameraMiniPermission() {
    return new Promise((resolve, reject) => {
        if (!isAndroid) {
            reject("not support");
            return;
        }
        XPERMISSION.checkCameraMiniPermission(
            (result) => {
                resolve(result);
            }, () => {
                reject("cancel");
            });
    });
}

//定位权限
function checkLocationPermission() {
    return new Promise((resolve, reject) => {
        if (!isAndroid) { reject("not support"); return; }
        XPERMISSION.checkLocationPermission(
            (result) => { resolve(result); },
            () => {
                reject("cancel");
            });
    });
}

//存储权限
function checkStoragePermission() {
    return new Promise((resolve, reject) => {
        if (!isAndroid) { reject("not support"); return; }
        XPERMISSION.checkStoragePermission(
            (result) => { resolve(result); },
            () => {
                reject("cancel");
            });
    });
}

//permissions: string
function isGrantedPermission(permission) {
    return new Promise((resolve, reject) => {
        if (!isAndroid) {
            reject("not support");
            return;
        }
        XPERMISSION.isGrantedPermission(permission, resolve, reject);
    });
}

//permissions: string[]
function isGrantedPermissions(permissions) {
    return new Promise((resolve, reject) => {
        if (!isAndroid) {
            reject("not support");
            return;
        }
        XPERMISSION.isGrantedPermissions(permissions, resolve, reject);
    });
}

/**
 * 安装本地Apk文件
 * @param path 本地路径
 * @platform Android
 */
function installApk(path) {
    return new Promise((resolve, reject) => {
        if (isAndroid)
            XPERMISSION.installApk(path, () => resolve(), (e) => reject(e));
        else
            reject('Not support');
    });
}


/**
 * 安装权限
 * 
 * 
 * 
 */
function canInstallApk() {
    return new Promise((resolve, reject) => {
        if (!isAndroid) {
            reject("not support");
            return;
        }
        XPERMISSION.isGrantedPermissions(permissions, resolve, reject);
    });
}

// export default xpermission;
export { isGrantedPermission, isGrantedPermissions, installApk, checkCameraPermission, checkCameraMiniPermission, checkLocationPermission, checkStoragePermission };

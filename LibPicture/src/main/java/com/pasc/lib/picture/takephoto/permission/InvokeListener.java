package com.pasc.lib.picture.takephoto.permission;

import com.pasc.lib.picture.takephoto.model.InvokeParam;

/**
 * 授权管理回调
 */
public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}

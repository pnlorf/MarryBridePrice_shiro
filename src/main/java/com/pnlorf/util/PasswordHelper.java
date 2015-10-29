package com.pnlorf.util;

import com.pnlorf.entity.UserFormMap;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Created by 冰诺莫语 on 2015/10/29.
 */
public class PasswordHelper {
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    private String algorithmName = "md5";

    private int hashIterations = 2;

    public void encryptPassword(UserFormMap userFormMap) {
        String salt = randomNumberGenerator.nextBytes().toHex();
        userFormMap.put("credentialsSalt", salt);
        String newPassword = new SimpleHash(algorithmName, userFormMap.get("password"), ByteSource.Util.bytes(userFormMap.get("accountName") + salt), hashIterations).toHex();
        userFormMap.put("password", newPassword);
    }
}

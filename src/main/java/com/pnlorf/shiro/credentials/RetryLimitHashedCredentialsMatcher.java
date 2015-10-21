package com.pnlorf.shiro.credentials;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.ByteSource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String) token.getPrincipal();
        // retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);

        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }

        if (retryCount.incrementAndGet() > 5) {
            // if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }

        boolean matches = super.doCredentialsMatch(token, info);

        if (matches) {
            passwordRetryCache.remove(username);
        }
        return matches;
    }

    public String buildCredentials(String userName, String password, String credentialsSalt) {
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName, password, ByteSource.Util.bytes(userName + credentialsSalt), userName);
        AuthenticationToken token = new UsernamePasswordToken(userName, password);
        return super.hashProvidedCredentials(token, authenticationInfo).toString();
    }
}

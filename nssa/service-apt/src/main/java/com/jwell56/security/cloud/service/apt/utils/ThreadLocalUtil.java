package com.jwell56.security.cloud.service.apt.utils;

import com.jwell56.security.cloud.service.apt.entity.User;

/**
 * 1、登陆后，将用户登录信息绑定到工具类中
 * 2、该工具类根据线程来判定是否是同一个用户，注意思路
 */
public class ThreadLocalUtil {

    private ThreadLocal<User> userInfoThreadLocal = new ThreadLocal<>();

    //new一个实例
    private static final ThreadLocalUtil instance = new ThreadLocalUtil();

    //私有化构造
    private ThreadLocalUtil() {
    }

    //获取单例
    public static ThreadLocalUtil getInstance() {
        return instance;
    }

    /**
     * 将用户对象绑定到当前线程中，键为userInfoThreadLocal对象，值为userInfo对象
     * @param userInfo
     * 21
     */
    public void bind(User userInfo) {
        userInfoThreadLocal.set(userInfo);
    }

    /**
     * 将用户数据绑定到当前线程中，键为userInfoThreadLocal对象，值为userInfo对象
     * @param userId
     */
    public void bind(Integer userId) {
        User userInfo = new User();
        userInfo.setUserId(userId);
        bind(userInfo);
    }

    /**
     * 得到绑定的用户对象
     * @return
     */
    public User getUserInfo() {
        User userInfo = userInfoThreadLocal.get();
        return userInfo;
    }

    /**
     * 移除绑定的用户对象
     */
    public void remove() {
        userInfoThreadLocal.remove();
    }

}

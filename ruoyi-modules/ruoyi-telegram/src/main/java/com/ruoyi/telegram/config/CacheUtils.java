package com.ruoyi.telegram.config;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.ruoyi.telegram.model.Admin;
import com.ruoyi.telegram.model.Chat;
import com.ruoyi.telegram.model.Member;
import com.ruoyi.telegram.model.Setting;

import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheUtils {
    public static Map<Long, Setting> settingMap = new HashMap<>();
    public static Admin adminCache = new Admin();

    public static String keyWords = "";
    public static List<Chat> chats = new ArrayList<>();
    public static TimedCache<String, User> exitsMember = CacheUtil.newTimedCache(60 * 1000);

    public static TimedCache<Long, Member> members = cn.hutool.cache.CacheUtil.newTimedCache(60 * 1000 * 60 * 24);


    public static TimedCache<String, File> files = cn.hutool.cache.CacheUtil.newTimedCache(60 * 1000 * 60 * 24);
}

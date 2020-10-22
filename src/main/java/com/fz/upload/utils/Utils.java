package com.fz.upload.utils;

import com.fz.upload.models.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**
     * 将source 对象中不为空的属性值赋给当前对象对应的属性
     *
     * @param target 目标对象
     * @param source 源对象
     * @author dingpeihua
     * @date 2019/3/21 09:17
     * @version 1.0
     */
    public static <T> T combineField(T target, T source) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] targetFields = target.getClass().getDeclaredFields();
        for (int i = 0; i < sourceFields.length; i++) {
            try {
                Field sourceField = sourceFields[i];
                Field targetField = targetFields[i];
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                Object sourceValue = sourceField.get(source);
                Object targetValue = targetField.get(target);
                if (sourceValue != null) {
                    if (checkData(sourceValue) || targetValue == null) {
                        if (!"".equals(sourceValue) && !Modifier.isFinal(targetField.getModifiers())) {
                            targetField.set(target, sourceValue);
                        }
                    } else {
                        combineField(targetValue, sourceValue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return target;
    }

    /**
     * 检查当前对象类型是否是基本数据类型（包括{@link Date}、
     * {@link BigDecimal}、{@link BigInteger}及{@link Character}）
     *
     * @param value
     * @author dingpeihua
     * @date 2019/4/1 14:29
     * @version 1.0
     */
    public static boolean checkData(Object value) {
        return checkData(value.getClass());
    }

    /**
     * 检查当前类型是否是基本数据类型（包括{@link Date}、
     * {{@link BigDecimal}、{@link BigInteger}及{@link Character}）
     *
     * @author dingpeihua
     * @date 2019/4/1 14:29
     * @version 1.0
     */
    public static boolean checkData(Class clazz) {
        return (clazz.equals(String.class)
                || clazz.equals(Integer.class)
                || clazz.equals(int.class)
                || clazz.equals(Byte.class)
                || clazz.equals(byte.class)
                || clazz.equals(Long.class)
                || clazz.equals(long.class)
                || clazz.equals(Double.class)
                || clazz.equals(double.class)
                || clazz.equals(Float.class)
                || clazz.equals(float.class)
                || clazz.equals(Character.class)
                || clazz.equals(Short.class)
                || clazz.equals(short.class)
                || clazz.equals(BigDecimal.class)
                || clazz.equals(BigInteger.class)
                || clazz.equals(Boolean.class)
                || clazz.equals(boolean.class)
                || clazz.equals(Date.class)
                || clazz.isPrimitive());
    }

    /**
     * 创建文件夹名
     *
     * @author dingpeihua
     * @date 2019/4/20 09:44
     * @version 1.0
     */
    public static String createFilePath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(path.getBytes(StandardCharsets.UTF_8));
            byte[] data = m.digest();
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                result.append(Integer.toHexString((0x000000ff & data[i]) | 0xffffff00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path.replace(".", "_");
    }

    /**
     * 判断链接是否有效
     *
     * @param urlLink 输入链接
     * @return true 当前URL有效，否则url无效
     * @author dingpeihua
     * @date 2020/1/19 9:27
     * @version 1.0
     */
    public static boolean isUrlValid(String urlLink) {
        try {
            URL url = new URL(urlLink);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("HEAD");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            int code = urlConnection.getResponseCode();
            String message = urlConnection.getResponseMessage();
            System.out.println("isUrlValid>>>code:" + code + ",message:" + message + ",url:" + urlLink);
            urlConnection.disconnect();
            //404表示当前文件不存在
            return code != 404;
        } catch (Exception e) {
            System.out.println("isUrlValid>>>message:" + e.getMessage() + ",url:" + urlLink);
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 编码中文
     *
     * @param url
     * @author dingpeihua
     * @date 2019/7/6 18:47
     * @version 1.0
     */
    public static String encodeChinese(String url) {
        try {
            Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url);
            while (matcher.find()) {
                String tmp = matcher.group();
                url = url.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 判断多个字符串是否为空
     *
     * @param str
     * @author dingpeihua
     * @date 2019/12/24 17:33
     * @version 1.0
     */
    public static boolean isEmpty(CharSequence... str) {
        for (CharSequence charSequence : str) {
            if (StringUtils.isEmpty(charSequence)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 版本号1.0.0-beta规则
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[vV]?(([0-9]|([1-9]([0-9]*))).){1,5}([0-9]|([1-9]([0-9]*)))([-](([0-9A-Za-z]|([1-9A-Za-z]([0-9A-Za-z]*)))[.])*([0-9A-Za-z]|([1-9A-Za-z]([0-9A-Za-z]*))))?([+](([0-9A-Za-z]+)[.])*([0-9A-Za-z]+))?$");
    /**
     * 版本号1.0.0规则
     */
    private static final Pattern VERSION_PATTERN2 = Pattern.compile("^[vV]?(([0-9]|([1-9]([0-9]*))).){1,5}([0-9]|([1-9]([0-9]*)))$");

    /**
     * 判断版本号是否正确,后面可跟Alpha 或beta
     *
     * @param version
     * @author dingpeihua
     * @date 2020/1/21 17:45
     * @version 1.0
     */
    public static boolean isValidVersionAlphaOrBeta(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        return matcher.find();
    }

    /**
     * 判断版本号是否正确，只能数字加点
     *
     * @param version
     * @author dingpeihua
     * @date 2020/1/21 17:45
     * @version 1.0
     */
    public static boolean isValidVersion(String version) {
        Matcher matcher = VERSION_PATTERN2.matcher(version);
        return matcher.find();
    }

    /**
     * 版本号比较，版本号逐位比较
     *
     * @param version1 第一个版本号
     * @param version2 第二个版本号
     * @author dingpeihua
     * @date 2020/3/23 14:49
     * @version 1.0
     */
    public static int compareVersion(String version1, String version2) {
        if (!isValidVersion(version1) || !isValidVersion(version2)) {
            throw new IllegalArgumentException("Verion number is invalid.");
        }
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int len1 = versionArray1.length;
        int len2 = versionArray2.length;
        int len = Math.min(len1, len2);
        //共有版本号部分，从前向后比较对应位置数字
        int x1, x2;
        for (int i = 0; i < len; i++) {
            x1 = Integer.parseInt(versionArray1[i]);
            x2 = Integer.parseInt(versionArray2[i]);
            if (x1 > x2) {
                return 1;
            } else if (x1 < x2) {
                return -1;
            }
        }
        //共有版本号相等的情况下，谁的版本号段数更多且多余部分不全为0，谁的版本更新
        if (len1 > len2) {
            for (int i = len; i < len1; i++) {
                if (Integer.parseInt(versionArray1[i]) > 0) {
                    return 1;
                }
            }
        } else if (len1 < len2) {
            for (int i = len; i < len2; i++) {
                if (Integer.parseInt(versionArray2[i]) > 0) {
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void notNull( Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Pseudo URL prefix for loading from the class path: "classpath:".
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * Resolve the given resource location to a {@code java.net.URL}.
     * <p>Does not check whether the URL actually exists; simply returns
     * the URL that the given location would correspond to.
     *
     * @param resourceLocation the resource location to resolve: either a
     *                         "classpath:" pseudo URL, a "file:" URL, or a plain file path
     * @return a corresponding URL object
     * @throws FileNotFoundException if the resource cannot be resolved to a URL
     */
    public static URL getURL(String resourceLocation) throws FileNotFoundException {
        notNull(resourceLocation, "Resource location must not be null");
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
            ClassLoader cl = getDefaultClassLoader();
            URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
            if (url == null) {
                String description = "class path resource [" + path + "]";
                throw new FileNotFoundException(description +
                        " cannot be resolved to URL because it does not exist");
            }
            return url;
        }
        try {
            // try URL
            return new URL(resourceLocation);
        } catch (MalformedURLException ex) {
            // no URL -> treat as file path
            try {
                return new File(resourceLocation).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new FileNotFoundException("Resource location [" + resourceLocation +
                        "] is neither a URL not a well-formed file path");
            }
        }
    }

    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = Utils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    public static HttpResponse<String> parseResponse(String result) {
        return new Gson().fromJson(result, new TypeToken<HttpResponse<String>>() {
        }.getType());
    }

    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private static final String IP_ADDRESS_STRING =
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))";
    public static final Pattern IP_ADDRESS = Pattern.compile(IP_ADDRESS_STRING);

    /**
     * 验证是否是邮箱
     *
     * @param target 要验证的文本
     * @return 是返回true, 否则返回false
     */
    public static boolean isEmail(String target) {
        return !StringUtils.isEmpty(target) && EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * 验证是否是邮箱
     *
     * @param target 要验证的文本
     * @return 是返回true, 否则返回false
     */
    public static boolean isIpAddress(String target) {
        return StringUtils.isNotEmpty(target) && IP_ADDRESS.matcher(target).matches();
    }
}

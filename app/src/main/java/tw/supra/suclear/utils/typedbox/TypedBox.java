package tw.supra.suclear.utils.typedbox;

import android.os.Handler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 泛型工具箱
 *
 * @author wangjia20
 * @since 2019-06-13
 */
public final class TypedBox {

    /**
     * 私有构造函数
     */
    private TypedBox() {
    }

    /**
     * 安全的泛型回调
     *
     * @param callback 泛型回调
     * @param msg      消息
     * @param <MsgT>   消息泛型
     */
    public static <MsgT> void safeCallback(TypedCallback<MsgT> callback, MsgT msg) {
        if (null != callback) {
            callback.onCallback(msg);
        }
    }

    /**
     * 安全的泛型映射
     *
     * @param mapping  泛型映射
     * @param key      键
     * @param <KeyT>   键泛型
     * @param <ValueT> 值泛型
     * @return 值
     */
    public static <KeyT, ValueT> ValueT safeMapping(TypedMapping<KeyT, ValueT> mapping, KeyT key) {
        return safeMapping(mapping, key, null);
    }

    /**
     * 安全的泛型映射
     *
     * @param mapping       泛型映射
     * @param key           键
     * @param fallbackValue 兜底值
     * @param <KeyT>        键泛型
     * @param <ValueT>      值泛型
     * @return 值
     */
    public static <KeyT, ValueT> ValueT safeMapping(
            TypedMapping<KeyT, ValueT> mapping, KeyT key, ValueT fallbackValue) {
        return (null == mapping) ? fallbackValue : mapping.map(key);
    }

    /**
     * 遍历
     *
     * @param action     遍历执行的操作
     * @param collection 遍历的对象集合
     * @param <T>        遍历的对象类型
     */
    public static <T> void forEach(TypedCallback<T> action, T... collection) {
        forEach(null, action, collection);
    }

    /**
     * 遍历
     *
     * @param action     遍历执行的操作
     * @param collection 遍历的对象集合
     * @param <T>        遍历的对象类型
     */
    public static <T> void forEach(TypedCallback<T> action, Collection<T> collection) {
        forEach(null, action, collection);
    }

    /**
     * 遍历
     *
     * @param handler    执行的操作目标 Handler
     * @param action     遍历执行的操作
     * @param collection 遍历的对象集合
     * @param <T>        遍历的对象类型
     */
    public static <T> void forEach(Handler handler, TypedCallback<T> action, T... collection) {
        if (null == action || null == collection || collection.length < 1) {
            return;
        }
        for (T item : collection) {
            invoke(handler, action, item);
        }
    }

    /**
     * 遍历
     *
     * @param handler    执行的操作目标 Handler
     * @param action     遍历执行的操作
     * @param collection 遍历的对象集合
     * @param <T>        遍历的对象类型
     */
    public static <T> void forEach(Handler handler, final TypedCallback<T> action, Collection<T> collection) {
        if (null == action || null == collection || collection.isEmpty()) {
            return;
        }
        for (final T item : collection) {
            invoke(handler, action, item);
        }
    }

    /**
     * 调用
     *
     * @param handler 执行的操作目标 Handler
     * @param action  执行的操作
     * @param item    对象
     */
    public static <T> void invoke(Handler handler, final TypedCallback<T> action, final T item) {
        if (null == handler) {
            action.onCallback(item);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    action.onCallback(item);
                }
            });
        }
    }

    /**
     * 转成 HashMap
     *
     * @param map       给定 map
     * @param allowCast 允许直接强转类型返回
     * @param <KeyT>    键类型
     * @param <ValueT>  值类型
     * @return hashmap
     */
    public static <KeyT, ValueT> HashMap<KeyT, ValueT> toHashMap(Map<KeyT, ValueT> map, boolean allowCast) {
        if (allowCast && map instanceof HashMap) {
            return (HashMap<KeyT, ValueT>) map;
        }
        return null == map ? new HashMap<KeyT, ValueT>() : new HashMap<>(map);
    }


    /**
     * 遍历 Mapping
     *
     * @param in       输入
     * @param mappings 映射集
     * @param <InT>    输入类型
     * @param <OuT>    输出类型
     * @return 是否完成遍历
     */
    public static <InT, OuT> boolean forEachMapping(InT in, Collection<TypedMapping<InT, OuT>> mappings) {
        return forEachMapping(in, mappings, null);
    }

    /**
     * 遍历 Mapping
     *
     * @param in              输入
     * @param mappings        映射集
     * @param continueChecker 继续检查
     * @param <InT>           输入类型
     * @param <OuT>           输出类型
     * @return 是否完成遍历
     */
    public static <InT, OuT> boolean forEachMapping(
            InT in, Collection<TypedMapping<InT, OuT>> mappings, TypedMapping<OuT, Boolean> continueChecker) {
        for (TypedMapping<InT, OuT> mapping : mappings) {
            OuT out = mapping.map(in);
            if (null != continueChecker && !continueChecker.map(out)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 连 Mapping
     *
     * @param in              输入
     * @param mappings        映射集
     * @param <T>             类型
     * @return 是否完成遍历
     */
    public static <T> T chainMapping(T in, Collection<TypedMapping<T, T>> mappings) {
        for (TypedMapping<T, T> mapping : mappings) {
            in = mapping.map(in);
        }
        return in;
    }

}

package com.github.sky;

/**
 * Description:IKeyNamingPolicy
 * 架构师可以通过实现此类制定全局性的 key 命名策略，
 * 例如 Integer、String、OtherType 这些不同类型的对象
 * 选择不同的命名方式，默认命名方式是  Object.toString()
 *
 * Author: sukai
 * Date: 2017-08-11
 */
public interface IKeyNamingPolicy {

    String getKeyName(Object key);

    IKeyNamingPolicy defaultKeyNamingPolicy = new IKeyNamingPolicy() {
        public String getKeyName(Object key) {
            return key.toString();
        }
    };
}

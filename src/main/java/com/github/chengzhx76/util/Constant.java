package com.github.chengzhx76.util;

/**
 * Desc:
 * Author: 光灿
 * Date: 2017/4/29
 */
public interface Constant {

    String EMPTY_STRING = "";

    String ID = "id";

    String TITLE = "title";

    abstract class Type {
        public final static String HTML = "html";
        public final static String MEDIA = "media";
        public final static String JSON = "json";
    }

    abstract class Charset {
        public final static String UTF_8 = "UTF-8";
        public final static String GBK = "gbk";
    }
}

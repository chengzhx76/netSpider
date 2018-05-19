package com.github.chengzhx76.util;

/**
 * Some constants of Http protocal.
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class HttpConstant {

    public static abstract class Method {

        public static final String GET = "GET";

        public static final String HEAD = "HEAD";

        public static final String POST = "POST";

        public static final String PUT = "PUT";

        public static final String DELETE = "DELETE";

        public static final String TRACE = "TRACE";

        public static final String CONNECT = "CONNECT";

    }

    public static abstract class StatusCode {

        public static final int CODE_200 = 200;

    }

    public static abstract class Header {

        public static final String ACCEPT = "Accept";

        public static final String CACHE_CONTROL = "Cache-Control";

        public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

        public static final String REFERER = "Referer";

        public static final String USER_AGENT = "User-Agent";

        public static final String X_REQUESTED_WITH = "X-Requested-With";

        public static final String ACCEPT_ENCODING = "Accept-Encoding";

        public static final String ACCEPT_LANGUAGE = "Accept-Language";

        public static final String CONNECTION = "Connection";

        public static final String HOST = "Host";

        public static final String CONTENT_TYPE = "Content-Type";

        public static final String ORIGIN = "Origin";

        public static final String UPGRADE_INSECURE_REQUESTS = "Upgrade-Insecure-Requests";
    }


    public abstract class HeaderValue {

        public static final String APPLICATION_ALL = "*/*";

        public static final String APPLICATION_JSON_TEXT = "application/json, text/javascript, */*; q=0.01";

        public static final String APPLICATION_TEXT_HTML_XML_IMG = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";

        public static final String XML = "XMLHttpRequest";

        public static final String MAX_AGE_ZERO = "max-age=0";

        public static final String UPGRADE_ONE = "1";

        public static final String ENCODING = "gzip, deflate, br";

        public static final String LANGUAGE = "zh-CN,zh;q=0.9,en;q=0.8";

        public static final String KEEP_ALIVE = "keep-alive";

        public static final String NO_CACHE = "no-cache";

        public static final String ZERO = "0";

    }


    public abstract class UserAgent {

        public static final String CHROME = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

        public static final String FIREFOX = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0";

        public static final String IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    }

}

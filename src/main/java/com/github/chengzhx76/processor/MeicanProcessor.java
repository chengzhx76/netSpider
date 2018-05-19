package com.github.chengzhx76.processor;

import com.github.chengzhx76.NetSpider;
import com.github.chengzhx76.Response;
import com.github.chengzhx76.Site;

/**
 * @author code4crafter@gmail.com <br>
 * Date: 13-5-20
 * Time: 下午5:31
 */
public class MeicanProcessor implements Processor {
    @Override
    public void process(Response response) {
        System.out.println(response);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setDomain("meican.com")
                .setCharset("utf-8").
                setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
    }

    public static void main(String[] args) {
        NetSpider.create(new MeicanProcessor()).addUrl("http://www.meican.com/shanghai/districts").run();
    }
}

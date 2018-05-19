package com.github.chengzhx76.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.chengzhx76.NetSpider;
import com.github.chengzhx76.Request;
import com.github.chengzhx76.Response;
import com.github.chengzhx76.Site;
import com.github.chengzhx76.pipeline.ConsolePipeline;
import com.github.chengzhx76.scheduler.PriorityScheduler;
import com.github.chengzhx76.util.Constant.*;
import com.github.chengzhx76.util.HttpConstant.Header;
import com.github.chengzhx76.util.HttpConstant.HeaderValue;
import com.github.chengzhx76.util.HttpConstant.Method;
import com.github.chengzhx76.util.HttpConstant.UserAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author: 光灿
 * Date: 2018/5/19
 */
public class TimeProcessor implements Processor {

    private Site site;

    private long priority = 1L;

    @Override
    public void process(Response response) {
        String result = response.getRawText().toString();
        if (!result.startsWith("#")) {
            JSONObject resultJson = JSON.parseObject(result);

            // 1.先获取每章视频列表
            JSONArray sectionList = resultJson.getJSONArray("list");
            if (sectionList != null && !sectionList.isEmpty()) {
                List<String> urls = new ArrayList<>();
                for (int i = 0; i < sectionList.size(); i++) {
                    JSONObject section = sectionList.getJSONObject(i);
                    // 2.解释资源
                    String videoMediaJson = section.getString("video_media");
                    JSONObject videoMediaObj = JSON.parseObject(videoMediaJson);
                    // 3.获取当前章节的分片视屏
                    JSONObject hd = videoMediaObj.getJSONObject("hd");
                    urls.add(hd.getString("url"));
                }
                response.addTargetPriorityRequest(urls, priority);
            }
        } else {
            // 4.获取分片信息
            // 正则匹配 hd-00001.ts
            System.out.println(result);
        }


    }

    @Override
    public Site getSite() {
        if (site == null) {
            site = Site.me()
                    .setDomain("time.geekbang.org")
                    .setUserAgent(UserAgent.CHROME)
                    .setCharset(Charset.UTF_8)
                    .addCookie("GCID", "0c0d8a0-6ad9cb7-9448e40-18da930")
                    .addCookie("SERVERID", "fe79ab1762e8fabea8cbf989406ba8f4|1526746061|1526744366")
                    .setHeaders(Header.ACCEPT, HeaderValue.APPLICATION_JSON_TEXT)
                    .setHeaders(Header.ACCEPT_ENCODING, HeaderValue.ENCODING)
                    .setHeaders(Header.ACCEPT_LANGUAGE, HeaderValue.LANGUAGE)
                    .setHeaders(Header.CONNECTION, HeaderValue.KEEP_ALIVE)
                    .setHeaders(Header.HOST, "time.geekbang.org")
                    .setHeaders(Header.ORIGIN, "https://time.geekbang.org")
                    .setHeaders(Header.REFERER, "https://time.geekbang.org/paid-content")
                    .setSleepTime(1000);


                    //.isDownloadMedia(true)
                    //.setMediaDirectory("C:\\QQDownload\\time\\");
        }
        return site;
    }

    public static void main(String[] args) {
        List<Request> requests = new ArrayList<>();
        Request request = new Request("https://time.geekbang.org/serv/v1/column/all");
        request.setMethod(Method.POST);
        requests.add(request);

        NetSpider.create(new TimeProcessor())
                .addPipeline(new ConsolePipeline())
                .setScheduler(new PriorityScheduler())
                .startRequest(requests)
                .run();
    }
}

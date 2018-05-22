package com.github.chengzhx76.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.chengzhx76.*;
import com.github.chengzhx76.util.Constant;
import com.github.chengzhx76.util.HttpConstant;
import com.github.chengzhx76.util.HttpConstant.Header;
import com.github.chengzhx76.util.HttpConstant.HeaderValue;
import com.github.chengzhx76.util.UrlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Author: 光灿
 * Date: 2018/5/19
 */
public class TimeMp4Processor implements Processor {

    private Site site;

    //private long priority = 1L;

    @Override
    public void process(Response response) {
        try {
            if (!response.isStreaming()) {
                String result = response.getRawText().toString();
                if (!result.startsWith("#")) {
                    JSONObject resultJson = JSON.parseObject(result);
                    JSONObject data = resultJson.getJSONObject("data");

                    // 1.先获取每章视频列表
                    JSONArray sectionList = data.getJSONArray("list");
                    if (sectionList != null && !sectionList.isEmpty()) {
                        List<Request> requests = new ArrayList<>();
                        //for (int i = 0; i < sectionList.size(); i++) {
                        //    JSONObject section = sectionList.getJSONObject(i);
                            JSONObject section = sectionList.getJSONObject(0); // 0 标识第一课 1 标识第二课
                            // 2.解释资源
                            String videoMediaJson = section.getString("video_media").replaceAll("\\\\", "");
                            String title = section.getString("article_title");
                            JSONObject videoMediaObj = JSON.parseObject(videoMediaJson);
                            // 3.获取当前章节的分片视屏
                            JSONObject sd = videoMediaObj.getJSONObject("sd");
                            String url = sd.getString("url");
                            Request request = Request.createGetRequest(url)
                                    .putExtra(Constant.MEDIA_NAME, title)
                                    .putExtra("preUrl", UrlUtils.getPreUrl(url))
                                    .setAddSiteCookie(false)
                                    .setAddSiteHeader(false);

                            response.putField("标题", title);
                            response.putField("MP4路径", url);

                            requests.add(request);
                        //}
                        response.addTargetRequest(requests);
                    }
                } else {
                    // 4.获取分片信息
                    // 正则匹配 hd-00001.ts
                    String[] results =  result.split("\n");
                    List<Request> requests = new ArrayList<>();
                    for (String line : results) {
                        if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
                            Request request = Request.createMediaGetRequest(response.getRequest().getExtra("preUrl")+line)
                                    .putExtra(Constant.MEDIA_NAME, response.getRequest().getExtra(Constant.MEDIA_NAME))
                                    .setSubdires(response.getRequest().getSubdires())
                                    .setAddSiteCookie(false) // 二级目录
                                    .setAddSiteHeader(false);
                            requests.add(request);
                        }
                    }
                    response.addTargetRequest(requests);
                }
            } else {
                File file = new File(site.getMediaDirectory()+File.separator+response.getRequest().getSubdires()+File.separator, response.getMediaFileName());
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                FileOutputStream outputStream = new FileOutputStream(file);
                IOUtils.write(response.getBytes(), outputStream);
                System.out.println("------------"+response.getRequest().getExtra(Constant.MEDIA_NAME) + "-->" + response.getMediaFileName() + "下载完成------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        if (site == null) {
            site = Site.me()
                    .setDomain("time.geekbang.org")
                    .setUserAgent(HttpConstant.UserAgent.CHROME)
                    .setCharset(Constant.Charset.UTF_8)
                    .addCookie("GCID", "0c0d8a0-6ad9cb7-9448e40-18da930")
                    .addCookie("SERVERID", "97796d411bb56cf20a5612997f113254|1526875814|1526875811")
                    .setHeaders(Header.ACCEPT, HeaderValue.APPLICATION_JSON_TEXT)
                    .setHeaders(Header.ACCEPT_ENCODING, HeaderValue.ENCODING)
                    .setHeaders(Header.ACCEPT_LANGUAGE, HeaderValue.LANGUAGE)
                    .setHeaders(Header.CONNECTION, HeaderValue.KEEP_ALIVE)
                    .setHeaders(Header.HOST, "time.geekbang.org")
                    .setHeaders(Header.ORIGIN, "https://time.geekbang.org")
                    .setHeaders(Header.REFERER, "https://time.geekbang.org/course/detail/66-2184")
                    .setSleepTime(1000)
                    .setTimeOut(5 * 60 * 1000)
                    .setMediaDirectory("d:\\time\\");
        }
        return site;
    }

    public static void main(String[] args) {
        Request request = Request.createPostRequest("https://time.geekbang.org/serv/v1/column/articles")
                .setRequestBody(HttpRequestBody.json("{\"cid\":\"66\",\"size\":100,\"prev\":0,\"order\":\"earliest\"}", Constant.Charset.UTF_8));
        NetSpider.create(new TimeMp4Processor()).startRequest(request).start();
    }
}

package com.github.chengzhx76.download;

import com.github.chengzhx76.Request;
import com.github.chengzhx76.Response;
import com.github.chengzhx76.Site;

/**
 * Desc:
 * Author: 光灿
 * Date: 2018/1/11
 */
public interface Downloader {

    Response request(Request request, Site site);
}

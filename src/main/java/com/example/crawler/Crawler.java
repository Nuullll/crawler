package com.example.crawler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.*;
import org.jsoup.*;
import org.jsoup.select.Elements;

/**
 * Created by Nuullll on 2016/7/7.
 *
 * Practice for grabbing data from internet.
 * To get news titles @ http://news.ifeng.com/listpage/11502/0/1/rtlist.shtml
 */
public class Crawler {

    // URL of "即时新闻"
    private static final String NEWS_URL = "http://news.ifeng.com/listpage/11502/0/1/rtlist.shtml";
    private static final String FORMAT = "%s %s %s \n";
    private static final String YEAR = "2016";

    public static void main(String[] args) {
        try {
            // Get html document.
            Document doc = Jsoup.connect(NEWS_URL).get();
            // Extract the news list.
            Elements newsList = doc.select("div.newsList").first().select("li");

            for (Element news : newsList) {
                // Parse the news.
                String time = news.select("h4").first().text();
                Element link = news.select("a").first();
                String url = link.attr("href");
                String title = link.text();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date timeStamp = formatter.parse(YEAR + "/" + time);

                System.out.printf(FORMAT, title, timeStamp.toString(), url);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

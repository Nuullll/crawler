package com.example.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

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
    // MySQL : database java
    private static final String SQL_URL = "jdbc:mysql://localhost:3306/java?useUnicode=true&characterEncoding=UTF8";

    public static void main(String[] args) {
        try {
            // Connect to mysql.
            Connection conn = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(SQL_URL, "crawler", "crawler");
            PreparedStatement ps = null;
            String sql = "insert ignore into news_list (title,time_stamp,url) values(?,?,?)";

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

                // Convert time stamp to Date type.
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                java.util.Date timeStamp = formatter.parse(YEAR + "/" + time);

                // Prepare statement for sql.
                ps = conn.prepareStatement(sql);
                ps.setString(1, title);
                ps.setTimestamp(2, new java.sql.Timestamp(timeStamp.getTime()));
                ps.setString(3, url);
                ps.executeUpdate();
                System.out.printf(FORMAT, title, timeStamp.toString(), url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

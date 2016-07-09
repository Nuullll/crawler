package com.example.crawler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

/**
 * Created by Nuullll on 2016/7/8.
 */
@RestController
public class NewsController {

    private static final String FORMAT = "<li><h4>%s</h4>" +    // <h4>TimeStamp</h4>
            "<a href=\"%s\" target=\"_blank\">%s</a></li>";
    private static final String SQL_URL = "jdbc:mysql://localhost:3306/java?" +     // Database: java
            "useUnicode=true&characterEncoding=UTF8";   // Support Chinese characters.

    @RequestMapping("/news")
    public String news(@RequestParam(value = "clear", defaultValue = "false") boolean clear,
                       @RequestParam(value = "user", defaultValue = "") String user,
                       @RequestParam(value = "password", defaultValue = "") String password,
                       @RequestParam(value = "update", defaultValue = "false") boolean update) {

        StringBuilder html = new StringBuilder("<h2>新闻列表</h2><ul>");

        try {
            // Connect to mysql.
            // @user: crawler
            // @password: crawler
            Connection conn = DriverManager.getConnection(SQL_URL, "crawler", "crawler");
            Class.forName("com.mysql.jdbc.Driver");
            Statement stmt = conn.createStatement();

            // Clear news_list?
            if (clear && user.equals("crawler") && password.equals("crawler")) {
                String sql = "TRUNCATE TABLE news_list";    // MySQL: Clear the news list.
                stmt.executeQuery(sql);
                return "数据库已清空！";
            }

            if (update) {
                // Update the news list.
                Crawler.update(conn);
            }

            // View database.
            stmt.executeQuery("SELECT * FROM news_list ORDER BY time_stamp DESC");
            ResultSet newsList = stmt.getResultSet();
            while (newsList.next()) {
                String title = newsList.getString("title");
                Timestamp timeStamp = newsList.getTimestamp("time_stamp");
                String url = newsList.getString("url");
                html.append(String.format(FORMAT, timeStamp.toString(), url, title));
            }
            html.append("</ul>");
            return html.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

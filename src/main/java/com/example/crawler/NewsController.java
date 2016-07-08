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

    private static final String FORMAT = "<li><h4>%s</h4><a href=\"%s\" target=\"_blank\">%s</a></li>";
    private static final String SQL_URL = "jdbc:mysql://localhost:3306/java?useUnicode=true&characterEncoding=UTF8";

    @RequestMapping("/news")
    public String news(@RequestParam(value = "clear", defaultValue = "false") boolean clear,
                       @RequestParam(value = "user", defaultValue = "") String user,
                       @RequestParam(value = "password", defaultValue = "") String password,
                       @RequestParam(value = "update", defaultValue = "false") boolean update) {

        StringBuilder html = new StringBuilder("<h2>新闻列表</h2><ul>");

        try {
            // Connect to mysql.
            Connection conn = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(SQL_URL, "crawler", "crawler");
            PreparedStatement ps = null;

            // Clear news_list?
            if (clear && user.equals("crawler") && password.equals("crawler")) {
                String sql = "truncate table news_list";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();
                return "数据库已清空！";
            }

            if (update) {
                // Update the news list.
                Crawler.update(conn);
            }

            // View database.
            Statement stmt = conn.createStatement();
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

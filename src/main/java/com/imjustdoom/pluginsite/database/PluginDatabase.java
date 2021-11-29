package com.imjustdoom.pluginsite.database;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Getter
public class PluginDatabase {

    private Statement stmt;
    private Connection conn;

    public void init() {

        String user = PluginSiteApplication.config.username;
        String pass = PluginSiteApplication.config.password;
        String server = PluginSiteApplication.config.host;
        String port = PluginSiteApplication.config.port;
        String database = PluginSiteApplication.config.database;

        //Connect and setup database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + server + ":" + port + "/" + database, user, pass);
            stmt = conn.createStatement();

            String sql = "CREATE TABLE IF NOT EXISTS resources (" +
                    "id INT NOT NULL, " +
                    "name VARCHAR(128) NOT NULL," +
                    "blurb VARCHAR(256) NOT NULL," +
                    "description TEXT(65535) NOT NULL," +
                    "donation VARCHAR(2048) NOT NULL," +
                    "source VARCHAR(2048) NOT NULL," +
                    "creation INT NOT NULL," +
                    "updated INT NOT NULL," +
                    "downloads INT NOT NULL," +
                    "authorid INT NOT NULL," +
                    "CONSTRAINT id PRIMARY KEY (id));";

            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INT NOT NULL, " +
                    "username VARCHAR(16) NOT NULL," +
                    "email VARCHAR(320) NOT NULL," +
                    "password VARCHAR(64) NOT NULL," +
                    "provider VARCHAR(16) NOT NULL," +
                    "CONSTRAINT id PRIMARY KEY (id));";

            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS files (" +
                    "id INT NOT NULL, " +
                    "fileId INT NOT NULL, " +
                    "filename VARCHAR(1024) NOT NULL," +
                    "CONSTRAINT fileId PRIMARY KEY (fileId));";

            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

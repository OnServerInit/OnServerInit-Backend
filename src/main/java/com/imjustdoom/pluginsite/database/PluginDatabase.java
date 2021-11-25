package com.imjustdoom.pluginsite.database;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Getter
public class PluginDatabase {

    private Statement stmt;

    public void init() {

        String user = PluginSiteApplication.config.username;
        String pass = PluginSiteApplication.config.password;
        String server = PluginSiteApplication.config.host;
        String port = PluginSiteApplication.config.port;
        String database = PluginSiteApplication.config.database;

        //Connect and setup database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://" + server + ":" + port + "/" + database, user, pass);
            stmt = con.createStatement();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

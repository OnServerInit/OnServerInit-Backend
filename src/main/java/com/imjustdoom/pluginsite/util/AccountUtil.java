package com.imjustdoom.pluginsite.util;

import com.imjustdoom.pluginsite.PluginSiteApplication;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountUtil {

    public static String getAuthorFromId(int id) {
        try {
            ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT username FROM accounts WHERE id=" + id);

            if (rs.next()) return rs.getString("username");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }
}

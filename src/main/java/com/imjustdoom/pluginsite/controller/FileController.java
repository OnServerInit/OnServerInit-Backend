package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class FileController {

    @GetMapping("/files/{id}/download/{fileId}")
    @ResponseBody
    public ResponseEntity serveFile(@PathVariable("id") int id, @PathVariable("fileId") int fileId) throws SQLException, MalformedURLException {

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM files WHERE id=" + id + " AND fileId=" + fileId);

        if(!rs.next()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error. Can't find file on this plugin");

        Path path = Paths.get("./resources/plugins/" + fileId + "/");
        Resource file = new UrlResource(path.resolve(rs.getString("filename")).toUri());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}

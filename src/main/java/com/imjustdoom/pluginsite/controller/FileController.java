package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.PluginSiteApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class FileController {

    @GetMapping("/logo/{id}")
    @ResponseBody
    public HttpEntity<byte[]> serveLogo(@PathVariable("id") int id) throws IOException {

        Path path = Paths.get("resources/logos/%s".formatted(id));
        Resource file = new UrlResource(path.resolve("default.png").toUri());

        byte[] image = file.getInputStream().readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);

        return new HttpEntity<byte[]>(image, headers);
    }

    @GetMapping("/files/{id}/download/{fileId}")
    @ResponseBody
    public ResponseEntity serveFile(@PathVariable("id") int id, @PathVariable("fileId") int fileId) throws SQLException, MalformedURLException {

        ResultSet rs = PluginSiteApplication.getDB().getStmt().executeQuery("SELECT * FROM files WHERE id=%s AND fileId=%s".formatted(id, fileId));

        if(!rs.next()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error. Can't find file on this plugin");

        if(!rs.getString("external").equals("")) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", rs.getString("external"));
            return new ResponseEntity<String>(headers,HttpStatus.FOUND);
        }

        Path path = Paths.get("resources/plugins/" + fileId + "/");
        Resource file = new UrlResource(path.resolve(rs.getString("filename")).toUri());

        PluginSiteApplication.getDB().getStmt().executeUpdate("UPDATE resources SET downloads=downloads + 1 WHERE id=%s".formatted(id));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}

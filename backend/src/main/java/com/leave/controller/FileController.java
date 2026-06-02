package com.leave.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files")
public class FileController {

    @GetMapping("/{dir}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String dir, @PathVariable String filename) throws MalformedURLException {
        Path filePath = Paths.get("./uploads/" + dir).resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (filename.endsWith(".png")) contentType = "image/png";
        else if (filename.endsWith(".gif")) contentType = "image/gif";
        else if (filename.endsWith(".pdf")) contentType = "application/pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}

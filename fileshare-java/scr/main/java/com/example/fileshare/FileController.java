package com.example.fileshare;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Collectors;

@Controller
public class FileController {
    private final Path uploadDir = Paths.get("uploads");

    public FileController() throws IOException {
        Files.createDirectories(uploadDir);
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        var files = Files.list(uploadDir)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        model.addAttribute("files", files);
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        Path destination = uploadDir.resolve(file.getOriginalFilename());
        file.transferTo(destination);
        return "redirect:/";
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public Resource downloadFile(@PathVariable String filename) throws MalformedURLException {
        Path file = uploadDir.resolve(filename);
        return new UrlResource(file.toUri());
    }
}
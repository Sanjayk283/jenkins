package com.example.test.serviceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class UserServiceImpl {


    public void generateAndDownloadProject(String projectName, String ait, OutputStream outputStream) throws IOException {

        Path tempDir = Files.createTempDirectory("skeleton_project");

        // Create project folders
        Path mainFolder = Files.createDirectory(tempDir.resolve(projectName));
        Path cdFolder = Files.createDirectory(tempDir.resolve(projectName + "_cd"));

        // Add skeleton files and replace placeholders
        createSkeletonFile(mainFolder, projectName, ait);
        createSkeletonFile(cdFolder, projectName + "_cd", ait);

        // Create a ZIP file of the folders
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            zipFolder(tempDir, tempDir, zos);
        }

        // Cleanup temporary files
        deleteDirectory(tempDir.toFile());
    }

    private void createSkeletonFile(Path folder, String projectName, String ait) throws IOException {
        String content = "Project Name: {{project_name}}, AIT: {{ait}}";
        content = content.replace("{{project_name}}", projectName).replace("{{ait}}", ait);

        Files.write(folder.resolve("skeleton.txt"), content.getBytes());
    }

    private void zipFolder(Path rootDir, Path sourceDir, ZipOutputStream zos) throws IOException {
        Files.walk(sourceDir).forEach(path -> {
            try {
                String zipEntryName = rootDir.relativize(path).toString();
                if (Files.isDirectory(path)) {
                    if (!zipEntryName.isEmpty()) {
                        zos.putNextEntry(new ZipEntry(zipEntryName + "/"));
                        zos.closeEntry();
                    }
                } else {
                    zos.putNextEntry(new ZipEntry(zipEntryName));
                    Files.copy(path, zos);
                    zos.closeEntry();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while zipping folder", e);
            }
        });
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }



}

package dev.mm.core.coreservice.service;

import dev.mm.core.coreservice.model.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileSystemFileStorageService implements FileStorageService {

    @Value("${dev.mm.core.coreservice.file-storage-location}")
    private String fileStorageLocation;

    private Path fileStorageLocationPath;

    @PostConstruct
    public void postConstruct() throws Exception {
        fileStorageLocationPath = Path.of(fileStorageLocation);
        Files.createDirectories(fileStorageLocationPath);
    }

    @Override
    public void storeFileAndSetUrl(File file, MultipartFile multipartFile) {
        Path saveDir = fileStorageLocationPath;
        saveDir = Path.of(saveDir.toString(), file.getUuid());
        try {
            Files.copy(multipartFile.getInputStream(), saveDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        file.setUrl(saveDir.toString());
    }

    @Override
    public AbstractResource getFileContent(File file) {
        return new FileSystemResource(Path.of(file.getUrl()));
    }

    @Override
    public void deleteFile(File file) {
        try {
            Files.deleteIfExists(Path.of(file.getUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

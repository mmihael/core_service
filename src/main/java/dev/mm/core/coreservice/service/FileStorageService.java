package dev.mm.core.coreservice.service;

import dev.mm.core.coreservice.model.File;
import org.springframework.core.io.AbstractResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void storeFileAndSetUrl(File file, MultipartFile multipartFile);

    AbstractResource getFileContent(File file);

    void deleteFile(File file);

}

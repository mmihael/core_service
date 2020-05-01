package dev.mm.core.coreservice.controller.app;

import dev.mm.core.coreservice.constants.FileAccessType;
import dev.mm.core.coreservice.dto.file.FileDto;
import dev.mm.core.coreservice.dto.file.FilePageRequestDto;
import dev.mm.core.coreservice.security.UserDetailsImpl;
import dev.mm.core.coreservice.service.AuthorizationService;
import dev.mm.core.coreservice.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

import static dev.mm.core.coreservice.constants.Uris.API_FILE;
import static dev.mm.core.coreservice.constants.Uris.API_FILE_ID;
import static dev.mm.core.coreservice.constants.Uris.API_FILE_ID_CONTENT;
import static dev.mm.core.coreservice.constants.Uris.PUBLIC_API_FILE_ID_CONTENT;

@RestController
public class FileController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private FileService fileService;

    @GetMapping(API_FILE)
    public ResponseEntity getFilesPage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        FilePageRequestDto filePageRequestDto
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(fileService.getPage(null, userDetails.getId(), filePageRequestDto));
    }

    @GetMapping(API_FILE_ID)
    public ResponseEntity getFile(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable long fileId) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(new FileDto(fileService.getFileOrThrow(fileId)));
    }

    @GetMapping(API_FILE_ID_CONTENT)
    public ResponseEntity getFileContent(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long fileId,
        WebRequest webRequest
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        FileService.ResourceAndFile resourceAndFile = fileService.getFile(fileId);
        if (webRequest.checkNotModified(resourceAndFile.getCreatedAt())) { return null; }
        return ResponseEntity.ok().lastModified(resourceAndFile.getCreatedAt()).contentType(resourceAndFile.getType())
            .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS)).body(resourceAndFile.getResource());
    }

    @GetMapping(PUBLIC_API_FILE_ID_CONTENT)
    public ResponseEntity getPublicFileContent(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long fileId,
        WebRequest webRequest
    ) {
        FileService.ResourceAndFile resourceAndFile = fileService.getAppPublicFile(fileId);
        if (webRequest.checkNotModified(resourceAndFile.getCreatedAt())) { return null; }
        return ResponseEntity.ok().lastModified(resourceAndFile.getCreatedAt()).contentType(resourceAndFile.getType())
            .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS)).body(resourceAndFile.getResource());
    }

    @PostMapping(API_FILE)
    public ResponseEntity createFile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam MultipartFile file,
        @RequestParam(defaultValue = "null") String fileAccessType
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        FileAccessType fileAccessTypeEnum = fileAccessType.equals(null + "") ? null : FileAccessType.valueOf(fileAccessType);
        return ResponseEntity.ok(new FileDto(fileService.saveFile(userDetails.getId(), null, file, fileAccessTypeEnum)));
    }

    @PutMapping(API_FILE_ID)
    public ResponseEntity updateFile(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable long fileId,
        @RequestParam(defaultValue = "null") String fileAccessType
    ) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        FileAccessType fileAccessTypeEnum = fileAccessType.equals(null + "") ? null : FileAccessType.valueOf(fileAccessType);
        return ResponseEntity.ok(new FileDto(fileService.updateFile(fileId, fileAccessTypeEnum)));
    }

    @DeleteMapping(API_FILE_ID)
    public ResponseEntity deleteFile(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable long fileId) {
        authorizationService.ensureUserIsSuperAdmin(userDetails);
        return ResponseEntity.ok(new FileDto(fileService.deleteFile(fileId)));
    }

}

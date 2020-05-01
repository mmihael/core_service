package dev.mm.core.coreservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import dev.mm.core.coreservice.constants.FileAccessType;
import dev.mm.core.coreservice.dto.file.FileDto;
import dev.mm.core.coreservice.dto.file.FilePageRequestDto;
import dev.mm.core.coreservice.dto.response.PageResponseDto;
import dev.mm.core.coreservice.exception.EntityNotFoundException;
import dev.mm.core.coreservice.exception.ForbiddenException;
import dev.mm.core.coreservice.exception.ValidationErrorException;
import dev.mm.core.coreservice.model.File;
import dev.mm.core.coreservice.model.QFile;
import dev.mm.core.coreservice.repository.FileRepository;
import dev.mm.core.coreservice.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.AbstractResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static dev.mm.core.coreservice.constants.FileAccessType.ORGANIZATION_ONLY;
import static dev.mm.core.coreservice.constants.FileAccessType.PUBLIC;
import static dev.mm.core.coreservice.util.TranslationUtil.translate;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

@Service
public class FileService {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PaginationService paginationService;

    public PageResponseDto getPage(Long organizationId, long userId, FilePageRequestDto filePageRequestDto) {

        String originalName = filePageRequestDto.getOriginalName();
        String fileType = filePageRequestDto.getFileType();

        PageResponseDto pageResponse = paginationService.getPage(filePageRequestDto, jpaQuery -> {

            jpaQuery.from(QFile.file).distinct();

            BooleanExpression expression = null;

            if (organizationId != null) {
                expression = QFile.file.organizationId.eq(organizationId)
                    .and(QFile.file.accessType.in(ORGANIZATION_ONLY, PUBLIC).or(QFile.file.ownerId.eq(userId)));
            }

            if (StringUtil.isNotBlank(originalName)) {
                BooleanExpression newExpression = QFile.file.originalName.containsIgnoreCase(originalName);
                expression = expression == null ? newExpression : expression.and(newExpression);
            }

            if (StringUtil.isNotBlank(fileType)) {
                BooleanExpression newExpression = QFile.file.fileType.containsIgnoreCase(fileType);
                expression = expression == null ? newExpression : expression.and(newExpression);
            }

            jpaQuery.where(expression);

            jpaQuery.orderBy(QFile.file.id.asc());

        });

        pageResponse.setContent(((List<File>) pageResponse.getContent()).stream().map(FileDto::new).collect(toList()));

        return pageResponse;
    }

    @Transactional
    public File saveFile(long userId, Long organizationId, MultipartFile multipartFile, FileAccessType fileAccessType) {
        ensureFileAccessTypeIsNotNull(fileAccessType);
        File file = new File();
        file.setFileType(multipartFile.getContentType());
        file.setSize(multipartFile.getSize());
        file.setOriginalName(multipartFile.getOriginalFilename());
        file.setUuid(UUID.randomUUID().toString());
        file.setAccessType(fileAccessType);
        file.setOwner(userService.getUserOrThrow(userId));
        if (organizationId != null) {
            file.setOrganization(organizationService.getOrganizationByIdOrThrow(organizationId));
        }
        fileStorageService.storeFileAndSetUrl(file, multipartFile);
        return fileRepository.save(file);
    }

    @Transactional
    public File updateFile(long fileId, FileAccessType fileAccessType) {
        ensureFileAccessTypeIsNotNull(fileAccessType);
        File file = getFileOrThrow(fileId);
        file.setAccessType(fileAccessType);
        return fileRepository.save(file);
    }

    @Transactional
    public File updateFileInOrganization(long fileId, long userId, long organizationId, FileAccessType fileAccessType) {
        ensureFileAccessTypeIsNotNull(fileAccessType);
        File file = getFileInOrganizationOrThrow(organizationId, fileId);
        ensureUserCanAccessFileInOrganization(file, userId);
        file.setAccessType(fileAccessType);
        return fileRepository.save(file);
    }

    public File getFileInOrganizationIfUserCanAccessIt(long organizationId, long fileId, long userId) {
        File file = getFileInOrganizationOrThrow(organizationId, fileId);
        ensureUserCanAccessFileInOrganization(file, userId);
        return file;
    }

    public ResourceAndFile getFile(long fileId) {
        File file = getFileOrThrow(fileId);
        return new ResourceAndFile(fileStorageService.getFileContent(file), file);
    }

    public ResourceAndFile getAppPublicFile(long fileId) {
        File file = getPublicAppFileOrThrow(fileId);
        return new ResourceAndFile(fileStorageService.getFileContent(file), file);
    }

    public ResourceAndFile getFileInOrganization(long organizationId, long fileId) {
        File file = getFileInOrganizationOrThrow(organizationId, fileId);
        return new ResourceAndFile(fileStorageService.getFileContent(file), file);
    }

    public ResourceAndFile getPublicFileInOrganization(long organizationId, long fileId) {
        File file = getPublicFileInOrganizationOrThrow(organizationId, fileId);
        return new ResourceAndFile(fileStorageService.getFileContent(file), file);
    }

    @Transactional
    public File deleteFile(long fileId) {
        return deleteFile(getFileOrThrow(fileId));
    }

    @Transactional
    public File deleteFileInOrganization(long fileId, long organizationId, long userId) {
        File file = getFileInOrganizationOrThrow(organizationId, fileId);
        ensureUserCanAccessFileInOrganization(file, userId);
        return deleteFile(file);
    }

    private File deleteFile(File file) {
        fileRepository.delete(file);
        fileStorageService.deleteFile(file);
        return file;
    }

    public File getFileOrThrow(long fileId) {
        return fileRepository
            .findById(fileId)
            .orElseThrow(() -> new EntityNotFoundException(
                translate("File with id: {{ id }} not found", singletonMap("id", fileId))
            ));
    }

    public File getPublicAppFileOrThrow(long fileId) {
        return fileRepository
            .findOneByIdAndAndAccessTypeAndOrganizationIdIsNull(fileId, PUBLIC)
            .orElseThrow(() -> new EntityNotFoundException(
                translate("Public file with id: {{ id }} not found", singletonMap("id", fileId))
            ));
    }

    public File getFileInOrganizationOrThrow(long organizationId, long fileId) {
        return fileRepository
            .findOneByIdAndOrganizationId(fileId, organizationId)
            .orElseThrow(() -> new EntityNotFoundException(
                translate("File with id: {{ id }} not found in organization", singletonMap("id", fileId))
            ));
    }

    public File getPublicFileInOrganizationOrThrow(long organizationId, long fileId) {
        return fileRepository
            .findOneByIdAndOrganizationIdAndAccessType(fileId, organizationId, PUBLIC)
            .orElseThrow(() -> new EntityNotFoundException(
                translate("Public file with id: {{ id }} not found", singletonMap("id", fileId))
            ));
    }

    public void ensureFileAccessTypeIsNotNull(FileAccessType fileAccessType) {
        if (fileAccessType == null) {
            throw new ValidationErrorException("fileAccessType", translate("Access type can't be null"));
        }
    }

    public void ensureUserCanAccessFileInOrganization(File file, long userId) {
        if (file.getOwnerId() != userId) {
            try {
                authorizationService.ensureUserIsOrganizationOwner(file.getOrganizationId(), userId);
            } catch (ForbiddenException e) {
                throw new ForbiddenException(translate("Only organization owner or file owner can access file"));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ResourceAndFile {
        private AbstractResource resource;
        private File file;
        public MediaType getType() {
            return MediaType.parseMediaType(file.getFileType());
        }
        public long getCreatedAt() {
            return file.getCreatedAt().getTime();
        }
    }

}

package ru.hogwarts.school.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.entity.Avatar;
import ru.hogwarts.school.entity.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarServiceImpl implements AvatarService {

    /*
    Added avatar repository
     */
    private final AvatarRepository avatarRepository;

    /*
    Added log
     */
    private final static Logger log = LoggerFactory.getLogger(AvatarServiceImpl.class);

    /*
    Added student repository
     */
    private final StudentRepository studentRepository;

    /*
    Path from avatar
     */
    @Value("${path.to.avatars.folder}")
    private String avatarsDir;


    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    /*
    Uploaded avatar methods
     */
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        log.info("Avatar is uploaded");

        Student student = studentRepository.getReferenceById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtensions(Objects.requireNonNull(file.getOriginalFilename())));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    /*
    Find avatar methods
     */
    public Avatar findAvatar(long studentId) {
        log.info("Find avatar is done");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }


    private String getExtensions(String fileName) {
        log.info("Get extensions");
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /*
    Get avatars by page methods
     */
    public Page<Avatar> getAvatarsByPage(int pageNumber, int pageSize) {
        log.info("Get avatars by page");
        PageRequest pageable = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageable);
    }
}

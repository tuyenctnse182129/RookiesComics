package application.aicomic.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import application.aicomic.enums.ChaptersEnums;
import application.aicomic.enums.Role;
import application.aicomic.models.Chapters;
import application.aicomic.models.Users;
import application.aicomic.repositories.ChaptersRepository;
import application.aicomic.repositories.UsersRepository;

@Service
public class ChaptersService {
    private final ChaptersRepository chaptersRepository;
    private final UsersRepository usersRepository;

    public ChaptersService(ChaptersRepository chaptersRepository, UsersRepository usersRepository) {
        this.chaptersRepository = chaptersRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Get all chapters.
     */
    public List<Chapters> getAllChapters() {
        return chaptersRepository.findAll();
    }

    /**
     * Get a chapter by ID.
     */
    public Chapters getChapterById(String chapterId) {
        return chaptersRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found."));
    }

    /**
     * Add a new chapter. It will be set to PENDING and needs MODERATOR approval.
     */
    public Chapters addChapter(Chapters chapter, String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!isAuthorizedToManageChapters(user)) {
            throw new SecurityException("Unauthorized to create a chapter.");
        }

        chapter.setStatus(ChaptersEnums.PENDING.getValue());
        chapter.setPublishedDate(LocalDateTime.now());

        return chaptersRepository.save(chapter);
    }

    /**
     * Update a chapter only if its status is PENDING.
     */
    public Chapters updateChapter(String chapterId, Chapters updatedChapter, String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!isAuthorizedToManageChapters(user)) {
            throw new SecurityException("Unauthorized to update a chapter.");
        }

        Chapters existingChapter = chaptersRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found."));

        if (existingChapter.getStatus() != ChaptersEnums.PENDING.getValue()) {
            throw new IllegalStateException("You can only update a chapter when its status is PENDING.");
        }

        existingChapter.setChapterName(updatedChapter.getChapterName());
        existingChapter.setDescription(updatedChapter.getDescription());
        existingChapter.setPublishedDate(updatedChapter.getPublishedDate());

        return chaptersRepository.save(existingChapter);
    }

    /**
     * Moderator approves or declines a chapter.
     */
    public Chapters reviewChapter(String chapterId, boolean isApproved, String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!isModerator(user)) {
            throw new SecurityException("Only moderators can review chapters.");
        }

        Chapters chapter = chaptersRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found."));

        if (chapter.getStatus() != ChaptersEnums.PENDING.getValue()) {
            throw new IllegalStateException("Only pending chapters can be reviewed.");
        }

        chapter.setStatus(isApproved ? ChaptersEnums.UNLOCKED.getValue() : ChaptersEnums.DELETED.getValue());

        return chaptersRepository.save(chapter);
    }

    /**
     * Delete a chapter.
     */
    public void deleteChapter(String chapterId, String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!isAuthorizedToManageChapters(user)) {
            throw new SecurityException("Unauthorized to delete this chapter.");
        }

        if (!chaptersRepository.existsById(chapterId)) {
            throw new RuntimeException("Chapter not found.");
        }

        chaptersRepository.deleteById(chapterId);
    }

    /**
     * Check if a user is authorized to manage chapters.
     */
    private boolean isAuthorizedToManageChapters(Users user) {
        Role userRole = Role.fromValue(user.getRole()); 
        return userRole == Role.CUSTOMER_AUTHOR || userRole == Role.CUSTOMER_VIP || userRole == Role.ADMIN;
    }

    /**
     * Check if the user is a moderator.
     */
    private boolean isModerator(Users user) {
        Role userRole = Role.fromValue(user.getRole()); 
        return userRole == Role.MODERATOR;
    }
}

package application.aicomic.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import application.aicomic.enums.ChaptersEnums;
import application.aicomic.enums.WalletType;
import application.aicomic.models.ChapterImages;
import application.aicomic.models.Chapters;
import application.aicomic.models.Comics;
import application.aicomic.models.Users;
import application.aicomic.repositories.ChaptersRepository;
import application.aicomic.repositories.ComicsRepository;
import application.aicomic.repositories.UsersRepository;
import application.aicomic.repositories.WalletsRepository;

@Service
public class ChaptersService {
    private final ChaptersRepository chaptersRepository;
    private final ComicsRepository comicsRepository;
    private final UsersRepository usersRepository;
    private final WalletsRepository walletsRepository;

    public ChaptersService(ChaptersRepository chaptersRepository, ComicsRepository comicsRepository, UsersRepository usersRepository, WalletsRepository walletsRepository) {
        this.chaptersRepository = chaptersRepository;
        this.comicsRepository = comicsRepository;
        this.usersRepository = usersRepository;
        this.walletsRepository = walletsRepository;
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
    public Chapters addChapter(Chapters chapter) {
        chapter.setStatus(ChaptersEnums.PENDING.getValue());
        chapter.setPublishedDate(LocalDateTime.now());

        // Extract the images temporarily
        List<ChapterImages> originalImages = chapter.getChapterImages();
        chapter.setChapterImages(null); // avoid premature cascade issues

        // Save chapter to generate chapterId
        Chapters savedChapter = chaptersRepository.save(chapter);

        // Reattach and fix each image if not null
        if (originalImages != null && !originalImages.isEmpty()) {
            for (ChapterImages image : originalImages) {
                if (image.getImageURL() != null && !image.getImageURL().isBlank()) {
                    image.setChapterId(savedChapter.getChapterId());
                }
            }
            savedChapter.setChapterImages(originalImages);
            savedChapter = chaptersRepository.save(savedChapter); // save again with images attached
        }

        return savedChapter;
    }


    /**
     * Update a chapter only if its status is PENDING.
     */
    public Chapters updateChapter(String chapterId, Chapters updatedChapter) {
        Chapters existingChapter = chaptersRepository.findById(chapterId)
        .orElseThrow(() -> new RuntimeException("Chapter not found."));

        if (existingChapter.getStatus() != ChaptersEnums.PENDING.getValue()) {
            throw new IllegalStateException("Only PENDING chapters can be updated.");
        }

        existingChapter.setChapterName(updatedChapter.getChapterName());
        existingChapter.setDescription(updatedChapter.getDescription());
        existingChapter.setPublishedDate(updatedChapter.getPublishedDate());

        return chaptersRepository.save(existingChapter);
    }

/**
 * Moderator reviews a chapter and updates the creator's PROMOTION wallet balance if approved.
 */
public Chapters reviewChapter(String chapterId, boolean isApproved, String modComment) {
    Chapters chapter = chaptersRepository.findById(chapterId)
            .orElseThrow(() -> new RuntimeException("Chapter not found."));

    if (chapter.getStatus() != ChaptersEnums.PENDING.getValue()) {
        throw new IllegalStateException("Only pending chapters can be reviewed.");
    }

    if (isApproved) {
        ChaptersEnums.Type chapterType = ChaptersEnums.Type.fromValue(chapter.getType());

        if (chapterType == ChaptersEnums.Type.PAID) {
            chapter.setStatus(ChaptersEnums.LOCKED.getValue());

            Comics comic = comicsRepository.findById(chapter.getComicId())
                    .orElseThrow(() -> new RuntimeException("Comic not found."));

            Users comicCreator = usersRepository.findById(comic.getUserId())
                    .orElseThrow(() -> new RuntimeException("Comic creator not found."));

            walletsRepository.findByUserAndType(comicCreator, WalletType.PROMOTION)
                    .ifPresent(wallet -> {
                        wallet.setBalance(wallet.getBalance() + 699);
                        wallet.setUpdatedDate(LocalDateTime.now());
                        walletsRepository.save(wallet);
                    });

        } else if (chapterType == ChaptersEnums.Type.FREE) {
            chapter.setStatus(ChaptersEnums.UNLOCKED.getValue());
        }
    } else {
        chapter.setStatus(ChaptersEnums.DELETED.getValue());
        chapter.setModComment(
                (modComment != null && !modComment.isBlank())
                        ? modComment
                        : "Declined without comment."
        );
    }

    return chaptersRepository.save(chapter);
}

    /**
     * Delete a chapter.
     */
    public void deleteChapter(String chapterId) {
        if (!chaptersRepository.existsById(chapterId)) {
            throw new RuntimeException("Chapter not found.");
        }

        chaptersRepository.deleteById(chapterId);
    }


}
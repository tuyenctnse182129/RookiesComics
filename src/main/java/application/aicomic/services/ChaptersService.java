package application.aicomic.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import application.aicomic.enums.ChaptersEnums;
import application.aicomic.enums.Role;
import application.aicomic.enums.WalletType;
import application.aicomic.models.Chapters;
import application.aicomic.models.Comics;
import application.aicomic.models.Users;
import application.aicomic.models.Wallets;
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
     * Moderator reviews a chapter and updates the creator's PROMOTION wallet balance if approved.
     */
    public Chapters reviewChapter(String chapterId, boolean isApproved, String userId) {
        Users moderator = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Moderator not found."));

        if (!isModerator(moderator)) {
            throw new SecurityException("Only moderators can review chapters.");
        }

        Chapters chapter = chaptersRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found."));

        if (chapter.getStatus() != ChaptersEnums.PENDING.getValue()) {
            throw new IllegalStateException("Only pending chapters can be reviewed.");
        }

        if (isApproved) {
            chapter.setStatus(ChaptersEnums.UNLOCKED.getValue());

            Comics comic = comicsRepository.findById(chapter.getComicId())
                    .orElseThrow(() -> new RuntimeException("Comic not found."));

            Users comicCreator = usersRepository.findById(comic.getUserId())
                    .orElseThrow(() -> new RuntimeException("Comic creator not found."));

            ChaptersEnums.Type chapterType = ChaptersEnums.Type.fromValue(chapter.getType());
            if (chapterType == ChaptersEnums.Type.PAID) {
                Optional<Wallets> promotionWalletOpt = walletsRepository.findByUserAndType(comicCreator,
                        WalletType.PROMOTION);

                if (promotionWalletOpt.isPresent()) {
                    Wallets promotionWallet = promotionWalletOpt.get();
                    promotionWallet.setBalance(promotionWallet.getBalance() + 699); // Reward amount can be adjusted
                    promotionWallet.setUpdatedDate(LocalDateTime.now());

                    walletsRepository.save(promotionWallet);
                }
            }
        } else {
            chapter.setStatus(ChaptersEnums.DELETED.getValue());
        }

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

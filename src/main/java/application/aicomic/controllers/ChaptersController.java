package application.aicomic.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import application.aicomic.models.Chapters;
import application.aicomic.services.ChaptersService;

@RestController
@RequestMapping("/chapters")
public class ChaptersController {
    private final ChaptersService chaptersService;

    public ChaptersController(ChaptersService chaptersService) {
        this.chaptersService = chaptersService;
    }

    /**
     * Retrieves all chapters.
     */
    @GetMapping
    public ResponseEntity<List<Chapters>> getAllChapters() {
        return ResponseEntity.ok(chaptersService.getAllChapters());
    }

    /**
     * Retrieves a single chapter by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(chaptersService.getChapterById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Creates a new chapter.
     */
    @PostMapping
    public ResponseEntity<?> createChapter(@RequestBody Chapters chapter) {
        try {
            return ResponseEntity.ok(chaptersService.addChapter(chapter));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Updates an existing chapter.
     */
    @PutMapping("/{chapterId}")
    public ResponseEntity<?> updateChapter(@PathVariable String chapterId, @RequestBody Chapters chapter) {
        try {
            return ResponseEntity.ok(chaptersService.updateChapter(chapterId, chapter));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes a chapter by ID.
     */
    @DeleteMapping("/{chapterId}")
    public ResponseEntity<?> deleteChapter(@PathVariable String chapterId) {
        try {
            chaptersService.deleteChapter(chapterId);
            return ResponseEntity.ok("Chapter deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/review")
    public ResponseEntity<?> reviewChapter(
            @RequestParam String chapterId,
            @RequestParam boolean isApproved,
            @RequestParam(required = false) String modComment) {
        try {
            Chapters updatedChapter = chaptersService.reviewChapter(chapterId, isApproved, modComment);
            return ResponseEntity.ok(updatedChapter); // Return the updated chapter data
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Handle Chapter not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

}
package application.aicomic.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import application.aicomic.enums.ChaptersEnums;
import application.aicomic.models.Chapters;
import application.aicomic.services.ChaptersService;

@RestController
@RequestMapping("/chapters")
public class ChaptersController {
    private final ChaptersService chaptersService;

    public ChaptersController(ChaptersService chaptersService) {
        this.chaptersService = chaptersService;
    }

    @GetMapping
    public ResponseEntity<List<Chapters>> getAllChapters() {
        return ResponseEntity.ok(chaptersService.getAllChapters());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER_READER', 'CUSTOMER_VIP', 'ADMIN')")
    public ResponseEntity<Chapters> createChapter(@RequestBody Chapters chapter, Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(chaptersService.addChapter(chapter, userId));
    }

    @PutMapping("/{chapterId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER_READER', 'CUSTOMER_VIP', 'ADMIN')")
    public ResponseEntity<?> updateChapter(@PathVariable String chapterId, @RequestBody Chapters chapter, Principal principal) {
        String userId = principal.getName();
        Chapters existingChapter = chaptersService.getChapterById(chapterId);
        
        if (existingChapter == null) {
            return ResponseEntity.badRequest().body("Chapter not found.");
        }

        if (existingChapter.getStatus() != ChaptersEnums.PENDING.getValue()) {
            return ResponseEntity.badRequest().body("You can only update a chapter when its status is PENDING.");
        }

        return ResponseEntity.ok(chaptersService.updateChapter(chapterId, chapter, userId));
    }

    @DeleteMapping("/{chapterId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER_READER', 'CUSTOMER_VIP', 'ADMIN')")
    public ResponseEntity<String> deleteChapter(@PathVariable String chapterId, Principal principal) {
        String userId = principal.getName();
        chaptersService.deleteChapter(chapterId, userId);
        return ResponseEntity.ok("Chapter deleted successfully.");
    }
}

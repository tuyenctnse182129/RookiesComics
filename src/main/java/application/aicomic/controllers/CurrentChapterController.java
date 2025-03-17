package application.aicomic.controllers;

import application.aicomic.dataAccess.CurrentChapterDTO;
import application.aicomic.models.CurrentChapter;
import application.aicomic.services.CurrentChapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/current-chapter")
@RestController
public class CurrentChapterController {
    private CurrentChapterService currentChapterService;
    @GetMapping
    public List<CurrentChapter> getAllCurrentChapter() {
        return currentChapterService.getAllCurrentChapter();
    }
    @PostMapping
    public CurrentChapter addCurrentChapter(@RequestBody CurrentChapter currentChapter) {
        return currentChapterService.addCurrentChapter(currentChapter);
    }

    @PutMapping("/{id}")
    public CurrentChapter updateCurrentChapter(@PathVariable String id, @RequestBody CurrentChapterDTO currentChapterDTO) {
        return currentChapterService.updateCurrentChapter(id, currentChapterDTO);
    }
    @GetMapping("/{id}")
    public CurrentChapter getCurrentChapterById(@PathVariable String id) {
        return currentChapterService.getCurrentChapterById(id);
    }
    @DeleteMapping("/{id}")
    public CurrentChapter deleteCurrentChapter(@PathVariable String id) {
        return currentChapterService.deleteCurrentChapter(id);
    }
}

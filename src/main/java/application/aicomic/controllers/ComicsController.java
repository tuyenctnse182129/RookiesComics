
package application.aicomic.controllers;


import org.springframework.web.bind.annotation.*;

import application.aicomic.services.ComicsService;

import java.util.List;

import org.springframework.http.ResponseEntity;

import application.aicomic.dataAccess.ComicsDTO;
import application.aicomic.models.Comics;
import application.aicomic.repositories.ComicsRepository;

@RestController
@RequestMapping("/comics")
public class ComicsController {
    private final ComicsService comicsService;
    private final ComicsRepository comicsRepository;

    public ComicsController(ComicsService comicsService, ComicsRepository comicsRepository) {
        this.comicsService = comicsService;
        this.comicsRepository = comicsRepository;
    }

    @GetMapping
    public List<Comics> getAllComics() {
        return comicsService.getAllComics();
    }

    @GetMapping("/{getName}")
    public Comics getComicsById(@PathVariable String comicName) {
        return comicsService.getComicsByName(comicName);
    }

    @GetMapping("/processing/{isProcessing}")
    public ResponseEntity<List<Comics>> getComicsByProcessingStatus(@RequestParam(defaultValue = "false") boolean isProcessing) {
        List<Comics> comicsList = comicsService.getComicsByProcessingStatus(isProcessing);
        return ResponseEntity.ok(comicsList);
    }
    @PutMapping("/approved/{isApproved}")
    public ResponseEntity<List<Comics>> getComicsByApprovedStatus(@RequestParam(defaultValue = "false") boolean isApproved) {
        List<Comics> comicsList = comicsService.getComicsByApprovedStatus(isApproved);
        return ResponseEntity.ok(comicsList);
    }
    @PutMapping("/rejected/{isRejected}")
    public ResponseEntity<List<Comics>> getComicsByRejectedStatus(@RequestParam(defaultValue = "false") boolean isRejected) {
        List<Comics> comicsList = comicsService.getComicsByRejectedStatus(isRejected);
        return ResponseEntity.ok(comicsList);
    }
    @GetMapping("/ongoing/{isOnGoing}")
    public ResponseEntity<List<Comics>> getComicsByOnGoingStatus(@RequestParam(defaultValue = "false") boolean isOnGoing) {
        List<Comics> comicsList = comicsService.getComicsByOnGoingStatus(isOnGoing);
        return ResponseEntity.ok(comicsList);
    }
    @GetMapping("/completed/{isCompleted}")
    public ResponseEntity<List<Comics>> getComicsByCompletionStatus(@RequestParam(defaultValue = "false") boolean isCompleted) {
        List<Comics> comicsList = comicsService.getComicsByCompletionStatus(isCompleted);
        return ResponseEntity.ok(comicsList);
    }
    @GetMapping("/stopped/{isStopped}")
    public ResponseEntity<List<Comics>> getComicsByStoppedStatus(@RequestParam(defaultValue = "false") boolean isStopped) {
        List<Comics> comicsList = comicsService.getComicsByStoppedStatus(isStopped);
        return ResponseEntity.ok(comicsList);
    }
    @GetMapping("/genres/genresName")
    public ResponseEntity<List<Comics>> getComicsByGenres_GenresName(@PathVariable String genresName){
        List<Comics> comicsList = comicsService.getComicsByGenres_GenresName(genresName);
        return ResponseEntity.ok(comicsList);
    }

    @PostMapping("/{create}")
    public Comics createComics(@RequestBody Comics comics) {
        return comicsService.saveComics(comics);
    }

    @PutMapping("/{update}")
    public Comics updateComics(@PathVariable String id, @RequestBody ComicsDTO comicsDTO) {
        return comicsService.updateComics(id, comicsDTO);
    }

    @DeleteMapping("/{delete}")
    public Comics deleteComics(@PathVariable String id) {
        return comicsService.deleteComics(id);
    }
}

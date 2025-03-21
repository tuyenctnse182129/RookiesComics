
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

    @GetMapping("/{status}")
    public List<Comics> findByStatus(byte status){
        return comicsService.findByStatus(status);
    }

    @GetMapping("/{name}")
    public Comics getComicsByName(@PathVariable String comicName) {
        return comicsService.getComicsByName(comicName);
    }
    @GetMapping("/{id}")
    public Comics getComicsById(@PathVariable String comicId) {
        return comicsService.getComicsByID(comicId);
    }

    @GetMapping("/genres/{genresName}")
    public ResponseEntity<List<Comics>> getComicsByGenres_GenresName(@PathVariable String genresName){
        List<Comics> comicsList = comicsService.getComicsByGenres_GenresName(genresName);
        return ResponseEntity.ok(comicsList);
    }

    @PostMapping
    public Comics createComics(@RequestBody Comics comics) {
        return comicsService.saveComics(comics);
    }

    @PutMapping("/{id}")
    public Comics updateComics(@PathVariable String id, @RequestBody ComicsDTO comicsDTO) {
        return comicsService.updateComics(id, comicsDTO);
    }

    @DeleteMapping("/{id}")
    public Comics deleteComics(@PathVariable String id) {
        return comicsService.deleteComics(id);
    }

    @GetMapping("/top-week")
    public List<Comics> getTopWeekComics() {
        return comicsService.getTopComicsWeek();
    }

    @GetMapping("/top-month")
    public List<Comics> getTopMonthComics() {
        return comicsService.getTopComicsMonth();
    }
}

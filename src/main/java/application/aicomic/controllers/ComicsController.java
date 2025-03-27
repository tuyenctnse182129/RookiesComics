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

    // ✅ Đảm bảo trả về UTF-8
    @GetMapping(produces = "application/json; charset=UTF-8")
    public List<Comics> getAllComics() {
        return comicsService.getAllComics();
    }

    @GetMapping(value = "/{comicId}", produces = "application/json; charset=UTF-8")
    public Comics getComicsById(@PathVariable String comicId) {
        return comicsService.getComicsByID(comicId);
    }

    @GetMapping(value = "/genres/{genresName}", produces = "application/json; charset=UTF-8")
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

    @GetMapping(value = "/top-week", produces = "application/json; charset=UTF-8")
    public List<Comics> getTopWeekComics() {
        return comicsService.getTopComicsWeek();
    }

    @GetMapping(value = "/top-month", produces = "application/json; charset=UTF-8")
    public List<Comics> getTopMonthComics() {
        return comicsService.getTopComicsMonth();
    }
}

package application.aicomic.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.aicomic.dataAccess.GenresDTO;
import application.aicomic.models.Genres;
import application.aicomic.services.GenresService;

@RestController
@RequestMapping("/genres")
public class GenresController {

    private final GenresService genresService;

    public GenresController(GenresService genresService) {
        this.genresService = genresService;
    }

    // Get all genres
    @GetMapping
    public ResponseEntity<List<Genres>> getAllGenres() {
        List<Genres> genresList = genresService.getAllGenres();
        return ResponseEntity.ok(genresList);
    }

    // Get genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Genres> getGenreById(@PathVariable String id) {
        Genres genre = genresService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    // Create a new genre
    @PostMapping
    public ResponseEntity<Genres> addGenre(@RequestBody Genres genres) {
        Genres createdGenre = genresService.addGenre(genres);
        return ResponseEntity.ok(createdGenre);
    }

    // Update an existing genre
    @PutMapping("/{updateId}")
    public ResponseEntity<Genres> updateGenre(@PathVariable String id, @RequestBody GenresDTO genresDTO) {
        Genres updatedGenre = genresService.updateGenre(id, genresDTO);
        return ResponseEntity.ok(updatedGenre);
    }

    // Soft delete a genre
    @DeleteMapping("/{deleteId}")
    public ResponseEntity<Genres> deleteGenre(@PathVariable String id) {
        Genres deletedGenre = genresService.deleteGenre(id);
        return ResponseEntity.ok(deletedGenre);
    }

    // Restore a deleted genre
    @PutMapping("/restore/{id}")
    public ResponseEntity<Genres> restoreGenre(@PathVariable String id) {
        Genres restoredGenre = genresService.restoreGenre(id);
        return ResponseEntity.ok(restoredGenre);
    }
}

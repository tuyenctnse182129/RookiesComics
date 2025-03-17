package application.aicomic.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import application.aicomic.dataAccess.GenresDTO;
import application.aicomic.models.Genres;
import application.aicomic.services.GenresService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/genres")
public class GenresController {
    @Autowired
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
    @PutMapping("/{id}")
    public ResponseEntity<Genres> updateGenre(@PathVariable String id, @RequestBody GenresDTO genresDTO) {
        Genres updatedGenre = genresService.updateGenre(id, genresDTO);
        return ResponseEntity.ok(updatedGenre);
    }

    // Soft delete a genre
    @DeleteMapping("/{id}")
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

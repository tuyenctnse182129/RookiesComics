package application.aicomic.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.aicomic.models.Bookshelves;
import application.aicomic.services.BookshelvesService;

@RestController
@RequestMapping("/bookshelves")
public class BookshelvesController {

    @Autowired
    private BookshelvesService bookshelvesService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createBookshelf(@PathVariable String userId, @RequestBody Bookshelves newBookshelf) {
        try {
            Bookshelves createdBookshelf = bookshelvesService.createBookshelf(userId, newBookshelf);
            return ResponseEntity.ok(createdBookshelf);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{bookshelfId}")
    public ResponseEntity<?> deleteBookshelf(@PathVariable String bookshelfId) {
        try {
            bookshelvesService.deleteBookshelf(bookshelfId);
            return ResponseEntity.ok("Bookshelf deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bookshelves>> getBookshelvesByUser(@PathVariable String userId) {
        List<Bookshelves> bookshelves = bookshelvesService.getBookshelvesByUser(userId);
        return ResponseEntity.ok(bookshelves);
    }

    @PostMapping("/{bookshelfId}/addComic/{comicId}")
    public ResponseEntity<?> addComicToBookshelf(@PathVariable String bookshelfId, @PathVariable String comicId) {
        try {
            Bookshelves updatedBookshelf = bookshelvesService.addComicToBookshelf(bookshelfId, comicId);
            return ResponseEntity.ok(updatedBookshelf);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{bookshelfId}/removeComic/{comicId}")
    public ResponseEntity<?> removeComicFromBookshelf(@PathVariable String bookshelfId, @PathVariable String comicId) {
        try {
            Bookshelves updatedBookshelf = bookshelvesService.removeComicFromBookshelf(bookshelfId, comicId);
            return ResponseEntity.ok(updatedBookshelf);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

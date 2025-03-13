package application.aicomic.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.aicomic.enums.Role;
import application.aicomic.models.Bookshelves;
import application.aicomic.models.Comics;
import application.aicomic.models.Users;
import application.aicomic.repositories.BookshelvesRepository;
import application.aicomic.repositories.ComicsRepository;
import application.aicomic.repositories.UsersRepository;

@Service
public class BookshelvesService {

    @Autowired
    private BookshelvesRepository bookshelvesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ComicsRepository comicsRepository;

    public Bookshelves createBookshelf(String userId, Bookshelves newBookshelf) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Count the number of bookshelves the user currently owns
        int userBookshelfCount = bookshelvesRepository.countByUser(user);

        // Determine bookshelf limit based on user role
        Role userRole = Role.fromValue(user.getRole());
        int bookshelfLimit = (userRole == Role.CUSTOMER_NORMAL) ? 5 : 10;

        if (userBookshelfCount >= bookshelfLimit) {
            throw new RuntimeException("Bookshelf limit reached! You can have up to " + bookshelfLimit + " bookshelves.");
        }

        // Assign user to the bookshelf and save it
        newBookshelf.setUser(user);
        return bookshelvesRepository.save(newBookshelf);
    }

    public void deleteBookshelf(String bookshelfId) {
        if (!bookshelvesRepository.existsById(bookshelfId)) {
            throw new RuntimeException("Bookshelf not found.");
        }
        bookshelvesRepository.deleteById(bookshelfId);
    }

    public List<Bookshelves> getBookshelvesByUser(String userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookshelvesRepository.findByUser(user);
    }

    public Bookshelves addComicToBookshelf(String userId, String comicId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user has any bookshelves
        List<Bookshelves> userBookshelves = bookshelvesRepository.findByUser(user);
        Bookshelves bookshelf;

        if (userBookshelves.isEmpty()) {
            // If no bookshelf exists, create one named "Follow"
            bookshelf = createFollowBookshelf(user);
        } else {
            // Use the first available bookshelf
            bookshelf = userBookshelves.get(0);
        }

        Comics comic = comicsRepository.findById(comicId)
                .orElseThrow(() -> new RuntimeException("Comic not found"));

        // Prevent duplicate comics in the bookshelf
        if (bookshelf.getComics().contains(comic)) {
            throw new RuntimeException("This comic is already in the bookshelf.");
        }

        bookshelf.getComics().add(comic);
        return bookshelvesRepository.save(bookshelf);
    }

    private Bookshelves createFollowBookshelf(Users user) {
        Bookshelves followBookshelf = new Bookshelves();
        followBookshelf.setBookshelveName("Follow");
        followBookshelf.setDescription("Default bookshelf for following comics.");
        followBookshelf.setStatus((byte) 1);
        followBookshelf.setUser(user);
        return bookshelvesRepository.save(followBookshelf);
    }

    public Bookshelves removeComicFromBookshelf(String bookshelfId, String comicId) {
        Bookshelves bookshelf = bookshelvesRepository.findById(bookshelfId)
                .orElseThrow(() -> new RuntimeException("Bookshelf not found"));

        Comics comic = comicsRepository.findById(comicId)
                .orElseThrow(() -> new RuntimeException("Comic not found"));

        // Remove the comic from the bookshelf
        if (!bookshelf.getComics().contains(comic)) {
            throw new RuntimeException("This comic is not in the bookshelf.");
        }

        bookshelf.getComics().remove(comic);
        return bookshelvesRepository.save(bookshelf);
    }
}

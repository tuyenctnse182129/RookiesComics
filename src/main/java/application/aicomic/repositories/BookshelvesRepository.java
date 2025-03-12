package application.aicomic.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.aicomic.models.Bookshelves;
import application.aicomic.models.Users;

@Repository
public interface BookshelvesRepository extends JpaRepository<Bookshelves, String> {

    /**
     * Finds all bookshelves belonging to a specific user.
     *
     * @param user The user entity.
     * @return A list of bookshelves.
     */
    List<Bookshelves> findByUser(Users user);

    /**
     * Counts the number of bookshelves a user owns.
     *
     * @param user The user entity.
     * @return The count of bookshelves owned by the user.
     */
    int countByUser(Users user);
}

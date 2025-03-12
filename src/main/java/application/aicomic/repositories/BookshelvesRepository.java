package application.aicomic.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.aicomic.models.Bookshelves;
import application.aicomic.models.Users;

@Repository
public interface BookshelvesRepository extends JpaRepository<Bookshelves, String> {

    List<Bookshelves> findByUser(Users user);

    int countByUser(Users user);
}

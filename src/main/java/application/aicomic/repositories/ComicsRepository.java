
package application.aicomic.repositories;

import application.aicomic.models.Comics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComicsRepository extends JpaRepository<Comics, String> {

    Optional<Comics> findByComicName(String comicName);

    Optional<Comics> findByComicID(String comicId);
    List<Comics> findByStatus(byte status);
    List<Comics> findByGenres_GenresName(String genresName);
    List<Comics> findTop8ByCreatedDateAfterOrderByViewDesc(LocalDateTime startDate);

}

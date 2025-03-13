package application.aicomic.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.aicomic.models.Chapters; 

@Repository
public interface ChaptersRepository extends JpaRepository<Chapters, String> {
    List<Chapters> findByStatus(byte status); 
}

package application.aicomic.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import application.aicomic.dataAccess.GenresDTO;
import application.aicomic.enums.GenresEnums;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.Genres;
import application.aicomic.repositories.GenresRepository;

@Service
public class GenresService {
    private final GenresRepository genresRepository;
    private final Mapper mapper;

    public GenresService(GenresRepository genresRepository, Mapper mapper) {
        this.genresRepository = genresRepository;
        this.mapper = mapper;
    }

    // Get all genres
    public List<Genres> getAllGenres() {
        return genresRepository.findAll();
    }

    // Get genre by ID
    public Genres getGenreById(String id) {
        return genresRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    // Add a new genre
    public Genres addGenre(Genres genres) {
        genres.setStatus(GenresEnums.AVAILABLE.getValue());
        return genresRepository.save(genres);
    }

    // Update an existing genre
    public Genres updateGenre(String id, GenresDTO genresDTO) {
        Genres genres = genresRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
        mapper.updateGenres(genres, genresDTO); // Maps fields from DTO to the entity
        return genresRepository.save(genres);
    }

    // Soft delete a genre
    public Genres deleteGenre(String id) {
        Optional<Genres> genresOptional = genresRepository.findById(id);
        if (genresOptional.isPresent()) {
            Genres genres = genresOptional.get();
            genres.setStatus(GenresEnums.DELETED.getValue());
            return genresRepository.save(genres);
        }
        throw new RuntimeException("Genre not found");
    }

    // Restore a deleted genre
    public Genres restoreGenre(String id) {
        Genres genres = genresRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
        if (genres.getStatus() == GenresEnums.DELETED.getValue()) {
            genres.setStatus(GenresEnums.AVAILABLE.getValue());
            return genresRepository.save(genres);
        }
        throw new RuntimeException("Genre is not deleted and cannot be restored");
    }
}

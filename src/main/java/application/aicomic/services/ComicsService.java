
package application.aicomic.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import application.aicomic.enums.ComicsEnums;
import application.aicomic.dataAccess.ComicsDTO;
import application.aicomic.dataAccess.ComicsServiceResponseDTO;
import application.aicomic.models.Comics;
import application.aicomic.repositories.ComicsRepository;
import application.aicomic.mapper.Mapper;

@Service
public class ComicsService {

    private final ComicsRepository comicsRepository;
    private Mapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ComicsService.class);

    public ComicsService(ComicsRepository comicsRepository) {

        this.comicsRepository = comicsRepository;
    }

    public List<Comics> getAllComics() {
        return comicsRepository.findAll();
    }

    public Comics getComicsByName(String comicName) {
        return comicsRepository.findByComicName(comicName).orElse(null);
    }
    public Comics getComicsByID(String comicId) {
        return comicsRepository.findByComicID(comicId).orElse(null);
    }

    public Comics getComicsById(String comicId) {
        return comicsRepository.findByComicId(comicId).orElse(null);
    }

    public List<Comics> findByStatus(byte status){
        return comicsRepository.findByStatus(status);
    }

    public List<Comics> getComicsByGenres_GenresName(String genresName){
        return comicsRepository.findByGenres_GenresName(genresName);
    }

    public Comics saveComics(Comics comics) {
        return comicsRepository.save(comics);
    }

    public ComicsServiceResponseDTO getByName(String name) {
        Optional<Comics> comicsOptional = comicsRepository.findById(name);
        if (comicsOptional.isPresent()) {
            Comics comics = comicsOptional.get();
            return new ComicsServiceResponseDTO(true, "Comics found.", Collections.singletonList(comics));
        }
        return new ComicsServiceResponseDTO(false, "No Comics found for the given Comics Name.",
                Collections.emptyList());
    }

    public Comics addComics(Comics comics) {
        return comicsRepository.save(comics);
    }

    public Comics updateComics(String id, ComicsDTO comicsDTO) {
        Comics comics = comicsRepository.findById(id).orElseThrow(() -> new RuntimeException("Comics not found"));
        mapper.updateComics(comics, comicsDTO);
        return comicsRepository.save(comics);
    }

    public Comics deleteComics(String id) {
        Optional<Comics> comics = comicsRepository.findById(id);
        if (comics.isPresent()) {
            Comics x = comics.get();
            x.setStatus(ComicsEnums.PROCESSING.getValue());

            return comicsRepository.save(x);
        } else if (comics.isPresent()) {
            Comics x = comics.get();
            x.setStatus(ComicsEnums.COMPLETED.getValue());
            return comicsRepository.save(x);
        } else if (comics.isPresent()) {
            Comics x = comics.get();
            x.setStatus(ComicsEnums.STOPPED.getValue());
            return comicsRepository.save(x);
        }
        return null;
    }
    public List<Comics> getTopComicsWeek(){
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return comicsRepository.findTop8ByCreatedDateAfterOrderByViewDesc(oneWeekAgo);
    }
    public List<Comics> getTopComicsMonth(){
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        return comicsRepository.findTop8ByCreatedDateAfterOrderByViewDesc(oneMonthAgo);
    }
}

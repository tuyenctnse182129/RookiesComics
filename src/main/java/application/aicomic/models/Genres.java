package application.aicomic.models;

import java.util.List;

import application.aicomic.enums.CommentsEnums;
import application.aicomic.enums.GenresEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Genres")
@Data
public class Genres {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "genres_id", length = 50)
    private String genresId;

    @Column(name = "genres_name", length = 50)
    private String genresName;

    @Column(name = "genres_description", length = 10000)
    private String genresDescription;

    @NotNull
    @Column(name = "status")
    private byte status = GenresEnums.AVAILABLE.getValue();

    @JsonIgnore
    @OneToMany(mappedBy = "genres")
    private List<Comics> comics;
}

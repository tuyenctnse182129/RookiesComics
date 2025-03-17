package application.aicomic.models;

import application.aicomic.enums.CommentsEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Comics")
@Data
public class Comics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comic_id", length = 50)
    private String comicId;

    @NotNull
    @Column(name = "comic_name", length = 50)
    private String comicName;

    @NotNull
    @Column(name = "user_id", length = 50)
    private String userId;

    @NotNull
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @NotNull
    @Column(name = "quantity_chap")
    private int quantityChap;

    @NotNull
    @Column(name = "cover_url", length = 250)
    private String coverUrl;

    @Column(name = "description", length = 250)
    private String description;

    @NotNull
    @Column(name = "status")
    private byte status = CommentsEnums.AVAILABLE.getValue();

    @NotNull
    @Column(name = "view")
    private long view;

    @NotNull
    @Column(name = "genres_id", length = 50)
    private String genresId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "genres_id", referencedColumnName = "genres_id", insertable = false, updatable = false)
    private Genres genres;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private Users user;

    @JsonIgnore
    @ManyToMany(mappedBy = "comics")
    private List<Bookshelves> bookshelves;

    @JsonIgnore
    @OneToOne(mappedBy = "comics")
    private Orders orders;

    @JsonIgnore
    @OneToMany(mappedBy = "comic")
    private List<Chapters> chapters;
}

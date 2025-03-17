package application.aicomic.models;

import application.aicomic.enums.CommentsEnums;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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


    @ManyToOne
    @JoinColumn(name = "genres_id", referencedColumnName = "genres_id")
    @JsonManagedReference
    private Genres genres;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    @ManyToMany(mappedBy = "comics")
    private List<Bookshelves> bookshelves;

    @OneToOne(mappedBy = "comics")
    private Orders orders;

    @OneToMany(mappedBy = "comic")
    private List<Chapters> chapters;

    public String getUserId() {
        return user != null ? user.getUserId() : null;
    }

}

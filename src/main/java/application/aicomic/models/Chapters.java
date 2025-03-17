package application.aicomic.models;

import java.time.LocalDateTime;
import java.util.List;

import application.aicomic.enums.ChaptersEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Chapters")
@Data
public class Chapters {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "chapter_id", length = 50)
    private String chapterId;

    @NotNull
    @Column(name = "chapter_name", length = 50)
    private String chapterName;

    @NotNull
    @Column(name = "comic_id", length = 50)
    private String comicId;

    @NotNull
    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @NotNull
    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "status")
    private byte status;

    @Column(name = "type")
    private byte type;

    @ManyToOne
    @JoinColumn(name = "comic_id", referencedColumnName = "comic_id", insertable = false, updatable = false)
    @JsonIgnore
    private Comics comic;

    @OneToMany(mappedBy = "chapter")
    private List<Comments> comments;

    @OneToMany(mappedBy = "chapter")
    private List<ChapterImages> chapterImages;

    @OneToMany(mappedBy = "chapter")
    private List<OrderDetails> orderDetails;

    @OneToMany(mappedBy = "chapter")
    private List<CurrentChapter> currentChapters;
}

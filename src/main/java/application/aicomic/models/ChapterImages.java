package application.aicomic.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Table(name = "ChapterImages")
@Data
public class ChapterImages {
    @Id
    @Column(name = "image_id", length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String imageId;

    @Column(name = "image_url", length = 1000)
    private String imageURL;

    @NotNull
    @Column(name = "chapter_id", length = 50)
    private String chapterId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "chapter_id", referencedColumnName = "chapter_id", insertable = false, updatable = false)
    private Chapters chapter;
}

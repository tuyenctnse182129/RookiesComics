package application.aicomic.models;

import java.util.List;

import application.aicomic.enums.BookshelvesEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Bookshelves")
@Data
public class Bookshelves {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bookshelve_id", length = 50, unique = true, nullable = false)
    private String bookshelveId;

    @NotNull
    @Column(name = "bookshelve_name", length = 50, nullable = false)
    private String bookshelveName;

    @Column(name = "description", length = 250)
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private byte status = BookshelvesEnums.ACTIVE.getValue();

    @ManyToMany
    @JoinTable(
            name = "Bookshelves_Comics",
            joinColumns = @JoinColumn(name = "bookshelve_id"),
            inverseJoinColumns = @JoinColumn(name = "comic_id")
    )
    @JsonIgnore
    private List<Comics> comics;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}

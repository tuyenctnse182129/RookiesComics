package application.aicomic.models;

import java.time.LocalDate;
import java.util.ArrayList;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import application.aicomic.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Users")
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", length = 50)
    private String userId;


    @NotNull
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 10)
    private String gender;


    @Unique
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @NotNull
    @Column(name = "role")
    private byte role;

    @Column(name = "status")
    private byte status;

    @OneToMany(mappedBy = "user")
    private List<Comics> comics;

    @OneToMany(mappedBy = "user")
    private List<Comments> comments;

    @OneToMany(mappedBy = "user")
    private List<Comments> reports;

    @OneToMany(mappedBy = "user")
    private List<Wallets> wallets;

    @OneToMany(mappedBy = "user")
    private List<CurrentChapter> purchasedComics;

    @OneToMany(mappedBy = "user")
    private List<Orders> orders;

    @OneToMany(mappedBy = "user")
    private List<PurchasedCoins> purchasedCoins;

    @OneToMany(mappedBy = "user")
    private List<CurrentChapter> currentChapters;

    @OneToMany(mappedBy = "user")
    private List<Bookshelves> bookshelves;
    
    public Role getRoleEnum() {
        return Role.fromValue(this.role);
    }

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRoleEnum().name()));
    }
}


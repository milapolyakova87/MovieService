package app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
public class UserList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false)
    private String publicId;

    @Setter
    @Getter
    @ManyToOne
    private User owner;

    @Setter
    @Getter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserListMovie> movies;

    @Setter
    @Getter
    @Column(nullable = false)
    private boolean isPublic;

}

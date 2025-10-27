package app.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@Table(name = "user_list_movie")
@Getter
@Setter
public class UserListMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserList list;

    @ManyToOne
    private UserList userList;

    @ManyToOne
    private Movie movie;

    private Integer rating;
    private LocalDate viewDate;

}

package hr.fer.zemris.rznu.lab1.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Data
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    private User author;

    @NonNull
    private String body;
}

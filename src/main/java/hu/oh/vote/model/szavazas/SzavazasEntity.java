package hu.oh.vote.model.szavazas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        schema = "oh",
        name = "szavazas",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_szavazas_azonosito",
                        columnNames = "azonosito"
                ),
                @UniqueConstraint(
                        name = "uk_szavazas_idopont",
                        columnNames = "idopont"
                )
        }
)
@Getter
@Setter
public class SzavazasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String azonosito;

    @Column(nullable = false)
    private Instant idopont;

    @Column(nullable = false)
    private String targy;

    @Column(nullable = false, length = 1)
    private String tipus;

    @Column(nullable = false, length = 1)
    private String eljaras;

    @Column(nullable = false)
    private String elnok;

    @OneToMany(
            mappedBy = "szavazasEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SzavazatEntity> szavazatok = new ArrayList<>();
}
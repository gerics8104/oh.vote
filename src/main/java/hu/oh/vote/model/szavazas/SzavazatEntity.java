package hu.oh.vote.model.szavazas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(
        schema = "oh",
        name = "szavazat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_szavazat_kepviselo",
                        columnNames = {"szavazas_id", "kepviselo"}
                )
        }
)
@Getter
@Setter
public class SzavazatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kepviselo;

    @Column(nullable = false, length = 1)
    private String szavazat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "szavazas_id", nullable = false)
    private SzavazasEntity szavazasEntity;
}
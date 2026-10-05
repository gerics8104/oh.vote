package hu.oh.vote.repsoitory.szavazas;


import hu.oh.vote.model.szavazas.SzavazasEntity;
import hu.oh.vote.model.szavazas.SzavazatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SzavazasRepository extends JpaRepository<SzavazasEntity, Long> {

    @Query("""
        SELECT sz
        FROM SzavazatEntity sz
        JOIN sz.szavazasEntity s
        WHERE s.azonosito = :szavazas
          AND sz.kepviselo = :kepviselo
        """)
    Optional<SzavazatEntity> getSzavazat(
            @Param("szavazas") String szavazas,
            @Param("kepviselo") String kepviselo
    );

    @Query("""
    SELECT COUNT(sz)
    FROM SzavazatEntity sz
    WHERE sz.szavazasEntity.idopont = (
        SELECT MAX(s.idopont)
        FROM SzavazasEntity s
        WHERE s.tipus = 'j'
          AND s.idopont < :idopont
    )
    """)
    long countJelenlevok(@Param("idopont") Instant idopont);

    @Query("""
    SELECT s
    FROM SzavazasEntity s
    WHERE s.azonosito = :azonosito
    """)
    Optional<SzavazasEntity> findByAzonosito(
            @Param("azonosito") String azonosito);
}

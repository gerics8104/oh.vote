package hu.oh.vote.repository.szavazas;


import hu.oh.vote.model.szavazas.SzavazasEntity;
import hu.oh.vote.model.szavazas.SzavazatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SzavazasRepository extends JpaRepository<SzavazasEntity, Long> {

    @Query("""
            SELECT sz
            FROM SzavazatEntity sz
            JOIN sz.szavazasEntity s
            WHERE s.azonosito = :szavazasId
              AND sz.kepviselo = :kepviselo
            """)
    Optional<SzavazatEntity> getSzavazat(
            @Param("szavazasId") String szavazasId,
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

    @Query("""
    SELECT s
    FROM SzavazasEntity s
    LEFT JOIN FETCH s.szavazatok
    WHERE s.idopont >= :tol
              AND s.idopont < :ig
    ORDER BY s.idopont
    """)
    List<SzavazasEntity> findNapiSzavazasok(
            @Param("tol") Instant tol,
            @Param("ig") Instant ig
    );

    @Query("""
            SELECT COUNT(sz)
            FROM SzavazatEntity sz
            JOIN sz.szavazasEntity s
            WHERE s.idopont >= :tol
              AND s.idopont < :ig
              AND s.tipus <> 'j'
            """)

    long countReszvetelek(
            @Param("tol") Instant tol,
            @Param("ig") Instant ig);


    @Query("""
            SELECT s
            FROM SzavazasEntity s
            LEFT JOIN FETCH s.szavazatok
            WHERE s.idopont >= :tol
              AND s.idopont < :ig
              AND s.eljaras IN ('s', 'k', 'e')
            ORDER BY s.idopont
            """)
    List<SzavazasEntity> findKulonlegesEljarasuSzavazasok(
            @Param("tol") Instant tol,
            @Param("ig") Instant ig
    );
}

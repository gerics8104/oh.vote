package hu.oh.vote.repsoitory.szavazas;


import hu.oh.vote.model.szavazas.SzavazasEntity;
import hu.oh.vote.model.szavazas.SzavazatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}

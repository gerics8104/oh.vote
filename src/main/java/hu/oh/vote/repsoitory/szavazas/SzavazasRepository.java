package hu.oh.vote.repsoitory.szavazas;


import hu.oh.vote.model.szavazas.SzavazasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SzavazasRepository extends JpaRepository<SzavazasEntity, Long> {
}

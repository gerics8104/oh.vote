package hu.oh.vote.service.szavazas.impl;

import hu.oh.vote.Szavazas;
import hu.oh.vote.SzavazasValasz;
import hu.oh.vote.exception.szavazas.SzavazasValidationException;
import hu.oh.vote.model.szavazas.SzavazasEntity;
import hu.oh.vote.model.szavazas.SzavazatEntity;
import hu.oh.vote.repsoitory.szavazas.SzavazasRepository;
import hu.oh.vote.service.szavazas.SzavazasService;
import hu.oh.vote.service.szavazas.util.SzavazasIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SzavazasServiceImpl implements SzavazasService {

    private final SzavazasRepository szavazasRepository;

    @Autowired
    public SzavazasServiceImpl(SzavazasRepository szavazasRepository) {
        this.szavazasRepository = szavazasRepository;
    }

    @Transactional
    @Override
    public SzavazasValasz szavazas(Szavazas dto) {

        ellenorizElnokSzavazott(dto);

        SzavazasEntity entity = new SzavazasEntity();

        entity.setAzonosito(SzavazasIdGenerator.generate());
        entity.setIdopont(dto.getIdopont().toInstant());
        entity.setTargy(dto.getTargy());
        entity.setTipus(dto.getTipus().value());

        if (dto.getEljaras() != null) {
            entity.setEljaras(dto.getEljaras().value());
        }

        entity.setElnok(dto.getElnok());

        dto.getSzavazatok().forEach(dtoSzavazat -> {
            SzavazatEntity szavazat = new SzavazatEntity();
            szavazat.setKepviselo(dtoSzavazat.getKepviselo());
            szavazat.setSzavazat(dtoSzavazat.getSzavazat().value());
            szavazat.setSzavazasEntity(entity);
            entity.getSzavazatok().add(szavazat);
        });
        szavazasRepository.save(entity);
        SzavazasValasz valasz = new SzavazasValasz();
        valasz.setSzavazasId(entity.getAzonosito());
        return valasz;
    }

    private void ellenorizElnokSzavazott(Szavazas dto) {

        boolean szavazott = dto.getSzavazatok()
                .stream()
                .anyMatch(szavazat ->
                        dto.getElnok().equals(szavazat.getKepviselo()));

        if (!szavazott) {
            throw new SzavazasValidationException(
                    "A szavazást vezető elnöknek is szavaznia kell."
            );
        }
    }
}
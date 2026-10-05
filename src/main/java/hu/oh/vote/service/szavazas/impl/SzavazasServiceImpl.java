package hu.oh.vote.service.szavazas.impl;

import hu.oh.vote.Szavazas;
import hu.oh.vote.SzavazasEredmenyValasz;
import hu.oh.vote.SzavazasValasz;
import hu.oh.vote.SzavazatValasz;
import hu.oh.vote.exception.szavazas.SzavazasNotFoundException;
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

    private static final int OSSZES_KEPVISELO = 200;


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

    @Override
    @Transactional(readOnly = true)
    public SzavazatValasz getSzavazat(String szavazas, String kepviselo) {
        SzavazatEntity entity = szavazasRepository
                .getSzavazat(szavazas, kepviselo)
                .orElseThrow(() ->
                        new SzavazasNotFoundException(
                                "A szavazás vagy a képviselő szavazata nem található."
                        )
                );

        SzavazatValasz valasz = new SzavazatValasz();
        valasz.setSzavazat(entity.getSzavazat());

        return valasz;
    }


    @Override
    @Transactional(readOnly = true)
    public SzavazasEredmenyValasz getEredmeny(String szavazasAzonosito) {

        SzavazasEntity szavazas = szavazasRepository
                .findByAzonosito(szavazasAzonosito)
                .orElseThrow(() ->
                        new SzavazasNotFoundException(
                                "A megadott azonosítóval nem található szavazás."
                        )
                );

        long igenek = szavazas.getSzavazatok().stream()
                .filter(sz -> "i".equals(sz.getSzavazat()))
                .count();

        long nemek = szavazas.getSzavazatok().stream()
                .filter(sz -> "n".equals(sz.getSzavazat()))
                .count();

        long tartozkodasok = szavazas.getSzavazatok().stream()
                .filter(sz -> "t".equals(sz.getSzavazat()))
                .count();

        long kepviselokSzama;
        boolean elfogadott;

        switch (szavazas.getTipus()) {
            case "j" -> {
                kepviselokSzama = szavazas.getSzavazatok().size();
                elfogadott = true;
            }

            case "e" -> {
                kepviselokSzama = szavazasRepository
                        .countJelenlevok(szavazas.getIdopont());
                if (kepviselokSzama == 0) {
                    throw new SzavazasValidationException(
                            "Nem található korábbi jelenléti szavazás."
                    );
                }
                elfogadott = igenek > kepviselokSzama / 2;
            }

            case "m" -> {
                kepviselokSzama = OSSZES_KEPVISELO;
                elfogadott = igenek > OSSZES_KEPVISELO / 2;
            }

            default -> throw new IllegalStateException(
                    "Ismeretlen szavazástípus: " + szavazas.getTipus()
            );
        }

        SzavazasEredmenyValasz valasz = new SzavazasEredmenyValasz();

        valasz.setEredmeny(
                elfogadott
                        ? SzavazasEredmenyValasz.Eredmeny.F
                        : SzavazasEredmenyValasz.Eredmeny.U
        );

        valasz.setKepviselokSzama((int) kepviselokSzama);
        valasz.setIgenekSzama((int) igenek);
        valasz.setNemekSzama((int) nemek);
        valasz.setTartozkodasokSzama((int) tartozkodasok);

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
package hu.oh.vote.service.szavazas.impl;

import hu.oh.vote.exception.szavazas.SzavazasNotFoundException;
import hu.oh.vote.exception.szavazas.SzavazasValidationException;
import hu.oh.vote.kimutatasok.KepviseloReszvetelAtlag;
import hu.oh.vote.kimutatasok.KulonlegesEljarasokSzamaValasz;
import hu.oh.vote.model.szavazas.SzavazasEntity;
import hu.oh.vote.model.szavazas.SzavazatEntity;
import hu.oh.vote.napi_szavazasok.NapiSzavazasokValasz;
import hu.oh.vote.napi_szavazasok.Szavazasok;
import hu.oh.vote.napi_szavazasok.Szavazatok;
import hu.oh.vote.repository.szavazas.SzavazasRepository;
import hu.oh.vote.service.szavazas.SzavazasService;
import hu.oh.vote.service.szavazas.util.SzavazasIdGenerator;
import hu.oh.vote.szavazas.Szavazas;
import hu.oh.vote.szavazas.SzavazasValasz;
import hu.oh.vote.szavazas_eredmeny.SzavazasEredmenyValasz;
import hu.oh.vote.szavazat.SzavazatValasz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SzavazasServiceImpl implements SzavazasService {

    private static final int OSSZES_KEPVISELO = 200;

    private static final ZoneId ZONE_ID =
            ZoneId.of("Europe/Budapest");


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
    public SzavazatValasz getSzavazat(String szavazasId, String kepviselo) {
        SzavazatEntity entity = szavazasRepository
                .getSzavazat(szavazasId, kepviselo)
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
        return getEredmeny(szavazas);
    }

    @Override
    @Transactional(readOnly = true)
    public NapiSzavazasokValasz getNapiSzavazasok(LocalDate datum) {
        Instant tol = datum
                .atStartOfDay(ZONE_ID)
                .toInstant();

        Instant ig = datum
                .plusDays(1)
                .atStartOfDay(ZONE_ID)
                .toInstant();

        List<SzavazasEntity> szavazasok =
                szavazasRepository.findNapiSzavazasok(tol, ig);

        NapiSzavazasokValasz valasz = new NapiSzavazasokValasz();
        valasz.setSzavazasok(
                szavazasok.stream()
                        .map(this::toNapiSzavazas)
                        .toList()
        );
        return valasz;
    }


    @Override
    @Transactional(readOnly = true)
    public KepviseloReszvetelAtlag getKepviseloReszvetelAtlag(
            LocalDate tol,
            LocalDate ig) {

        Instant tolInstant = tol
                .atStartOfDay(ZONE_ID)
                .toInstant();

        Instant igInstant = ig
                .plusDays(1)
                .atStartOfDay(ZONE_ID)
                .toInstant();

        long reszvetelek = szavazasRepository
                .countReszvetelek(tolInstant, igInstant);
        double atlag = reszvetelek / (double) OSSZES_KEPVISELO;
        KepviseloReszvetelAtlag valasz =
                new KepviseloReszvetelAtlag();
        valasz.setAtlag(
                Math.round(atlag * 100.0) / 100.0
        );
        return valasz;
    }


    @Override
    @Transactional(readOnly = true)
    public KulonlegesEljarasokSzamaValasz getKulonlegesEljarasokSzama(
            LocalDate tol,
            LocalDate ig) {

        int osszesElfogadott = 0;
        int osszesElutasitott = 0;

        KulonlegesEljarasokSzamaValasz valasz =
                new KulonlegesEljarasokSzamaValasz();
        Instant tolInstant = tol
                .atStartOfDay(ZONE_ID)
                .toInstant();
        Instant igInstant = ig
                .plusDays(1)
                .atStartOfDay(ZONE_ID)
                .toInstant();
        List<SzavazasEntity> szavazasok =
                szavazasRepository.findKulonlegesEljarasuSzavazasok(
                        tolInstant,
                        igInstant
                );

        Map<String, hu.oh.vote.kimutatasok.Szavazasok> szavazasErdemenyekMap =
                new LinkedHashMap<>();

        for (SzavazasEntity szavazasEntity : szavazasok) {
            SzavazasEredmenyValasz eredmeny = getEredmeny(szavazasEntity);

            if (eredmeny.getEredmeny()
                    == SzavazasEredmenyValasz.Eredmeny.F) {
                osszesElfogadott++;
            } else {
                osszesElutasitott++;
            }

            String key = szavazasEntity.getEljaras()
                    + "_"
                    + eredmeny.getEredmeny().value();

            var adottSzavazas = szavazasErdemenyekMap.get(key);

            if (adottSzavazas == null) {
                adottSzavazas = new hu.oh.vote.kimutatasok.Szavazasok();
                adottSzavazas.setEljaras(
                        hu.oh.vote.kimutatasok.Szavazasok.Eljaras
                                .fromValue(szavazasEntity.getEljaras())
                );
                adottSzavazas.setEredmeny(
                        hu.oh.vote.kimutatasok.Szavazasok.Eredmeny
                                .fromValue(eredmeny.getEredmeny().value())
                );
                adottSzavazas.setSzam(1);
                szavazasErdemenyekMap.put(key, adottSzavazas);
            } else {
                adottSzavazas.setSzam(adottSzavazas.getSzam() + 1);
            }

        }

        hu.oh.vote.kimutatasok.Szavazasok osszesElfogadottSzavazs = new hu.oh.vote.kimutatasok.Szavazasok();
        osszesElfogadottSzavazs.setEljaras(hu.oh.vote.kimutatasok.Szavazasok.Eljaras.ÖSSZES);
        osszesElfogadottSzavazs.setEredmeny(hu.oh.vote.kimutatasok.Szavazasok.Eredmeny.F);
        osszesElfogadottSzavazs.setSzam(osszesElfogadott);

        hu.oh.vote.kimutatasok.Szavazasok osszesElutasitottSzavazs = new hu.oh.vote.kimutatasok.Szavazasok();
        osszesElutasitottSzavazs.setEljaras(hu.oh.vote.kimutatasok.Szavazasok.Eljaras.ÖSSZES);
        osszesElutasitottSzavazs.setEredmeny(hu.oh.vote.kimutatasok.Szavazasok.Eredmeny.U);
        osszesElutasitottSzavazs.setSzam(osszesElutasitott);

        hu.oh.vote.kimutatasok.Szavazasok osszesSzavazas = new hu.oh.vote.kimutatasok.Szavazasok();
        osszesSzavazas.setEljaras(hu.oh.vote.kimutatasok.Szavazasok.Eljaras.ÖSSZES);
        osszesSzavazas.setEredmeny(hu.oh.vote.kimutatasok.Szavazasok.Eredmeny.ÖSSZES);
        osszesSzavazas.setSzam(osszesElfogadott + osszesElutasitott);

        szavazasErdemenyekMap.put("O-F", osszesElfogadottSzavazs);
        szavazasErdemenyekMap.put("O-U", osszesElutasitottSzavazs);
        szavazasErdemenyekMap.put("O-O", osszesSzavazas);
        valasz.setSzavazasok(szavazasErdemenyekMap.values().stream().toList());
        return valasz;
    }


    private Szavazasok toNapiSzavazas(SzavazasEntity entity) {

        SzavazasEredmenyValasz eredmeny = getEredmeny(entity);

        Szavazasok dto = new Szavazasok();

        dto.setIdopont(Date.from(entity.getIdopont()));
        dto.setTargy(entity.getTargy());
        dto.setTipus(Szavazasok.Tipus.fromValue(entity.getTipus()));

        if (entity.getEljaras() != null) {
            dto.setEljaras(
                    Szavazasok.Eljaras.fromValue(entity.getEljaras())
            );
        }

        dto.setElnok(entity.getElnok());

        dto.setEredmeny(
                Szavazasok.Eredmeny.fromValue(
                        eredmeny.getEredmeny().value()
                )
        );

        dto.setKepviselokSzama(
                eredmeny.getKepviselokSzama()
        );

        dto.setSzavazatok(
                entity.getSzavazatok().stream()
                        .map(this::toNapiSzavazat)
                        .toList()
        );

        return dto;
    }

    private Szavazatok toNapiSzavazat(SzavazatEntity entity) {
        Szavazatok dto = new Szavazatok();
        dto.setKepviselo(entity.getKepviselo());
        dto.setSzavazat(entity.getSzavazat());
        return dto;
    }

    private SzavazasEredmenyValasz getEredmeny(
            SzavazasEntity szavazas) {

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
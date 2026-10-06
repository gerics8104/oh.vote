package hu.oh.vote.service.szavazas;

import hu.oh.vote.kimutatasok.KepviseloReszvetelAtlag;
import hu.oh.vote.kimutatasok.KulonlegesEljarasokSzamaValasz;
import hu.oh.vote.napi_szavazasok.NapiSzavazasokValasz;
import hu.oh.vote.szavazas.Szavazas;
import hu.oh.vote.szavazas.SzavazasValasz;
import hu.oh.vote.szavazas_eredmeny.SzavazasEredmenyValasz;
import hu.oh.vote.szavazat.SzavazatValasz;

import java.time.LocalDate;

public interface SzavazasService {

    SzavazasValasz szavazas(Szavazas dto);

    SzavazatValasz getSzavazat(String szavazasId, String kepviselo);

    SzavazasEredmenyValasz getEredmeny(String szavazasId);

    NapiSzavazasokValasz getNapiSzavazasok(LocalDate datum);

    KepviseloReszvetelAtlag getKepviseloReszvetelAtlag(
            LocalDate tol,
            LocalDate ig
    );

    KulonlegesEljarasokSzamaValasz getKulonlegesEljarasokSzama(
            LocalDate tol,
            LocalDate ig);
}

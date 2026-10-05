package hu.oh.vote.service.szavazas;

import hu.oh.vote.*;

import java.time.LocalDate;
import java.util.List;

public interface SzavazasService {

     SzavazasValasz szavazas(Szavazas dto);

     SzavazatValasz getSzavazat(String szavazas, String kepviselo);

     SzavazasEredmenyValasz getEredmeny(String szavazasAzonosito);

     NapiSzavazasokValasz getNapiSzavazasok(LocalDate datum);
}

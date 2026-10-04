package hu.oh.vote.service.szavazas;

import hu.oh.vote.Szavazas;
import hu.oh.vote.SzavazasValasz;
import hu.oh.vote.SzavazatValasz;

public interface SzavazasService {

     SzavazasValasz szavazas(Szavazas dto);

     SzavazatValasz getSzavazat(String szavazas, String kepviselo);
}

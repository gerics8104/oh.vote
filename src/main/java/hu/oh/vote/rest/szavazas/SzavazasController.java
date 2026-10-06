package hu.oh.vote.rest.szavazas;


import hu.oh.vote.kimutatasok.KepviseloReszvetelAtlag;
import hu.oh.vote.kimutatasok.KulonlegesEljarasokSzamaValasz;
import hu.oh.vote.napi_szavazasok.NapiSzavazasokValasz;
import hu.oh.vote.service.szavazas.SzavazasService;
import hu.oh.vote.szavazas.Szavazas;
import hu.oh.vote.szavazas.SzavazasValasz;
import hu.oh.vote.szavazas_eredmeny.SzavazasEredmenyValasz;
import hu.oh.vote.szavazat.SzavazatValasz;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/szavazasok")
public class SzavazasController {

    private final SzavazasService szavazasService;

    @Autowired
    public SzavazasController(SzavazasService szavazasService) {
        this.szavazasService = szavazasService;
    }

    @PostMapping("/szavazas")
    public ResponseEntity<SzavazasValasz> szavazas(
            @Valid @RequestBody Szavazas szavazas) {
        return ResponseEntity.ok(szavazasService.szavazas(szavazas));
    }

    @GetMapping("/szavazat")
    public ResponseEntity<SzavazatValasz> szavazat(
            @RequestParam("szavazasId") String szavazasId,
            @RequestParam("kepviselo") String kepviselo) {

        return ResponseEntity.ok(
                szavazasService.getSzavazat(szavazasId, kepviselo)
        );
    }

    @GetMapping("/eredmeny")
    public ResponseEntity<SzavazasEredmenyValasz> getEredmeny(
            @RequestParam("szavazasId") String szavazasId) {

        return ResponseEntity.ok(
                szavazasService.getEredmeny(szavazasId)
        );
    }

    @GetMapping("/napi-szavazasok")
    public ResponseEntity<NapiSzavazasokValasz> getNapiSzavazasok(
            @RequestParam("datum") LocalDate datum) {

        return ResponseEntity.ok(
                szavazasService.getNapiSzavazasok(datum)
        );
    }

    @GetMapping("/kepviselo-reszvetel-atlag")
    public ResponseEntity<KepviseloReszvetelAtlag> getKepviseloReszvetelAtlag(
            @RequestParam("idoszak-kezdete") LocalDate tol,
            @RequestParam("idoszak-vege") LocalDate ig) {

        return ResponseEntity.ok(
                szavazasService.getKepviseloReszvetelAtlag(tol, ig)
        );
    }

    @GetMapping("/kulonleges-eljarasok-szama")
    public ResponseEntity<KulonlegesEljarasokSzamaValasz> getKulonlegesEljarasokSzama(
            @RequestParam("idoszak-kezdete") LocalDate tol,
            @RequestParam("idoszak-vege") LocalDate ig) {

        return ResponseEntity.ok(
                szavazasService.getKulonlegesEljarasokSzama(tol, ig)
        );
    }

}

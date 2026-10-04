package hu.oh.vote.rest.szavazas;


import hu.oh.vote.Szavazas;
import hu.oh.vote.SzavazasValasz;
import hu.oh.vote.service.szavazas.SzavazasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

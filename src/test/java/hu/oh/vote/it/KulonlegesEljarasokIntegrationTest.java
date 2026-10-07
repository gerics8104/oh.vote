package hu.oh.vote.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({
        "/sql/CLEANUP.sql",
        "/sql/SZAVAZAS.sql",
        "/sql/SZAVAZAT.sql"
})
class KulonlegesEljarasokIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void kulonlegesEljarasokLekerdezese() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasok").isArray())
                .andExpect(jsonPath("$.szavazasok.length()").value(8));
    }

    @Test
    void surgosEljarasokSzamaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 's' && @.eredmeny == 'F')].szam")
                        .value(3))
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 's' && @.eredmeny == 'U')].szam")
                        .value(1));
    }

    @Test
    void kivetelesEljarasokSzamaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-02")
                        .param("idoszak-vege", "2026-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'k' && @.eredmeny == 'F')].szam")
                        .value(2));
    }

    @Test
    void elteroEljarasokSzamaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-03")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'e' && @.eredmeny == 'F')].szam")
                        .value(1))
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'e' && @.eredmeny == 'U')].szam")
                        .value(1));
    }

    @Test
    void osszesElfogadottSzavazasSzamaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'F')].szam")
                        .value(6));
    }

    @Test
    void osszesElutasitottSzavazasSzamaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'U')].szam")
                        .value(2));
    }

    @Test
    void osszesKulonlegesSzavazasSzamaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'összes')].szam")
                        .value(8));
    }

    @Test
    void normalEsJelenletiSzavazastNemSzamitjaBele() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'összes')].szam")
                        .value(4));
    }

    @Test
    void szavazasNelkulIdoszakNullasOsszesitestAd() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-04")
                        .param("idoszak-vege", "2026-10-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'F')].szam")
                        .value(0))
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'U')].szam")
                        .value(0))
                .andExpect(jsonPath(
                        "$.szavazasok[?(@.eljaras == 'összes' && @.eredmeny == 'összes')].szam")
                        .value(0));
    }

    @Test
    void hibasKezdoDatumHibatAd() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026.10.01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hibasVegDatumHibatAd() throws Exception {
        mockMvc.perform(get("/szavazasok/kulonleges-eljarasok-szama")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026.10.03"))
                .andExpect(status().isBadRequest());
    }
}
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
class KepviseloReszvetelAtlagIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void kepviseloReszvetelAtlagLekerdezese() throws Exception {
        mockMvc.perform(get("/szavazasok/kepviselo-reszvetel-atlag")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atlag").value(0.12));
    }

    @Test
    void jelenletiSzavazastNemSzamitjaBele() throws Exception {
        mockMvc.perform(get("/szavazasok/kepviselo-reszvetel-atlag")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atlag").value(0.06));
    }

    @Test
    void szavazasNelkulIdoszakNullaAtlagotAd() throws Exception {
        mockMvc.perform(get("/szavazasok/kepviselo-reszvetel-atlag")
                        .param("idoszak-kezdete", "2026-10-04")
                        .param("idoszak-vege", "2026-10-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atlag").value(0.0));
    }

    @Test
    void hibasKezdoDatumHibatAd() throws Exception {
        mockMvc.perform(get("/szavazasok/kepviselo-reszvetel-atlag")
                        .param("idoszak-kezdete", "2026.10.01")
                        .param("idoszak-vege", "2026-10-03"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hibasVegDatumHibatAd() throws Exception {
        mockMvc.perform(get("/szavazasok/kepviselo-reszvetel-atlag")
                        .param("idoszak-kezdete", "2026-10-01")
                        .param("idoszak-vege", "2026.10.03"))
                .andExpect(status().isBadRequest());
    }
}
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
class NapiSzavazasokIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void napiSzavazasokLekerdezese() throws Exception {
        mockMvc.perform(get("/szavazasok/napi-szavazasok")
                        .param("datum", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasok.length()").value(5));
    }

    @Test
    void csakAzAdottNapSzavazasaitAdjaVissza() throws Exception {
        mockMvc.perform(get("/szavazasok/napi-szavazasok")
                        .param("datum", "2026-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasok.length()").value(2))
                .andExpect(jsonPath("$.szavazasok[0].targy")
                        .value("Kivételes ügy 1"))
                .andExpect(jsonPath("$.szavazasok[1].targy")
                        .value("Kivételes ügy 2"));
    }

    @Test
    void napiSzavazasTartalmaHelyes() throws Exception {
        mockMvc.perform(get("/szavazasok/napi-szavazasok")
                        .param("datum", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasok[0].targy")
                        .value("Októberi nyitó jelenlét"))
                .andExpect(jsonPath("$.szavazasok[0].tipus").value("j"))
                .andExpect(jsonPath("$.szavazasok[0].eljaras").value("n"))
                .andExpect(jsonPath("$.szavazasok[0].elnok").value("Kepviselo1"))
                .andExpect(jsonPath("$.szavazasok[0].eredmeny").value("F"))
                .andExpect(jsonPath("$.szavazasok[0].kepviselokSzama").value(3))
                .andExpect(jsonPath("$.szavazasok[0].szavazatok.length()").value(3));
    }

    @Test
    void napiSzavazasSzavazataiHelyesek() throws Exception {
        mockMvc.perform(get("/szavazasok/napi-szavazasok")
                        .param("datum", "2026-10-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasok[0].szavazatok[0].kepviselo")
                        .value("Kepviselo1"))
                .andExpect(jsonPath("$.szavazasok[0].szavazatok[0].szavazat")
                        .value("i"))
                .andExpect(jsonPath("$.szavazasok[0].szavazatok[1].kepviselo")
                        .value("Kepviselo2"))
                .andExpect(jsonPath("$.szavazasok[0].szavazatok[1].szavazat")
                        .value("n"))
                .andExpect(jsonPath("$.szavazasok[0].szavazatok[2].kepviselo")
                        .value("Kepviselo3"))
                .andExpect(jsonPath("$.szavazasok[0].szavazatok[2].szavazat")
                        .value("t"));
    }

    @Test
    void szavazasNelkulNapUresListatAdVissza() throws Exception {
        mockMvc.perform(get("/szavazasok/napi-szavazasok")
                        .param("datum", "2026-10-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasok.length()").value(0));
    }

    @Test
    void hibasDatumFormatumHibatAd() throws Exception {
        mockMvc.perform(get("/szavazasok/napi-szavazasok")
                        .param("datum", "2026.10.01"))
                .andExpect(status().isBadRequest());
    }
}
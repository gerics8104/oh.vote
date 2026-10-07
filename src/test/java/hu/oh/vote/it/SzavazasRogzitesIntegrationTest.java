package hu.oh.vote.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/sql/CLEANUP.sql")
class SzavazasRogzitesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helyesSzavazasRogzitese() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    },
                                    {
                                      "kepviselo": "Kepviselo2",
                                      "szavazat": "n"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazasId").isString())
                .andExpect(jsonPath("$.szavazasId")
                        .value(matchesPattern("[A-Z]{2}[0-9]{4}")));
    }

    @Test
    void elnokNemSzavazott() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo2",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void kepviseloCsakEgyszerSzavazhat() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    },
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "n"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void azonosIdopontraNemRogzithetoKetSzavazas() throws Exception {

        String elsoSzavazas = """
                {
                  "idopont": "2026-10-05T10:00:00Z",
                  "targy": "Első szavazás",
                  "tipus": "e",
                  "eljaras": "n",
                  "elnok": "Kepviselo1",
                  "szavazatok": [
                    {
                      "kepviselo": "Kepviselo1",
                      "szavazat": "i"
                    }
                  ]
                }
                """;

        String masodikSzavazas = """
                {
                  "idopont": "2026-10-05T10:00:00Z",
                  "targy": "Második szavazás",
                  "tipus": "e",
                  "eljaras": "n",
                  "elnok": "Kepviselo1",
                  "szavazatok": [
                    {
                      "kepviselo": "Kepviselo1",
                      "szavazat": "i"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(elsoSzavazas))
                .andExpect(status().isOk());

        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(masodikSzavazas))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoIdopontHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoTargyHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoTipusHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoElnokHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoSzavazatokHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ervenytelenTipusHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "x",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ervenytelenEljarasHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "x",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoKepviseloHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hianyzoSzavazatHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ervenytelenSzavazatHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "2026-10-05T10:00:00Z",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "x"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void hibasIdopontFormatumHibatAd() throws Exception {
        mockMvc.perform(post("/szavazasok/szavazas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idopont": "nem-egy-datum",
                                  "targy": "Új szavazás",
                                  "tipus": "e",
                                  "eljaras": "n",
                                  "elnok": "Kepviselo1",
                                  "szavazatok": [
                                    {
                                      "kepviselo": "Kepviselo1",
                                      "szavazat": "i"
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
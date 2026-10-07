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
class EredmenyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void jelenletiSzavazasEredmenyeElfogadott() throws Exception {
        mockMvc.perform(get("/szavazasok/eredmeny")
                        .param("szavazasId", "BN8961"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eredmeny").value("F"))
                .andExpect(jsonPath("$.kepviselokSzama").value(3));
    }

    @Test
    void egyszeruTobbsegEsetenElfogadott() throws Exception {
        mockMvc.perform(get("/szavazasok/eredmeny")
                        .param("szavazasId", "QO7081"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eredmeny").value("F"))
                .andExpect(jsonPath("$.kepviselokSzama").value(3));
    }

    @Test
    void egyszeruTobbsegEsetenElutasitott() throws Exception {
        mockMvc.perform(get("/szavazasok/eredmeny")
                        .param("szavazasId", "XQ7312"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eredmeny").value("U"))
                .andExpect(jsonPath("$.kepviselokSzama").value(3));
    }

    @Test
    void nemLetezoSzavazasEredmenyeHibatAd() throws Exception {
        mockMvc.perform(get("/szavazasok/eredmeny")
                        .param("szavazasId", "ZZ9999"))
                .andExpect(status().isNotFound());
    }
}
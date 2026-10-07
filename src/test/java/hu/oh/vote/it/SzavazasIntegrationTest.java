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
public class SzavazasIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    void kepviseloSzavazatanakLekerdezese() throws Exception {
        mockMvc.perform(get("/szavazasok/szavazat")
                        .param("szavazasId", "RK8763")
                        .param("kepviselo", "Kepviselo1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.szavazat").value("i"));
    }

    @Test
    void nemLetezoSzavazasSzavazatanakLekerdezese() throws Exception {
        mockMvc.perform(get("/szavazasok/szavazat")
                        .param("szavazasId", "ZZ9999")
                        .param("kepviselo", "Kepviselo1"))
                .andExpect(status().isNotFound());
    }
}

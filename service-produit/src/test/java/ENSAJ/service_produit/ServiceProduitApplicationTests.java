package ENSAJ.service_produit;

import ENSAJ.service_produit.dto.ProduitRequest;
import ENSAJ.service_produit.model.Produit;
import ENSAJ.service_produit.repository.ProduitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
@AutoConfigureMockMvc
@Testcontainers
class ServiceProduitApplicationTests {
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0.11");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ProduitRepository repository;

    @BeforeEach void clean() { repository.deleteAll(); }

    @Test void shouldCreateProduit() throws Exception {
        var request = ProduitRequest.builder().libelle("TV").description("Télévision").prix(5000d).build();
        mockMvc.perform(post("/api/produit").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        assertEquals(1, repository.findAll().size());
    }

    @Test void shouldGetAllProduit() throws Exception {
        repository.save(Produit.builder().libelle("TV").description("Télévision").prix(5000).build());
        repository.save(Produit.builder().libelle("Iphone 15").description("Téléphone Apple").prix(15000).build());
        mockMvc.perform(get("/api/produit")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test void shouldUpdateProduit() throws Exception {
        var saved = repository.save(Produit.builder().libelle("Ancien").description("D").prix(1).build());
        var request = ProduitRequest.builder().libelle("Nouveau").description("D2").prix(2d).build();
        mockMvc.perform(put("/api/produit/{id}", saved.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.libelle").value("Nouveau"));
    }

    @Test void shouldDeleteProduit() throws Exception {
        var saved = repository.save(Produit.builder().libelle("TV").description("D").prix(1).build());
        mockMvc.perform(delete("/api/produit/{id}", saved.getId())).andExpect(status().isNoContent());
        assertEquals(0, repository.count());
    }
}

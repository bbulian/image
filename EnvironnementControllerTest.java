package fr.ca.cats.p0498.s0764.compas.controller.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import fr.ca.cats.p0498.s0764.compas.controller.dto.DeploiementsProduitSolutionEnvironnement;
import fr.ca.cats.p0498.s0764.compas.controller.dto.Environnement;
import fr.ca.cats.p0498.s0764.compas.exception.CompasException;
import fr.ca.cats.p0498.s0764.compas.service.EnvironnementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class EnvironnementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnvironnementService environnementService;

    @InjectMocks
    private EnvironnementController environnementController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(environnementController).build();
    }

    @Test
    void testGetKubernetesEnvironments_OK() throws Exception {
        Environnement env1 = mock(Environnement.class);
        Environnement env2 = mock(Environnement.class);

        when(environnementService.getKubernetesEnvironnements()).thenReturn(List.of(env1, env2));

        mockMvc.perform(MockMvcRequestBuilders.get("/environnement/kubernetes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(environnementService, times(1)).getKubernetesEnvironnements();
    }

    @Test
    void testGetKubernetesRunningApps_OK() throws Exception {
        String env = "test-env";
        DeploiementsProduitSolutionEnvironnement deployment1 = mock(DeploiementsProduitSolutionEnvironnement.class);
        DeploiementsProduitSolutionEnvironnement deployment2 = mock(DeploiementsProduitSolutionEnvironnement.class);

        when(environnementService.isExists(env)).thenReturn(true);
        when(environnementService.getDeploymentsByEnvType(env, K8S)).thenReturn(List.of(deployment1, deployment2));

        mockMvc.perform(MockMvcRequestBuilders.get("/environnement/kubernetes/{env}", env)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(environnementService, times(1)).isExists(env);
        verify(environnementService, times(1)).getDeploymentsByEnvType(env, K8S);
    }

    @Test
    void testGetKubernetesRunningApps_EnvironmentNotFound() throws Exception {
        String env = "non-existent-env";

        when(environnementService.isExists(env)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/environnement/kubernetes/{env}", env)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(r -> assertInstanceOf(CompasException.class, r.getResolvedException()))
                .andExpect(r -> assertEquals("L'environnement demandé est introuvable", 
                        r.getResolvedException().getMessage()));

        verify(environnementService, times(1)).isExists(env);
        verify(environnementService, times(0)).getDeploymentsByEnvType(env, K8S);
    }

    @Test
    void testGetKubernetesRunningApps_ServerError() throws Exception {
        String env = "test-env";

        when(environnementService.isExists(env)).thenReturn(true);
        when(environnementService.getDeploymentsByEnvType(env, K8S)).thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(MockMvcRequestBuilders.get("/environnement/kubernetes/{env}", env)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(environnementService, times(1)).isExists(env);
        verify(environnementService, times(1)).getDeploymentsByEnvType(env, K8S);
    }
}

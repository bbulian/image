package fr.ca.cats.p0498.s0764.compas.controller.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import fr.ca.cats.p0498.s0764.compas.controller.dto.Artifact;
import fr.ca.cats.p0498.s0764.compas.exception.CompasException;
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

import fr.ca.cats.p0498.s0764.compas.service.ArtifactDependenciesService;

@ExtendWith(MockitoExtension.class)
class ArtifactDependenciesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ArtifactDependenciesService artifactDependenciesService;

    @InjectMocks
    private ArtifactDependenciesController artifactDependenciesController;

    private String checksumArtefact;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(artifactDependenciesController).build();
        checksumArtefact = "test-checksum";
    }
    
    @Test
    void testGetArtifactDependencies_OK() throws Exception {
        Artifact artifact = mock(Artifact.class);

        when(artifactDependenciesService.getArtifact(checksumArtefact)).thenReturn(artifact);

        when(artifactDependenciesService.getArtifactDependencies(checksumArtefact)).thenReturn(List.of(artifact));

        mockMvc.perform(MockMvcRequestBuilders.get("/artefact/{checksum}/dependances", checksumArtefact)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(artifactDependenciesService, times(1)).getArtifact(checksumArtefact);
        verify(artifactDependenciesService, times(1)).getArtifactDependencies(checksumArtefact);
    }

    @Test
    void testGetArtifactDependencies_Fail() throws Exception {
        Artifact artifact = mock(Artifact.class);

        when(artifactDependenciesService.getArtifact(checksumArtefact)).thenReturn(null);

        when(artifactDependenciesService.getArtifactDependencies(checksumArtefact)).thenReturn(List.of(artifact));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/artefact/{checksum}/dependances", checksumArtefact)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(r -> assertInstanceOf(CompasException.class, r.getResolvedException()))
                .andExpect(r -> assertEquals("L'artefact demandé est introuvable", ((CompasException)r).getMessage()));

        verify(artifactDependenciesService, times(1)).getArtifact(checksumArtefact);
        verify(artifactDependenciesService, times(0)).getArtifactDependencies(checksumArtefact);
    }
}

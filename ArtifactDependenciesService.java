package fr.ca.cats.p0498.s0764.compas.controller.resources;

import java.util.List;

import fr.ca.cats.p0498.s0764.compas.controller.dto.Artifact;
import fr.ca.cats.p0498.s0764.compas.exception.CompasException;
import static fr.ca.cats.p0498.s0764.compas.exception.CompasException.artifactNotFound;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ca.cats.p0498.s0764.compas.service.ArtifactDependenciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/artefact")
@Tag(name = "DEPENDANCES ARTIFACT")
@RequiredArgsConstructor
public class ArtifactDependenciesController {

	private final ArtifactDependenciesService artifactDependencies;

	@Operation(description = "Récupère les dépendances d'un artefact en fonction de son checksum")

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération en succès", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Artifact[].class)) }),

			@ApiResponse(responseCode = "404", description = "La ressource n'existe pas", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "{\"code\": 404,\"message\": \"message\"}")) }),

			@ApiResponse(responseCode = "404", description = "L'artefact demandé est introuvable", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "{\"code\": 404,\"message\": \"message\"}")) }),

			@ApiResponse(responseCode = "500", description = "Une erreur est survenue au niveau du serveur", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "{\"code\": 500,\"message\": \"message\"}")) }) })
	@GetMapping("/{checksumArtefact}/dependances")
	public ResponseEntity<List<Artifact>> getArtifactDependencies(@PathVariable String checksumArtefact) throws CompasException {
		// Check if artefact exists
		if (artifactDependencies.getArtifact(checksumArtefact) == null) {
			throw artifactNotFound();
		}

		return ResponseEntity.ok(artifactDependencies.getArtifactDependencies(checksumArtefact));
	}

}

package fr.ca.cats.p0498.s0764.compas.controller.resources;

import fr.ca.cats.p0498.s0764.compas.controller.dto.DeploiementsProduitSolutionEnvironnement;
import fr.ca.cats.p0498.s0764.compas.controller.dto.Environnement;
import fr.ca.cats.p0498.s0764.compas.exception.CompasException;
import fr.ca.cats.p0498.s0764.compas.service.EnvironnementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.ca.cats.p0498.s0764.compas.exception.CompasException.environementNotFound;
import static fr.ca.cats.p0498.s0764.compas.repositories.domain.RunningApp.InstalledFrom.K8S;

@RestController
@RequestMapping(value = "/environnement")
@Tag(name = "ENVIRONEMENTS")
@RequiredArgsConstructor
public class EnvironnementController {

	private final EnvironnementService environnementService;

	@Operation(description = "Liste les environnements Kubernetes avec le nombre de d'applications actuellement déployées")

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération en succès", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Environnement[].class)) }),

			@ApiResponse(responseCode = "500", description = "Une erreur est survenue au niveau du serveur", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "{\"code\": 500,\"message\": \"message\"}")) }) })
	@GetMapping("/kubernetes")
	public ResponseEntity<List<Environnement>> getKubenetesEnvironments() {
		return ResponseEntity.ok(environnementService.getKubernetesEnvironnements());
	}

	@Operation(description = "Liste les 'applications actuellement déployées dans un environnement")

	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Opération en succès", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DeploiementsProduitSolutionEnvironnement[].class)) }),

			@ApiResponse(responseCode = "404", description = "L'environnement demandé est introuvable", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "{\"code\": 404,\"message\": \"message\"}")) }),

			@ApiResponse(responseCode = "500", description = "Une erreur est survenue au niveau du serveur", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(example = "{\"code\": 500,\"message\": \"message\"}")) }) })
	@GetMapping("/kubernetes/{env}")
	public ResponseEntity<List<DeploiementsProduitSolutionEnvironnement>> getKubenetesRunningApps(@PathVariable String env) throws CompasException {
		if (!environnementService.isExists(env)) {
			throw environementNotFound();
		}

		return ResponseEntity.ok(environnementService.getDeploymentsByEnvType(env, K8S));
	}
}

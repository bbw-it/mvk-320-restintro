package ch.bbw.m320.restintro;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Diese Klasse soll nicht modifiziert werden!
 * Dieser Test simuliert die Client-Seite mittels WebTestClient
 * (<a href="https://spring.getdocs.org/en-US/spring-framework-docs/docs/testing/integration-testing/webtestclient.html">Beispiele</a>).
 * Implementiere die Server-Seite (ein RestController), sodass alle Tests grün werden.
 */
@WebFluxTest
@ExtendWith(SpringExtension.class)
class RestintroApplicationTest implements WithAssertions {

	private static final MovieDto AVENGERS = new MovieDto(0, "Avengers", 2012);

	@Autowired
	private WebTestClient webClient;

	/**
	 * Einfache Ping-Pong Methode, nutze ein @GetMapping und gib einen einfachen String zurück
	 * https://www.baeldung.com/spring-controller-vs-restcontroller#spring-mvc-rest-controller
	 */
	@Test
	void pingPong() {
		// Auch ohne die vollständige Syntax von webClient zu verstehen können wir hier folgendes herauslesen:
		// - die HTTP-Methode ist ein GET (https://de.wikipedia.org/wiki/Hypertext_Transfer_Protocol#HTTP_GET)
		// - der Pfad ist /api/ping
		// - Die HTTP-Response hat z.B. ein Status-Code 200 OK
		// - Im Body der HTTP-Response steht der einfache String "pong"
		webClient.get()
				.uri("/api/ping")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody(String.class)
				.isEqualTo("pong");
	}

	/**
	 * Nun geben wir ein (hardcodetes) {@link MovieDto} zurück und nutzen {@link PathVariable}.
	 */
	@Test
	void getMovieById() {
		// for a start, just hardcode the returned movie
		webClient.get()
				.uri("/api/movies/{id}", 0)
				.exchange()
				.expectBody(MovieDto.class)
				.isEqualTo(AVENGERS);
	}

	/**
	 * Das funktioniert auch mit Listen.
	 */
	@Test
	void getMoviesReturnsAList() {
		// for a start, simply return a list with 1 Movie element in it (hardcoded)
		webClient.get()
				.uri("/api/movies")
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBodyList(MovieDto.class)
				.contains(AVENGERS);
	}

	/**
	 * Nun speichern wir mit einem POST und holen uns denselben Record wieder mit einem GET.
	 * Beachte, dass diese POST Methode gleich wieder ein {@link MovieDto} zurück gibt mit gesetzter ID.
	 * https://www.appsdeveloperblog.com/postmapping-requestbody-spring-mvc/
	 */
	@Test
	void saveAndGet() {
		// we don't specify an ID. The server side wil set one for us.
		var toMovieToCreate = new MovieDto(null, "The Dark Knight", 2008);
		var createdMovie = webClient.post()  // POST to create a new movie
				.uri("/api/movies")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(toMovieToCreate)
				.exchange()
				.expectBody(MovieDto.class)
				.returnResult()
				.getResponseBody();
		assertThat(createdMovie.title()).isEqualTo(toMovieToCreate.title());
		assertThat(createdMovie.id()).as("expected the server to assign a new ID")
				.isPositive()
				.isNotEqualTo(AVENGERS.id());
		// now we read it again
		webClient.get()
				.uri("/api/movies/{id}", createdMovie.id())
				.exchange()
				.expectBody(MovieDto.class)
				.isEqualTo(createdMovie);
	}

	/**
	 * Nun starten wir mit eigenem Fehlerhandling. Erstelle dazu eine neue `UnknownMovieException`:
	 * https://www.baeldung.com/exception-handling-for-rest-with-spring#3-responsestatusexceptionresolver
	 * (beachte `@ResponseStatus`)
	 */
	@Test
	void getMovieByUnknownId() {
		webClient.get()
				.uri("/api/movies/{id}", 1337) // that ID is unknown
				.exchange()
				.expectStatus()
				.isEqualTo(HttpStatus.BAD_REQUEST); // we expect an HTTP/400 here (not 404)
	}

	/**
	 * Falls wir die Response weiter customizen wollen, können wir stattdesse eine
	 * {@link org.springframework.http.ResponseEntity} zurück geben.
	 * https://www.baeldung.com/spring-response-entity
	 */
	@Test
	void customFuuMethod() {
		webClient.patch()  // PATCH method
				.uri("/api/fuu")
				.exchange()
				.expectStatus()
				.isEqualTo(212) // custom HTTP/212
				.expectHeader()
				.valueEquals("X-Men", "Wolverine") // custom Header
				.expectBody(String.class)
				.isEqualTo("bar");
	}

	/**
	 * Eine DELETE-Methode gibt im Normalfall kein Body zurück, wir wollen in diesm Fall ein HTTP/204.
	 * → `ResponseEntity<Void>`
	 */
	@Test
	void createAndDelete() {
		var createdMovie = webClient.post()  // POST to create a new movie
				.uri("/api/movies")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(new MovieDto(null, "Whatever", 1997))
				.exchange()
				.expectBody(MovieDto.class)
				.returnResult()
				.getResponseBody();
		webClient.delete()
				.uri("/api/movies/" + createdMovie.id())
				.exchange()
				.expectStatus()
				.isNoContent();  // is an HTTP/204
	}

	@Test
	void deleteUnknown() {
		webClient.delete()
				.uri("/api/movies/1337")  // doesn't exist
				.exchange()
				.expectStatus()
				.is4xxClientError();
	}
}

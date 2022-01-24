package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static de.ksbrwsk.people.Constants.API;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@Import({PersonHandler.class, PersonRouter.class})
class PersonRouterTest {
    @Autowired
    WebTestClient webTestClient;

    @MockBean
    PersonRepository personRepository;

    @Test
    void handleNotFound() {
        this.webTestClient
                .get()
                .uri("/api/peple")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void handleFindAll() {
        when(this.personRepository.findAll())
                .thenReturn(Flux.just(
                        new Person("4711", "Name1"),
                        new Person("4712", "Name2")
                ));
        this.webTestClient
                .get()
                .uri(API)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].name")
                .isEqualTo("Name1")
                .jsonPath("$[1].name")
                .isEqualTo("Name2");
    }

    @Test
    void handleFindById() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.just(
                        new Person("4711", "Name")
                ));
        this.webTestClient
                .get()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", "Name"));
    }

    @Test
    void handleFindByIdNotFound() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.empty());
        this.webTestClient
                .get()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void handleDeleteById() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.just(
                        new Person("4711", "Name")
                ));
        when(this.personRepository.delete(any(Person.class)))
                .thenReturn(Mono.empty());
        this.webTestClient
                .delete()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .isEqualTo("successfully deleted!");
    }

    @Test
    void handleDeleteByIdNotFound() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.empty());
        this.webTestClient
                .delete()
                .uri(API + "/4711")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void handleCreate() {
        when(this.personRepository.save(new Person("Name")))
                .thenReturn(Mono.just(new Person("4711", "Name")));
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person("Name"))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", "Name"));
    }

    @Test
    void handleCreateBadRequest() {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(Optional.empty())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void handleUpdate() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.just(
                        new Person("4711", "Old")
                ));
        when(this.personRepository.save(new Person("4711", "Update")))
                .thenReturn(Mono.just(
                        new Person("4711", "Name")
                ));
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", "Update"))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", "Name"));
    }

    @Test
    void handleUpdateNotFound() {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.empty());
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", "Update"))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleCreateValid(String name) {
        when(this.personRepository.save(new Person(name)))
                .thenReturn(Mono.just(
                        new Person("4711", name)
                ));
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .exists("Location")
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", name));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"00123456789"})
    void handleCreateInvalid(String name) {
        this.webTestClient
                .post()
                .uri(API)
                .bodyValue(new Person(name))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "Name", "0123456789"})
    void handleUpdateValid(String name) {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.just(
                        new Person("4711", "Old")
                ));
        when(this.personRepository.save(new Person("4711", name)))
                .thenReturn(Mono.just(new Person("4711", name)));
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", name))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .isEqualTo(new Person("4711", name));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"00123456789"})
    void handleUpdateInvalid(String name) {
        when(this.personRepository.findById("4711"))
                .thenReturn(Mono.just(
                        new Person("4711", "Old")
                ));
        this.webTestClient
                .put()
                .uri(API + "/4711")
                .bodyValue(new Person("4711", name))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
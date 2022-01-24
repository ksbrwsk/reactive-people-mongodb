package de.ksbrwsk.people;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static de.ksbrwsk.people.Constants.API;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonHandler {
    private final PersonRepository personRepository;
    private final Validator validator;

    public Mono<ServerResponse> handleFindAll(ServerRequest serverRequest) {
        return ok()
                .body(this.personRepository.findAll(), Person.class);
    }

    public Mono<ServerResponse> handleFindById(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        return this.personRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "person not found")))
                .flatMap(person -> ok()
                        .bodyValue(person));
    }

    public Mono<ServerResponse> handleDeleteById(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        return this.personRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "person not found")))
                .flatMap(this.personRepository::delete)
                .thenReturn(Mono.just("successfully deleted!"))
                .flatMap(msg -> ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(msg, String.class));
    }

    public Mono<ServerResponse> handleCreate(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Person.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "person must not be null")))
                .doOnNext(this::validate)
                .flatMap(this.personRepository::save)
                .flatMap(person -> created(URI.create(API + "/" + person.getId()))
                        .bodyValue(person));
    }

    public Mono<ServerResponse> handleUpdate(ServerRequest serverRequest) {
        var id = serverRequest.pathVariable("id");
        Mono<Person> update = serverRequest.bodyToMono(Person.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "person must not be null")))
                .doOnNext(this::validate);
        return this.personRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "person not found")))
                .flatMap(old -> ok()
                        .body(
                                fromPublisher(
                                        update.map(p -> new Person(id, p.getName()))
                                                .flatMap(this.personRepository::save),
                                        Person.class
                                )
                        )
                );
    }

    private void validate(Person person) {
        Set<ConstraintViolation<Person>> violations = this.validator.validate(person);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(this::formatError)
                    .toList();
            log.info("person not valid -> {}", errors.toString());
            throw new ServerWebInputException(errors.toString());
        }
    }

    private String formatError(ConstraintViolation<Person> personConstraintViolation) {
        String field = StringUtils.capitalize(personConstraintViolation.getPropertyPath().toString());
        String error = personConstraintViolation.getMessage();
        return String.format("%s - %s", field, error);
    }
}

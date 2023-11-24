package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@DataMongoTest
@Testcontainers
class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.3");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @Test
    void persist() {
        Mono<Long> longMono = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person("Name1")))
                .then(this.personRepository.save(new Person("Name2")))
                .then(this.personRepository.count());
        StepVerifier
                .create(longMono)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<Person> personMono = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person("Name")))
                .flatMap(person -> this.personRepository.findById(person.getId()));
        StepVerifier
                .create(personMono)
                .expectNextMatches(person -> person.getId() != null &&
                        person.getName().equalsIgnoreCase("name"))
                .verifyComplete();
    }

    @Test
    void delete() {
        Mono<Long> longMono = this.personRepository
                .deleteAll()
                .then(this.personRepository.save(new Person("Name")))
                .flatMap(this.personRepository::delete)
                .then(this.personRepository.count());
        StepVerifier
                .create(longMono)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void testdata() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            people.add(new Person("person@" + i));
        }
        Flux<Person> personFlux = this.personRepository
                .deleteAll()
                .thenMany(this.personRepository.saveAll(people));
        StepVerifier
                .create(personFlux)
                .expectNextCount(100L)
                .verifyComplete();
    }
}
package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@DataMongoTest
public class GenerateTestdataTest {
//
//    @Autowired
//    PersonRepository personRepository;
//
//    @Test
//    void testdata() {
//        List<Person> people = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            people.add(new Person("person@" + i));
//        }
//        Flux<Person> personFlux = this.personRepository
//                .deleteAll()
//                .thenMany(this.personRepository.saveAll(people));
//        StepVerifier
//                .create(personFlux)
//                .expectNextCount(100L)
//                .verifyComplete();
//    }
}

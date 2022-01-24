package de.ksbrwsk.people;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonTest {

    @Test
    void create() {
        Person person = new Person("4711", "name");
        assertEquals("4711", person.getId());
        assertEquals("name", person.getName());
    }
}
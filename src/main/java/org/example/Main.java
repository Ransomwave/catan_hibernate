package org.example;

import org.hibernate.cfg.Configuration;

public class Main {
    static void main() {
        try (var sessionFactory = new Configuration().configure().buildSessionFactory()) {
            IO.println("Base de dades creada");
        }
    }
}

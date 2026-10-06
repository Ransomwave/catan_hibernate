package org.example.classes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ciutat")
public class Ciutat extends Assentament{

}

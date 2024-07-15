package it.unipi.healthhub.dao.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node
public class User {
    @Id
    private Long id;
    private String name;

    @Relationship(type = "ENDORSED")
    private Set<Doctor> endorsedDoctors = new HashSet<>();

    @Relationship(type = "REVIEWED")
    private Set<Doctor> reviewedDoctors = new HashSet<>();

    public User() {
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

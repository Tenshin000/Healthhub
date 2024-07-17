package it.unipi.healthhub.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
public class UserDAO {
    @Id
    private String id;
    private String name;

    @Relationship(type = "ENDORSED")
    private Set<DoctorDAO> endorsedDoctors = new HashSet<>();

    @Relationship(type = "REVIEWED")
    private Set<DoctorDAO> reviewedDoctors = new HashSet<>();

    public UserDAO() {
    }

    public UserDAO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DoctorDAO> getEndorsedDoctors() {
        return endorsedDoctors;
    }

    public Set<DoctorDAO> getReviewedDoctors() {
        return reviewedDoctors;
    }
}

package it.unipi.healthhub.dao.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Node
public class Doctor {
    @Id
    private Long id;

    private String name;
    private List<String> specializations;

    public Doctor() {
    }

    public Doctor(Long id, String name, List<String> specializations) {
        this.id = id;
        this.name = name;
        this.specializations = specializations;
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


    public List<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }
}

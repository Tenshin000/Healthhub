package it.unipi.healthhub.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.ArrayList;
import java.util.List;

@Node("Doctor")
public class DoctorDAO {
    @Id
    private String id;

    private String name;
    private List<String> specializations;

    public DoctorDAO() {
        specializations = new ArrayList<>();
    }

    public DoctorDAO(String id, String name, List<String> specializations) {
        this.id = id;
        this.name = name;
        this.specializations = specializations;
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

    public List<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public String toString() {
        return "DoctorDAO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", specializations=" + specializations +
                '}';
    }
}

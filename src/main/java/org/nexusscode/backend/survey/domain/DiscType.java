package org.nexusscode.backend.survey.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "disc_types")
public class DiscType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "disc_type_keywords", joinColumns = @JoinColumn(name = "disc_type_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Builder
    public DiscType(String name, String description, List<String> keywords) {
        this.name = name;
        this.description = description;
        this.keywords = keywords;
    }
}

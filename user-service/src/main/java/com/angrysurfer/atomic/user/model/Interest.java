package com.angrysurfer.atomic.user.model;




import jakarta.persistence.*;
import java.io.Serializable;

@Entity()
//@Table(schema = "social")
public class Interest implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1896715641419245592L;

    @Id
    @SequenceGenerator(name = "interest_sequence", sequenceName = "interest_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interest_sequence")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    private String name;

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

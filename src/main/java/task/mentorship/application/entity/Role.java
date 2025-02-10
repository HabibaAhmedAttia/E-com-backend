package task.mentorship.application.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName name;

    public String getName() {
        return name.name();
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public enum RoleName {
        CLIENT, ADMIN
    }
}


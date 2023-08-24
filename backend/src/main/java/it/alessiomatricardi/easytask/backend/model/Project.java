package it.alessiomatricardi.easytask.backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity(name = "projects")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    // relation one-to-many with tasks
    // a project can have many tasks

    @OneToMany(mappedBy = "project")
    @JsonManagedReference
    private List<Task> tasks;

    // relation many-to-one with a project manager
    // a project has a project manager

    @ManyToOne
    @JoinColumn(name = "fk_projectmanager")
    @JsonBackReference
    private Employee projectManager;

    // relation many-to-many with employes
    // a project has many members

    @ManyToMany
    @JoinTable(name = "projects_employees", joinColumns = { @JoinColumn(name = "fk_project") }, inverseJoinColumns = {
            @JoinColumn(name = "fk_employee") })
    // @JsonIgnore
    private Set<Employee> members;

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setProject(this);
    }

    public void addMember(Employee employee) {
        this.members.add(employee);
        employee.getWorkingProjects().add(this);
    }

    public void removeMember(Employee employee) {
        this.members.remove(employee);
        employee.getWorkingProjects().remove(this);
    }

    @Override
    public int hashCode() {
        return 55;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        return id != null && id.equals(other.getId());
    }

}
package it.alessiomatricardi.easytask.backend.model;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "delivery_date", nullable = false)
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // relation many-to-one with project

    @ManyToOne
    @JoinColumn(name = "fk_project")
    @JsonBackReference
    private Project project;

    // relation many-to-many with employees
    @ManyToMany
    @JoinTable(name = "tasks_employees", joinColumns = { @JoinColumn(name = "fk_task") }, inverseJoinColumns = {
            @JoinColumn(name = "fk_employee") })
    @JsonIgnore
    private Set<Employee> assignedMembers;

    // relation many-to-many with employees
    // a task can be commented by more employees
    @OneToMany(mappedBy = "task")
    @JsonManagedReference
    private Set<Comment> comments;

    public void addMember(Employee employee) {
        this.assignedMembers.add(employee);
        employee.getAssignedTasks().add(this);
    }

    public void removeMember(Employee employee) {
        this.assignedMembers.remove(employee);
        employee.getAssignedTasks().remove(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
    }

    @Override
    public int hashCode() {
        return 69;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Task other = (Task) obj;
        return id != null && id.equals(other.getId());
    }

}

package it.alessiomatricardi.easytask.backend.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "employees")
@Getter
@Setter
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private Boolean isActive = true;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private EmployeeRole role;

    // relation one-to-many with owned projects
    // a project manager can manage many projects

    @OneToMany(mappedBy = "projectManager")
    @JsonManagedReference
    private List<Project> managingProjects;

    // relation many-to-many with projects
    // a member can work in many projects

    @ManyToMany(mappedBy = "members")
    // @JsonIgnore
    private Set<Project> workingProjects;

    // relation many-to-many with tasks
    // a member can be assigned to many tasks

    @ManyToMany(mappedBy = "assignedMembers")
    private Set<Task> assignedTasks;

    // relation many-to-many with tasks
    // a member can comment many tasks
    @OneToMany(mappedBy = "employee")
    @JsonManagedReference
    private Set<Comment> comments;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getIsActive();
    }

    public void addManagingProject(Project project) {
        this.managingProjects.add(project);
        project.setProjectManager(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setEmployee(this);
    }

    @Override
    public int hashCode() {
        return 17;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Employee other = (Employee) obj;
        return id != null && id.equals(other.getId());
    }

}

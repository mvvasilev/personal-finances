package dev.mvvasilev.common.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity implements DatabaseStorable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigserial")
    private Long id;

    @Column(name = "time_created", insertable = false, updatable = false)
    private LocalDateTime timeCreated;

    @Column(name = "time_last_modified", insertable = false)
    private LocalDateTime timeLastModified;

    protected AbstractEntity() {}

    @Override
    public long getId() {
        return id;
    }

    @Override
    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    @Override
    public LocalDateTime getTimeLastModified() {
        return timeLastModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public void setTimeLastModified(LocalDateTime timeLastModified) {
        this.timeLastModified = timeLastModified;
    }

    @PreUpdate
    protected void onUpdate() {
        this.timeLastModified = LocalDateTime.now();
    }
}

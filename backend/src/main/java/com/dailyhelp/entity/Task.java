package com.dailyhelp.entity;

import jakarta.persistence.*;
import java.time.*;

@Entity @Table(name="tasks", indexes={@Index(name="idx_tasks_owner_status",columnList="owner_id,status"),@Index(name="idx_tasks_owner_due",columnList="owner_id,due_date")})
public class Task {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="owner_id",nullable=false) private AppUser owner;
 @Column(nullable=false,length=160) private String title;
 @Column(length=2000) private String description;
 @Column(name="due_date") private LocalDate dueDate;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TaskPriority priority=TaskPriority.MEDIUM;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TaskStatus status=TaskStatus.PENDING;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 protected Task() {}
 public Task(AppUser owner,String title,String description,LocalDate dueDate,TaskPriority priority,TaskStatus status){this.owner=owner;this.title=title;this.description=description;this.dueDate=dueDate;this.priority=priority==null?TaskPriority.MEDIUM:priority;this.status=status==null?TaskStatus.PENDING:status;}
 @PrePersist void onCreate(){createdAt=Instant.now();updatedAt=createdAt;} @PreUpdate void onUpdate(){updatedAt=Instant.now();}
 public void update(String title,String description,LocalDate dueDate,TaskPriority priority,TaskStatus status){this.title=title;this.description=description;this.dueDate=dueDate;this.priority=priority;this.status=status;}
 public void setStatus(TaskStatus status){this.status=status;}
 public Long getId(){return id;} public AppUser getOwner(){return owner;} public String getTitle(){return title;} public String getDescription(){return description;} public LocalDate getDueDate(){return dueDate;} public TaskPriority getPriority(){return priority;} public TaskStatus getStatus(){return status;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
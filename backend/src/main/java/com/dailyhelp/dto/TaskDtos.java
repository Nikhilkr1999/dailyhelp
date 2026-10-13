package com.dailyhelp.dto;
import com.dailyhelp.entity.*; import jakarta.validation.constraints.*; import java.time.*;
public final class TaskDtos {private TaskDtos(){}
 public record TaskRequest(@NotBlank @Size(max=160) String title,@Size(max=2000) String description,LocalDate dueDate,@NotNull TaskPriority priority,@NotNull TaskStatus status){}
 public record StatusRequest(@NotNull TaskStatus status){}
 public record TaskResponse(Long id,String title,String description,LocalDate dueDate,TaskPriority priority,TaskStatus status,Instant createdAt,Instant updatedAt){}
 public record DashboardResponse(long total,long pending,long inProgress,long completed,long overdue,double completionPercentage,java.util.List<TaskResponse> recentTasks,java.util.List<TaskResponse> upcomingTasks){}
}
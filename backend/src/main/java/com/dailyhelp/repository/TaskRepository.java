package com.dailyhelp.repository;
import com.dailyhelp.entity.*; import java.time.LocalDate; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface TaskRepository extends JpaRepository<Task,Long>{
 Optional<Task> findByIdAndOwnerId(Long id,Long ownerId); Page<Task> findByOwnerId(Long ownerId,Pageable pageable);
 @Query("select t from Task t where t.owner.id=:ownerId and (:status is null or t.status=:status) and (:priority is null or t.priority=:priority) and (:q is null or lower(t.title) like lower(concat('%',:q,'%'))) ") Page<Task> search(@Param("ownerId") Long ownerId,@Param("status") TaskStatus status,@Param("priority") TaskPriority priority,@Param("q") String q,Pageable pageable);
 long countByOwnerId(Long ownerId); long countByOwnerIdAndStatus(Long ownerId,TaskStatus status); long countByOwnerIdAndDueDateBeforeAndStatusNot(Long ownerId,LocalDate date,TaskStatus status);
 List<Task> findTop5ByOwnerIdOrderByCreatedAtDesc(Long ownerId); List<Task> findTop5ByOwnerIdAndStatusNotAndDueDateGreaterThanEqualOrderByDueDateAsc(Long ownerId,TaskStatus status,LocalDate dueDate);
}
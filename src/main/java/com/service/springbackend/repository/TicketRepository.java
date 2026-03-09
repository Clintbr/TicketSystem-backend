package com.service.springbackend.repository;

import com.service.springbackend.dto.TicketHistoryDTO;
import com.service.springbackend.model.Status;
import com.service.springbackend.model.Ticket;
import com.service.springbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByCreatedBy(User user);
    List<Ticket> findByAssignedTo(User user);
    List<Ticket> findAll();

    @Query(value = "SELECT " +
            "  TO_CHAR(created_at, 'YYYY-MM') as date, " +
            "  COUNT(*) as count " +
            "FROM tickets " +
            "GROUP BY TO_CHAR(created_at, 'YYYY-MM') " +
            "ORDER BY date ASC",
            nativeQuery = true)
    List<TicketHistoryDTO> getTicketHistory();
    Long countByStatus(Status status);
}
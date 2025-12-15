package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.MovementType;
import ua.edu.viti.military.entity.SupplyMovement;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplyMovementRepository extends JpaRepository<SupplyMovement, Long> {

    // Історія операцій для конкретного item
    List<SupplyMovement> findByItemIdOrderByPerformedAtDesc(Long itemId);

    // Пошук за типом операції
    List<SupplyMovement> findByTypeOrderByPerformedAtDesc(MovementType type);

    // Операції за період
    List<SupplyMovement> findByPerformedAtBetweenOrderByPerformedAtDesc(
            LocalDateTime start, LocalDateTime end);

    // Операції за типом та періодом
    List<SupplyMovement> findByTypeAndPerformedAtBetweenOrderByPerformedAtDesc(
            MovementType type, LocalDateTime start, LocalDateTime end);

    // Статистика: загальна кількість виданих/повернених матеріалів
    @Query("SELECT SUM(m.quantity) FROM SupplyMovement m WHERE m.item.id = :itemId AND m.type = :type")
    Integer getTotalQuantityByItemAndType(@Param("itemId") Long itemId, @Param("type") MovementType type);

    // Останні N операцій
    List<SupplyMovement> findTop10ByOrderByPerformedAtDesc();
}

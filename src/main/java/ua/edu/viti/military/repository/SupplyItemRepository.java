package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.HazardClass;
import ua.edu.viti.military.entity.ItemStatus;
import ua.edu.viti.military.entity.SupplyItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplyItemRepository extends JpaRepository<SupplyItem, Long> {

    // Пошук по номеру партії
    Optional<SupplyItem> findByBatchNumber(String batchNumber);

    // Перевірка існування по номеру партії
    boolean existsByBatchNumber(String batchNumber);

    // Пошук по категорії
    List<SupplyItem> findByCategoryId(Long categoryId);

    // Пошук по статусу
    List<SupplyItem> findByStatus(ItemStatus status);

    // Підрахунок по статусу
    long countByStatus(ItemStatus status);

    // Знайти матеріали з терміном придатності раніше заданої дати
    List<SupplyItem> findByExpirationDateBefore(LocalDate date);

    // Знайти по складу
    List<SupplyItem> findByWarehouseId(Long warehouseId);

    // Знайти по класу небезпеки
    List<SupplyItem> findByHazardClass(HazardClass hazardClass);

    // Знайти з кількістю менше ніж X (для сповіщень про низькі залишки)
    List<SupplyItem> findByQuantityLessThan(Integer threshold);

    // JPQL запит з JOIN FETCH для ефективного завантаження зв'язків
    @Query("SELECT si FROM SupplyItem si " +
           "JOIN FETCH si.category " +
           "LEFT JOIN FETCH si.warehouse " +
           "WHERE si.id = :id")
    Optional<SupplyItem> findByIdWithDetails(Long id);

    // JPQL запит для пошуку всіх елементів з повними деталями
    @Query("SELECT DISTINCT si FROM SupplyItem si " +
           "JOIN FETCH si.category " +
           "LEFT JOIN FETCH si.warehouse")
    List<SupplyItem> findAllWithDetails();
}

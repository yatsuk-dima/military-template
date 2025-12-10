package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.Warehouse;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // Пошук по коду
    Optional<Warehouse> findByCode(String code);

    // Перевірка існування по коду
    boolean existsByCode(String code);

    // Пошук складів з холодильним обладнанням
    List<Warehouse> findByHasRefrigeration(Boolean hasRefrigeration);
}

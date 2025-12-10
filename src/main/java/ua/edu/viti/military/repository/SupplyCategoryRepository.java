package ua.edu.viti.military.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.viti.military.entity.SupplyCategory;

import java.util.Optional;

@Repository
public interface SupplyCategoryRepository extends JpaRepository<SupplyCategory, Long> {

    // Пошук по імені
    Optional<SupplyCategory> findByName(String name);

    // Перевірка існування по імені
    boolean existsByName(String name);

    // Пошук по коду
    Optional<SupplyCategory> findByCode(String code);

    // Перевірка існування по коду
    boolean existsByCode(String code);
}

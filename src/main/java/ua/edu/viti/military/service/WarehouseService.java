package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.WarehouseCreateDTO;
import ua.edu.viti.military.dto.WarehouseResponseDTO;
import ua.edu.viti.military.entity.Warehouse;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    /**
     * Створити новий склад
     */
    @Transactional
    public WarehouseResponseDTO create(WarehouseCreateDTO dto) {
        log.info("Creating new warehouse with code: {}", dto.getCode());

        // Перевірка унікальності коду
        if (warehouseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException(
                    "Склад з кодом '" + dto.getCode() + "' вже існує"
            );
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setCode(dto.getCode());
        warehouse.setAddress(dto.getAddress());
        warehouse.setCapacity(dto.getCapacity());
        warehouse.setCurrentOccupancy(dto.getCurrentOccupancy());
        warehouse.setHasRefrigeration(dto.getHasRefrigeration());

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse created with ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    /**
     * Отримати склад по ID
     */
    public WarehouseResponseDTO getById(Long id) {
        log.debug("Fetching warehouse with ID: {}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Склад з ID " + id + " не знайдено"
                ));

        return toResponseDTO(warehouse);
    }

    /**
     * Отримати всі склади з фільтрацією
     */
    public List<WarehouseResponseDTO> getAll(Boolean hasRefrigeration) {
        log.debug("Fetching all warehouses with filter - hasRefrigeration: {}", hasRefrigeration);

        List<Warehouse> warehouses;

        if (hasRefrigeration != null) {
            warehouses = warehouseRepository.findByHasRefrigeration(hasRefrigeration);
        } else {
            warehouses = warehouseRepository.findAll();
        }

        return warehouses.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Видалити склад
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting warehouse with ID: {}", id);

        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Склад з ID " + id + " не знайдено"
            );
        }

        warehouseRepository.deleteById(id);
        log.info("Warehouse with ID {} deleted successfully", id);
    }

    /**
     * Маппінг Entity -> DTO
     */
    private WarehouseResponseDTO toResponseDTO(Warehouse entity) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setAddress(entity.getAddress());
        dto.setCapacity(entity.getCapacity());
        dto.setCurrentOccupancy(entity.getCurrentOccupancy());
        dto.setHasRefrigeration(entity.getHasRefrigeration());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

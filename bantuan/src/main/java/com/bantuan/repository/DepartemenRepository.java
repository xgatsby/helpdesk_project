package com.bantuan.repository;

import com.bantuan.entity.Departemen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface DepartemenRepository extends JpaRepository<Departemen, UUID> {
    List<Departemen> findByIndukDepartemenId(UUID indukDepartemenId);
}

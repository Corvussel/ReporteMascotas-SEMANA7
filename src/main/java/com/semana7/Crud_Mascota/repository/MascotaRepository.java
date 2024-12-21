package com.semana7.Crud_Mascota.repository;

import com.semana7.Crud_Mascota.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
}

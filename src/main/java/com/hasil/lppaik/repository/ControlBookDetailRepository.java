package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.ControlBookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlBookDetailRepository extends JpaRepository<ControlBookDetail, String> {
}

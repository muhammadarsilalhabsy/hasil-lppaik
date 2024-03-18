package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.ControlBookDetail;
import com.hasil.lppaik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlBookDetailRepository extends JpaRepository<ControlBookDetail, String>,
        JpaSpecificationExecutor<ControlBookDetail> {
}

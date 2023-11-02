package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.ActivityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityImageRepository extends JpaRepository<ActivityImage, String> {
}

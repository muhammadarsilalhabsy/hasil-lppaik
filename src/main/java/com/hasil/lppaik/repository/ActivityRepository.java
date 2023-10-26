package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

  Optional<Activity> findByObjId(String id);
}

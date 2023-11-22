package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.Activity;
import com.hasil.lppaik.entity.ControlBookDetail;
import com.hasil.lppaik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String>,
        JpaSpecificationExecutor<Activity> {

  boolean existsByUsersAndId(User user, String activityId);
}

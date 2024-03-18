package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.Activity;
import com.hasil.lppaik.entity.ActivityRegister;
import com.hasil.lppaik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRegisterRepository extends JpaRepository<ActivityRegister, String> {

  List<ActivityRegister> findByActivityId(String activityId);

  boolean existsByUserAndActivity(User candidate, Activity activityTarget);

  boolean existsByUser(User user);

  void deleteByUser(User user);
}

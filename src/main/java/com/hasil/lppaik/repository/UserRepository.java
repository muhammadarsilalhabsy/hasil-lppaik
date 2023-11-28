package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>,
        JpaSpecificationExecutor<User> {

  Optional<User> findFirstByToken(String token);

  List<User> findByRoles_Name(RoleEnum roleName);
}
package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.Role;
import com.hasil.lppaik.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String > {

  Optional<Role> findByName(RoleEnum roleEnum);
}

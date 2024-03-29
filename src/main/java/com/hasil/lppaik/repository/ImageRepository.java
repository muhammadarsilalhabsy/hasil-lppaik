package com.hasil.lppaik.repository;

import com.hasil.lppaik.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

  Optional<Image> findByPath(String path);
  void deleteByPath(String path);
}

package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.Image;
import com.hasil.lppaik.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
  private final ImageRepository repository;

  @Autowired
  public ImageServiceImpl(ImageRepository repository) {
    this.repository = repository;
  }


  @Override
  @Transactional
  public String saveImageToDb(MultipartFile file) throws IOException {

    // .mp3
    if(!Objects.equals(file.getContentType(), "image/png")
            && !Objects.equals(file.getContentType(), "image/jpeg")
            && !Objects.equals(file.getContentType(), "image/jpg")){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only allow file with type [.png, .jpeg, .jpg]");
    }

    repository.save(Image.builder()
            .path(Utils.nameConversion(file))
            .data(Utils.compressImage(file.getBytes()))
            .type(file.getContentType())
            .build());

    return Utils.nameConversion(file);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] getImageFromDb(String name) {
    Image image = repository.findByPath(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

    return Utils.decompressImage(image.getData());
  }

  @Override
  @Transactional
  public void removePrevImage(String path) {
    repository.deleteByPath(path);
  }
}

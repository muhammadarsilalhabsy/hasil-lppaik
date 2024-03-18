package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.*;
import com.hasil.lppaik.model.request.CreateActivityRequest;
import com.hasil.lppaik.model.request.PagingRequest;
import com.hasil.lppaik.model.request.SearchActivityRequest;
import com.hasil.lppaik.model.request.UpdateActivityRequest;
import com.hasil.lppaik.model.response.ActivityResponse;
import com.hasil.lppaik.model.response.UserResponse;
import com.hasil.lppaik.repository.ActivityImageRepository;
import com.hasil.lppaik.repository.ActivityRegisterRepository;
import com.hasil.lppaik.repository.ActivityRepository;
import com.hasil.lppaik.repository.UserRepository;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.hasil.lppaik.service.PdfUtils.*;

@Service
public class ActivityServiceImpl implements ActivityService {

  private final ActivityRepository activityRepository;
  private final ActivityRegisterRepository activityRegisterRepository;

  private final ActivityImageRepository activityImageRepository;
  private final ActivityRegisterServiceImpl activityRegisterService;

  private final UserRepository userRepository;

  private final Utils utils;

  @Autowired
  public ActivityServiceImpl(ActivityRepository activityRepository, ActivityRegisterRepository activityRegisterRepository, ActivityImageRepository activityImageRepository, ActivityRegisterServiceImpl activityRegisterService, UserRepository userRepository, Utils utils) {
    this.activityRepository = activityRepository;
    this.activityRegisterRepository = activityRegisterRepository;
    this.activityImageRepository = activityImageRepository;
    this.activityRegisterService = activityRegisterService;
    this.userRepository = userRepository;
    this.utils = utils;
  }

  @Override
  public Page<ActivityResponse> getAllActivities(SearchActivityRequest request) {
    int page = request.getPage() - 1;

    utils.validate(request);

    // query
    Specification<Activity> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if(Objects.nonNull(request.getTitle())){
        predicates.add(builder.like(root.get("title"),"%" + request.getTitle() +"%"));
      }

      if (Objects.nonNull(request.getMandatory())){
        predicates.add(builder.equal(root.get("mandatory"), request.getMandatory()));
      }


      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
    };

    Pageable pageable = PageRequest.of(page, request.getSize(), Sort.by("date").descending());
    Page<Activity> activities = activityRepository.findAll(specification, pageable);
    List<ActivityResponse> activityResponse = activities.getContent().stream()
            .map(utils::activityToActivityResponse)
            .toList();

    return new PageImpl<>(activityResponse, pageable, activities.getTotalElements());
  }

  @Override
  @Transactional
  public void addActivityToOtherUser(User user, String id, String username, String regId) {

    boolean isAllow = user.getRoles()
            .stream()
            .anyMatch(role ->
                    role.getName().equals(RoleEnum.ADMIN) ||
                    role.getName().equals(RoleEnum.KATING)
            );

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    User candidate = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    candidate.getActivities().add(activity);

    activityRegisterService.remove(user, regId);

    userRepository.save(candidate);
  }

  @Override
  @Transactional
  public void addActivityToUser(User user, String id, String username) {
    boolean isAllow = user.getRoles()
            .stream()
            .anyMatch(role ->
                    role.getName().equals(RoleEnum.ADMIN) ||
                            role.getName().equals(RoleEnum.KATING)
            );

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    User candidate = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    candidate.getActivities().add(activity);

    activityRegisterService.remove(candidate);

    userRepository.save(candidate);
  }

  @Override
  @Transactional
  public void updateActivity(User user, UpdateActivityRequest request) {
    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(request.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + request.getId() + " is NOT FOUND"));

    if(Objects.nonNull(request.getLocation())){
      activity.setLocation(request.getLocation());
    }
    if(Objects.nonNull(request.getDescription())){
      activity.setDescription(request.getDescription());
    }
    if(Objects.nonNull(request.getMandatory())){
      activity.setMandatory(request.getMandatory());
    }
    if(Objects.nonNull(request.getEndTime())){
      activity.setEndTime(request.getEndTime());
    }
    if(Objects.nonNull(request.getStartTime())){
      activity.setStartTime(request.getStartTime());
    }
    if(Objects.nonNull(request.getTitle())){
      activity.setTitle(request.getTitle());
    }
    if(Objects.nonNull(request.getDate())){
      activity.setDate(request.getDate());
    }

    if (request.isOnline()) {
      if(Objects.nonNull(request.getLink())){
        activity.setLink(request.getLink());
      }
    }

    if(Objects.nonNull(request.getImages()) && request.getImages().size() != 0){
      activityImageRepository.deleteAll(activity.getImages());

      for(String data : request.getImages()){
        ActivityImage activityImage = new ActivityImage();
        activityImage.setId(UUID.randomUUID().toString());
        activityImage.setImage(data);
        activityImage.setActivity(activity);
        activityImageRepository.save(activityImage);
      }
    }

    activityRepository.save(activity);

  }

  @Override
  @Transactional
  public void createActivity(User user, CreateActivityRequest request) {

    utils.validate(request);

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = new Activity();
    activity.setId(UUID.randomUUID().toString());
    activity.setDescription(request.getDescription());
    activity.setMandatory(request.getMandatory());
    activity.setStartTime(request.getStartTime());
    activity.setLocation(request.getLocation());
    activity.setEndTime(request.getEndTime());
    activity.setTitle(request.getTitle());
    activity.setDate(request.getDate());
    if (request.isOnline()) {
      activity.setLink(request.getLink());
    }

    activityRepository.save(activity);

    for(String data : request.getImages()){
      ActivityImage activityImage = new ActivityImage();
      activityImage.setId(UUID.randomUUID().toString());
      activityImage.setImage(data);
      activityImage.setActivity(activity);
      activityImageRepository.save(activityImage);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ActivityResponse getActivityById(String id) {

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    return utils.activityToActivityResponse(activity);
  }

  @Override
  public Page<UserResponse> getAttendance(User user, PagingRequest request) {

    utils.validate(request);

    int page = request.getPage() - 1;

    String activityId = request.getUsername();

    Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + activityId + " is NOT FOUND"));

    // validate if user doesn't contain role 'ADMIN || KETING || DOSEN
    boolean isAllow = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN)
                    || role.getName().equals(RoleEnum.DOSEN)
                    || role.getName().equals(RoleEnum.KATING));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    boolean isKATING = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(RoleEnum.KATING));

    // query
    Specification<User> specification = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();

      Join<Activity, User> joinActivities = root.join("activities");
      predicates.add(builder.equal(joinActivities.get("id"), activity.getId()));

      if(isKATING) {
        Join<User, Role> role = root.join("roles");
        predicates.add(builder.and(
                builder.notEqual(role.get("name"), RoleEnum.ADMIN),
                builder.notEqual(role.get("name"), RoleEnum.DOSEN)
        ));
      }

      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

    };

    Pageable pageable = PageRequest.of(page, request.getSize());
    Page<User> users = userRepository.findAll(specification, pageable);
    List<UserResponse> userResponses = users.getContent().stream()
            .map(person -> utils.getUserResponse(person))
            .collect(Collectors.toList());

    return new PageImpl<>(userResponses, pageable, users.getTotalElements());
  }

  @Override
  @Transactional
  public void deleteActivity(User user, String id) {

    // validate if user contain 'ADMIN' role
    boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));

    if(!isAdmin){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    // delete all imagenya
    activityImageRepository.deleteAll(activity.getImages());

    // ambil usernya lalu putuskan relasinya
    activity.getUsers().forEach( u -> u.getActivities().remove(activity));

    // ambil activitynya lalu hapus activity registernys
    Set<ActivityRegister> activityRegister = activity.getActivityRegisters();
    if (Objects.nonNull(activityRegister) && !activityRegister.isEmpty()) {
      activityRegisterRepository.deleteAll(activityRegister); // Hapus semua registrasi aktivitas
    }

    // save usernya
    userRepository.saveAll(activity.getUsers());

    // delete activitynya
    activityRepository.delete(activity);

  }

  @Override
  public void removeUserFromActivity(User user, String id, String username) {

    boolean isAllow = user.getRoles()
            .stream()
            .anyMatch(role ->
                    EnumSet.of(
                    RoleEnum.ADMIN,
                    RoleEnum.TUTOR,
                    RoleEnum.KATING)
                    .contains(role.getName()));

    if(!isAllow){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This Operation is not support for you role!");
    }

    Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Activity with id " + id + " is NOT FOUND"));

    User candidate = userRepository.findById(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is NOT FOUND"));

    // Check if the candidate user is in the activity
    if (!activity.getUsers().contains(candidate)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + username + " is not associated with the activity");
    }

    activity.getUsers().remove(candidate);
    candidate.getActivities().remove(activity);

    activityRepository.save(activity);

  }
  @Override
  public Resource downloadCurrentUserActivity(User user) throws IOException {

    makeActivityReportForCurrentUser(user);

    Path filePath = Path.of("assets/output-report-user-activities.pdf");
    if(!Files.exists(filePath)) {
      throw new FileNotFoundException("file was not found on the server");
    }

    return new UrlResource(filePath.toUri());
  }

  public void makeActivityReportForCurrentUser(User user) throws MalformedURLException, FileNotFoundException {

    // file destination
    String output = "assets/output-report-user-activities.pdf";
    String signatureImg = "assets/images/signature.png";
    String logoImg = "assets/images/umk1.png";

    PdfWriter writer = new PdfWriter(output);
    PdfDocument pdfDocument = new PdfDocument(writer);
    pdfDocument.setDefaultPageSize(PageSize.A4);

    // get ketua
    Optional<User> ketua = Optional.ofNullable(utils.findUserByRole(RoleEnum.KETUA)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Please create USER with role KETUA")));



    // HEADER
    Table header = new Table(new float[]{
            percentPerWidth(container, 1.0f / 12),
            percentPerWidth(container, 8.0f / 12),
            percentPerWidth(container, 3.0f / 12)
    });

    // header title
    Table headerTitle = new Table(new float[]{percentPerWidth(container, 9.0f / 12)});

    headerTitle.addCell(setTextBold("UNIVERSITAS MUHAMMADIYAH KENDARI", 13f)
            .setPadding(5f)
            .setCharacterSpacing(1.5f)
            .setTextAlignment(TextAlignment.CENTER)
    );
    headerTitle.addCell(setTextBold("LEMBAGA PENGKAJIAN DAN PENGAMALAN AIK", 10f)
            .setTextAlignment(TextAlignment.CENTER)
            .setCharacterSpacing(1.5f)
    );
    // -------------

    // header detail
    Table headerDetail = new Table(new float[]{percentPerWidth(container, 11f / 12)});
    headerDetail.addCell(setText("Jl. KH. Ahmad Dahlan No.10 Kendari", 5f));

    headerDetail.addCell(setText("Tlp : 08533661912", 5f));
    headerDetail.addCell(setText("Email : aik@gmail.com", 5f));
    headerDetail.addCell(setText("Website : lppaik.netlify", 5f));
    // -------------


    // header
    header.addCell(new Cell().add(image(logoImg)
                    .setWidth(55f)
                    .setHeight(50f))
            .setBorder(Border.NO_BORDER)
    );
    header.addCell(new Cell().add(headerTitle).setBorder(Border.NO_BORDER));
    header.addCell(new Cell().add(headerDetail).setBorder(Border.NO_BORDER).setBorderLeft(border(3, Color.BLACK)));
    // -------------


    // core
    Table core = new Table(new float[]{wFull});

    core.addCell(setText("Lembaga Pengkajian dan Pengamalan Al-Islam dan Kemuhammadiyahan" +
            " menyatakan bahwa berdasarkan hasil laporan kehadiran, mahasiswa tersebut di bawah ini:").setTextAlignment(TextAlignment.JUSTIFIED));

    Table userDetail = new Table(new float[]{percentPerWidth(container, 8.0f / 12)});



    userDetail.addCell(setText("Nama \t\t\t\t\t\t : " + user.getName()));
    userDetail.addCell(setText("Stanbuk \t\t\t\t\t : " + user.getUsername()));
    userDetail.addCell(setText("Program Studi\t\t\t: " + user.getMajor().getName()));

    core.addCell(new Cell().add(userDetail.setMarginTop(20).setMarginBottom(20).setMarginLeft(20)).setBorder(Border.NO_BORDER));
    core.addCell(setText("Dinyatakan telah mengikuti kegiatan - kegiatan sebagai berikut:"));
    // -------------

    float [] productTblWidth = {20f, 381f, 125f};

    Table tableActivityList = new Table(productTblWidth);

    Set<Activity> activities = user.getActivities();

    // Melakukan sorting berdasarkan tanggal secara descending
    Set<Activity> sortedActivities = activities.stream()
            .sorted(Comparator.comparing(Activity::getDate).reversed())
            .collect(Collectors.toCollection(LinkedHashSet::new));


    // list header
    tableActivityList.addCell(tableHead("No", 9).setTextAlignment(TextAlignment.CENTER));
    tableActivityList.addCell(tableHead("Judul Kegiatan", 9).setTextAlignment(TextAlignment.CENTER));
    tableActivityList.addCell(tableHead("Tanggal", 9).setTextAlignment(TextAlignment.CENTER));

    // list content
    int num = 0;
    int maxActivities = 10; // Jumlah maksimum elemen yang ingin ditampilkan

    for (Activity activity : sortedActivities) {
      num++;
      tableActivityList.addCell(tableData(String.valueOf(num)).setTextAlignment(TextAlignment.CENTER));
      tableActivityList.addCell(tableData(activity.getTitle(), 11).setTextAlignment(TextAlignment.CENTER));
      tableActivityList.addCell(tableData(String.valueOf(activity.getDate()), 11).setTextAlignment(TextAlignment.CENTER));
    }

    for (int i = sortedActivities.size() + 1; i <= maxActivities; i++) {
      tableActivityList.addCell(tableData(String.valueOf(i)).setTextAlignment(TextAlignment.CENTER));
      tableActivityList.addCell(tableData("").setTextAlignment(TextAlignment.CENTER));
      tableActivityList.addCell(tableData("").setTextAlignment(TextAlignment.CENTER));
    }

    // -------------

    // signature

    Table signature = new Table(new float[]{container});

    signature.addCell(setText("Kendari, 27 agustus 2023")
            .setTextAlignment(TextAlignment.RIGHT));

    signature.addCell(setTextBold("Kepala Lppaik", 11)
            .setTextAlignment(TextAlignment.RIGHT).setPaddingBottom(45));


    Text underlinedText = new Text(ketua.get().getName()).setUnderline(1, -2);
    Paragraph paragraph = new Paragraph(underlinedText)
            .setTextAlignment(TextAlignment.RIGHT)
            .setFontSize(12)
            .setCharacterSpacing(0.5f)
            .setMarginBottom(5);

    signature.addCell(new Cell().add(paragraph.setTextAlignment(TextAlignment.RIGHT))
            .setBorder(Border.NO_BORDER));

    signature.addCell(setText("NIDN." + ketua.get().getUsername()).setTextAlignment(TextAlignment.RIGHT).setPaddingTop(-10f));


    signature.addCell(new Cell().add(image(signatureImg)
                    .scaleAbsolute(210, 100)
                    .setFixedPosition(380, 60))
            .setBorder(Border.NO_BORDER)
    );
    // -------------

    // document
    Document document = new Document(pdfDocument);
    document.add(header);
    document.add(underline(container, 0.5f , Color.GRAY).setMarginTop(10));
    document.add(core.setMarginTop(40));
    document.add(tableActivityList.setMarginTop(10));
    document.add(signature.setMarginTop(20));

    document.close();

    System.out.println("Completed");
  }
}

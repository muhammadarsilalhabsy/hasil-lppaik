package com.hasil.lppaik.service;

import com.google.zxing.WriterException;
import com.hasil.lppaik.entity.Certificate;
import com.hasil.lppaik.entity.RoleEnum;
import com.hasil.lppaik.entity.User;
import com.hasil.lppaik.model.response.SimpleUserResponse;
import com.hasil.lppaik.repository.CertificateRepository;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CertificateService {

  private final CertificateRepository certificateRepository;

  private final Utils utils;

  @Autowired
  public CertificateServiceImpl(CertificateRepository certificateRepository, Utils utils) {
    this.certificateRepository = certificateRepository;
    this.utils = utils;
  }

  @Override
  public Resource download(User user) throws IOException, WriterException {

    makeCertificate(user);

    Path filePath = Path.of("assets/output.pdf");
    if(!Files.exists(filePath)) {
      throw new FileNotFoundException("file was not found on the server");
    }

    return new UrlResource(filePath.toUri());
  }

  @Override
  public SimpleUserResponse getUserCertificateWithId(String id) {
    Certificate certificate = certificateRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate is NOT FOUND!"));

    User user = certificate.getUser();

    return utils.userToSimpleUser(user);
  }

  @Override
  public void createCertificate(User user){
    Certificate certificate = new Certificate();
    certificate.setId(UUID.randomUUID().toString());
    certificate.setUser(user);

    certificateRepository.save(certificate);
  }


  @Override
  public void removeCertificate(String id){
    Certificate certificate = certificateRepository.findById(id).orElse(null);

    if(Objects.nonNull(certificate)){
      certificateRepository.delete(certificate);
    }

  }

  public void makeCertificate(User user) throws IOException, WriterException {

    String path = "assets/template.pdf";
    String dest = "assets/output.pdf";
    String qrLoc = "assets/images/qr.jpg";

    // check certificate if is Exist
    Certificate certificate = user.getCertificate();

    if(!Objects.nonNull(certificate)){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate is NOT FOUND!");
    }

    // get ketua
    Optional<User> ketua = Optional.ofNullable(utils.findUserByRole(RoleEnum.KETUA)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Please create USER with role KETUA")));

    // get REKTOR
    Optional<User> rektor = Optional.ofNullable(utils.findUserByRole(RoleEnum.REKTOR)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Please create USER with role REKTOR")));


    // fonts
    FontProgram nameFont = FontProgramFactory.createFont("assets/fonts/KaushanScript-Regular.ttf");
    FontProgram defaultFont = FontProgramFactory.createFont("assets/fonts/OpenSans-VariableFont_wdth,wght.ttf");

    PdfFont font = PdfFontFactory.createFont(
            nameFont, PdfEncodings.WINANSI, true);

    PdfFont fontReg = PdfFontFactory.createFont(
            defaultFont, PdfEncodings.WINANSI, true);

    PdfReader reader = new PdfReader(path);
    PdfWriter writer = new PdfWriter(dest);

    PdfDocument document = new PdfDocument(reader, writer);

    // NAME
    Paragraph userName = new Paragraph(user.getName())
            .setTextAlignment(TextAlignment.CENTER).setFont(font).setFontSize(40).setFixedPosition(170,310,500);
    // MAJOR
    Paragraph userMajor = new Paragraph("Program Studi: " + user.getMajor().getName())
            .setFont(fontReg).setFixedPosition(170, 290, 500).setTextAlignment(TextAlignment.CENTER).setFontSize(15f);;

    // REKTOR
    Paragraph rektorName = new Paragraph(rektor.get().getName())
            .setFont(fontReg).setBold().setTextAlignment(TextAlignment.CENTER).setFixedPosition(65, 85, 400);

    // REKTOR: ID
    Paragraph rektorNIDN = new Paragraph(rektor.get().getUsername())
            .setFont(fontReg).setBold().setFixedPosition(195, 60, 120);

    // LPPAIK
    Paragraph kepalaName = new Paragraph(ketua.get().getName())
            .setFont(fontReg).setBold().setTextAlignment(TextAlignment.CENTER).setFixedPosition(465, 85, 300);

    // LPPAIK: ID
    Paragraph kepalaNIDN = new Paragraph(ketua.get().getUsername())
            .setFont(fontReg).setBold().setFixedPosition(575, 60, 120);

    // DATE
    Paragraph tanggalIslam = new Paragraph("13 Rabiul Awal 1445 H")
            .setFont(fontReg).setFixedPosition(650, 234, 150);
    Paragraph tanggalMasehi = new Paragraph("13 Desember 2023 M")
            .setFont(fontReg).setFixedPosition(650, 215, 150);

    // SIGNATURE
    ImageData signature = ImageDataFactory.create("assets/images/signature.png");
    Image image = new Image(signature);
    image.scaleAbsolute(210, 100);
    image.setFixedPosition(120, 80);

//    Table temp = new Table(new float[]{150});
//    temp.addCell(new Cell().add("Program Studi Ilmu Hukum"));
//    temp.setFixedPosition(170, 290, 500).setTextAlignment(TextAlignment.CENTER).setFontSize(18f);

    Document doc = new Document(document);


    String qrResult = "https://my-lppaik.netlify.app/certificate?id=" + certificate.getId();
    BitMatrix matrix = new MultiFormatWriter()
            .encode(qrResult, BarcodeFormat.QR_CODE, 250, 250);

    MatrixToImageWriter.writeToPath(matrix, "jpg", Paths.get(qrLoc));

    // QR CODE
    ImageData qrImage = ImageDataFactory.create("assets/images/qr.jpg");
    Image qrAcutualImage = new Image(qrImage);
    qrAcutualImage.scaleAbsolute(100, 100);
    qrAcutualImage.setFixedPosition(371, 135);

    // INSERT INTO doc
    doc.add(userName);
    doc.add(userMajor);
    doc.add(rektorName);
    doc.add(rektorNIDN);
    doc.add(kepalaName);
    doc.add(kepalaNIDN);
    doc.add(tanggalIslam);
    doc.add(tanggalMasehi);
    doc.add(image);
    doc.add(qrAcutualImage);
//    doc.add(temp);

    doc.close();
  }
}

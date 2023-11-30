package com.hasil.lppaik.service;

import com.hasil.lppaik.entity.ControlBookDetail;
import com.hasil.lppaik.entity.User;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;

import static com.hasil.lppaik.service.PdfUtils.*;
import static com.hasil.lppaik.service.Utils.nameSplit;


@Service
public class PdfService {

  public static void makeCBDReport(User user) throws FileNotFoundException, MalformedURLException {

    // file destination
    String output = "assets/output-report-user-cbd.pdf";
    String logoImg = "assets/images/umk1.png";

    PdfWriter writer = new PdfWriter(output);
    PdfDocument pdfDocument = new PdfDocument(writer);
    pdfDocument.setDefaultPageSize(PageSize.A4);

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

    core.addCell(setTextBold("Buku kontrol BTQ (Baca Tulis Al-Qur'an)", 18).setTextAlignment(TextAlignment.CENTER));

    Table userDetail = new Table(new float[]{percentPerWidth(container, 8.0f / 12)});


    userDetail.addCell(setText("Nama \t\t\t\t\t\t : " + user.getName()));
    userDetail.addCell(setText("Stanbuk \t\t\t\t\t : " + user.getUsername()));
    userDetail.addCell(setText("Program Studi\t\t\t: " + user.getMajor().getName()));

    core.addCell(new Cell().add(userDetail.setMarginTop(20).setMarginBottom(20)).setBorder(Border.NO_BORDER));

    float [] productTblWidth = {20f, 175, 200, 100, 100};

    Table cbdTable = new Table(productTblWidth);

    int targetSize = 17; // Jumlah kolom yang diinginkan

    // list header
    cbdTable.addCell(tableHead("No", 9).setTextAlignment(TextAlignment.CENTER));
    cbdTable.addCell(tableHead("Pelajaran", 9).setTextAlignment(TextAlignment.CENTER));
    cbdTable.addCell(tableHead("Keterangan", 9).setTextAlignment(TextAlignment.CENTER));
    cbdTable.addCell(tableHead("Tanggal", 9).setTextAlignment(TextAlignment.CENTER));
    cbdTable.addCell(tableHead("Tutor", 9).setTextAlignment(TextAlignment.CENTER));

    // list content
    List<ControlBookDetail> cbdList = user.getControlBookDetailUser().stream()
            .sorted(Comparator.comparing(ControlBookDetail::getDate).reversed())
            .toList();


    for (int i = 0; i < targetSize; i++) {
      int num = i + 1;
      if (i < cbdList.size()) {
        ControlBookDetail cbd = cbdList.get(i);
        cbdTable.addCell(tableData(String.valueOf(num)).setTextAlignment(TextAlignment.CENTER));
        cbdTable.addCell(tableData(cbd.getLesson(), 11).setTextAlignment(TextAlignment.CENTER));
        cbdTable.addCell(tableData(cbd.getDescription(), 11).setTextAlignment(TextAlignment.CENTER));
        cbdTable.addCell(tableData(cbd.getDate().toString(), 11).setTextAlignment(TextAlignment.CENTER));
        cbdTable.addCell(tableData(nameSplit(cbd.getTutor().getName()), 11).setTextAlignment(TextAlignment.CENTER));
      } else {
        // Tambahkan kolom kosong
        cbdTable.addCell(tableData(String.valueOf(num)).setTextAlignment(TextAlignment.CENTER));
        cbdTable.addCell(tableData(""));
        cbdTable.addCell(tableData(""));
        cbdTable.addCell(tableData(""));
        cbdTable.addCell(tableData(""));
      }
    }
    // -------------

    // document
    Document document = new Document(pdfDocument);
    document.add(header);
    document.add(underline(container, 0.5f , Color.GRAY).setMarginTop(10));
    document.add(core.setMarginTop(40));
    document.add(cbdTable.setMarginTop(10));

    document.close();

    System.out.println("Completed");
  }

}

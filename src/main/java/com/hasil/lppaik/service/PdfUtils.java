package com.hasil.lppaik.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
public class PdfUtils {

  protected static final float wFull = 595f;
  protected static final float hFull = 842f;
  protected static final float container = 570f;

  public static Image image(String path) throws MalformedURLException {
    return new Image(ImageDataFactory.create(path));
  }

  public static Border border(float volume, Color color){

    return new SolidBorder(color, volume);
  }
  public static Table underline(float width, float volume, Color color){

    Border grayBorder = new SolidBorder(color, volume);
    Table borderDivideHeaderAndContent = new Table(new float[] {width});
    borderDivideHeaderAndContent.setBorder(grayBorder);

    return borderDivideHeaderAndContent;
  }
  public static float percentPerWidth(float current, float percent) {
    float result = current * percent;
    return Float.parseFloat(String.format("%.1f", result));
  }

  public static Cell setTextBold(String data){
    return new Cell().add(data)
            .setBorder(Border.NO_BORDER)
            .setBold();
  }
  public static  Cell setTextBold(String data, float size){
    return new Cell().add(data)
            .setBorder(Border.NO_BORDER)
            .setBold()
            .setFontSize(size);
  }
  public static  Cell setText(String data, float size){
    return new Cell().add(data)
            .setCharacterSpacing(0.8f)
            .setBorder(Border.NO_BORDER)
            .setFontSize(size);
  }
  public static  Cell setText(String data){
    return new Cell().add(data)
            .setCharacterSpacing(0.8f)
            .setBorder(Border.NO_BORDER);
  }

  public static  Cell tableHead(String data){
    return new Cell().add(data)
            .setCharacterSpacing(0.8f)
            .setBold()
            .setPadding(5);
  }
  public static  Cell tableHead(String data, float size){
    return new Cell().add(data)
            .setCharacterSpacing(0.8f)
            .setBold()
            .setPadding(5)
            .setFontSize(size);
  }
  public static  Cell tableData(String data, float size){
    return new Cell().add(data)
            .setCharacterSpacing(0.8f)
            .setPadding(5)
            .setFontSize(size);
  }
  public static  Cell tableData(String data){
    return new Cell().add(data)
            .setPadding(5)
            .setCharacterSpacing(0.8f);
  }
}

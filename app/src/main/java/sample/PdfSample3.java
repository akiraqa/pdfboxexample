package sample;

import java.awt.geom.Point2D;

import java.io.InputStream;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * ミリ単位での位置指定でPDFを作成するサンプル.
 */
public class PdfSample3 {
  /**
   * PDFを作成する(ミリ単位での位置指定).
   */
  public void createPdf() {
    try (PDDocument document = new PDDocument()) {

      // ページの生成
      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      // フォントの生成
      InputStream fontStream = PdfSample1.class.getResourceAsStream("/ipaexm.ttf");
      PDFont font = PDType0Font.load(document, fontStream);

      try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

        // 文字列の出力
        cs.beginText();
        cs.setFont(font, 12);
        Point2D.Float point = PdfUtil.mm2pt(new Point2D.Float(25, 30), PDRectangle.A4);
        cs.newLineAtOffset(point.x, point.y);
        cs.showText("Hello World");
        cs.showText("日本語が使える");
        cs.endText();

        for (int i = 0; i < 5; i++) {
          // 文字列の出力
          cs.beginText();
          cs.setFont(font, 12);
          point = PdfUtil.mm2pt(new Point2D.Float(i * 50, i * 50), PDRectangle.A4);
          cs.newLineAtOffset(point.x, point.y);
          cs.showText("←ここが(" + (i * 50) + "," + (i * 50) + ")");
          cs.endText();
        }

        // 線を出力
        for (int i = 0; i < 30; i++) {
          // 横線
          cs.setLineWidth(1);
          cs.setStrokingColor(Color.BLACK);
          Point2D.Float pointStart = PdfUtil.mm2pt(new Point2D.Float(0, i * 10), PDRectangle.A4);
          Point2D.Float pointEnd = PdfUtil.mm2pt(new Point2D.Float(210, i * 10), PDRectangle.A4);
          cs.moveTo(pointStart.x, pointStart.y);
          cs.lineTo(pointEnd.x, pointEnd.y);
          cs.stroke();
          // 縦線
          cs.setLineWidth(1);
          cs.setStrokingColor(Color.BLACK);
          Point2D.Float pointStartV = PdfUtil.mm2pt(new Point2D.Float(i * 10, 0), PDRectangle.A4);
          Point2D.Float pointEndV = PdfUtil.mm2pt(new Point2D.Float(i * 10, 290), PDRectangle.A4);
          cs.moveTo(pointStartV.x, pointStartV.y);
          cs.lineTo(pointEndV.x, pointEndV.y);
          cs.stroke();
        }
      } catch (Exception e) {
        // TODO: handle exception
      }

      // ファイルへの保存
      document.save("./out_example03.pdf");
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}

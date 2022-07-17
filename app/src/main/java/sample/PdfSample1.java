package sample;

import java.io.InputStream;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * PDFを新規作成する一番基本的なサンプル.
 */
public class PdfSample1 {

  /**
   * PDFを新規作成する.
   */
  public void createPdf() {
    try (PDDocument document = new PDDocument()) {

      // ページの生成
      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      // フォントの生成
      PDFont font = PDType1Font.HELVETICA_BOLD;
      InputStream fontStream = getClass().getResourceAsStream("/ipaexm.ttf");
      PDFont fontJp = PDType0Font.load(document, fontStream);

      try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

        // 文字列1の出力
        cs.beginText();
        cs.setFont(font, 24);
        cs.newLineAtOffset(20, 500);
        cs.showText("Hello World");
        cs.endText();

        // 文字列2の出力
        cs.beginText();
        cs.setFont(fontJp, 18);
        cs.newLineAtOffset(30, 400);
        cs.showText("日本語が使える");
        cs.endText();

        // 線を出力
        // 1本目
        cs.setLineWidth(1);
        cs.setStrokingColor(Color.BLACK);
        // cs.setStrokingColor(r, g, b);
        cs.moveTo(20, 500);
        cs.lineTo(320, 500);
        cs.stroke();

        // 2本目
        cs.setLineWidth(2);
        cs.setStrokingColor(Color.RED);
        cs.moveTo(20, 400);
        cs.lineTo(480, 400);
        cs.lineTo(480, 200);
        cs.stroke();
      } catch (Exception e) {
        e.printStackTrace();
      }

      // ファイルへの保存
      document.save("./out_example01.pdf");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

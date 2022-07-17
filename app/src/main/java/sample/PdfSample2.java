package sample;

import java.io.File;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * 既存のPDFを更新するサンプル.
 */
public class PdfSample2 {
  /**
   * 既存のPDFを更新する
   */
  public void updatePdf() {
    File file = new File("./org_example02.pdf");
    try (PDDocument document = PDDocument.load(file)) {
      // フォントの生成
      InputStream fontStream = this.getClass().getResourceAsStream("/ipaexm.ttf");
      PDFont font = PDType0Font.load(document, fontStream);

      PDPage page = document.getPage(0);
      try (PDPageContentStream cs = new PDPageContentStream(document, page, AppendMode.APPEND, false)) {
        // 文字列の出力
        for (int i = 0; i < 48; i++) {
          cs.beginText();
          cs.setFont(font, 12);
          cs.newLineAtOffset(0, i * 20);
          cs.showText("Hello World" + i);
          cs.showText("日本語が使える" + i);
          cs.endText();
        }

      } catch (Exception e) {
        // TODO: handle exception
      }
      // ファイルへの保存
      document.save("./out_example02.pdf");

    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}

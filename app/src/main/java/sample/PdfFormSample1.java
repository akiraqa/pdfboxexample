package sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

/**
 * PDFフォームの入力と保護設定・暗号のサンプル.
 * 
 * 入力フォームに日本語入力するサンプル。
 * 元のPDFを開いて、すべてのTextFieldのフォントをIPAex明朝に指定することで日本語を文字化けせず表示できる。
 */
public class PdfFormSample1 {
  /**
   * 「/Helvetica 10 Tf g」などのDefaultAppearanceからfont名を取得するpattern.
   */
  private static final Pattern FONT_PATTERN = Pattern.compile(".*\\/(\\w+)\\s.*");

  /**
   * PDF暗号化：所有者パスワード
   */
  private static final String OWNER_PASSWD = "password";

  /**
   * PDF暗号化：利用者パスワードは設定しない
   */
  private static final String USER_PASSWD = "";

  /**
   * PDF暗号化：鍵長（40,128,256のどれか）
   */
  private static final int ENCRYPT_KEY_LEN = 256;

  /**
   * 元のPDFの入力欄を記入して、更新不可に設定して暗号化して保存する.
   * 
   * すべてのTextFieldのフォントをIPAex明朝に指定する。
   * TextFieldをreadonly指定することで、出来上がったPDFが入力フォームのようには見えない仕上がりになる。
   */
  public void updatePdf() {
    Map<String, String> fieldValues = new HashMap<>();
    fieldValues.put("header01", "領収書");
    fieldValues.put("atena01", "上 様");
    fieldValues.put("address01", "東京都千代田区どこかのまち1-3-5");
    fieldValues.put("date01", "2022/07/01");
    fieldValues.put("footer01", "品物代として");

    File file = new File("./org_form01.pdf");
    try (PDDocument document = PDDocument.load(file)) {
      PDDocumentCatalog docCatalog = document.getDocumentCatalog();
      PDAcroForm acroForm = docCatalog.getAcroForm();
      PDResources defaultResource = acroForm.getDefaultResources();
      InputStream fontStream = getClass().getResourceAsStream("/ipaexm.ttf");
      PDFont font = PDType0Font.load(document, fontStream);
      COSName fontName = defaultResource.add(font);
      acroForm.setDefaultResources(defaultResource);

      changeAllFieldFont(acroForm, fontName.getName());

      for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
        setFieldValue(acroForm, entry.getKey(), entry.getValue());
      }
      for (int i = 1; i <= 3; i++) {
        String fieldNum = String.format("%02d", i);
        setFieldValue(acroForm, "serial" + fieldNum, Integer.toString(i));
        setFieldValue(acroForm, "prd" + fieldNum, Integer.toString(10000 + i));
        setFieldValue(acroForm, "name" + fieldNum, "テスト用商品名" + Integer.toString(i));
        setFieldValue(acroForm, "price" + fieldNum, Integer.toString(1000 * i));
        setFieldValue(acroForm, "qty" + fieldNum, Integer.toString(1));
        setFieldValue(acroForm, "sum" + fieldNum, Integer.toString(1000 * i));
      }

      // 保護を設定
      protectDoc(document);
      // ファイルへの保存
      document.save("./out_exampleForm01.pdf");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * TextFieldのフォントを指定フォントに入れ替える
   * 
   * @param field    formのフィールド
   * @param fontName フォント名
   */
  private void changeFieldFont(PDField field, String fontName) {
    if (field == null || !(field instanceof PDTextField)) {
      return;
    }
    PDTextField textField = (PDTextField) field;
    String da = textField.getDefaultAppearance();

    // 「/Helvetica 10 Tf g」などの「/Font名」部分を置き換える
    Matcher matcher = FONT_PATTERN.matcher(da);
    if (matcher.matches()) {
      String oldFontName = matcher.group(1);
      da = da.replaceFirst(oldFontName, fontName);
      textField.setDefaultAppearance(da);
    }
  }

  /**
   * すべてのフィールドのフォント名を指定フォントに変更する.
   * @param acroForm フォーム
   * @param fontName フォント名
   */
  private void changeAllFieldFont(PDAcroForm acroForm, String fontName) {
    if (acroForm == null || fontName == null) {
      return;
    }
    Iterator<PDField> it = acroForm.getFields().iterator();
    while (it.hasNext()) {
      PDField field = it.next();
      changeFieldFont(field, fontName);
    }
  }

  /**
   * フィールドに値文字列をセットする.
   * @param acroForm  フォーム
   * @param fieldName フィールド名
   * @param value PDFに表示したい値文字列
   * @throws IOException
   */
  private void setFieldValue(PDAcroForm acroForm, String fieldName, String value) throws IOException {
    if (acroForm == null || fieldName == null) {
      return;
    }
    PDField field = (PDField) acroForm.getField(fieldName);
    if (field == null) {
      return;
    }
    field.setValue(value);
    field.setReadOnly(true);
  }

  /**
   * PDFを保護。変更NG、印刷可に設定して暗号化.
   * @param document
   * @throws IOException
   */
  private void protectDoc(PDDocument document) throws IOException {
    AccessPermission ap = new AccessPermission();
    ap.setCanAssembleDocument(false);
    ap.setCanExtractContent(false);
    ap.setCanExtractForAccessibility(false);
    ap.setCanFillInForm(false);
    ap.setCanModify(false);
    ap.setCanModifyAnnotations(false);
    ap.setCanPrint(true);
    ap.setCanPrintDegraded(true);

    StandardProtectionPolicy spp = new StandardProtectionPolicy(OWNER_PASSWD, USER_PASSWD, ap);
    spp.setEncryptionKeyLength(ENCRYPT_KEY_LEN);
    document.protect(spp);
  }

}

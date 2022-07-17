package sample;

import java.awt.geom.Point2D;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfUtil {
  private static final double MM_PER_INCH = 25.4;
  private static final double POINTS_PER_INCH  = 72.0;
  private static final double POINTS_PER_MM    = 1 / MM_PER_INCH * POINTS_PER_INCH;

  /**
   * ミリをptに変換
   * @param mm
   * @return
   */
  public static double mm2pt(double mm) {
      return mm * POINTS_PER_MM;
  }

  /**
   * ptをミリに変換
   * @param pt
   * @return
   */
  public static double pt2mm(double pt) {
      return pt / POINTS_PER_INCH * MM_PER_INCH;
  }

  /**
   * ミリ単位・左上基準での座標指定をpt単位・左下基準に変換する
   * @param mmPoint ミリ単位でのx,y座標（左上を0,0とする）
   * @param rect PDFページ
   * @return 変換結果（単位pt・左下基準）
   */
  public static Point2D.Float mm2pt(Point2D.Float mmPoint, PDRectangle rect) {
    float y = rect.getHeight() - (float)mm2pt(mmPoint.getY());
    float x = (float)mm2pt(mmPoint.getX());
    return new Point2D.Float(x, y);
  }

  /**
   * ミリ単位・左上基準でのY座標指定をpt単位・左下基準に変換する
   * @param mm ミリ単位でのy座標（左上を0,0とする）
   * @param rect PDFページ
   * @return 変換結果（単位pt・左下基準）
   */
  public static float mm2pt(double mm, PDRectangle rect) {
    return rect.getHeight() - (float)mm2pt(mm);
  }
}

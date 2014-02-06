package jp.ac.aiit.jointry.services.broker.util;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtil {

    public static byte[] getByteArray(BufferedImage bimage, String formatName) {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ImageWriter writer = getImageWriter(formatName); // ★エラーチェック
        ImageOutputStream stream = null;
        try {
            stream = ImageIO.createImageOutputStream(bytesOut);
            writer.setOutput(stream);
            writer.write(bimage);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.dispose();
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable t) {
                }
            }
        }
        return bytesOut.toByteArray();
    }

    private static ImageWriter getImageWriter(String formatName) {
        Iterator<ImageWriter> it
                = ImageIO.getImageWritersByFormatName(formatName);
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public static BufferedImage getBufferedImage(Image image) {
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, -1, -1, false);
        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        ColorModel cm = pixelGrabber.getColorModel();
        int w = pixelGrabber.getWidth();
        int h = pixelGrabber.getHeight();
        WritableRaster raster = cm.createCompatibleWritableRaster(w, h);
        Hashtable table = new Hashtable();
        BufferedImage bimage
                = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), table);
        bimage.getRaster().setDataElements(0, 0, w, h, pixelGrabber.getPixels());
        return bimage;
    }

    public static boolean writeBufferedImage(String fname, BufferedImage bimage) {
        try {
            String suffix = Util.imageSuffix(fname);
            if (suffix != null) {
                ImageIO.write(bimage, suffix, new File(fname));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static BufferedImage createImage(String fname) {
        return createImage(new File(fname));
    }

    public static BufferedImage createImage(File imageFile) {
        InputStream in = null;
        try {
            in = new FileInputStream(imageFile);
            return createImage(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static BufferedImage createImage(InputStream is) {
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    synchronized public static BufferedImage createImage(List<String> textBlock) {
        try {
            InputStream in = new Base64Decoder(textBlock).getInputStream();
            BufferedImage bimage = ImageUtil.createImage(in);
            in.close();
            return bimage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * base64テキストを入力し、デコードしたデータを読み出す。 InputStreamを生成する。
     */
    private static class Base64Decoder extends Thread {

        private List<String> textBlock;
        private PipedInputStream pin = new PipedInputStream();
        private PipedOutputStream pos;

        PipedInputStream getInputStream() {
            return pin;
        }

        Base64Decoder(List<String> textBlock) throws IOException {
            this.textBlock = textBlock;
            this.pos = new PipedOutputStream(pin);
            start();
        }

        @Override
        public void run() {
            try {
                Base64.decode64(textBlock, pos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    pos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static final String DATE_FORMAT = "%tY/%<tm/%<td(%<ta) %<tH:%<tM:%<tS.%<tL";
    static Font font = new Font("MS Gothic", Font.PLAIN, 14);

    public static void timeStamp(BufferedImage bimage, int count) {
        Graphics2D g2d = bimage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(font);
        g2d.drawString(String.format(DATE_FORMAT, new Date()), 4, 16);
        g2d.drawString("" + count, 4, 30);
    }
}

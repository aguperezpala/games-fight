package hello;


import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.*;

public class Resizer {

     //Si scale = 0 entonces no se hace ningun resize de la imagen
    static Sprite spriteResized (String nomImg, int px, int py, int scale, boolean alpha)
    {
        Image image;
        Sprite sprite = null;
         try {
            //cargamos la imagen
            image = Image.createImage("/hello/" + nomImg);
            if (scale != 0) {
                float factor = Resizer.getFactor(py, image.getHeight(), scale);
                
                if (alpha) 
                    image = resizeBitmap(image, (int) (image.getWidth() * factor),
                        (int) (image.getHeight() * factor));
                else
                    image = resizeImage(image, (int) (image.getWidth() * factor),
                        (int) (image.getHeight() * factor));                
            }
            System.gc();
            sprite = new Sprite(image);
            System.gc();
            //seteamos el pixel de referencia justo en el medio de la imagen
            sprite.defineReferencePixel(image.getWidth() / 2, image.getHeight() / 2);
            
            
        } catch (Exception e){System.out.print("Error creando sprite "+nomImg+"\n");}

        return sprite;
    }
     static Image resizeImage(Image src, float factor) {
      return resizeImage(src, (int) (src.getWidth() * factor), (int) (src.getHeight() * factor));
      }//resize image

     static Image resizeImage(Image src,int width,int height) {
      int srcWidth = src.getWidth();
      int srcHeight = src.getHeight();
      Image tmp = Image.createImage(width, srcHeight);
      Graphics g = tmp.getGraphics();
      int ratio = (srcWidth << 16) / width;
      int pos = ratio/2;

      //Horizontal Resize
       g.setColor(0, 0, 0);
       g.fillRect(0,0,width,srcHeight);
       

      for (int x = 0; x < width; x++) {
          g.setClip(x, 0, 1, srcHeight);
          g.drawImage(src, x - (pos >> 16), 0, Graphics.LEFT | Graphics.TOP);
          pos += ratio;
      }

      Image resizedImage = Image.createImage(width, height);
      g = resizedImage.getGraphics();
      ratio = (srcHeight << 16) / height;
      pos = ratio/2;

      //Vertical resize
       g.setColor(0, 0, 0);
       g.fillRect(0,0,width,height);

      for (int y = 0; y < height; y++) {
          g.setClip(0, y, width, 1);
          g.drawImage(tmp, 0, y - (pos >> 16), Graphics.LEFT | Graphics.TOP);
          pos += ratio;
      }
      return resizedImage;

  }//resize image
     
     //Esta funcion toma o el ancho o el alto tanto de la pantalla como la imagen
    static float getFactor (int screenSize, int imageSize, int scale)
    {        
        return (float) ((float)(scale * screenSize)/(float)(100 * imageSize));        
    }

    private static int[] rescaleArray(int[] ini, int x, int y, int x2, int y2)
	{
		int out[] = new int[x2*y2];
		for (int yy = 0; yy < y2; yy++)
		{
			int dy = yy * y / y2;
			for (int xx = 0; xx < x2; xx++)
			{
				int dx = xx * x / x2;
				out[(x2 * yy) + xx] = ini[(x * dy) + dx];
			}
		}
		return out;
	}


	public static Image resizeBitmap(Image image, int width, int height)
	{
		// Note from DCC:
		// an int being 4 bytes is large enough for Alpha/Red/Green/Blue in an 8-bit plane...
		// my brain was fried for a little while here because I am used to larger plane sizes for each
		// of the color channels....
		//

		//Need an array (for RGB, with the size of original image)
		//
                //System.out.print("image.getWidth()*image.getHeight():" + image.getWidth()*image.getHeight() + "\n");
            
		int rgb[] = new int[image.getWidth()*image.getHeight()];
		//Get the RGB array of image into "rgb"
		//
		image.getRGB(rgb, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
		//Call to our function and obtain RGB2
		//
		int rgb2[] = rescaleArray(rgb, image.getWidth(), image.getHeight(), width, height);
		//Create an image with that RGB array
		//
		Image temp2 = Image.createRGBImage(rgb2, width, height, true);

		return temp2;
	}

}
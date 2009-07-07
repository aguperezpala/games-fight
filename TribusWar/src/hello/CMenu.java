/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hello;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

/**
 *
 * @author agustin
 */
public class CMenu implements Runnable {

    private static final int CIRCLE_SIZE = 3;
    private static final int EFFECT_VEL = 5;

    /* Public */
    public String strOp[] = null;   /* vamos a almacenar el texto u opciones */
    public boolean alive = true;
    public int op[] = null;         /* donde vamos almacenar la opcion */


    /* Private */
    private Graphics g = null;
    private GameCanvas gc = null;
    private int actualOp = 0;
    private Font font = null;
    private Image backImg = null;
    /* en opCoords vamos a almacenar las coordenadas de las opciones del menu,
     * en el formato TOP|LEFT, tambien vamos a usar esto para el efecto de
     * seleccion
     */
    private int opCoords[][] = null;
    private int color = 0;      /* color para pintar el "recuadro" */
    private boolean normalMenu = true;
    private int effectPorc = 0;     /* porcentaje del efecto completado */
    private int effectCoords[][] = {{0,0},{0,0}};


    

    /* Constructor: Le pasamos los graficos sobre los cuales vamos a graficar
     * el menu.
     * PARAMS:
     *      g = graphics
     *      gc = GameCanvas (para tamaños de la pantalla y teclas -keystates-)
     */
    public CMenu (Graphics g, GameCanvas gc)
    {
        if (g == null || gc == null) {
            System.out.print ("Menu g || gc  null\n");
            return;
        }

        this.g = g;
        this.gc = gc;
        this.font = Font.getDefaultFont();
        System.out.print("CREAMOS EL MENU\n");
    }

    /* Esta funcion es para cargar el menu de forma "normal", osea, tenemos opciones
     * y no es un simple texto de ayuda.
     * PARAMS:
     *       backImg = imagen de fondo
     */
    public void showNormalMenu (String backImg)
    {
        int strLen = 0, posY = 0, ySeparator = 0;

        this.normalMenu = true;
        /* vamos a cargar la imagen de fondo */
        try {
            this.backImg = Image.createImage(getClass().getResourceAsStream(backImg));
            this.backImg = Resizer.resizeImage(this.backImg, gc.getWidth(), gc.getHeight());
            /* limpiamos un poco */
            System.gc();
        } catch (Exception e) {System.out.print("Error cargando "+backImg+"\n");}

        /* vamos a posicionar todos los "strings" de las opciones */
        this.opCoords = null;
        System.gc();
        this.opCoords = new int[this.strOp.length][2];
        posY = font.getBaselinePosition() * this.strOp.length;
        /* obtenemos la separacion entre las opciones */
        ySeparator = (gc.getHeight() - posY)/(2*this.strOp.length);
        posY = (gc.getHeight() - ySeparator + posY)/2;

        for (int i = 0; i < this.strOp.length; i++) {
            strLen = font.stringWidth(this.strOp[i]);
            strLen = (gc.getWidth() - strLen) / 2;
            this.opCoords[i][0] = strLen;
            this.opCoords[i][1] = posY + i*(ySeparator + font.getBaselinePosition());
        }
        /* seteamos la posicion del efecto */
        this.actualizeEffect(0);
       
    }


    private void manageKeys (int keyCode)
    {
        if ((GameCanvas.DOWN_PRESSED & keyCode) != 0) {
            if (this.actualOp == 0)
                this.actualOp = this.strOp.length - 1;
            else
                this.actualOp--;
            /* actualizamos el efecto */
            this.actualizeEffect(this.actualOp);
        } else if ((GameCanvas.UP_PRESSED & keyCode) != 0) {
            this.actualOp = (actualOp + 1) % this.strOp.length;
            this.actualizeEffect(this.actualOp);
        } else if ((GameCanvas.FIRE_PRESSED & keyCode) != 0) {
            this.op[0] = this.actualOp;
            /* terminamos el menu ademas */
            this.cleanMenu();
            
        }

    }

    public void run ()
    {
        /* algunas inicializaciones */
        this.actualOp = 0;
        this.effectPorc = 0;
        this.alive = true;
        
        while (this.alive) {
            /* trabajamos la entrada */
            manageKeys (gc.getKeyStates());
            if (this.effectPorc < 100)
                this.effectPorc++;

            /* aumentamos el color */
            color = (color + 5) % 0x00FFFFFF;
            /* dibujamos */

            if (this.alive)
                this.paint();
            gc.flushGraphics();
            try {
                Thread.sleep(CMenu.EFFECT_VEL);
            } catch (InterruptedException e) {
                System.out.println(e.toString());
            }
        }

    }

    private void paint ()
    {
        g.setColor(0);
        g.fillRect(0, 0, gc.getWidth(), gc.getHeight());

        /* dibujamos la imagen QUE SIEMPRE existe */
        g.drawImage(backImg, 0, 0, Graphics.LEFT|Graphics.TOP);

        /* Seteamos el color de las letras */
        g.setColor(0x00FF00FF);

        /* verificamos si tenemos que dibujar de forma "normal" */
        if (normalMenu) {
            int factor;
            for (int i = 0; i < this.opCoords.length; i++) {
                g.drawString(strOp[i], opCoords[i][0], opCoords[i][1], 
                        Graphics.LEFT|Graphics.TOP);                
            }
            /* ahora dibujamos el "efecto" que va a ser 2 circulos, uno en cada
             * extremo de la palabra (superior izquierdo e inferior derecho) y
             * vamos a ir dibujando de a poco el tamaño de las lineas que van a
             * ir encerrando la opcion.
             */
            //g.setColor(color);
            g.setColor(0x000000FF);

            /* dibujamos primero los circulos */
            g.fillArc(effectCoords[0][0], effectCoords[0][1], CMenu.CIRCLE_SIZE,
                    CMenu.CIRCLE_SIZE, 0, 360);
            g.fillArc(effectCoords[1][0], effectCoords[1][1], CMenu.CIRCLE_SIZE,
                    CMenu.CIRCLE_SIZE, 0, 360);

            factor = (effectCoords[1][0] - effectCoords[0][0]) * effectPorc / 100;

            /* ahora debemos dibujar las 4 lineas, primero las horizontales */

            g.drawLine(effectCoords[0][0], effectCoords[0][1], effectCoords[0][0] +
                    factor, effectCoords[0][1]);
            g.drawLine(effectCoords[1][0], effectCoords[1][1], effectCoords[1][0] -
                    factor, effectCoords[1][1]);

            /* ahora las verticales */
            factor = (effectCoords[1][1] - effectCoords[0][1]) * effectPorc / 100;

            g.drawLine(effectCoords[0][0], effectCoords[0][1], effectCoords[0][0],
                    effectCoords[0][1] + factor);
            g.drawLine(effectCoords[1][0], effectCoords[1][1], effectCoords[1][0],
                    effectCoords[1][1] - factor);
        }



    }


    private void actualizeEffect (int p)
    {
        int strLen = 0;

        this.effectCoords[0][0] = this.opCoords[p][0] - 4*CMenu.CIRCLE_SIZE;
        this.effectCoords[0][1] = this.opCoords[p][1] - CMenu.CIRCLE_SIZE/2;
        strLen = font.stringWidth(strOp[p]);
        this.effectCoords[1][0] = this.opCoords[p][0] + strLen + 4*CMenu.CIRCLE_SIZE;
        this.effectCoords[1][1] = this.opCoords[p][1] + CMenu.CIRCLE_SIZE/2 +
                this.font.getBaselinePosition();
        this.effectPorc = 0;

    }

    private void cleanMenu ()
    {
        /* limpiamos todas las cosas que podemos llegar a haber usado */
        this.backImg = null;
        this.alive = false;
        this.effectCoords = null;
        this.strOp = null;
        this.opCoords = null;
        System.gc();
    }

}

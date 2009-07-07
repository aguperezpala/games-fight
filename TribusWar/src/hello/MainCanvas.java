
package hello;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author agustin
 */
class MainCanvas extends GameCanvas implements Runnable {
    private static final String menuOps[] = {"Iniciar","Ayuda","Salir"};


    private Graphics g;
    private boolean paused = false;
    private boolean alive = true;
    private Thread threadMenu = null;
    private Thread threadGame = null;
    private CMenu menu = null;
    private int menuResp[] = {0};

    public MainCanvas ()
    {
        super (true);   /* tema de suppreskeys */
        this.g = this.getGraphics();
    }

    
    public void run() 
    {
        while (this.alive) {
            /* creamos el menu */
            this.menu = new CMenu (g, this);
            this.menu.op = menuResp;
            this.menu.strOp = menuOps;
            menu.showNormalMenu("backImg.png");
            this.threadMenu = new Thread(menu);
            /* ahora esperamos que termine el menu y vemos que opcion nos devolvio */
            try {
                threadMenu.join();
            } catch (Exception e) {}
            System.out.print("Se selecciono la opcion: " + this.menuResp[0] + "\n");
        }
    }

}

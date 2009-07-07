
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


    private Graphics g;

    public MainCanvas ()
    {
        super (true);   /* tema de suppreskeys */
        this.g = this.getGraphics();
    }


    public void run() {

    }

}

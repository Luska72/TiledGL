package mweis.game;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

// this is required by JOGL's frame - I made it its own .java file to make it easy to read.
public class WindowHandler extends WindowAdapter {
	
	public WindowHandler(Game game){
		game.window.addWindowListener(this);
	}
	
	public void windowDestroyNotify(WindowEvent arg0) {
        System.exit(0);
    }
}

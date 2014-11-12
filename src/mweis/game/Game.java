package mweis.game;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import mweis.game.gfx.Screen;
import mweis.game.gfx.SpriteSheet;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

/*
 	DEBUG:
  	-Djogl.debug.DebugGL
	-Djogl.debug.TraceGL
 */

public class Game implements GLEventListener {
	
	public double t = 0.0; // time
	public final double dt = 0.01; // delta time (is fixed)
	
	public double currentTime = time_in_seconds(); // curr time
	public double accumulator = 0.0; // extra time from frames added here, if we have extra time which is smaller than a physics update, we store it here.
	private long lastTimer = System.currentTimeMillis(); // for counting frames and ticks
	
	private GLProfile glp; // to make gl work on all machines
	private GLCapabilities caps;
	GLWindow window; // referenced by InputHandler and WindowHandler
	
	private SpriteSheet spriteSheet;
	private Animator animator;
	public InputHandler input;
	private Screen screen;
	
	// updates and frames are just counters so we can calc FPS and UPS
	int updates = 0;
	int frames = 0;
	
	// Main class constructor, START HERE, this sets up window and animator (which is update loop)
	public Game() {
		glp = GLProfile.getDefault();
		caps = new GLCapabilities(glp);
		
		window = GLWindow.create(caps);
		
		window.setSize(Screen.WIDTH*Screen.SCALE, Screen.HEIGHT*Screen.SCALE);
		window.setVisible(true);
		window.setTitle("NEWT Window");
		//window.get
		new WindowHandler(this); // create a new window handler, attached to this window
		window.addGLEventListener(this); // add this class as an event listener. 
		//window.setFullscreen(true);
		
		// start loop | this calls display. Can be set to 60 frames if needed
		// animator makes a seperate thread for display
		animator = new Animator(window);
		animator.setRunAsFastAsPossible(true);
		
		animator.start(); // will call init once, and display as often as possibe
		
	}
	
	
	@Override
	public void display(GLAutoDrawable drawable) {
//		GL2 gl = drawable.getGL().getGL2(); // do I need this every time?
		GL2 gl = new DebugGL2(drawable.getGL().getGL2()); // get GL - jogl related

		
		double newTime = time_in_seconds();
		
		double frameTime = newTime - currentTime;
		if (frameTime > 0.25)
			frameTime = 0.25;
		
		currentTime = newTime;
		
		accumulator += frameTime;
				
		while (accumulator >= dt) {
			update();
			updates++;
			t += dt;
			
			accumulator -= dt;
		}
		
		
		render(gl);
		frames++;
		
		// for timing FPS and UPS
		if(System.currentTimeMillis() - lastTimer > 1000){ // 1 s is 1000 ms
			lastTimer += 1000;
			window.setTitle(updates+" ticks, "+frames+" frames");
			frames = 0;
			updates = 0;
		}
	}
	
	// toggles v-sync. pass gl and T/F for on/off.
	final static void toggleVSync(GL2 gl, boolean isOn) {
		if (isOn) {
			gl.setSwapInterval(1); // v-sync on
			System.out.println("v-sync is now on.");
		} else {
			gl.setSwapInterval(0); // v-sync off
			System.out.println("v-sync is now off.");
		}
	}
	
	// get time in seconds
	static double time_in_seconds() {
		return System.nanoTime()/1000000000D;
	}
	
	// called by animator when we dispose graphics
	@Override
	public void dispose(GLAutoDrawable drawable) {
		System.out.println("cleanup, remember to release shaders if they exist!");
        System.exit(0);
	}


	// init called by animator
	@Override
	public void init(GLAutoDrawable drawable) {
		System.out.println("init called");
		//
		GL2 gl = new DebugGL2(drawable.getGL().getGL2()); // get GL - jogl related
//		GL2 gl = drawable.getGL().getGL2();
		
		toggleVSync(gl, false); // turn v-sync off
		
		screen = new Screen(gl); // create the screen, all draw operations are handled by this class
		
		input = new InputHandler(this); // create the input handler - we can check all user input with this class
		
		// should be moved to a new class
		spriteSheet = new SpriteSheet(gl, "/ExampleEfficientTileset.png", ".png"); // new spritesheet class
		spriteSheet.load();
		spriteSheet.enable();
		
		getSpecs(drawable); // RawGL2ES2demo, we get specs of our machine.
	}
	
	// closes if the opengl version is out of date.
	public void closeIfOutOfDate(GL2 gl){
		String versionStr = gl.glGetString( GL2.GL_VERSION );
        versionStr = versionStr.substring(0, 4);
        float version = new Float( versionStr ).floatValue();
        boolean versionOK = ( version >= 1.59f ) ? true : false;
        if (versionOK){
        	System.out.println("GL version:"+versionStr+" is ok.");
        	return;
        }
        System.out.println("GL version:"+versionStr+" is out of date. Exiting..");
        System.exit(0);
	}
	
	// from RawGL2ES2demo
	public void getSpecs(GLAutoDrawable drawable) {
//		GL2 gl = new DebugGL2(drawable.getGL().getGL2());
		GL2 gl = drawable.getGL().getGL2();
		
		System.out.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.out.println("INIT GL IS: " + gl.getClass().getName());
        System.out.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
        System.out.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
        System.out.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));
        
        // test memory
        final int mb = 1024*1024;
        
        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();
         
        System.out.println("##### Heap utilization statistics [MB] #####");
         
        //Print used memory
        System.out.println("Used Memory:"
            + (runtime.totalMemory() - runtime.freeMemory()) / mb);
 
        //Print free memory
        System.out.println("Free Memory:"
            + runtime.freeMemory() / mb);
         
        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);
 
        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
	}
	
	
	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		
	}
	
	// this is the update function, it is called every dt seconds
	public synchronized void update(){		
//		level.update();
	}
	
	// called from display - as often as possible
	public synchronized void render(GL2 gl){
//		level.render();
		screen.render(0, 0, gl);
	}
	
	// the very start of the code, just creates a new game
	public static void main(String[] args){
		new Game();
	}
}

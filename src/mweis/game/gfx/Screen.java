package mweis.game.gfx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import mweis.game.Game;
import mweis.game.util.Vec2;

public class Screen {
	
	public final boolean DOES_SUPPORT_VBO;
	public static final int SCALE = 3; // scale of game - this is actually causing some problems for me..
	public static final int WIDTH = 320; // no need for scale, because window auto-scales
	public static final int HEIGHT = WIDTH / 16 * 9; // height is an aspect ratio to width
	public static final int TILESIZE = 16; // size of tiles in game - not of those on the spritesheet
	public static final int HORIZONTAL_SPRITE_SHEET_TILES = 15; // all sprite sheets should have the same number
	public static final int VERTICAL_SPRITE_SHEET_TILES = 23; // all sprite sheets should have the same number
	
	// can make pointers to variables in shader programs here.
	private int shaderProgram;
	private int vertex_shader_pointer;
	
	public Screen(GL2 gl){
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	    gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity(); // resets all translates and rotates
		gl.glOrtho(0, WIDTH, 0, HEIGHT, 0, 1); // left, right, bot, top, zNear, zFar
		DOES_SUPPORT_VBO = testVBOSupport(gl);
		if (!DOES_SUPPORT_VBO) System.err.println("This computer doesn't support VBOs!");
		
		// init shaders
//		try {
//			initShaders(gl);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private boolean testVBOSupport(GL2 gl){
		// Check if extension is available.
		boolean extensionOK = gl.isExtensionAvailable("GL_ARB_vertex_buffer_object");
	    
	    // Check for VBO functions.
	    boolean functionsOK = 
	    		gl.isFunctionAvailable("glGenBuffersARB") &&
	    		gl.isFunctionAvailable("glBindBufferARB") &&
	    		gl.isFunctionAvailable("glBufferDataARB") &&
	    		gl.isFunctionAvailable("glDeleteBuffersARB");      
	
	    if( ! extensionOK || ! functionsOK ){
	    	// VBO not supported.
	    	return false;
	    }
	    return true;
	}
	
	public void render(float x, float y, GL2 gl){
		
		if (DOES_SUPPORT_VBO){
			// vbo draw
		} else {
			// immediate draw
		}
		
		// set shader translate
//		gl.glUseProgram(shaderProgram); // use that shader. WARNING!!!! It might be possible to call this, render everything, then turn it off. Rather than once per operation.
//		gl.glUniform2f(vertex_shader_pointer, x, y); // set shader var to whatever we are drawing the model at.
		
		final float txi = 1.0f / HORIZONTAL_SPRITE_SHEET_TILES;
		final float tyi = 1.0f / VERTICAL_SPRITE_SHEET_TILES;
		final float tileX = 5 * txi;
		final float tileY = 5 * tyi;
		
		final float WIDTH = this.WIDTH;
		final float HEIGHT = this.HEIGHT;
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex2f(0.0f, 0.0f);
			
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex2f(0.0f + WIDTH, 0.0f);
			
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex2f(0.0f + WIDTH, 0.0f + HEIGHT);
			
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex2f(0.0f,0.0f + HEIGHT);
		gl.glEnd();
		
//		gl.glUseProgram(0); // turn off program.
	}
	
	
	// shader methods:
	
	private void initShaders(GL2 gl) throws IOException {
		// the position shader
		int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		String vsrc = readFromStream(Game.class.getResourceAsStream("/Vertex.glsl"));
		gl.glShaderSource(v, 1, new String[] {vsrc}, (int[])null, 0);
		gl.glCompileShader(v);
		
		// the lighting shader
//		int f = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
//		String fsrc = readFromStream(Game.class.getResourceAsStream("/Fragment.glsl"));
//		gl.glShaderSource(f, 1, new String[] {fsrc}, (int[])null, 0);
//		gl.glCompileShader(f);
		
		
		shaderProgram = gl.glCreateProgram(); // all shaders attached to the program
		gl.glAttachShader(shaderProgram, v);
//		gl.glAttachShader(shaderProgram, f);
		
		gl.glLinkProgram(shaderProgram);
		gl.glValidateProgram(shaderProgram);
		gl.glUseProgram(shaderProgram);
		
		// make pointers to the variables inside the program
		vertex_shader_pointer = gl.glGetUniformLocation(shaderProgram, "newPosition");
		
		// set the vars, which we access with its pointer
		gl.glUniform2f(vertex_shader_pointer, 0f, 0f); // init
		gl.glUseProgram(0);
		
	}
	
	private static String readFromStream(InputStream ins) throws IOException {
        if (ins == null) {
            throw new IOException("Could not read from stream.");
        }
        StringBuffer buffer = new StringBuffer();
        Scanner scanner = new Scanner(ins);
        try {
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine() + "\n");
            }
        } finally {
            scanner.close();
        }

        return buffer.toString();
    }
}






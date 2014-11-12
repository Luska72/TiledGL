package mweis.game.gfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

// JOGL has a texture class that can be used to easily make opengl textures, this class makes their class a little easier to use.
// http://jogamp.org/deployment/jogamp-next/javadoc/jogl/javadoc/com/jogamp/opengl/util/texture/Texture.html
// a render buffer should only be used when storing something that isn't an image format. Such as aa depth or stencil buffer.
// what is a FBO? http://stackoverflow.com/questions/2213030/whats-the-concept-of-and-differences-between-framebuffer-and-renderbuffer-in explains it well. (2nd answer)

/*
 * renderbuffer / framebuffer:
 * 	offscreen
 * 	drawing to this is MUCH faster than drawing to a texture
 * 	use if I'm going to draw to a texture/image
 * 
 * texture:
 * 	offscreen or onscreen
 * 	reading from this is MUCH faster than reading from a renderbuffer
 * 
 * tldr; if im drawing TO an object, use renderbuffer. if im just rendering an image, use texture.
 */
public class SpriteSheet {
		
	private GL2 gl;
	private Texture texture; // the actual image from file
	private String path;
	private String type;
	private boolean isLoaded = false;
	private boolean isEnabled = false;
	
	public SpriteSheet(GL2 gl, String path, String type) {
		
		if (!type.equals(".png")){
			System.out.println("WARNING: .png should be used for this project");
		}
				
		this.gl = gl;
		this.path = path;
		this.type = type;
		
	}
	
	// enable the spritesheet
	public void enable() {
				
		// check if SpriteSheet is loaded
		if (!isLoaded){
			load();
		}
		
		texture.enable(gl);
		texture.bind(gl); // might not even need bind
		isEnabled = true;
	}
	
	// disable the spritesheet
	public void disable() {
		texture.disable(gl);
		isEnabled = false;
	}
	
	// load the spritesheet
	public void load() {
		texture = createTexture();
		isLoaded = true;
	}
	
	// unload the spritesheet
	public void unload() {
		texture.destroy(gl);
		texture = null;
		isLoaded = false;
		isEnabled = false;
	}
	
	public boolean isEnabled(){
		if (isEnabled)
			return true;
		return false;
	}
	
	// creates a texture, sets a few params
	private Texture createTexture(){
		
		Texture t = null;
		
		try {
			t = TextureIO.newTexture(this.getClass().getResource(path), false, type);
			t.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			t.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
			t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
			t.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GLException e) {
			e.printStackTrace();
		}
		
		return t;
	}
	
}

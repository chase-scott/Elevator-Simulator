package state;

/**
 * Lamp class
 * 
 * @author Chase Scott - 101092194
 */
public class Lamp {
	
	private boolean lit;
	
	public Lamp() {
		this.lit = false;
	}
	
	public void switchState() {
		this.lit = !lit;
	}
	
	public boolean isLit() {return this.lit;}
	

}

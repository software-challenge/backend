package sc.plugin2016.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;
import sc.plugin2016.Field;
import sc.plugin2016.FieldType;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.gui.renderer.FrameRenderer;

/**
 * 
 * 
 * @author soeren
 * 
 */
public class GuiField extends PrimitiveBase{
	
	// Fields
	private float x, y;
	private Field field;
	private float width;
	
	private boolean highlighted = false;

	public GuiField(FrameRenderer parent, float startX, float startY, float width, int fieldX, int fieldY) {
		super(parent);
		setX(startX);
		setY(startY);
		setWidth(width);
		if(parent.currentGameState != null && parent.currentGameState.getBoard() != null) {
		  this.setField(parent.currentGameState.getBoard().getField(fieldX, fieldY));
		}
	}

	public void update(Field field) {
		
		this.setField(field);
			
	}

	public void draw() {
		parent.pushStyle();
		if(getField() != null) {
    	if(highlighted){
    		parent.fill(GuiConstants.colorHighLighted);
    	} else if(getField().getType() == FieldType.SWAMP){
    		parent.fill(GuiConstants.colorSwampFields);
    	} else if(getField().getOwner() == PlayerColor.RED) {
    	  parent.fill(GuiConstants.colorRed);
    	} else if(getField().getOwner() == PlayerColor.BLUE) {
        parent.fill(GuiConstants.colorBlue);
    	} else if(getField().getType() == FieldType.RED){
    	  parent.fill(GuiConstants.colorLightRed);
    	} else if(getField().getType() == FieldType.BLUE){
    	  parent.fill(GuiConstants.colorLightBlue);
    	} else {
    	  parent.fill(GuiConstants.colorBlack);
    	}
    
    	parent.pushMatrix();
    	parent.translate(getX(), getY());
    	parent.ellipse(0, 0, getWidth(), getWidth());
    	
    	parent.fill(0);
    	parent.textFont(GuiConstants.fonts[0]);
    	parent.textSize(GuiConstants.fontSizes[0]);

      parent.popMatrix();
		}
		parent.popStyle();

	}
	
	public void resize(float startX, float startY, float width){
		setX(startX);
		setY(startY);
		setWidth(width);
		
	}

	public void setWidth(float width) {
    this.width = width;
    
  }

  public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}


	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

  public float getWidth() {
    return width;
  }

  public Field getField() {
    return field;
  }

  private void setField(Field field) {
    this.field = field;
  }

}

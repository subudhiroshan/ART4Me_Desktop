package com.data;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Author: Roshan C Subudhi
 * USC, Columbia
 * Ph: 803-743-2899
 * 
 * for EHG, Boston, MA
 * 
 * This class places elements in a Vertical Flow Layout.
 *  
 */
public class VerticalFlowLayout implements LayoutManager, java.io.Serializable {

	private boolean maximizeOtherDimension = false;
	public void setMaximizeOtherDimension(boolean max)
	{
		maximizeOtherDimension = max;
	}
	public boolean isMaximizeOtherDimension()
	{
		return maximizeOtherDimension;
	}
   
    public static final int TOP 	= 0;

    
    public static final int CENTER 	= 1;

    public static final int BOTTOM 	= 2;

    public static final int LEADING	= 3;

    public static final int TRAILING = 4;

    int align;
    int newAlign;       // This is the one we actually use
    int hgap;
    int vgap;
    private static final long serialVersionUID = -7262534875583282631L;
    
    public VerticalFlowLayout() {
	this(CENTER, 5, 5);
    }
    
    public VerticalFlowLayout(int align) {
	this(align, 5, 5);
    }

    public VerticalFlowLayout(int align, int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
        setAlignment(align);
    }

    public int getAlignment() {
	return newAlign;
    }

    public void setAlignment(int align) {
	this.newAlign = align;

        switch (align) {
	case LEADING:
            this.align = TOP;
	    break;
	case TRAILING:
            this.align = BOTTOM;
	    break;
        default:
            this.align = align;
	    break;
        }
    }

    public int getHgap() {
	return hgap;
    }

    public void setHgap(int hgap) {
	this.hgap = hgap;
    }

    public int getVgap() {
	return vgap;
    }

    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
	Dimension dim = new Dimension(0, 0);
	int nmembers = target.getComponentCount();
        boolean firstVisibleComponent = true;

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.isVisible()) {
		Dimension d = m.getPreferredSize();
		dim.width = Math.max(dim.width, d.width);
                if (firstVisibleComponent) {
                    firstVisibleComponent = false;
                } else {
                    dim.height += vgap;
                }
		dim.height += d.height;
	    }
	}

	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right + hgap*2;
	dim.height += insets.top + insets.bottom + vgap*2;
	return dim;
      }
    }

    public Dimension minimumLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
	Dimension dim = new Dimension(0, 0);
	int nmembers = target.getComponentCount();

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.isVisible()) {
		Dimension d = m.getMinimumSize();
		dim.width = Math.max(dim.width, d.width);
		if (i > 0) {
		    dim.height += vgap;
		}
		dim.height += d.height;
	    }
	}
	Insets insets = target.getInsets();
	dim.width += insets.left + insets.right + hgap*2;
	dim.height += insets.top + insets.bottom + vgap*2;
	return dim;
      }
    }

    private void moveComponents(Container target, int x, int y, int width, int height,
                                int colStart, int colEnd, boolean ltr) {
      synchronized (target.getTreeLock()) {
	switch (newAlign) {
	case TOP:
	    y += ltr ? 0 : height;
	    break;
	case CENTER:
	    y += height / 2;
	    break;
	case BOTTOM:
	    y += ltr ? height : 0;
	    break;
	case LEADING:
	    break;
	case TRAILING:
	    y += height;
	    break;
	}
	for (int i = colStart ; i < colEnd ; i++) {
	    Component m = target.getComponent(i);
	    if (m.isVisible()) {
	        if (ltr) {
        	    m.setLocation(x + (width - m.getWidth()) / 2, y);
	        } else {
	            m.setLocation(x + (width - m.getWidth()) / 2, target.getHeight() - y - m.getHeight());
                }
                y += m.getHeight() + vgap;
	    }
	}
      }
    }

    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
	Insets insets = target.getInsets();
	int maxwidth = target.getWidth() - (insets.left + insets.right + hgap*2);
	int maxheight = target.getHeight() - (insets.top + insets.bottom + vgap*2);
	int nmembers = target.getComponentCount();
	int x = insets.left + hgap, y = 0;
	int colw = 0, start = 0;

        boolean ltr = target.getComponentOrientation().isLeftToRight();

	for (int i = 0 ; i < nmembers ; i++) {
	    Component m = target.getComponent(i);
	    if (m.isVisible()) {
		Dimension d = m.getPreferredSize();
		if (maximizeOtherDimension) {
			d.width = maxwidth;
		}
		m.setSize(d.width, d.height);

		if ((y == 0) || ((y + d.height) <= maxheight)) {
		    if (y > 0) {
			y += vgap;
		    }
		    y += d.height;
		    colw = Math.max(colw, d.width);
		} else {
		    moveComponents(target, insets.left + hgap, y, maxheight - x, colw, start, i, ltr);
		    moveComponents(target, x, insets.top + vgap, colw, maxheight - y, start, i, ltr);
		    y = d.height;
		    x += hgap + colw;
		    colw = d.width;
		    start = i;
		}
	    }
	}
	moveComponents(target, x, insets.top + vgap, colw, maxheight - y, start, nmembers, ltr);
      }
    }

    private static final int currentSerialVersion = 1;
    private int serialVersionOnStream = currentSerialVersion;
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();

        if (serialVersionOnStream < 1) {
            setAlignment(this.align);
        }
        serialVersionOnStream = currentSerialVersion;
    }

    public String toString() {
	String str = "";
	switch (align) {
	  case TOP:        str = ",align=top"; break;
	  case CENTER:      str = ",align=center"; break;
	  case BOTTOM:       str = ",align=bottom"; break;
	  case LEADING:     str = ",align=leading"; break;
	  case TRAILING:    str = ",align=trailing"; break;
	}
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
    }
}

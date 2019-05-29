package me.onenrico.ecore.guiapi;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuClickEvent extends Event implements Cancellable{
    private boolean isCancelled = false;

    private GUIView guiview;
    private MenuItem menuitem;
    private Player clicker;
    
    public MenuClickEvent(GUIView guiview, MenuItem menuitem, Player clicker) {
    	this.guiview = guiview;
    	this.menuitem = menuitem;
    	this.clicker = clicker;
    }
    
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
	public GUIView getGuiview() {
		return guiview;
	}
	public MenuItem getMenuitem() {
		return menuitem;
	}

	public Player getClicker() {
		return clicker;
	}
    
    
}

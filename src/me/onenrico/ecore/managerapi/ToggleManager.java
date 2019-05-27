
package me.onenrico.ecore.managerapi;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class ToggleManager {
	public HashMap<UUID, Long> toggle_data;

	public ToggleManager() {
		toggle_data = new HashMap<>();
	}
	
	public UUID getRandom() {
		Random r = new Random();
		if(toggle_data.size() < 1) {
			return null;
		}
		int ran = r.nextInt(toggle_data.size());
		int index = 0;
		for(UUID u : toggle_data.keySet()) {
			if(index == ran) {
				return u;
			}
			index++;
		}
		return null;
	}
	
	public void add(final UUID p) {
		toggle_data.put(p,0l);
	}
	public void add(final UUID p,final int duration) {
		toggle_data.put(p,System.currentTimeMillis() + (duration * 1000l));
	}

	public void remove(final UUID p) {
		toggle_data.remove(p);
	}

	public boolean isToggle(final UUID p) {
		if(!toggle_data.containsKey(p)) {
			return false;
		}
		long duration = toggle_data.get(p);
		if(duration == 0) {
			return true;
		}
		duration = duration - System.currentTimeMillis();
		if(duration > 0) {
			return true;
		}else {
			toggle_data.remove(p);
			return false;
		}
	}
}

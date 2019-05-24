package me.onenrico.ecore.configapi;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.onenrico.ecore.guiapi.GUIMenu;
import me.onenrico.ecore.itemapi.ItemBuilder;
import me.onenrico.ecore.managerapi.PlaceholderManager;

public class GUIConfig extends EYaml {
//      GUI.yml FORMAT
//
//		MenuName:
//		  Title: "&lMenu Name &8▚&1{page}&8/&1{maxpage}&8▞"
//		  BorderItem:
//		    Material: BLACK_STAINED_GLASS_PANE
//		    Displayname: "&r"
//		    Description:
//		    - ""
//		    Slot: -1
	public PlaceholderManager pm;
	public String locale;
	public List<GUIMenu> loadedGUI = new ArrayList<>();

	public GUIConfig(Plugin handler, String path, String locale) {
		super(handler, path.replace("gui", "gui_" + locale.toUpperCase()));
		this.locale = locale;
		setup();
	}

	@Override
	public void setup() {
		loadedGUI.clear();
		Locales locales = ConfigModule.request(handler).getLocaleConfig();
		pm = locales.pm;
		for(String gui : getSection("").getKeys(false)) {
			GUIMenu gm = new GUIMenu(handler, gui, getStr(gui+"."+"Title",gui+" Title Not Set !"), 6);
			if(getSection(gui) == null) {
				continue;
			}
			for(String item : getSection(gui).getKeys(false)) {
				if(item.equalsIgnoreCase("title"))continue;
				String pref = gui+"."+item+".";
				Material material = ItemBuilder.getMaterial(getStr(pref+"Material", "TORCH"));
				String displayname = getStr(pref+"Displayname",item+" Displayname not set...");
				List<String> description = getStrList(pref+"Description",new ArrayList<>());
				int slot = getInt(pref+"Slot");
				ItemStack is = ItemBuilder.createItem(material, displayname, description);
				gm.addConfigItems(item, is, slot);
			}
			loadedGUI.add(gm);
		}
	}
	
	public GUIMenu request(String gui) {
		for(GUIMenu gm : loadedGUI) {
			if(gm.getAlias().equalsIgnoreCase(gui)) {
				return gm;
			}
		}
		return null;
	}
}

package me.onenrico.ecore.managerapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.inventory.ItemStack;

import me.onenrico.ecore.itemapi.ItemBuilder;
import me.onenrico.ecore.utilsapi.MathUT;
import me.onenrico.ecore.utilsapi.ParserUT;
import me.onenrico.ecore.utilsapi.StringUT;

public class PlaceholderManager {
	final static String start = "{";
	final static String end = "}";

	private HashMap<String, Object> values = new HashMap<>();
	private StrSubstitutor processor;

	private StrSubstitutor bridgeprocessor;
	private StrLookup bridge;
	private HashMap<String, StrLookup> placeholders = new HashMap<>();

	@SuppressWarnings("unchecked")
	public PlaceholderManager(PlaceholderManager old) {
		setValues((HashMap<String, Object>) old.getValues().clone());
		setPlaceholders((HashMap<String, StrLookup>) old.getPlaceholders().clone());
		setProcessor(new StrSubstitutor(values, start, end, '!'));
		bridge = new StrLookup() {
			@Override
			public String lookup(String msg) {
				StrLookup lookup = placeholders.getOrDefault(msg, null);
				if (lookup != null) {
					return lookup.lookup(msg);
				}
				return "!{" + msg + "}";
			}
		};
		setBridgeprocessor(new StrSubstitutor(bridge, start, end, '!'));
	}

	public PlaceholderManager() {
		setProcessor(new StrSubstitutor(values, start, end, '!'));
		bridge = new StrLookup() {
			@Override
			public String lookup(String msg) {
				StrLookup lookup = placeholders.getOrDefault(msg, null);
				if (lookup != null) {
					return lookup.lookup(msg);
				}
				return "!{" + msg + "}";
			}
		};
		setBridgeprocessor(new StrSubstitutor(bridge, start, end, '!'));
		return;
	}

	public String get(String placeholder) {
		return "" + values.getOrDefault(placeholder, "");
	}

	public void add(String placeholder, Object msg) {
		values.put(placeholder, msg);
		setProcessor(new StrSubstitutor(values, start, end, '!'));
	}

	public void add(String placeholder, StrLookup msg) {
		placeholders.put(placeholder, msg);
	}

	public void remove(String placeholder) {
		values.remove(placeholder);
		setProcessor(new StrSubstitutor(values, start, end, '!'));
		placeholders.remove(placeholder);
	}

	public PlaceholderManager(HashMap<String, Object> values) {
		this.values = values;
		processor = new StrSubstitutor(values, start, end, '!');
	}

	public static PlaceholderManager custom(HashMap<String, StrLookup> values2) {
		PlaceholderManager pm = new PlaceholderManager();
		pm.setPlaceholders(values2);
		return pm;
	}

	public String process(String text) {
		if (text == null) {
			return "";
		}
		String result = processor.replace(text);
		if (bridgeprocessor != null) {
			result = bridgeprocessor.replace(result);
		}
		
		if(result.contains("<bar>")) {
			result = getBarFromString(result);
		}
		return StringUT.t(result);
	}
	
	public static String getBarFromString(String bar) {
		while (bar.contains("<bar>") && bar.contains("</bar>")) {
			final HashMap<String, String> value = ParserUT.extract("bar", bar).get(0);
			final int totalbar = MathUT.strInt(value.getOrDefault("bars", "20"));
			final String symbol = value.getOrDefault("symbol", "|");
			final String filledcolor = value.getOrDefault("fill", "&a&l");
			final String unfilledcolor = value.getOrDefault("empty", "&f&l");
			final double v = MathUT.safe(value.getOrDefault("value", "0"));
			final double maxvalue = MathUT.safe(value.getOrDefault("maxvalue", "0"));
			bar = bar.replace(bar.substring(bar.indexOf("<bar>"), bar.indexOf("</bar>") + "</bar>".length()),
					getBar(totalbar, v, maxvalue, symbol, false, filledcolor, unfilledcolor));
		}
		return bar;
	}
	
	public static String getBar(int totalbar, double waiting, double maxvalue, String symbol, boolean reverse, String fc, String ufc) {
		String bar = symbol;
		
		bar = StringUtils.repeat(bar, totalbar);

		double persentase = MathUT.getPersentase(waiting, maxvalue);
		
		if(reverse)persentase = 100 - persentase;
		
		int value = (int) MathUT.getRealvalue(totalbar, persentase);
		bar = bar.substring(0, value) + ufc + bar.substring(value, bar.length());

		
		String result = fc+"{bar}&r";
		result = result.replace("{bar}", bar);
		return result;
	}
	
	public List<String> process(List<String> text) {
		if (text == null) {
			return new ArrayList<>();
		}
		for (int i = 0; i < text.size(); i++) {
			text.set(i, process(text.get(i)));
		}
		return text;
	}

	public ItemStack process(ItemStack item) {
		ItemBuilder.changeDisplayName(item, process(ItemBuilder.getName(item)));
		ItemBuilder.changeLore(item, process(ItemBuilder.getLore(item)));
		return item;
	}

	public HashMap<String, Object> getValues() {
		return values;
	}

	public void setValues(HashMap<String, Object> values) {
		this.values = values;
	}

	public StrSubstitutor getProcessor() {
		return processor;
	}

	public void setProcessor(StrSubstitutor processor) {
		this.processor = processor;
	}

	public StrSubstitutor getBridgeprocessor() {
		return bridgeprocessor;
	}

	public void setBridgeprocessor(StrSubstitutor bridgeprocessor) {
		this.bridgeprocessor = bridgeprocessor;
	}

	public StrLookup getBridge() {
		return bridge;
	}

	public void setBridge(StrLookup bridge) {
		this.bridge = bridge;
	}

	public HashMap<String, StrLookup> getPlaceholders() {
		return placeholders;
	}

	public void setPlaceholders(HashMap<String, StrLookup> placeholders) {
		this.placeholders = placeholders;
	}

}

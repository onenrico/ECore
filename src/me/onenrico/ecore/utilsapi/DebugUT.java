package me.onenrico.ecore.utilsapi;

import me.onenrico.ecore.messageapi.MessageUT;

public class DebugUT {
	private long start;
	private long lasthit;

	public DebugUT() {
		start = System.currentTimeMillis();
		lasthit = start;
	}

	public void hit(String msg) {
		MessageUT.cmsg(msg + "> After: " + (lasthit - start) + "ms");
		lasthit = System.currentTimeMillis();
		MessageUT.cmsg(msg + "> Total: " + (System.currentTimeMillis() - start) + "ms");
	}
}

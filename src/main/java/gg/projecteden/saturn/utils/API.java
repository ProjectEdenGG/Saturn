package gg.projecteden.saturn.utils;

import gg.projecteden.api.common.EdenAPI;
import gg.projecteden.api.common.utils.Env;

public class API extends EdenAPI {

	public API() {
		instance = this;
	}

	@Override
	public Env getEnv() {
		return Env.TEST;
	}

	@Override
	public void shutdown() {}

}

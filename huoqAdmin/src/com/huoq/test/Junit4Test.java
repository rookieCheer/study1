package com.huoq.test;

import org.junit.Test;

public class Junit4Test extends Junit4Base {

	@Override
	String[] getOtherConfigs() {
		return new String[] { applicationContextFile };
	}

	@Test
	public void test() {
	}

}

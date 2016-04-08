package com.bosch.upa.uhu.pipe.core;

import java.util.Random;

public final class FakeServiceRegistration {
	
	private FakeServiceRegistration() {
		// intentional private constructor to prevent instantiation of this utility class
	}

	public static String generateUniqueServiceID(){
		Random random = new Random();
		return Base62Random(random);
	}
	
	private static String Base62Random(Random random) {
		long randNum = random.nextLong();
		return Base62ToString(Math.abs(randNum));
	}

	private static String Base62ToString(long value) {
		long x = value % 62;
		long y = value / 62;
		if (y > 0) {
			return Base62ToString(y) + String.valueOf(ValToChar(x));
		}
		else {
			return String.valueOf(ValToChar(x));
		}
	}

	private static char ValToChar(long value) {
		if (value > 9) {
			int ascii = (65 + (int)value - 10);
			if (ascii > 90) {
				ascii += 6;
			}
			return (char) ascii;
		} 
		else {
			return Long.toString(value).charAt(0);
		}
	}
}

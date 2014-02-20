package com.github.davidmoten.cglib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CglibUtilTest {

	static class A {
		private final B b;

		public A(B b) {
			this.b = b;
		}

		public B b() {
			return b;
		};
	}

	static class B {
		C c;

		public B(C c) {
			super();
			this.c = c;
		}

		public C c() {
			return c;
		}
	}

	static class C {
		public String value() {
			return "boo";
		}
	}

	@Test
	public void testSafeInvoker() throws Exception {
		A a = new A(new B(new C()));
		assertEquals("boo", CglibUtil.safeInvoker(a).b().c());
	}
}

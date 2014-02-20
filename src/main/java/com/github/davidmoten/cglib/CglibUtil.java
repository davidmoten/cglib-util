package com.github.davidmoten.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibUtil {

	private static final ThreadLocal<Boolean> isNull = new ThreadLocal<Boolean>();

	public static <T> T safeInvoker(T a) {
		if (a == null)
			throw new NullPointerException();
		return nonNullEnhance(a, (Class<? super T>) a.getClass());
	}

	private static <T> T nonNullEnhance(final T value, Class<? super T> cls) {
		System.out.println("superclass=" + cls);
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(cls);
		MethodInterceptor callback = new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				Object result = proxy.invokeSuper(value, args);
				if (result == null)
					return nullEnhance(method.getReturnType());
				else
					return nonNullEnhance(result,
							(Class<Object>) method.getReturnType());
			}
		};
		enhancer.setCallback(callback);
		return (T) enhancer.create();
	}

	private static <T> T nullEnhance(Class<T> cls) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(cls);
		MethodInterceptor callback = new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args,
					MethodProxy proxy) throws Throwable {
				isNull.set(true);
				return nullEnhance(method.getReturnType());
			}
		};
		return (T) enhancer.create();
	}

	public static <T> boolean isNull(T t) {
		isNull.set(false);
		t.hashCode();
		return isNull.get();
	}

	// Enhancer enhancer = new Enhancer();
	// enhancer.setNamingPolicy(new IndexedNamingPolicy());
	// enhancer.setCallbackType(NoOp.class);
	// enhancer.setUseCache(false);
	// enhancer.setStrategy(new DefaultGeneratorStrategy() {
	// @Override
	// protected ClassGenerator transform(ClassGenerator cg) throws Exception {
	// return new TransformingClassGenerator(cg, new
	// DefaultConstructorEmitter(key));
	// }
	// });
	//
	// enhancer.setSuperclass(clazz);
	// return enhancer.createClass();
	//
	// private class DefaultConstructorEmitter extends ClassEmitterTransformer {
	// private final Signature CALL_SIGNATURE =
	// TypeUtils.parseSignature("void someMethod(Object)");
	//
	// private final String parametersKey;
	//
	// public DefaultConstructorEmitter(final String key) {
	// parametersKey = key;
	// }
	//
	// @Override
	// public CodeEmitter begin_method(int access, Signature sig, Type[]
	// exceptions) {
	// final CodeEmitter emitter = super.begin_method(access, sig, exceptions);
	// if (sig.getName().equals(Constants.CONSTRUCTOR_NAME)) {
	// return new CodeEmitter(emitter) {
	// @Override
	// public void visitInsn(int arg0) {
	// if (arg0 == Opcodes.RETURN) {
	// Type classType = ...
	// emitter.load_this();
	// emitter.push(parametersKey);
	// emitter.invoke_static(classType, CALL_SIGNATURE);
	// }
	// super.visitInsn(arg0);
	// }
	// };
	// }
	//
	// return emitter;
	//
	// }
}
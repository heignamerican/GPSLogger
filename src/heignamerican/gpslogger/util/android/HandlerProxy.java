package heignamerican.gpslogger.util.android;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.os.Handler;

public class HandlerProxy {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface HasUIMethod {
	}

	public static <T> T getUIHandlerProxy(final T aObject) {
		final Handler tUIHandler = new Handler();

		@SuppressWarnings("unchecked")
		T tNewProxyInstance = (T) Proxy.newProxyInstance(aObject.getClass().getClassLoader(), aObject.getClass().getInterfaces(), new InvocationHandler() {
			@Override
			public Object invoke(Object aProxy, final Method aMethod, final Object[] aArgs) throws Throwable {
				if (aObject.getClass().getAnnotation(HasUIMethod.class) != null && aMethod.getReturnType().equals(void.class)) {
					tUIHandler.post(new Runnable() {
						@Override
						public void run() {
							try {
								aMethod.invoke(aObject, aArgs);
							} catch (Exception aCause) {
								throw new RuntimeException(aCause);
							}
						}
					});
					return null;
				}
				return aMethod.invoke(aObject, aArgs);
			}
		});
		return tNewProxyInstance;
	}

	public static class NullableContainer<T> {
		private T mContent;
		private final T mNullContent;

		public NullableContainer(Class<T> aClass) {
			@SuppressWarnings("unchecked")
			T tProxyInstance = (T) Proxy.newProxyInstance(aClass.getClassLoader(), aClass.getInterfaces(), new InvocationHandler() {
				@Override
				public Object invoke(Object aProxy, Method aMethod, Object[] aArgs) throws Throwable {
					return null;
				}
			});
			mNullContent = tProxyInstance;
			mContent = mNullContent;
		}

		public void set(T aContent) {
			mContent = aContent;
		}

		public T get() {
			return mContent;
		}

		public void unset() {
			mContent = mNullContent;
		}
	}
}

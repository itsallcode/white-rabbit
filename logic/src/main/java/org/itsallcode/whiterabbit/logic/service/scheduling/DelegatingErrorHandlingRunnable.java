package org.itsallcode.whiterabbit.logic.service.scheduling;

import java.lang.reflect.UndeclaredThrowableException;

public class DelegatingErrorHandlingRunnable implements Runnable {

	private final Runnable delegate;

	private final ErrorHandler errorHandler;

	public DelegatingErrorHandlingRunnable(Runnable delegate, ErrorHandler errorHandler) {
		this.delegate = delegate;
		this.errorHandler = errorHandler;
	}

	@Override
	public void run() {
		try {
			this.delegate.run();
		} catch (final UndeclaredThrowableException ex) {
			this.errorHandler.handleError(ex.getUndeclaredThrowable());
		} catch (final Exception ex) {
			this.errorHandler.handleError(ex);
		}
	}

	@Override
	public String toString() {
		return "DelegatingErrorHandlingRunnable for " + this.delegate;
	}
}

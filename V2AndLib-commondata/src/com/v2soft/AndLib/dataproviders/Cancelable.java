package com.v2soft.AndLib.dataproviders;

/**
 * Cancelable interface.
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public interface Cancelable {
	public void cancel();
	public boolean canBeCanceled();
	public boolean isCanceled();
}

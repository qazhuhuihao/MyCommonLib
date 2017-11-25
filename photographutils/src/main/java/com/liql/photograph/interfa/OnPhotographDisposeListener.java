package com.liql.photograph.interfa;

/**
 * 路径转换其它对象接口
 * 
 * @author Liqi
 * 
 * @param <T>
 */
public interface OnPhotographDisposeListener<T> {
	/**
	 * 把路径转换成其它对象
	 * 
	 * @param path 路径
	 * @return T
	 */
    T getPhotographDisposeData(String path);
}

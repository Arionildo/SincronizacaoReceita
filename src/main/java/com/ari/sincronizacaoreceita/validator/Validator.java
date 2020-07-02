package com.ari.sincronizacaoreceita.validator;

public interface Validator<R,T> {
	R accept(T t);
}

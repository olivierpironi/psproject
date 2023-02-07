package com.fourcamp.sbanco.infra.exceptions;

public record DetalhaBOGPException(String msg) {
	public DetalhaBOGPException(BOGPExceptions e) {
		this(e.getMessage());
	}
}
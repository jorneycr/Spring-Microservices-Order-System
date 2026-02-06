package com.ordersystem.common.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, String id) {
        super("RESOURCE_NOT_FOUND", String.format("%s not found with id: %s", resource, id));
    }
}

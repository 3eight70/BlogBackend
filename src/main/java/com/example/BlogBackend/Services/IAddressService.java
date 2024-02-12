package com.example.BlogBackend.Services;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IAddressService {
    ResponseEntity<?> searchAddress(Long parentObjectId, String query);
    ResponseEntity<?> getAddressChain(UUID objectGuid);
}

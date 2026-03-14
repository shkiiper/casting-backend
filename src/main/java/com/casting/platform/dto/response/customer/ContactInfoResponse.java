package com.casting.platform.dto.response.customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContactInfoResponse {
    private String phone;
    private String email;
    private String whatsapp;
    private String telegram;
}

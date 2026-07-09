package com.pratik.walletsystem.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequest {

    @NotBlank(message = "Sender email is required")
    @Email(message = "Invalid sender email")
    private String senderEmail;

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Invalid receiver email")
    private String receiverEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
}
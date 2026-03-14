package com.casting.platform.controller;

import com.casting.platform.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/mock-pay")
@RequiredArgsConstructor
public class MockPayController {

    private final PaymentService paymentService;

    @Value("${payments.resultRedirectUrl:http://localhost:5173/payment/result}")
    private String resultRedirectUrl;

    @GetMapping(value = "/{externalId}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String page(@PathVariable String externalId) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1" />
                  <title>Mock Payment</title>
                  <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .card { max-width: 520px; border: 1px solid #ddd; border-radius: 10px; padding: 20px; }
                    .actions { display: flex; gap: 12px; margin-top: 20px; }
                    button { border: 0; border-radius: 8px; color: #fff; padding: 10px 14px; cursor: pointer; }
                    .ok { background: #198754; }
                    .fail { background: #dc3545; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <h2>Mock payment provider</h2>
                    <p>externalId: <b>%s</b></p>
                    <div class="actions">
                      <form method="post" action="/mock-pay/%s/complete">
                        <input type="hidden" name="status" value="SUCCESS" />
                        <button class="ok" type="submit">Pay SUCCESS</button>
                      </form>
                      <form method="post" action="/mock-pay/%s/complete">
                        <input type="hidden" name="status" value="FAILED" />
                        <button class="fail" type="submit">Pay FAILED</button>
                      </form>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(externalId, externalId, externalId);
    }

    @PostMapping("/{externalId}/complete")
    public ResponseEntity<Void> complete(@PathVariable String externalId,
                                         @RequestParam String status) {
        paymentService.processMockWebhook(externalId, status);
        String redirect = resultRedirectUrl + "?externalId=" +
                URLEncoder.encode(externalId, StandardCharsets.UTF_8);
        return ResponseEntity.status(302).header("Location", redirect).build();
    }
}

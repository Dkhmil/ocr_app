package com.khmil.crm.ocr.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.khmil.crm.ocr.service.util.Constants.OCR_COMMAND_TEMPLATE_DEFAULT;
import static com.khmil.crm.ocr.service.util.Constants.PROPERTY_PREFIX_APP_OCR;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = PROPERTY_PREFIX_APP_OCR)
public class AppOcrProperties {
    private String command = OCR_COMMAND_TEMPLATE_DEFAULT;
}

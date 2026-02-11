package com.social.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", System.getenv().getOrDefault(
                "CLOUDINARY_CLOUD_NAME", "dnltra0rc"
        ));
        config.put("api_key", System.getenv().getOrDefault(
                "CLOUDINARY_API_KEY", "438843129875376"
        ));
        config.put("api_secret", System.getenv().getOrDefault(
                "CLOUDINARY_API_SECRET", "ZbkPr-8WQEao0lO1_1sMU9Y9uVc"
        ));

        return new Cloudinary(config);
    }
}

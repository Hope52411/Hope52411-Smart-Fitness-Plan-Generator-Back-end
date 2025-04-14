package com.xxz.loginhouduan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ 1️⃣  获取当前项目的绝对路径，确保路径正确
        String uploadDir = "./uploads/";  // `./uploads/` 代表相对路径
        File uploadFolder = new File(uploadDir);

        // ✅ 2️⃣ 如果 `uploads/` 目录不存在，则自动创建
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // ✅ 3️⃣ 映射 `/uploads/**` 到本地 `uploads/` 目录
        String absoluteUploadPath = uploadFolder.getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadPath + "/");
    }
}

package com.huce.webblog.util;

import java.util.UUID;

public class SlugUtil {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) return "";

        // Tiến hành chuyển đổi về chữ thường và thay thế các ký tự không phải là chữ cái hoặc số thành dấu gạch ngang
        String slug = input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]+", "")
                .replaceAll("\\s+", "-")
                .replaceAll("(^-|-$)", "");

        String random = UUID.randomUUID()
                .toString()
                .replaceAll("-", "")
                .substring(0, 18);

        return slug + "-" + random;
    }
}

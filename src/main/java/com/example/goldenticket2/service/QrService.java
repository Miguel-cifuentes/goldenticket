package com.example.goldenticket2.service;

public interface QrService {
    byte[] generatePng(String content, int width, int height);
}

package com.example.tes24.qqueueing.channel.urlconnection;

import com.example.tes24.qqueueing.channel.Q2HttpHeader;

import java.net.HttpURLConnection;

public interface HttpURLConnectionFactory {
    HttpURLConnection getInstance(Q2HttpHeader q2HttpHeader);
}

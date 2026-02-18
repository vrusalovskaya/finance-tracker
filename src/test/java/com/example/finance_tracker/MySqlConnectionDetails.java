package com.example.finance_tracker;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface MySqlConnectionDetails extends ConnectionDetails {

    String getJdbcUrl();
    String getUsername();
    String getPassword();
}
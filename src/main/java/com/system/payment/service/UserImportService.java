package com.system.payment.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.system.payment.model.Merchant;
import com.system.payment.model.Role;
import com.system.payment.model.User;
import com.system.payment.repository.MerchantRepository;
import com.system.payment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImportService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceLoader resourceLoader;
    @Value("${csv-path}")
    private String csvPath;

    @Transactional
    public void importUsers() throws IOException, CsvException {
        Resource resource = resourceLoader.getResource(csvPath);
        if (!resource.exists()) {
            throw new IOException("CSV file not found at path: " + csvPath);
        }

        try (Reader reader = new InputStreamReader(resource.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {
            csvReader.skip(1); // Skip header

            List<UserCsvRecord> userRecords = parseCsvRecords(csvReader);
            userRecords.forEach(this::createUserFromCsvRecord);
        } catch (IOException | CsvException e) {
            log.error("Error occurred while importing users: {}", e.getMessage());
            throw e;
        }
    }

    private List<UserCsvRecord> parseCsvRecords(CSVReader csvReader) throws IOException, CsvException {
        List<UserCsvRecord> records = new ArrayList<>();
        String[] nextLine;

        while ((nextLine = csvReader.readNext()) != null) {
            try {
                records.add(mapToUserCsvRecord(nextLine));
            } catch (IllegalArgumentException e) {
                log.warn("Skipping invalid CSV record: {}", Arrays.toString(nextLine));
            }
        }

        return records;
    }

    private UserCsvRecord mapToUserCsvRecord(String[] record) {
        if (record.length < 5) {
            throw new IllegalArgumentException("CSV record is missing required fields");
        }

        String name = record[0];
        String email = record[1];
        String password = record[2];
        Role.RoleType roleType = Role.RoleType.valueOf(record[3]);
        User.UserStatus userStatus = User.UserStatus.valueOf(record[4]);
        String description = roleType == Role.RoleType.MERCHANT && record.length > 5 ? record[5] : null;

        return new UserCsvRecord(name, email, password, roleType, userStatus, description);
    }

    private void createUserFromCsvRecord(UserCsvRecord userCsvRecord) {
        if (userRepository.findByEmail(userCsvRecord.email()).isPresent()) {
            log.info("User with email {} already exists", userCsvRecord.email());
            return;
        }

        User user = userRepository.save(mapToUser(userCsvRecord));

        if (userCsvRecord.roleType() == Role.RoleType.MERCHANT) {
            Merchant merchant = new Merchant();
            merchant.setUser(user);
            merchant.setDescription(userCsvRecord.description());
            merchantRepository.save(merchant);
        }
    }

    private User mapToUser(UserCsvRecord userCsvRecord) {
        User user = new User();
        user.setName(userCsvRecord.name());
        user.setEmail(userCsvRecord.email());
        user.setPassword(passwordEncoder.encode(userCsvRecord.password()));
        user.setStatus(userCsvRecord.userStatus);

        Set<Role> roles = new HashSet<>();
        Role role = roleService.getRoleByType(userCsvRecord.roleType());
        roles.add(role);
        user.setRoles(roles);

        return user;
    }

    private record UserCsvRecord(
            String name,
            String email,
            String password,
            Role.RoleType roleType,
            User.UserStatus userStatus,
            String description
    ) {
    }

}

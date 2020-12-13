package com.perpetmatch.modules.Zone;

import com.perpetmatch.Domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone,Long> {
    Zone findByProvince(String province);
}
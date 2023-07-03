package com.Reboot.Minty.addressCode.repository;

import com.Reboot.Minty.addressCode.entity.AddressCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressCodeRepository extends JpaRepository<AddressCode, Long> {
}

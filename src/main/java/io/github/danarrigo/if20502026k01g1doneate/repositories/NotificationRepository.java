package io.github.danarrigo.if20502026k01g1doneate.repositories;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    // Ambil semua notif tanpa filter
    Slice<Notification> findByUser_UsernameOrderByTimeStampDesc(String username, Pageable pageable);
    
    // Filter hanya yang belum dibaca
    Slice<Notification> findByUser_UsernameAndIsReadFalseOrderByTimeStampDesc(String username, Pageable pageable);
    
    // Filter berdasarkan Tipe (DONASI / SISTEM)
    Slice<Notification> findByUser_UsernameAndTypeOrderByTimeStampDesc(String username, NotificationType type, Pageable pageable);
}
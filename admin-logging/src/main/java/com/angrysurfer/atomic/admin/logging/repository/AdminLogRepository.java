xpackage com.angrysurfer.atomic.admin.logging.repository;

import com.angrysurfer.atomic.admin.logging.AdminLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLogEntry, UUID> {
    // Additional custom queries can be added here if needed
}
package models;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Edge case tests for Agency and Service models
 */
class AgencyServiceEdgeCaseTest {

    // ==================== AGENCY EDGE CASES ====================

    @Test
    void agencyTotalCountersCanBeZero() {
        Agency agency = new Agency();
        agency.setTotalCounters(0);
        assertEquals(0, agency.getTotalCounters());
    }

    @Test
    void agencyTotalCountersCanBeLarge() {
        Agency agency = new Agency();
        agency.setTotalCounters(100);
        assertEquals(100, agency.getTotalCounters());
    }

    @Test
    void agencyActiveDefaultIsFalse() {
        Agency agency = new Agency();
        assertFalse(agency.isActive());
    }

    @Test
    void agencyCanBeActivated() {
        Agency agency = new Agency();
        agency.setActive(true);
        assertTrue(agency.isActive());
    }

    @Test
    void agencyCanBeDeactivated() {
        Agency agency = new Agency();
        agency.setActive(true);
        agency.setActive(false);
        assertFalse(agency.isActive());
    }

    @Test
    void agencyNameCanBeEmpty() {
        Agency agency = new Agency();
        agency.setName("");
        assertEquals("", agency.getName());
    }

    @Test
    void agencyNameCanContainSpecialCharacters() {
        Agency agency = new Agency();
        agency.setName("Agency & Co. #1 (Main)");
        assertEquals("Agency & Co. #1 (Main)", agency.getName());
    }

    @Test
    void agencyAddressCanBeMultiline() {
        Agency agency = new Agency();
        agency.setAddress("123 Main Street\nBuilding A\nFloor 2");
        assertTrue(agency.getAddress().contains("\n"));
    }

    @Test
    void agencyCityCanHaveAccents() {
        Agency agency = new Agency();
        agency.setCity("Montréal");
        assertEquals("Montréal", agency.getCity());
    }

    @Test
    void agencyPhoneCanHaveDifferentFormats() {
        Agency agency = new Agency();
        agency.setPhone("+1 (555) 123-4567");
        assertEquals("+1 (555) 123-4567", agency.getPhone());
    }

    @Test
    void agencyCreatedAtCanBeSet() {
        Agency agency = new Agency();
        LocalDateTime now = LocalDateTime.now();
        agency.setCreatedAt(now);
        assertEquals(now, agency.getCreatedAt());
    }

    @Test
    void agencyIdCanBeLarge() {
        Agency agency = new Agency();
        agency.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, agency.getId());
    }

    // ==================== SERVICE EDGE CASES ====================

    @Test
    void serviceEstimatedTimeCanBeZero() {
        Service service = new Service();
        service.setEstimatedTime(0);
        assertEquals(0, service.getEstimatedTime());
    }

    @Test
    void serviceEstimatedTimeCanBeLarge() {
        Service service = new Service();
        service.setEstimatedTime(480); // 8 hours
        assertEquals(480, service.getEstimatedTime());
    }

    @Test
    void serviceActiveDefaultIsFalse() {
        Service service = new Service();
        assertFalse(service.isActive());
    }

    @Test
    void serviceFromConstructorIsActive() {
        Service service = new Service(1, "Test", "Description", 15);
        assertTrue(service.isActive());
    }

    @Test
    void serviceCanBeDeactivated() {
        Service service = new Service(1, "Test", "Description", 15);
        service.setActive(false);
        assertFalse(service.isActive());
    }

    @Test
    void serviceNameCanBeEmpty() {
        Service service = new Service();
        service.setName("");
        assertEquals("", service.getName());
    }

    @Test
    void serviceDescriptionCanBeNull() {
        Service service = new Service();
        assertNull(service.getDescription());
    }

    @Test
    void serviceDescriptionCanBeLong() {
        Service service = new Service();
        String longDescription = "A".repeat(1000);
        service.setDescription(longDescription);
        assertEquals(1000, service.getDescription().length());
    }

    @Test
    void serviceDescriptionCanContainHtml() {
        Service service = new Service();
        service.setDescription("<p>HTML content</p>");
        assertEquals("<p>HTML content</p>", service.getDescription());
    }

    @Test
    void serviceCreatedAtCanBeSet() {
        Service service = new Service();
        LocalDateTime now = LocalDateTime.now();
        service.setCreatedAt(now);
        assertEquals(now, service.getCreatedAt());
    }

    @Test
    void serviceIdCanBeLarge() {
        Service service = new Service();
        service.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, service.getId());
    }

    @Test
    void serviceNameCanContainUnicode() {
        Service service = new Service();
        service.setName("خدمة العملاء"); // Arabic
        assertEquals("خدمة العملاء", service.getName());
    }

    @Test
    void serviceParameterizedConstructorAllFields() {
        Service service = new Service(5, "ID Renewal", "Renew your national ID", 30);
        assertEquals(5, service.getId());
        assertEquals("ID Renewal", service.getName());
        assertEquals("Renew your national ID", service.getDescription());
        assertEquals(30, service.getEstimatedTime());
        assertTrue(service.isActive());
    }
}
